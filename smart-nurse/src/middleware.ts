import { withAuth } from "next-auth/middleware";

export default withAuth(
  // `withAuth` augments your `Request` with the user's token.
  function middleware(req) {
    // Add any additional middleware logic here if needed
  },
  {
    callbacks: {
      authorized: ({ token }) => !!token,
    },
  }
);

export const config = {
  matcher: [
    // Protect the home page and any other authenticated routes
    "/home/:path*",
    // Add more protected routes here as needed
    // '/dashboard/:path*',
    // '/profile/:path*',
  ],
};
