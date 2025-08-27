package com.example.his;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class HisClient {
  private final HttpClient http;
  private final ObjectMapper om = new ObjectMapper();
  private final String endpoint;
  private final String apiKey;
  private final int timeoutMs;

  public HisClient(String endpoint, String apiKey, int timeoutMs) {
    this.endpoint = endpoint;
    this.apiKey = apiKey;
    this.timeoutMs = timeoutMs;
    this.http = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeoutMs)).build();
  }

  public HisProfile validate(String hisCode) throws Exception {
    var body = "{\"code\":\"" + hisCode + "\"}";
    var req = HttpRequest.newBuilder(URI.create(endpoint))
        .timeout(Duration.ofMillis(timeoutMs))
        .header("Content-Type","application/json")
        .header("Authorization", apiKey != null ? "Bearer " + apiKey : "")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

    var res = http.send(req, HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() != 200) return null;
    JsonNode j = om.readTree(res.body());
    if (!j.path("ok").asBoolean(false)) return null;

    var p = new HisProfile();
    p.sub = j.path("sub").asText();
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
