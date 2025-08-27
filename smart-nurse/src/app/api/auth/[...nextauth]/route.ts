import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

const handler = NextAuth({
  providers: [
    Keycloak({
      issuer: process.env.KC_ISSUER,
      clientId: process.env.KC_SN_CLIENT_ID || "",
      clientSecret: process.env.KC_SN_CLIENT_SECRET || "", // omit if public+PKCE
    }),
  ],
  session: { strategy: "jwt", maxAge: 60 * 60 * 8 },
});
export { handler as GET, handler as POST };
