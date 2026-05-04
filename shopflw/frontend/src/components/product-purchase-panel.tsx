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
  const [error, setError] = useState<string | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [selectedVariantId, setSelectedVariantId] = useState<number | null>(product.variants[0]?.id ?? null);
  const promo = product.prixPromo !== null;
  const savings = product.prixPromo !== null ? product.prix - product.prixPromo : 0;
  const discountPercent = promo ? Math.round((savings / product.prix) * 100) : 0;
  const selectedVariant = useMemo(
    () => product.variants.find((variant) => variant.id === selectedVariantId) ?? null,
    [product.variants, selectedVariantId],
  );
  const availableStock = product.stock + (selectedVariant?.stockSupplementaire ?? 0);
  const outOfStock = availableStock < 1;
  const quantityOptions = Array.from({ length: Math.min(5, Math.max(availableStock, 1)) }, (_, index) => index + 1);
  const selectedQuantity = Math.min(quantity, Math.max(availableStock, 1));
  const unitPrice = (product.prixPromo ?? product.prix) + (selectedVariant?.prixDelta ?? 0);
  const totalPrice = unitPrice * selectedQuantity;

  async function handleAddToCart() {
    const auth = readAuth();
    if (!auth || auth.role !== "CUSTOMER") {
      router.push("/login");
      return;
    }

    setPending(true);
    setError(null);
    try {
      await addToCart(product.id, selectedVariantId, selectedQuantity);
      startTransition(() => {
        router.push("/cart");
        router.refresh();
      });
    } catch (currentError) {
      if (currentError instanceof Error && currentError.message.includes("Authentication failed")) {
        router.push("/login");
        return;
      }
      setError(currentError instanceof Error ? currentError.message : "Unable to add this product.");
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
        {outOfStock ? (
          <span className="stock-pill danger">Out of stock</span>
        ) : promo ? (
          <span className="sale-pill">-{discountPercent}%</span>
        ) : (
          <span className="badge">New drop</span>
        )}
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
        <div className="inline-row">
          <span>Available stock</span>
          <strong>{availableStock}</strong>
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
          value={selectedQuantity}
          onChange={(event) => setQuantity(Number(event.target.value))}
        >
          {quantityOptions.map((value) => (
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

      <button className="button purchase-button" onClick={handleAddToCart} disabled={pending || outOfStock}>
        {pending ? "Adding to cart..." : outOfStock ? "Out of stock" : "Add to cart"}
      </button>
      {error ? <p className="card-error" style={{ marginTop: 14 }}>{error}</p> : null}
      <p className="muted" style={{ marginTop: 14 }}>
        {!outOfStock
          ? `Available now with ${availableStock} unit${availableStock > 1 ? "s" : ""} in stock.`
          : "This item is currently unavailable."}
      </p>
    </section>
  );
}
