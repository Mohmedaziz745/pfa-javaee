"use client";

import { Address, AuthPayload, Cart, Order, RegisterPayload } from "@/lib/types";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8081";
const AUTH_STORAGE_KEY = "shopflow.auth";
const AUTH_EVENT = "shopflow-auth-change";

function inBrowser() {
  return typeof window !== "undefined";
}

let cachedAuthRaw: string | null = null;
let cachedAuthSnapshot: AuthPayload | null = null;

export function readAuth(): AuthPayload | null {
  if (!inBrowser()) {
    return null;
  }
  const raw = window.localStorage.getItem(AUTH_STORAGE_KEY);
  if (raw === cachedAuthRaw) {
    return cachedAuthSnapshot;
  }
  cachedAuthRaw = raw;
  cachedAuthSnapshot = raw ? (JSON.parse(raw) as AuthPayload) : null;
  return cachedAuthSnapshot;
}

export function writeAuth(payload: AuthPayload) {
  if (!inBrowser()) {
    return;
  }
  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(payload));
  window.dispatchEvent(new Event(AUTH_EVENT));
}

export function clearAuth() {
  if (!inBrowser()) {
    return;
  }
  window.localStorage.removeItem(AUTH_STORAGE_KEY);
  window.dispatchEvent(new Event(AUTH_EVENT));
}

async function parseError(response: Response, fallback: string) {
  try {
    const body = (await response.json()) as {
      error?: string;
      details?: Record<string, string>;
    };
    if (body.details) {
      return Object.values(body.details)[0] ?? fallback;
    }
    return body.error ?? fallback;
  } catch {
    return fallback;
  }
}

export function subscribeAuth(listener: () => void) {
  if (!inBrowser()) {
    return () => undefined;
  }
  window.addEventListener(AUTH_EVENT, listener);
  return () => window.removeEventListener(AUTH_EVENT, listener);
}

async function refreshAuthToken(auth: AuthPayload): Promise<AuthPayload | null> {
  const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      refreshToken: auth.refreshToken,
    }),
  });

  if (!response.ok) {
    clearAuth();
    return null;
  }

  const refreshed = (await response.json()) as AuthPayload;
  writeAuth(refreshed);
  return refreshed;
}

async function authorizedFetch(path: string, init: RequestInit = {}) {
  let auth = readAuth();
  if (!auth) {
    throw new Error("AUTH_REQUIRED");
  }

  const headers = new Headers(init.headers);
  headers.set("Authorization", `Bearer ${auth.accessToken}`);
  if (!headers.has("Content-Type") && init.body) {
    headers.set("Content-Type", "application/json");
  }

  let response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  });

  if (response.status === 401) {
    const refreshed = await refreshAuthToken(auth);
    if (!refreshed) {
      throw new Error("AUTH_REQUIRED");
    }
    auth = refreshed;
    const retryHeaders = new Headers(init.headers);
    retryHeaders.set("Authorization", `Bearer ${auth.accessToken}`);
    if (!retryHeaders.has("Content-Type") && init.body) {
      retryHeaders.set("Content-Type", "application/json");
    }
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...init,
      headers: retryHeaders,
    });
  }

  if (!response.ok) {
    const fallback = await response.text();
    throw new Error(fallback || "REQUEST_FAILED");
  }

  return response;
}

export async function login(email: string, motDePasse: string): Promise<AuthPayload> {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email, motDePasse }),
  });

  if (!response.ok) {
    throw new Error(await parseError(response, "Email ou mot de passe invalide"));
  }

  const payload = (await response.json()) as AuthPayload;
  writeAuth(payload);
  return payload;
}

export async function register(payload: RegisterPayload): Promise<AuthPayload> {
  const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await parseError(response, "Registration failed"));
  }

  const authPayload = (await response.json()) as AuthPayload;
  writeAuth(authPayload);
  return authPayload;
}

export async function logout() {
  const auth = readAuth();
  if (!auth) {
    clearAuth();
    return;
  }

  try {
    await fetch(`${API_BASE_URL}/api/auth/logout`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        refreshToken: auth.refreshToken,
      }),
    });
  } finally {
    clearAuth();
  }
}

export async function fetchCart(): Promise<Cart> {
  const response = await authorizedFetch("/api/cart");
  return response.json() as Promise<Cart>;
}

export async function addToCart(productId: number, variantId?: number | null, quantite = 1) {
  const response = await authorizedFetch("/api/cart/items", {
    method: "POST",
    body: JSON.stringify({
      productId,
      variantId: variantId ?? null,
      quantite,
    }),
  });
  return response.json() as Promise<Cart>;
}

export async function updateCartItem(itemId: number, quantite: number) {
  const response = await authorizedFetch(`/api/cart/items/${itemId}`, {
    method: "PUT",
    body: JSON.stringify({ quantite }),
  });
  return response.json() as Promise<Cart>;
}

export async function removeCartItem(itemId: number) {
  const response = await authorizedFetch(`/api/cart/items/${itemId}`, {
    method: "DELETE",
  });
  return response.json() as Promise<Cart>;
}

export async function applyCoupon(code: string) {
  const response = await authorizedFetch("/api/cart/coupon", {
    method: "POST",
    body: JSON.stringify({ code }),
  });
  return response.json() as Promise<Cart>;
}

export async function fetchOrders(): Promise<Order[]> {
  const response = await authorizedFetch("/api/orders/my");
  return response.json() as Promise<Order[]>;
}

export async function fetchAddresses(): Promise<Address[]> {
  const response = await authorizedFetch("/api/addresses/my");
  return response.json() as Promise<Address[]>;
}

export async function checkout(addressId: number): Promise<Order> {
  const response = await authorizedFetch("/api/orders", {
    method: "POST",
    body: JSON.stringify({ addressId }),
  });
  return response.json() as Promise<Order>;
}
