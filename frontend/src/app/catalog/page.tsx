import { ProductCard } from "@/components/product-card";
import { getProducts, searchProducts } from "@/lib/api";

type CatalogPageProps = {
  searchParams: Promise<Record<string, string | string[] | undefined>>;
};

export default async function CatalogPage({ searchParams }: CatalogPageProps) {
  const params = await searchParams;
  const q = typeof params.q === "string" ? params.q : "";
  const promo = params.promo === "true";
  const pageData = q
    ? await searchProducts(q)
    : await getProducts(promo ? "promo=true" : "");

  return (
    <div className="catalog-shell">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Catalog</p>
          <h2>Search, filter and shop the live product feed.</h2>
        </div>
      </div>

      <div className="catalog-grid">
        <aside className="filter-panel">
          <p className="eyebrow">Quick filters</p>
          <div className="filter-group" style={{ marginTop: 18 }}>
            <a className="ghost-button" href="/catalog">
              All products
            </a>
            <a className="ghost-button" href="/catalog?promo=true">
              On promotion
            </a>
            <a className="ghost-button" href="/catalog?q=headphones">
              Search headphones
            </a>
            <a className="ghost-button" href="/catalog?q=shirt">
              Search shirt
            </a>
          </div>
        </aside>

        <section>
          {pageData.content.length === 0 ? (
            <div className="empty-state">Aucun produit ne correspond à ce filtre.</div>
          ) : (
            <div className="grid">
              {pageData.content.map((product) => (
                <div key={product.id} className="col-6">
                  <ProductCard product={product} />
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
