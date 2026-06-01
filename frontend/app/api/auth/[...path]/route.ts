import { NextRequest, NextResponse } from "next/server";

const BACKEND_API_BASE_URL =
  process.env.BACKEND_API_BASE_URL ||
  process.env.NEXT_PUBLIC_API_BASE_URL ||
  "http://localhost:8080";

type RouteContext = {
  params: Promise<{
    path: string[];
  }>;
};

async function proxyAuthRequest(request: NextRequest, context: RouteContext) {
  const { path } = await context.params;
  const body = await request.text();

  try {
    const backendResponse = await fetch(
      `${BACKEND_API_BASE_URL}/auth/${path.join("/")}`,
      {
        method: request.method,
        headers: {
          "Content-Type": request.headers.get("content-type") || "application/json",
          ...(request.headers.get("authorization")
            ? { Authorization: request.headers.get("authorization") as string }
            : {}),
        },
        body: body || undefined,
        cache: "no-store",
      },
    );

    const responseBody = await backendResponse.text();
    const contentType = backendResponse.headers.get("content-type") || "application/json";

    return new NextResponse(responseBody, {
      status: backendResponse.status,
      headers: {
        "Content-Type": contentType,
      },
    });
  } catch (error) {
    console.error("Auth backend request failed", error);

    return NextResponse.json(
      { message: "Auth backend is unavailable" },
      { status: 502 },
    );
  }
}

export async function POST(request: NextRequest, context: RouteContext) {
  return proxyAuthRequest(request, context);
}
