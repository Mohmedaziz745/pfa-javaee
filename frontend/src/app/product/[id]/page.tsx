/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { ProductPurchasePanel } from "@/components/product-purchase-panel";
import { getFeaturedProducts, getProduct } from "@/lib/api";
import { formatCurrency, formatDate } from "@/lib/format";

type ProductDetailPageProps = {
  params: Promise<{ id: string }>;
};

export default async function ProductDetailPage({ params }: ProductDetailPageProps) {
  const { id } = await params;
  const product = await getProduct(id);
  const recommendations = (await getFeaturedProducts()).filter((item) => item.id !== product.id).slice(0, 3);

  return (
    <div className="detail-shell">
      <section className="panel">
        <div className="gallery">
          <img
            className="gallery-main"
            src={product.images[0] ?? `https://picsum.photos/seed/${product.id}/900/900`}
            alt={product.nom}
          />
          <div className="gallery-strip">
            {(product.images.length > 0 ? product.images : [product.images[0], product.images[0], product.images[0]])
              .slice(0, 3)
              .map((image, index) => (
                <img
                  key={`${product.id}-${index}`}
                  src={image ?? `https://picsum.photos/seed/${product.id + index}/320/240`}
                  alt={`${product.nom} ${index + 1}`}
                />
              ))}
          </div>
        </div>

        <div style={{ marginTop: 24 }}>
          <p className="eyebrow">Customer Reviews</p>
          <div className="review-list">
            {product.reviews.length === 0 ? (
              <div className="empty-state">No approved reviews yet.</div>
            ) : (
              product.reviews.map((review) => (
                <article className="review-card" key={review.id}>
                  <div className="inline-row">
                    <strong>{review.customerName}</strong>
                    <span className="small">{review.note}/5</span>
                  </div>
                  <p style={{ marginTop: 10 }}>{review.commentaire}</p>
                  <p className="small" style={{ marginTop: 10 }}>
                    {formatDate(review.dateCreation)}
                  </p>
                </article>
              ))
            )}
          </div>
        </div>
      </section>

      <aside className="detail-sidebar">
        <span className="badge">{product.categories.join(" • ")}</span>
        <h1 style={{ marginTop: 14, fontSize: "clamp(2rem, 4vw, 3.6rem)" }}>{product.nom}</h1>
        <p className="muted" style={{ marginTop: 12 }}>
          {product.description}
        </p>
        <div className="price-row" style={{ marginTop: 18 }}>
          <span className="price">{formatCurrency(product.prixPromo ?? product.prix)}</span>
          {product.prixPromo ? (
            <span className="price-striked">{formatCurrency(product.prix)}</span>
          ) : null}
        </div>
        <div className="field-group" style={{ marginTop: 20 }}>
          <div className="inline-row">
            <span>Seller</span>
            <strong>{product.seller.nomBoutique}</strong>
          </div>
          <div className="inline-row">
            <span>Rating</span>
            <strong>{product.averageRating.toFixed(1)} / 5</strong>
          </div>
          <div className="inline-row">
            <span>Stock</span>
            <strong>{product.stock}</strong>
          </div>
        </div>

        <div className="chip-row" style={{ marginTop: 18 }}>
          {product.variants.map((variant) => (
            <span className="chip" key={variant.id}>
              {variant.attribut}: {variant.valeur}
            </span>
          ))}
        </div>

        <div style={{ marginTop: 22 }}>
          <ProductPurchasePanel product={product} />
        </div>

        <div style={{ marginTop: 22 }}>
          <div className="section-heading">
            <div>
              <p className="eyebrow">You may also like</p>
            </div>
            <Link href="/catalog" className="ghost-button">
              More
            </Link>
          </div>
          <div className="field-group">
            {recommendations.map((item) => (
              <Link key={item.id} href={`/product/${item.id}`} className="card">
                <div className="inline-row">
                  <strong>{item.nom}</strong>
                  <span>{formatCurrency(item.prixPromo ?? item.prix)}</span>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </aside>
    </div>
  );
}
