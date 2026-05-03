/* eslint-disable @next/next/no-img-element */
"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { startTransition, useState } from "react";
import { addToCart, readAuth } from "@/lib/client-auth";
import { formatCurrency } from "@/lib/format";
import { Product } from "@/lib/types";

type ProductCardProps = {
  product: Product;
};

export function ProductCard({ product }: ProductCardProps) {
  const router = useRouter();
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const promo = product.prixPromo !== null;
  const outOfStock = product.stock < 1;
  const savings = promo && product.prixPromo !== null ? product.prix - product.prixPromo : 0;

  async function handleAddToCart() {
    const auth = readAuth();
    if (!auth || auth.role !== "CUSTOMER") {
      router.push("/login");
      return;
    }

    setPending(true);
    setError(null);
    try {
      await addToCart(product.id, product.variants[0]?.id ?? null);
      startTransition(() => {
        router.push("/cart");
      });
    } catch (currentError) {
      setError(currentError instanceof Error ? currentError.message : "Unable to add this product.");
    } finally {
      setPending(false);
    }
  }

  return (
    <article className="card">
      <Link href={`/product/${product.id}`}>
        {outOfStock ? <span className="stock-ribbon">Out of stock</span> : null}
        <img
          src={product.images[0] ?? `https://picsum.photos/seed/${product.id}/640/720`}
          alt={product.nom}
        />
      </Link>

      <div className="card-top">
        <span className="badge">{product.categories[0] ?? "Featured"}</span>
        <span className={outOfStock ? "stock-pill danger" : "stock-pill"}>
          {outOfStock ? "Stock 0" : `${product.stock} in stock`}
        </span>
      </div>

      <div className="card-body">
        <h3>{product.nom}</h3>
        <p>{product.description}</p>
      </div>

      <div className="card-bottom">
        <div>
          <div className="price-row">
            <span className="price">
              {formatCurrency(product.prixPromo ?? product.prix)}
            </span>
            {promo ? (
              <span className="price-striked">{formatCurrency(product.prix)}</span>
            ) : null}
          </div>
          <span className="small">{promo ? `${formatCurrency(savings)} off` : `${product.salesCount} sales`}</span>
        </div>

        <button className="button" onClick={handleAddToCart} disabled={pending || outOfStock}>
          {pending ? "Adding..." : outOfStock ? "Unavailable" : "Add"}
        </button>
      </div>
      {error ? <p className="card-error">{error}</p> : null}
    </article>
  );
}
