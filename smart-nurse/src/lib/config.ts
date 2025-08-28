// Configuration constants for the application
export const SERVICE_MAP: Record<string, string> = {
  rtm: `${process.env.RTM_BASE_URL || "http://localhost:3210"}/auth/start`,
  sn: `${process.env.SN_BASE_URL || "http://localhost:3200"}`,
};

// Other configuration constants can be added here
export const CONFIG = {
  SN_BASE_URL: process.env.SN_BASE_URL,
  RTM_BASE_URL: process.env.RTM_BASE_URL || "http://localhost:3210",
};
