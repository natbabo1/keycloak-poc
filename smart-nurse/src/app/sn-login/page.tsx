"use client";
import { useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { signIn } from "next-auth/react";

export default function SnLogin() {
  const sp = useSearchParams();
  const error = sp.get("error");

  useEffect(() => {
    if (error) return; // avoid loops
    const origin = window.location.origin;
    const callbackUrl = sp.get("callbackUrl") || origin + "/";
    signIn("keycloak", { callbackUrl });
  }, [sp, error]);

  if (error) return <pre>Login failed: {error}</pre>;
  return <p>Signing you in…</p>;
}
