import { NextRequest, NextResponse } from "next/server";

const BACKEND_API_BASE_URL =
  process.env.BACKEND_API_BASE_URL ||
  process.env.NEXT_PUBLIC_API_BASE_URL ||
  "http://localhost:8080";

export async function POST(request: NextRequest) {
  const body = await request.text();
  const guestId = request.headers.get("x-guest-id");

  try {
    const backendResponse = await fetch(`${BACKEND_API_BASE_URL}/rates/calculate`, {
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
    console.error("Rates backend request failed", error);

    return NextResponse.json(
      { message: "Rates backend is unavailable" },
      { status: 502 },
    );
  }
}
