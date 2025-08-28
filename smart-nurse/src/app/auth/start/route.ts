import { CONFIG } from "@/lib/config";
import { NextRequest, NextResponse } from "next/server";

export async function GET(req: NextRequest) {
  const base = CONFIG.SN_BASE_URL || "http://localhost:3200";
  const returnTo = new URL(req.url).searchParams.get("returnTo") || "/";
  const cb = new URL(returnTo, base);

  console.log("FWD returnTo", returnTo);
  console.log("FWD cb", cb.toString());

  const signin = new URL("/sn-login", base);
  signin.searchParams.set("callback_url", cb.toString());
  // Optional: enforce silent SSO; if no SSO cookie, KC returns error
  signin.searchParams.set("prompt", "none");

  return NextResponse.redirect(signin.toString());
}
