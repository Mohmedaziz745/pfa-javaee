import { Product, ProductPage } from "@/lib/types";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:6969";

async function request<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`API request failed for ${path}`);
  }

  return response.json() as Promise<T>;
}

export async function getFeaturedProducts(): Promise<Product[]> {
  return request<Product[]>("/api/products/top-selling");
}

export async function getProducts(query = ""): Promise<ProductPage> {
  const path = query ? `/api/products?${query}` : "/api/products";
  return request<ProductPage>(path);
}

export async function searchProducts(q: string): Promise<ProductPage> {
  return request<ProductPage>(`/api/products/search?q=${encodeURIComponent(q)}`);
}

export async function getProduct(id: string): Promise<Product> {
  return request<Product>(`/api/products/${id}`);
}
