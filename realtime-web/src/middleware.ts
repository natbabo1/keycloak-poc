import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { getToken } from "next-auth/jwt";

const PUBLIC_PATHS = [
  "/api/auth",
  "/auth/start",
  "/rtm-login",
  "/_next",
  "/favicon.ico",
  "/assets",
  "/healthz",
];

export async function middleware(req: NextRequest) {
  const { pathname, search } = req.nextUrl;

  // allow public paths
  if (PUBLIC_PATHS.some((p) => pathname.startsWith(p))) {
    return NextResponse.next();
  }

  const token = await getToken({ req, secret: process.env.NEXTAUTH_SECRET });
  if (token) return NextResponse.next();

  // If you want to ALWAYS centralize login via Smart Nurse for direct hits:
  if (process.env.HUB_URL) {
    const hub = new URL(process.env.HUB_URL);
    // optional: preserve original path so SN can send them back later
    hub.searchParams.set("from", `${pathname}${search}`);
    return NextResponse.redirect(hub.toString());
  }

  // Otherwise, start OIDC directly (silent if SSO cookie exists)
  const url = req.nextUrl.clone();
  url.pathname = "/auth/start";
  url.searchParams.set("returnTo", `${pathname}${search}`);
  return NextResponse.redirect(url);
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
