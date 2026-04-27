"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState, useSyncExternalStore } from "react";
import { fetchOrders, logout, readAuth, subscribeAuth } from "@/lib/client-auth";
import { formatCurrency, formatDate, statusClass } from "@/lib/format";
import { Order } from "@/lib/types";

export function AccountClient() {
  const router = useRouter();
  const auth = useSyncExternalStore(subscribeAuth, readAuth, () => null);
  const [orders, setOrders] = useState<Order[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [pendingLogout, setPendingLogout] = useState(false);

  useEffect(() => {
    if (!auth || auth.role !== "CUSTOMER") {
      return;
    }
    fetchOrders().then(setOrders).catch(() => setError("Unable to load orders for this session."));
  }, [auth]);

  async function handleLogout() {
    setPendingLogout(true);
    try {
      await logout();
      router.push("/login");
      router.refresh();
    } finally {
      setPendingLogout(false);
    }
  }

  if (!auth) {
    return <div className="empty-state">Sign in to access your ShopFlow account.</div>;
  }

  if (error && auth.role === "CUSTOMER") {
    return <div className="empty-state">{error}</div>;
  }

  return (
    <div className="account-layout">
      <section className="order-list">
        {auth.role !== "CUSTOMER" ? (
          <div className="empty-state">
            {auth.role === "SELLER"
              ? "Seller session active. Use this account to manage products and store data through the API."
              : "Admin session active. This account is ready for dashboard and management endpoints."}
          </div>
        ) : orders.length === 0 ? (
          <div className="empty-state">No orders yet for this customer account.</div>
        ) : (
          orders.map((order) => (
            <article className="order-card" key={order.id}>
              <div className="inline-row">
                <div>
                  <h3>{order.numeroCommande}</h3>
                  <p className="muted">{formatDate(order.dateCommande)}</p>
                </div>
                <span className={`status-pill ${statusClass(order.statut)}`}>{order.statut}</span>
              </div>
              <div className="field-group" style={{ marginTop: 14 }}>
                {order.lignes.map((line) => (
                  <div className="inline-row" key={line.id}>
                    <span>
                      {line.productName} x{line.quantite}
                    </span>
                    <strong>{formatCurrency(line.prixUnitaire * line.quantite)}</strong>
                  </div>
                ))}
              </div>
              <div className="inline-row" style={{ marginTop: 16 }}>
                <span>{order.adresseLivraison.ville}</span>
                <strong>{formatCurrency(order.totalTTC)}</strong>
              </div>
            </article>
          ))
        )}
      </section>

      <aside className="account-panel">
        <p className="eyebrow">Session</p>
        <h2>{auth.email}</h2>
        <div className="field-group" style={{ marginTop: 18 }}>
          <div className="inline-row">
            <span>Role</span>
            <strong>{auth.role}</strong>
          </div>
          <div className="inline-row">
            <span>User ID</span>
            <strong>{auth.userId}</strong>
          </div>
          <p className="muted">
            Use the seeded customer, seller, or admin account from the login page, or create a new customer or seller account.
          </p>
        </div>
        <button className="ghost-button" style={{ marginTop: 18 }} onClick={handleLogout} disabled={pendingLogout}>
          {pendingLogout ? "Logging out..." : "Logout"}
        </button>
      </aside>
    </div>
  );
}
