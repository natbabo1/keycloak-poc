"use client";

import { useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { signIn } from "next-auth/react";

export default function HisLogin() {
  const sp = useSearchParams();

  useEffect(() => {
    const service = sp.get("service") ?? "";
    const his_code = sp.get("his_code") ?? "";
    const origin = window.location.origin;
    const callbackUrl = `${origin}/post-login${
      service ? `?service=${encodeURIComponent(service)}` : ""
    }`;

    console.log("FWD callbackUrl", callbackUrl);
    console.log("FWD his_code", his_code);

    // This forwards `his_code` into Keycloak's /auth request
    signIn("keycloak", undefined, { callbackUrl, his_code });
  }, [sp]);

  return <p>Redirecting to Keycloak…</p>;
}
