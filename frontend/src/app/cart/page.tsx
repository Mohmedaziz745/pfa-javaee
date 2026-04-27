import { CartClient } from "@/components/cart-client";

export default function CartPage() {
  return (
    <div className="cart-shell">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Cart</p>
          <h2>Persistent cart backed by the ShopFlow API.</h2>
        </div>
      </div>
      <CartClient />
    </div>
  );
}
