"use client";

import { useRouter } from "next/navigation";
import { startTransition, useMemo, useState } from "react";
import { addToCart, readAuth } from "@/lib/client-auth";
import { formatCurrency } from "@/lib/format";
import { Product } from "@/lib/types";

type ProductPurchasePanelProps = {
  product: Product;
};

export function ProductPurchasePanel({ product }: ProductPurchasePanelProps) {
  const router = useRouter();
  const [pending, setPending] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [selectedVariantId, setSelectedVariantId] = useState<number | null>(product.variants[0]?.id ?? null);
  const promo = product.prixPromo !== null;
  const savings = product.prixPromo !== null ? product.prix - product.prixPromo : 0;
  const discountPercent = promo ? Math.round((savings / product.prix) * 100) : 0;
  const selectedVariant = useMemo(
    () => product.variants.find((variant) => variant.id === selectedVariantId) ?? null,
    [product.variants, selectedVariantId],
  );
  const unitPrice = (product.prixPromo ?? product.prix) + (selectedVariant?.prixDelta ?? 0);
  const totalPrice = unitPrice * quantity;

  async function handleAddToCart() {
    const auth = readAuth();
    if (!auth || auth.role !== "CUSTOMER") {
      router.push("/login");
      return;
    }

    setPending(true);
    try {
      await addToCart(product.id, selectedVariantId, quantity);
      startTransition(() => {
        router.push("/cart");
        router.refresh();
      });
    } finally {
      setPending(false);
    }
  }

  return (
    <section className="purchase-panel">
      <div className="purchase-heading">
        <div>
          <p className="eyebrow">Purchase Panel</p>
          <h2>Ready to order</h2>
        </div>
        {promo ? <span className="sale-pill">-{discountPercent}%</span> : <span className="badge">New drop</span>}
      </div>

      <div className="field-group" style={{ marginTop: 18 }}>
        <div className="inline-row">
          <span>Current price</span>
          <strong>{formatCurrency(unitPrice)}</strong>
        </div>
        {promo ? (
          <>
            <div className="inline-row">
              <span>Original price</span>
              <span className="price-striked">{formatCurrency(product.prix)}</span>
            </div>
            <div className="inline-row">
              <span>You save</span>
              <strong>{formatCurrency(savings)}</strong>
            </div>
          </>
        ) : null}
        <div className="inline-row">
          <span>Units sold</span>
          <strong>{product.salesCount}</strong>
        </div>
        <div className="inline-row">
          <span>Customer reviews</span>
          <strong>{product.reviewCount}</strong>
        </div>
      </div>

      {product.variants.length > 0 ? (
        <div className="field-group" style={{ marginTop: 18 }}>
          <label htmlFor="variant">Option</label>
          <select
            id="variant"
            className="select"
            value={selectedVariantId ?? ""}
            onChange={(event) => setSelectedVariantId(event.target.value ? Number(event.target.value) : null)}
          >
            {product.variants.map((variant) => (
              <option key={variant.id} value={variant.id}>
                {variant.attribut}: {variant.valeur}
                {variant.prixDelta !== 0 ? ` (${formatCurrency(variant.prixDelta)})` : ""}
              </option>
            ))}
          </select>
        </div>
      ) : null}

      <div className="field-group" style={{ marginTop: 18 }}>
        <label htmlFor="quantity">Quantity</label>
        <select
          id="quantity"
          className="select"
          value={quantity}
          onChange={(event) => setQuantity(Number(event.target.value))}
        >
          {[1, 2, 3, 4, 5].map((value) => (
            <option key={value} value={value}>
              {value}
            </option>
          ))}
        </select>
      </div>

      <div className="purchase-total">
        <span>Total</span>
        <strong>{formatCurrency(totalPrice)}</strong>
      </div>

      <button className="button purchase-button" onClick={handleAddToCart} disabled={pending || product.stock < 1}>
        {pending ? "Adding to cart..." : product.stock < 1 ? "Out of stock" : "Add to cart"}
      </button>
      <p className="muted" style={{ marginTop: 14 }}>
        {product.stock > 0
          ? `Available now with ${product.stock} unit${product.stock > 1 ? "s" : ""} in stock.`
          : "This item is currently unavailable."}
      </p>
    </section>
  );
}
