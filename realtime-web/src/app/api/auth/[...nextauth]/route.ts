import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

const issuer = process.env.KC_ISSUER!;
const internal = process.env.KC_INTERNAL_BASE!;

const handler = NextAuth({
  providers: [
    Keycloak({
      issuer,
      authorization: `${issuer}/protocol/openid-connect/auth`, // browser hits localhost
      token: `${internal}/protocol/openid-connect/token`, // server hits keycloak:8080
      userinfo: `${internal}/protocol/openid-connect/userinfo`, // server hits keycloak:8080
      jwks_endpoint: `${internal}/protocol/openid-connect/certs`,
      clientId: process.env.KC_SN_CLIENT_ID || process.env.KC_RTM_CLIENT_ID!,
      clientSecret:
        process.env.KC_SN_CLIENT_SECRET || process.env.KC_RTM_CLIENT_SECRET!,
    }),
  ],
  session: { strategy: "jwt", maxAge: 60 * 60 }, // shorter at app level
  callbacks: {
    async jwt({ token, account }) {
      if (account?.access_token) token.accessToken = account.access_token;
      return token;
    },
    async session({ session, token }) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (session as any).accessToken = token.accessToken;
      return session;
    },
  },
});
export { handler as GET, handler as POST };
