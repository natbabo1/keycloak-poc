package com.example.his;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.HashMap;
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
        TrustManager[] trustAll = new TrustManager[] { new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
          }

          public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {
          }

          public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {
          }
        } };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new java.security.SecureRandom());
        b.sslContext(sc);
        SSLParameters sp = new SSLParameters();
        sp.setEndpointIdentificationAlgorithm(null);
        b.sslParameters(sp);
      } catch (Exception ignored) {
      }
    }
    this.http = b.build();
  }

  public HisProfile validate(String hisCode,
      String subjectPriorityCsv,
      boolean acceptAny2xx,
      boolean requireR0000,
      boolean checkExpiry,
      long clockSkewMs) throws Exception {

    String body = "{\"key\":\"" + hisCode + "\",\"restuser\":\"" + restUser + "\",\"restpass\":\"" + restPass + "\"}";

    HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(endpoint))
        .timeout(Duration.ofMillis(timeoutMs))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body));

    if (headerName != null && !headerName.isBlank() && headerValue != null && !headerValue.isBlank()) {
      rb.header(headerName, headerValue);
    }

    HttpResponse<String> res = http.send(rb.build(), HttpResponse.BodyHandlers.ofString());
    int sc = res.statusCode();
    if (sc < 200 || sc >= 300)
      return null; // must be 2xx

    JsonNode json = om.readTree(res.body());

    if (!acceptAny2xx) {
      String code = json.at("/ResponseStatus/Code").asText("");
      if (requireR0000 && !"R0000".equals(code))
        return null;
    }

    if (checkExpiry) {
      long now = System.currentTimeMillis();
      long exp = json.at("/Result/result/expired").asLong(0L);
      if (exp > 0 && now > exp + clockSkewMs)
        return null; // expired
    }

    // Extract candidates (null if missing/blank)
    String email = nv(json.at("/Result/result/email"));
    String employeeId = nv(json.at("/Result/result/employeeId"));
    String securityRowId = nv(json.at("/Result/result/securityRowId"));
    String appointmentId = nv(json.at("/Result/result/appointmentId"));
    String keyResult = nv(json.at("/Result/keyResult"));

    // Choose subject
    String[] pri = (subjectPriorityCsv == null || subjectPriorityCsv.isBlank())
        ? new String[] { "employeeId", "email", "securityRowId", "keyResult" }
        : subjectPriorityCsv.split("\\s*,\\s*");

    String sub = null;
    for (String p : pri) {
      switch (p) {
        case "employeeId":
          if (nz(employeeId)) {
            sub = "emp-" + employeeId;
            break;
          }
          continue;
        case "email":
          if (nz(email)) {
            sub = email;
            break;
          }
          continue;
        case "securityRowId":
          if (nz(securityRowId)) {
            sub = "sec-" + securityRowId;
            break;
          }
          continue;
        case "keyResult":
          if (nz(keyResult)) {
            sub = "key-" + keyResult;
            break;
          }
          continue;
        case "hisCode":
          if (nz(hisCode)) {
            sub = "his-" + hisCode;
            break;
          }
          continue;
        default:
          continue;
      }
      if (sub != null)
        break;
    }
    if (!nz(sub))
      sub = "his-" + hisCode; // last resort

    HisProfile p = new HisProfile();
    p.sub = sub;
    p.email = email;
    p.attrs.put("employeeId", n(employeeId));
    p.attrs.put("securityRowId", n(securityRowId));
    p.attrs.put("appointmentId", n(appointmentId));
    p.attrs.put("keyResult", n(keyResult));
    return p;
  }

  // --- helpers ---
  private static boolean nz(String s) {
    return s != null && !s.isBlank();
  }

  private static String n(String s) {
    return s == null ? "" : s;
  }

  private static String nv(JsonNode node) {
    if (node == null || node.isMissingNode() || node.isNull())
      return null;
    String s = node.asText(null);
    return (s == null || s.isBlank()) ? null : s;
  }

  public static class HisProfile {
    public String sub;
    public String email;
    public Map<String, String> attrs = new HashMap<>();
  }
}
