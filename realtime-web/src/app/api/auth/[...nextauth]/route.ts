import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

const handler = NextAuth({
  providers: [
    Keycloak({
      issuer: process.env.KC_ISSUER,
      clientId: process.env.KC_RTM_CLIENT_ID || "",
      clientSecret: process.env.KC_RTM_CLIENT_SECRET || "", // drop if public+PKCE
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
