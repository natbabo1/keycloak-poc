# Keycloak POC with Custom Auth Flow

This repository demonstrates how to use [Keycloak](https://www.keycloak.org/) as an Identity Provider (IdP) for multiple services using the OpenID Connect (OIDC) Authorization Code flow with PKCE. It includes a custom Keycloak authenticator and example applications wired to authenticate through Keycloak.

## Project Structure

- `keycloak/` – Keycloak image with the `HisCodeAuthenticator` extension and realm configuration.
- `smart-nurse/` – Next.js client application.
- `realtime-web/` – Next.js client application.
- `realtime-api/` – NestJS API secured with bearer tokens.
- `docker-compose.yml` – Orchestrates Keycloak, PostgreSQL, and the example services.

## Getting Started

1. **Prerequisites**
   - Docker and Docker Compose
   - Node.js 18+ (for development inside the app directories)

2. **Start the stack**

   ```bash
   docker-compose up
   ```

   The compose file provisions PostgreSQL, builds the custom Keycloak image, and launches the example applications with the proper environment variables for OIDC.

3. **Access the applications**
   - Keycloak Admin Console: <http://localhost:8080>
   - Smart Nurse app: <http://localhost:3200>
   - Realtime Web app: <http://localhost:3210>
   - Realtime API: <http://localhost:3211>

## Keycloak Configuration

The realm configuration is imported from `keycloak/realms/nursing-dev.json`. It defines two public clients (`smart-nurse-dev` and `realtime-monitoring-dev`) that require PKCE using `S256`.

### Custom Authenticator

`HisCodeAuthenticator` is a Keycloak extension that validates an incoming `his_code` query parameter against an external service and creates the user on-the-fly. It is registered in the authentication flow and allows Keycloak to accept tokens from external Hospital Information Systems.

## OIDC + PKCE Flow

1. The browser application initiates the Authorization Code flow with PKCE against Keycloak (`/protocol/openid-connect/auth`).
2. Keycloak runs the custom authenticator as part of the login flow. If the `his_code` is valid, the user is created or updated and the flow succeeds.
3. Keycloak returns an authorization code. The application exchanges it for tokens using the PKCE verifier at the token endpoint.
4. Client applications attach the access token to API requests. The `realtime-api` verifies the token using Keycloak’s JWKS endpoint and checks the audience and issuer claims.

## Development Tips

- Client applications are configured with environment variables in `docker-compose.yml`. Adjust them as needed for your environment.
- For local development inside a service directory, run the usual `npm install` and `npm run dev` commands.
- The custom authenticator code lives under `keycloak/keycloak-his-auth/`. Use Maven to build the jar if you need to modify it.

## Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [OpenID Connect Core](https://openid.net/specs/openid-connect-core-1_0.html)
- [RFC 7636: Proof Key for Code Exchange](https://www.rfc-editor.org/rfc/rfc7636)

