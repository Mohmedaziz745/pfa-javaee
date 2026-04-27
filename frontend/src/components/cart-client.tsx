"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { applyCoupon, checkout, fetchAddresses, fetchCart, removeCartItem, updateCartItem } from "@/lib/client-auth";
import { formatCurrency, formatDate } from "@/lib/format";
import { Address, Cart } from "@/lib/types";

export function CartClient() {
  const router = useRouter();
  const [cart, setCart] = useState<Cart | null>(null);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [coupon, setCoupon] = useState("WELCOME10");
  const [error, setError] = useState<string | null>(null);
  const [checkoutMessage, setCheckoutMessage] = useState<string | null>(null);
  const [pendingCheckout, setPendingCheckout] = useState(false);

  useEffect(() => {
    Promise.all([fetchCart(), fetchAddresses()])
      .then(([nextCart, nextAddresses]) => {
        setCart(nextCart);
        setAddresses(nextAddresses);
        setSelectedAddressId(nextAddresses.find((address) => address.principal)?.id ?? nextAddresses[0]?.id ?? null);
      })
      .catch(() => setError("Sign in with a customer account to use the cart and checkout."));
  }, []);

  async function handleCoupon() {
    try {
      const nextCart = await applyCoupon(coupon);
      setCart(nextCart);
      setError(null);
    } catch {
      setError("Invalid coupon or expired session.");
    }
  }

  async function handleCheckout() {
    if (!selectedAddressId) {
      setError("Select a delivery address before checkout.");
      return;
    }

    setPendingCheckout(true);
    setError(null);
    setCheckoutMessage(null);
    try {
      const order = await checkout(selectedAddressId);
      setCart(await fetchCart());
      setCheckoutMessage(`Order ${order.numeroCommande} created successfully.`);
      router.push("/account");
      router.refresh();
    } catch (currentError) {
      setError(currentError instanceof Error ? currentError.message : "Checkout failed.");
    } finally {
      setPendingCheckout(false);
    }
  }

  if (error && !cart) {
    return <div className="empty-state">{error}</div>;
  }

  if (!cart) {
    return <div className="empty-state">Loading cart...</div>;
  }

  return (
    <div className="cart-layout">
      <div className="cart-items">
        {cart.lignes.length === 0 ? (
          <div className="empty-state">Your cart is empty right now.</div>
        ) : (
          cart.lignes.map((item) => (
            <article className="cart-item" key={item.itemId}>
              <div className="inline-row">
                <div>
                  <h3>{item.productName}</h3>
                  <p className="muted">{item.variantLabel ?? "Standard product"}</p>
                </div>
                <strong>{formatCurrency(item.totalLigne)}</strong>
              </div>
              <div className="inline-row" style={{ marginTop: 14 }}>
                <div className="chip-row">
                  <button
                    className="ghost-button"
                    onClick={async () => setCart(await updateCartItem(item.itemId, Math.max(1, item.quantite - 1)))}
                  >
                    -
                  </button>
                  <span className="chip">Qty {item.quantite}</span>
                  <button
                    className="ghost-button"
                    onClick={async () => setCart(await updateCartItem(item.itemId, item.quantite + 1))}
                  >
                    +
                  </button>
                </div>
                <button className="ghost-button" onClick={async () => setCart(await removeCartItem(item.itemId))}>
                  Remove
                </button>
              </div>
            </article>
          ))
        )}
      </div>

      <aside className="cart-summary">
        <p className="eyebrow">Order Summary</p>
        <h2>Ready to checkout</h2>
        <div className="field-group" style={{ marginTop: 18 }}>
          <input className="field" value={coupon} onChange={(event) => setCoupon(event.target.value)} />
          <button className="button" onClick={handleCoupon}>
            Apply coupon
          </button>
        </div>
        <div className="field-group" style={{ marginTop: 18 }}>
          <label htmlFor="address">Delivery address</label>
          <select
            id="address"
            className="select"
            value={selectedAddressId ?? ""}
            onChange={(event) => setSelectedAddressId(event.target.value ? Number(event.target.value) : null)}
          >
            {addresses.map((address) => (
              <option key={address.id} value={address.id}>
                {address.rue}, {address.ville} {address.codePostal}, {address.pays}
              </option>
            ))}
          </select>
          {addresses.length === 0 ? <p className="small">No saved address found for this customer account.</p> : null}
        </div>
        <div className="field-group" style={{ marginTop: 18 }}>
          <div className="inline-row">
            <span>Subtotal</span>
            <strong>{formatCurrency(cart.sousTotal)}</strong>
          </div>
          <div className="inline-row">
            <span>Discount</span>
            <strong>- {formatCurrency(cart.remise)}</strong>
          </div>
          <div className="inline-row">
            <span>Shipping</span>
            <strong>{formatCurrency(cart.fraisLivraison)}</strong>
          </div>
          <div className="inline-row">
            <span>Total</span>
            <strong>{formatCurrency(cart.totalTtc)}</strong>
          </div>
        </div>
        <button
          className="button"
          style={{ marginTop: 18, width: "100%" }}
          onClick={handleCheckout}
          disabled={pendingCheckout || cart.lignes.length === 0 || addresses.length === 0}
        >
          {pendingCheckout ? "Placing order..." : "Buy products"}
        </button>
        <p className="small" style={{ marginTop: 16 }}>
          Last update: {formatDate(cart.dateModification)}
        </p>
        {cart.couponCode ? <span className="badge" style={{ marginTop: 16 }}>{cart.couponCode}</span> : null}
        {checkoutMessage ? <p className="small" style={{ marginTop: 16 }}>{checkoutMessage}</p> : null}
        {error ? <p className="small" style={{ marginTop: 16 }}>{error}</p> : null}
      </aside>
    </div>
  );
}
