import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

const issuer = process.env.KC_ISSUER!;
// const internal = process.env.KC_INTERNAL_BASE!;

const handler = NextAuth({
  providers: [
    Keycloak({
      issuer,
      clientId: process.env.KC_SN_CLIENT_ID!,
      clientSecret: "",
    }),
  ],
  pages: { signIn: "/sn-login" },
  session: { strategy: "jwt", maxAge: 60 * 60 * 8 },
});
export { handler as GET, handler as POST };
