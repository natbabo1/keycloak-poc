package com.example.his;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
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

  @Override
  public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
    return new AuthenticationExecutionModel.Requirement[] {
      AuthenticationExecutionModel.Requirement.REQUIRED,
      AuthenticationExecutionModel.Requirement.ALTERNATIVE
    };
  }

  @Override public boolean isUserSetupAllowed() { return false; }
  @Override public boolean isConfigurable() { return true; }
  @Override public String getReferenceCategory() { return "his"; }

  @Override public void init(Config.Scope config) {}
  @Override public void postInit(KeycloakSessionFactory factory) {}
  @Override public void close() {}

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return List.of(
      prop("hisEndpoint","HIS Validate Endpoint","POST URL to validate code", ProviderConfigProperty.STRING_TYPE, ""),
      prop("hisHeaderName","Auth Header Name","e.g. accesstoken", ProviderConfigProperty.STRING_TYPE, "accesstoken"),
      prop("hisHeaderValue","Auth Header Value","Bearer/API key value", ProviderConfigProperty.PASSWORD, ""),
      prop("hisRestUser","Body restuser","JSON body restuser", ProviderConfigProperty.STRING_TYPE, ""),
      prop("hisRestPass","Body restpass","JSON body restpass", ProviderConfigProperty.PASSWORD, ""),
      prop("timeoutMs","Timeout (ms)","HTTP timeout", ProviderConfigProperty.STRING_TYPE, "4000"),
      prop("insecureTLS","Allow self-signed (dev)","Disable TLS verify (DEV ONLY)", ProviderConfigProperty.BOOLEAN_TYPE, "false"),
      prop("failOnInvalid","Fail if invalid","true=stop, false=fallback", ProviderConfigProperty.BOOLEAN_TYPE, "true")
    );
  }

  private ProviderConfigProperty prop(String name, String label, String help, String type, String defVal) {
    var p = new ProviderConfigProperty();
    p.setName(name); p.setLabel(label); p.setHelpText(help);
    p.setType(type); p.setDefaultValue(defVal);
    return p;
  }
}
