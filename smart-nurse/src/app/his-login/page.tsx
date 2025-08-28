"use client";

import { useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { signIn } from "next-auth/react";

export default function HisLogin() {
  const sp = useSearchParams();
  const error = sp.get("error");

  useEffect(() => {
    if (error) return; // don't loop on errors
    const callbackUrl = sp.get("callback_url") ?? "";
    const his_code = sp.get("his_code") ?? ""; // or login_hint if you switched

    console.log("FWD his_code", his_code);
    console.log("FWD callback_url", callbackUrl);

    const params: Record<string, string> = { callbackUrl };
    if (his_code) params.his_code = his_code; // preferred over custom query
    signIn("keycloak", { callbackUrl }, params);
  }, [sp, error]);

  return <p>Redirecting to Keycloak…</p>;
}
