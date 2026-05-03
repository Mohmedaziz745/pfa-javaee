/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { ProductCard } from "@/components/product-card";
import { getFeaturedProducts } from "@/lib/api";

export default async function HomePage() {
  const featuredProducts = await getFeaturedProducts();

  return (
    <>
      <section className="hero">
        <div className="hero-card">
          <div className="hero-copy">
            <span className="badge">Spring Boot + Next.js storefront</span>
            <h1>Design-forward shopping for a backend-first project.</h1>
            <p>
              ShopFlow met en scène le catalogue, les promotions, le panier connecté et l’espace
              client avec une interface plus ambitieuse que le template par défaut.
            </p>
            <div className="hero-actions">
              <Link className="button" href="/catalog">
                Browse catalog
              </Link>
              <Link className="ghost-button" href="/login">
                Test customer login
              </Link>
            </div>
            <div className="hero-stats">
              <div className="stat-tile">
                <span className="small">Live API</span>
                <strong>6969</strong>
              </div>
              <div className="stat-tile">
                <span className="small">Top picks</span>
                <strong>{featuredProducts.length}</strong>
              </div>
              <div className="stat-tile">
                <span className="small">Demo flow</span>
                <strong>Login, cart, orders</strong>
              </div>
            </div>
          </div>
        </div>

        <div className="hero-card hero-visual">
          <img
            className="hero-image-main"
            src="https://images.unsplash.com/photo-1523381210434-271e8be1f52b?auto=format&fit=crop&w=1200&q=80"
            alt="Fashion and lifestyle storefront"
          />
          <img
            className="hero-image-accent"
            src="https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80"
            alt="Sneakers detail"
          />
          <div className="floating-note">
            <p className="eyebrow" style={{ color: "#ffd7c8" }}>
              Promo focus
            </p>
            <h3>Curated cards, warm palette, and real API wiring.</h3>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="promo-strip">
          <div>
            <p className="eyebrow">Coupon seed</p>
            <h2>Use `WELCOME10` in the cart for the demo discount flow.</h2>
          </div>
          <Link className="button" href="/cart">
            Open cart
          </Link>
        </div>
      </section>

      <section className="section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Top Selling</p>
            <h2>Products already coming from your backend.</h2>
          </div>
          <Link className="ghost-button" href="/catalog">
            View all
          </Link>
        </div>

        <div className="grid">
          {featuredProducts.map((product) => (
            <div key={product.id} className="col-4">
              <ProductCard product={product} />
            </div>
          ))}
        </div>
      </section>
    </>
  );
}
