"use client";

import { useRouter } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import { CreditCardForm } from "@/components/credit-card-form";
import { applyCoupon, checkout, fetchAddresses, fetchCart, removeCartItem, updateCartItem } from "@/lib/client-auth";
import { formatCurrency, formatDate } from "@/lib/format";
import { Address, Cart, Order } from "@/lib/types";

export function CartClient() {
  const router = useRouter();
  const [cart, setCart] = useState<Cart | null>(null);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [coupon, setCoupon] = useState("WELCOME10");
  const [error, setError] = useState<string | null>(null);
  const [checkoutMessage, setCheckoutMessage] = useState<string | null>(null);
  const [pendingCheckout, setPendingCheckout] = useState(false);
  const [cardValid, setCardValid] = useState(false);
  const [showPayment, setShowPayment] = useState(false);
  const [lastOrder, setLastOrder] = useState<Order | null>(null);
  const handleCardValidChange = useCallback((valid: boolean) => setCardValid(valid), []);

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
    if (!cardValid) {
      setError("Enter valid payment details before checkout.");
      return;
    }

    setPendingCheckout(true);
    setError(null);
    setCheckoutMessage(null);
    try {
      const order = await checkout(selectedAddressId);
      setCart(await fetchCart());
      setLastOrder(order);
      setShowPayment(false);
      setCardValid(false);
      setCheckoutMessage(`Order ${order.numeroCommande} created successfully.`);
      router.refresh();
    } catch (currentError) {
      if (currentError instanceof Error && currentError.message.includes("Authentication failed")) {
        router.push("/login");
        return;
      }
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
          cart.lignes.map((item) => {
            const stockAvailable = item.stockAvailable ?? 0;

            return (
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
                      disabled={item.quantite >= stockAvailable}
                      onClick={async () => setCart(await updateCartItem(item.itemId, item.quantite + 1))}
                    >
                      +
                    </button>
                  </div>
                  <button className="ghost-button" onClick={async () => setCart(await removeCartItem(item.itemId))}>
                    Remove
                  </button>
                </div>
                <div className="inline-row" style={{ marginTop: 14 }}>
                  <span className={stockAvailable < 1 ? "stock-pill danger" : "stock-pill"}>
                    {stockAvailable < 1 ? "Out of stock" : `${stockAvailable} in stock`}
                  </span>
                  {item.quantite >= stockAvailable ? <span className="small">Maximum quantity selected</span> : null}
                </div>
              </article>
            );
          })
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
        <div className="purchased-panel">
          <div className="inline-row">
            <p className="eyebrow">Your Products</p>
            <span className="chip">{cart.lignes.length}</span>
          </div>
          {cart.lignes.length === 0 ? (
            <p className="small">No products selected yet.</p>
          ) : (
            <div className="purchase-lines">
              {cart.lignes.map((item) => {
                const stockAvailable = item.stockAvailable ?? 0;

                return (
                  <div className="purchase-line" key={item.itemId}>
                    <div>
                      <strong>{item.productName}</strong>
                      <p className="small">
                        {item.variantLabel ?? "Standard product"} - Qty {item.quantite} - Stock {stockAvailable}
                      </p>
                    </div>
                    <span>{formatCurrency(item.totalLigne)}</span>
                  </div>
                );
              })}
            </div>
          )}
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
        {lastOrder ? (
          <div className="purchased-panel">
            <p className="eyebrow">Purchased</p>
            <h3>You bought these products</h3>
            <div className="purchase-lines">
              {lastOrder.lignes.map((item) => (
                <div className="purchase-line" key={item.id}>
                  <div>
                    <strong>{item.productName}</strong>
                    <p className="small">{item.variantLabel ?? "Standard product"} - Qty {item.quantite}</p>
                  </div>
                  <span>{formatCurrency(item.prixUnitaire * item.quantite)}</span>
                </div>
              ))}
            </div>
          </div>
        ) : null}
        {showPayment ? <CreditCardForm onValidChange={handleCardValidChange} /> : null}
        {showPayment ? (
          <button
            className="button"
            style={{ marginTop: 18, width: "100%" }}
            onClick={handleCheckout}
            disabled={pendingCheckout || cart.lignes.length === 0 || addresses.length === 0 || !cardValid}
          >
            {pendingCheckout ? "Placing order..." : "Buy products"}
          </button>
        ) : (
          <button
            className="button"
            style={{ marginTop: 18, width: "100%" }}
            onClick={() => {
              setLastOrder(null);
              setShowPayment(true);
            }}
            disabled={cart.lignes.length === 0 || addresses.length === 0}
          >
            Continue to payment
          </button>
        )}
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
