package com.example.his;

import org.keycloak.Config;
import org.keycloak.authentication.*;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class HisCodeAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {
  public static final String ID = "his-code-authenticator";

  @Override public String getId() { return ID; }
  @Override public String getDisplayType() { return "HIS Code Authenticator"; }
  @Override public String getHelpText() { return "Redeems his_code with HIS and logs user in."; }
  @Override public Authenticator create(KeycloakSession session) { return new HisCodeAuthenticator(); }
  @Override public Requirement[] getRequirementChoices() { return new Requirement[]{Requirement.REQUIRED, Requirement.ALTERNATIVE}; }
  @Override public boolean isUserSetupAllowed() { return false; }
  @Override public void init(Config.Scope config) {}
  @Override public void postInit(KeycloakSessionFactory factory) {}
  @Override public void close() {}
  @Override public String getReferenceCategory() { return "his"; }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return List.of(
      prop("hisEndpoint","HIS Validate Endpoint","URL to POST {code} for validation", ProviderConfigProperty.STRING_TYPE, ""),
      prop("hisApiKey","HIS API Key (Bearer)","Optional API key", ProviderConfigProperty.PASSWORD, ""),
      prop("timeoutMs","Timeout (ms)","HTTP timeout", ProviderConfigProperty.STRING_TYPE, "3000"),
      prop("failOnInvalid","Fail on invalid code","If false, fall back to normal login", ProviderConfigProperty.BOOLEAN_TYPE, "true")
    );
  }

  private ProviderConfigProperty prop(String name, String label, String help, String type, String defaultValue) {
    var p = new ProviderConfigProperty();
    p.setName(name); p.setLabel(label); p.setHelpText(help); p.setType(type); p.setDefaultValue(defaultValue);
    return p;
  }
}
