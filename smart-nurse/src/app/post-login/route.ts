import { SERVICE_MAP } from "@/lib/config";
import { NextResponse } from "next/server";

export async function GET(req: Request) {
  const url = new URL(req.url);
  const service = url.searchParams.get("service") || "";
  const target = SERVICE_MAP[service];
  if (!target)
    return NextResponse.json({ error: "invalid service" }, { status: 400 });
  return NextResponse.redirect(target);
}
