import { NextResponse } from "next/server";

export async function GET() {
  const signin = new URL("/api/auth/signin", process.env.RTM_BASE_URL);
  return NextResponse.redirect(signin.toString());
}
