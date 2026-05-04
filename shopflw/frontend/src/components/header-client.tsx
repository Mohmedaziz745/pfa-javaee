"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useState, useSyncExternalStore } from "react";
import { logout, readAuth, subscribeAuth } from "@/lib/client-auth";

const navItems = [
  { href: "/", label: "Home", exact: true },
  { href: "/catalog", label: "Catalog" },
  { href: "/cart", label: "Cart" },
  { href: "/account", label: "Account" },
];

export function HeaderClient() {
  const router = useRouter();
  const pathname = usePathname();
  const auth = useSyncExternalStore(subscribeAuth, readAuth, () => null);
  const [pendingLogout, setPendingLogout] = useState(false);

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

  return (
    <header className="site-header">
      <div className="site-header-inner">
        <Link href="/" className="brand">
          <div className="brand-mark">SF</div>
          <div className="brand-text">
            <strong>ShopFlow</strong>
            <span>Curated marketplace frontend</span>
          </div>
        </Link>

        <nav className="nav-links">
          {navItems.map((item) => {
            const isActive = item.exact ? pathname === item.href : pathname.startsWith(item.href);

            return (
              <Link
                key={item.href}
                href={item.href}
                className={`nav-link${isActive ? " active" : ""}`}
                aria-current={isActive ? "page" : undefined}
              >
                {item.label}
              </Link>
            );
          })}
        </nav>

        <div className="header-actions">
          {auth ? (
            <>
              <span className="badge">{auth.role}</span>
              <span className="small">{auth.email}</span>
              <button className="ghost-button" onClick={handleLogout} disabled={pendingLogout}>
                {pendingLogout ? "Logging out..." : "Logout"}
              </button>
            </>
          ) : (
            <Link href="/login" className="button">
              Sign in / Sign up
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
