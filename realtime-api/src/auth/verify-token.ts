// auth/verify-token.ts
import { createRemoteJWKSet, jwtVerify } from 'jose';

const JWKS = createRemoteJWKSet(new URL(process.env.JWKS_URI!));

export async function verifyAccessToken(bearer: string) {
  const token = bearer.replace(/^Bearer\s+/i, '');
  const { payload } = await jwtVerify(token, JWKS, {
    issuer: process.env.JWT_ISSUER!,
    audience: process.env.JWT_AUDIENCE!,
  });
  return payload; // { sub, email, realm_access, ... }
}
