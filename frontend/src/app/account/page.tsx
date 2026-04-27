import { AccountClient } from "@/components/account-client";

export default function AccountPage() {
  return (
    <div className="account-shell">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Account</p>
          <h2>Orders, session info and customer flow.</h2>
        </div>
      </div>
      <AccountClient />
    </div>
  );
}
