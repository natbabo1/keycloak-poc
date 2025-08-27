package com.example.his;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;

public class HisClient {
  private final HttpClient http;
  private final ObjectMapper om = new ObjectMapper();
  private final String endpoint;
  private final String headerName;
  private final String headerValue;
  private final String restUser;
  private final String restPass;
  private final int timeoutMs;

  public HisClient(String endpoint, String headerName, String headerValue,
                   String restUser, String restPass, int timeoutMs, boolean insecureTLS) {
    this.endpoint = endpoint;
    this.headerName = headerName;
    this.headerValue = headerValue;
    this.restUser = restUser;
    this.restPass = restPass;
    this.timeoutMs = timeoutMs;

    HttpClient.Builder b = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeoutMs));
    if (insecureTLS) {
      try {
        TrustManager[] trustAll = new TrustManager[]{ new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
          public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
          public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {}
        }};
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new java.security.SecureRandom());
        b.sslContext(sc);
        b.sslParameters(new SSLParameters(){{
          setEndpointIdentificationAlgorithm(null); // disable hostname verify
        }});
      } catch (Exception ignored) {}
    }
    this.http = b.build();
  }

  public HisProfile validate(String hisCode) throws Exception {
    String json = om.writeValueAsString(Map.of(
      "key", hisCode,
      "restuser", restUser,
      "restpass", restPass
    ));

    HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(endpoint))
      .timeout(Duration.ofMillis(timeoutMs))
      .header("Content-Type","application/json")
      .POST(HttpRequest.BodyPublishers.ofString(json));

    if (headerName != null && !headerName.isBlank() && headerValue != null && !headerValue.isBlank()) {
      rb.header(headerName, headerValue);
    }

    HttpResponse<String> res = http.send(rb.build(), HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() != 200) return null;

    JsonNode j = om.readTree(res.body());
    // If your mock returns a specific success flag, check it here:
    // if (!j.path("ok").asBoolean(true)) return null;

    HisProfile p = new HisProfile();
    // Try to read identity if present; else fall back to code-based subject
    p.sub = j.path("sub").asText(null);
    if (p.sub == null || p.sub.isBlank()) p.sub = "his-" + hisCode; // POC fallback
    p.email = j.path("email").asText(null);
    p.givenName = j.path("given_name").asText(null);
    p.familyName = j.path("family_name").asText(null);
    if (j.has("roles")) j.get("roles").forEach(n -> p.addRole(n.asText()));
    return p;
  }

  public static class HisProfile {
    public String sub, email, givenName, familyName;
    public java.util.Set<String> roles = new java.util.HashSet<>();
    public void addRole(String r) { roles.add(r); }
  }
}
