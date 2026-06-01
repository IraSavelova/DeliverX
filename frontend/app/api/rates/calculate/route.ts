import { NextRequest, NextResponse } from "next/server";

const BACKEND_API_BASE_URL =
  process.env.BACKEND_API_BASE_URL ||
  process.env.NEXT_PUBLIC_API_BASE_URL;

const DEFAULT_BACKEND_API_BASE_URL = "http://localhost:8080";
const DOCKER_BACKEND_API_BASE_URL = "http://gateway-service:8080";
const DOCKER_HOST_BACKEND_API_BASE_URL = "http://host.docker.internal:8080";

function getBackendBaseUrls() {
  const configuredUrl = BACKEND_API_BASE_URL || DEFAULT_BACKEND_API_BASE_URL;
  const urls = [configuredUrl];

  if (configuredUrl === DEFAULT_BACKEND_API_BASE_URL) {
    urls.push(DOCKER_BACKEND_API_BASE_URL);
    urls.push(DOCKER_HOST_BACKEND_API_BASE_URL);
  }

  return urls;
}

export async function POST(request: NextRequest) {
  const body = await request.text();
  const guestId = request.headers.get("x-guest-id");
  const query = request.nextUrl.search;

  for (const backendBaseUrl of getBackendBaseUrls()) {
    try {
      const backendResponse = await fetch(`${backendBaseUrl}/rates/calculate${query}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(guestId ? { "X-Guest-Id": guestId } : {}),
        },
        body,
        cache: "no-store",
      });

      const responseBody = await backendResponse.text();
      const contentType = backendResponse.headers.get("content-type") || "application/json";

      return new NextResponse(responseBody, {
        status: backendResponse.status,
        headers: {
          "Content-Type": contentType,
        },
      });
    } catch (error) {
      console.error(`Rates backend request failed for ${backendBaseUrl}`, error);
    }
  }

  return NextResponse.json(
    { message: "Rates backend is unavailable" },
    { status: 502 },
  );
}
