import { NextResponse } from "next/server";
import { getServerSession } from "next-auth";
import { SERVICE_MAP, CONFIG } from "@/lib/config";

export async function GET(req: Request) {
  const url = new URL(req.url);
  const service = url.searchParams.get("service") || "";
  const hisCode = url.searchParams.get("his_code") || "";
  const target = SERVICE_MAP[service];
  if (!target)
    return NextResponse.json({ error: "invalid service" }, { status: 400 });

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const session = await (getServerSession as any)();
  if (!session) {
    const post = new URL("/post-login", CONFIG.SN_BASE_URL);
    post.searchParams.set("service", service);

    const signin = new URL("/api/auth/signin", CONFIG.SN_BASE_URL);
    signin.searchParams.set("callbackUrl", post.toString());
    if (hisCode) signin.searchParams.set("his_code", hisCode);
    signin.searchParams.set("kc_idp_hint", "his");
    return NextResponse.redirect(hisCode.toString());
  }
  return NextResponse.redirect(target);
}
