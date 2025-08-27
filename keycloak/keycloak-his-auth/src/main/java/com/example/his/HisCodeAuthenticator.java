package com.example.his;

import org.keycloak.authentication.*;
import org.keycloak.models.*;
import org.keycloak.services.managers.AuthenticationManager;

import java.util.List;
import java.util.Objects;

public class HisCodeAuthenticator implements Authenticator {
  @Override
  public void authenticate(AuthenticationFlowContext ctx) {
    var qp = ctx.getHttpRequest().getUri().getQueryParameters();
    String hisCode = qp.getFirst("his_code");

    if (hisCode == null || hisCode.isBlank()) {
      ctx.attempted(); // no code -> continue normal Browser flow
      return;
    }

    // Read config from component
    var cfg = ctx.getAuthenticatorConfig() != null ? ctx.getAuthenticatorConfig().getConfig() : java.util.Map.of();
    String url     = cfg.getOrDefault("hisEndpoint", "");
    String apiKey  = cfg.getOrDefault("hisApiKey", "");
    int timeoutMs  = Integer.parseInt(cfg.getOrDefault("timeoutMs", "3000"));
    boolean failOnInvalid = Boolean.parseBoolean(cfg.getOrDefault("failOnInvalid","true"));

    HisClient.HisProfile prof;
    try {
      prof = new HisClient(url, apiKey, timeoutMs).validate(hisCode);
    } catch (Exception e) {
      if (failOnInvalid) { ctx.failure(AuthenticationFlowError.INTERNAL_ERROR); }
      else { ctx.attempted(); }
      return;
    }

    if (prof == null || prof.sub == null || prof.sub.isBlank()) {
      if (failOnInvalid) { ctx.failure(AuthenticationFlowError.INVALID_CREDENTIALS); }
      else { ctx.attempted(); }
      return;
    }

    // find or create user
    RealmModel realm = ctx.getRealm();
    KeycloakSession session = ctx.getSession();
    UserModel user = session.users().getUserByUsername(realm, prof.sub);
    if (user == null) {
      user = session.users().addUser(realm, prof.sub);
      user.setEnabled(true);
      if (prof.email != null) user.setEmail(prof.email);
      if (prof.givenName != null) user.setFirstName(prof.givenName);
      if (prof.familyName != null) user.setLastName(prof.familyName);
      user.setSingleAttribute("hisSource","true");
    }

    // optional: grant realm roles if they exist
    for (String r : prof.roles) {
      RoleModel role = realm.getRole(r);
      if (role != null) user.grantRole(role);
    }

    ctx.setUser(user);
    ctx.success();
  }

  @Override public void action(AuthenticationFlowContext ctx) {}
  @Override public boolean requiresUser() { return false; }
  @Override public boolean configuredFor(KeycloakSession s, RealmModel r, UserModel u) { return true; }
  @Override public void setRequiredActions(KeycloakSession s, RealmModel r, UserModel u) {}
  @Override public void close() {}
}
