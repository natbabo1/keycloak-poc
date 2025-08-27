package com.example.his;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class HisCodeAuthenticator implements Authenticator {

  @Override
  public void authenticate(AuthenticationFlowContext ctx) {
    MultivaluedMap<String, String> qp = ctx.getHttpRequest().getUri().getQueryParameters();
    String hisCode = qp.getFirst("his_code");

    if (hisCode == null || hisCode.isBlank()) {
      ctx.attempted(); // no code -> continue normal Browser flow
      return;
    }

    Map<String, String> cfg = Optional.ofNullable(ctx.getAuthenticatorConfig())
      .map(ac -> ac.getConfig())
      .orElse(Collections.emptyMap());

    String url       = cfg.getOrDefault("hisEndpoint", "");
    String hdrName   = cfg.getOrDefault("hisHeaderName", "accesstoken");
    String hdrValue  = cfg.getOrDefault("hisHeaderValue", "");
    String restUser  = cfg.getOrDefault("hisRestUser", "");
    String restPass  = cfg.getOrDefault("hisRestPass", "");
    int timeoutMs    = Integer.parseInt(cfg.getOrDefault("timeoutMs", "4000"));
    boolean insecure = Boolean.parseBoolean(cfg.getOrDefault("insecureTLS","false"));
    boolean hardFail = Boolean.parseBoolean(cfg.getOrDefault("failOnInvalid","true"));

    HisClient.HisProfile prof;
    try {
      prof = new HisClient(url, hdrName, hdrValue, restUser, restPass, timeoutMs, insecure)
        .validate(hisCode);
    } catch (Exception e) {
      if (hardFail) { ctx.failure(AuthenticationFlowError.INTERNAL_ERROR); }
      else { ctx.attempted(); }
      return;
    }

    if (prof == null || prof.sub == null || prof.sub.isBlank()) {
      if (hardFail) { ctx.failure(AuthenticationFlowError.INVALID_CREDENTIALS); }
      else { ctx.attempted(); }
      return;
    }

    RealmModel realm = ctx.getRealm();
    KeycloakSession session = ctx.getSession();
    UserProvider users = session.users();

    UserModel user = users.getUserByUsername(realm, prof.sub);
    if (user == null) {
      user = users.addUser(realm, prof.sub);
      user.setEnabled(true);
      if (prof.email != null) user.setEmail(prof.email);
      if (prof.givenName != null) user.setFirstName(prof.givenName);
      if (prof.familyName != null) user.setLastName(prof.familyName);
      user.setSingleAttribute("hisSource", "true");
    }

    // Optional: grant realm roles if they exist
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
