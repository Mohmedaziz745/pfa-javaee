import { LoginForm } from "@/components/login-form";

export default function LoginPage() {
  return (
    <div className="auth-shell">
      <div className="grid">
        <section className="col-7 panel">
          <p className="eyebrow">Access</p>
          <h1 style={{ fontSize: "clamp(2rem, 4vw, 4rem)", marginTop: 10 }}>
            Sign in, register, and switch between ShopFlow accounts.
          </h1>
          <p className="muted" style={{ marginTop: 14, maxWidth: "55ch" }}>
            This page uses the real authentication API. You can create a new customer or seller
            account, keep the JWT session locally, refresh protected requests, and log out cleanly.
          </p>
          <div className="chip-row" style={{ marginTop: 22 }}>
            <span className="chip">customer@shopflow.local / Customer123</span>
            <span className="chip">seller@shopflow.local / Seller123</span>
            <span className="chip">admin@shopflow.local / Admin123</span>
          </div>
        </section>

        <aside className="col-5 auth-panel">
          <p className="eyebrow">Auth</p>
          <h2 style={{ marginTop: 8 }}>JWT Session</h2>
          <div style={{ marginTop: 18 }}>
            <LoginForm />
          </div>
        </aside>
      </div>
    </div>
  );
}
