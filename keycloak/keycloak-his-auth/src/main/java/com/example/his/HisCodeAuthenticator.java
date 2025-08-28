package com.example.his;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.*;
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
      ctx.attempted();
      return;
    }

    Map<String, String> cfg = Optional.ofNullable(ctx.getAuthenticatorConfig())
        .map(c -> c.getConfig()).orElse(Collections.emptyMap());

    String url = cfg.getOrDefault("hisEndpoint", "");
    String hdrName = cfg.getOrDefault("hisHeaderName", "accesstoken");
    String hdrValue = cfg.getOrDefault("hisHeaderValue", "");
    String restUser = cfg.getOrDefault("hisRestUser", "");
    String restPass = cfg.getOrDefault("hisRestPass", "");
    String subjPri = cfg.getOrDefault("subjectPriority", "employeeId,email,securityRowId,keyResult");
    boolean any2xx = Boolean.parseBoolean(cfg.getOrDefault("acceptAny2xx", "true"));
    boolean needR0 = Boolean.parseBoolean(cfg.getOrDefault("requireR0000", "false"));
    boolean chkExp = Boolean.parseBoolean(cfg.getOrDefault("checkExpiry", "true"));
    long skewMs = Long.parseLong(cfg.getOrDefault("clockSkewMs", "60000"));
    int timeoutMs = Integer.parseInt(cfg.getOrDefault("timeoutMs", "4000"));
    boolean insecure = Boolean.parseBoolean(cfg.getOrDefault("insecureTLS", "false"));

    HisClient.HisProfile prof;
    try {
      prof = new HisClient(url, hdrName, hdrValue, restUser, restPass, timeoutMs, insecure)
          .validate(hisCode, subjPri, any2xx, needR0, chkExp, skewMs);
    } catch (Exception e) {
      ctx.failure(AuthenticationFlowError.INTERNAL_ERROR);
      return;
    }

    if (prof == null || prof.sub == null || prof.sub.isBlank()) {
      ctx.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
      return;
    }

    RealmModel realm = ctx.getRealm();
    KeycloakSession session = ctx.getSession();
    UserProvider users = session.users();

    UserModel user = users.getUserByUsername(realm, prof.sub);
    if (user == null) {
      user = users.addUser(realm, prof.sub);
      user.setEnabled(true);
    }

    // Fill common fields so KC doesn't ask for them
    if (prof.email != null && !prof.email.isBlank()) {
      user.setEmail(prof.email);
      user.setEmailVerified(true); // <- avoids "verify email" required action
    }
    // If you have names, set them; otherwise skip
    if (prof.attrs.get("firstName") != null)
      user.setFirstName(prof.attrs.get("firstName"));
    if (prof.attrs.get("lastName") != null)
      user.setLastName(prof.attrs.get("lastName"));
    if (user.getFirstName() == null)
      user.setFirstName("NA");
    if (user.getLastName() == null)
      user.setLastName("NA");

    // Kill first-login prompts for this user
    user.removeRequiredAction(UserModel.RequiredAction.UPDATE_PROFILE);
    user.removeRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);
    user.removeRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP);
    user.removeRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);

    // store extras as attributes (no lambda capture)
    for (java.util.Map.Entry<String, String> e : prof.attrs.entrySet()) {
      String v = e.getValue();
      if (v != null && !v.isBlank()) {
        user.setSingleAttribute(e.getKey(), v);
      }
    }
    user.setSingleAttribute("hisSource", "true");

    ctx.setUser(user);
    ctx.success();

  }

  @Override
  public void action(AuthenticationFlowContext ctx) {
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession s, RealmModel r, UserModel u) {
    return true;
  }

  @Override
  public void setRequiredActions(KeycloakSession s, RealmModel r, UserModel u) {
  }

  @Override
  public void close() {
  }
}
