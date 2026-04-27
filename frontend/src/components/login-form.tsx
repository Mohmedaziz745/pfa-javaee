"use client";

import { useRouter } from "next/navigation";
import { FormEvent, useState } from "react";
import { login, register } from "@/lib/client-auth";
import { RegisterPayload } from "@/lib/types";

type AuthMode = "login" | "register";
type AccountRole = "CUSTOMER" | "SELLER";

const DEFAULTS = {
  login: {
    email: "customer@shopflow.local",
    motDePasse: "Customer123",
  },
  register: {
    email: "",
    motDePasse: "",
    prenom: "",
    nom: "",
    role: "CUSTOMER" as AccountRole,
    nomBoutique: "",
    descriptionBoutique: "",
    logoBoutique: "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?auto=format&fit=crop&w=400&q=80",
  },
};

export function LoginForm() {
  const router = useRouter();
  const [mode, setMode] = useState<AuthMode>("login");
  const [email, setEmail] = useState(DEFAULTS.login.email);
  const [password, setPassword] = useState(DEFAULTS.login.motDePasse);
  const [registerData, setRegisterData] = useState(DEFAULTS.register);
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPending(true);
    setError(null);
    try {
      if (mode === "login") {
        await login(email, password);
      } else {
        const payload: RegisterPayload = {
          email: registerData.email,
          motDePasse: registerData.motDePasse,
          prenom: registerData.prenom,
          nom: registerData.nom,
          role: registerData.role,
          nomBoutique: registerData.role === "SELLER" ? registerData.nomBoutique : undefined,
          descriptionBoutique: registerData.role === "SELLER" ? registerData.descriptionBoutique : undefined,
          logoBoutique: registerData.role === "SELLER" ? registerData.logoBoutique : undefined,
        };
        await register(payload);
      }
      router.push("/account");
      router.refresh();
    } catch (currentError) {
      setError(currentError instanceof Error ? currentError.message : "Authentication failed");
    } finally {
      setPending(false);
    }
  }

  function setDemoAccount(nextEmail: string, nextPassword: string) {
    setMode("login");
    setEmail(nextEmail);
    setPassword(nextPassword);
    setError(null);
  }

  return (
    <div className="field-group">
      <div className="auth-mode-switch">
        <button
          className={mode === "login" ? "button" : "ghost-button"}
          type="button"
          onClick={() => setMode("login")}
        >
          Login
        </button>
        <button
          className={mode === "register" ? "button" : "ghost-button"}
          type="button"
          onClick={() => setMode("register")}
        >
          Sign up
        </button>
      </div>

      {mode === "login" ? (
        <div className="chip-row">
          <button className="ghost-button" type="button" onClick={() => setDemoAccount("customer@shopflow.local", "Customer123")}>
            Customer demo
          </button>
          <button className="ghost-button" type="button" onClick={() => setDemoAccount("seller@shopflow.local", "Seller123")}>
            Seller demo
          </button>
          <button className="ghost-button" type="button" onClick={() => setDemoAccount("admin@shopflow.local", "Admin123")}>
            Admin demo
          </button>
        </div>
      ) : null}

      <form className="field-group" onSubmit={handleSubmit}>
        {mode === "register" ? (
          <>
            <div className="auth-grid">
              <div className="field-group">
                <label htmlFor="prenom">First name</label>
                <input
                  id="prenom"
                  className="field"
                  value={registerData.prenom}
                  onChange={(event) => setRegisterData((current) => ({ ...current, prenom: event.target.value }))}
                />
              </div>
              <div className="field-group">
                <label htmlFor="nom">Last name</label>
                <input
                  id="nom"
                  className="field"
                  value={registerData.nom}
                  onChange={(event) => setRegisterData((current) => ({ ...current, nom: event.target.value }))}
                />
              </div>
            </div>

            <div className="field-group">
              <label htmlFor="register-email">Email</label>
              <input
                id="register-email"
                className="field"
                type="email"
                value={registerData.email}
                onChange={(event) => setRegisterData((current) => ({ ...current, email: event.target.value }))}
              />
            </div>

            <div className="field-group">
              <label htmlFor="register-password">Password</label>
              <input
                id="register-password"
                className="field"
                type="password"
                value={registerData.motDePasse}
                onChange={(event) => setRegisterData((current) => ({ ...current, motDePasse: event.target.value }))}
              />
              <p className="small">Use at least 8 characters with uppercase, lowercase, and a number.</p>
            </div>

            <div className="field-group">
              <label htmlFor="role">Account type</label>
              <select
                id="role"
                className="select"
                value={registerData.role}
                onChange={(event) =>
                  setRegisterData((current) => ({ ...current, role: event.target.value as AccountRole }))
                }
              >
                <option value="CUSTOMER">Customer</option>
                <option value="SELLER">Seller</option>
              </select>
            </div>

            {registerData.role === "SELLER" ? (
              <>
                <div className="field-group">
                  <label htmlFor="nomBoutique">Shop name</label>
                  <input
                    id="nomBoutique"
                    className="field"
                    value={registerData.nomBoutique}
                    onChange={(event) => setRegisterData((current) => ({ ...current, nomBoutique: event.target.value }))}
                  />
                </div>
                <div className="field-group">
                  <label htmlFor="descriptionBoutique">Shop description</label>
                  <textarea
                    id="descriptionBoutique"
                    className="textarea"
                    value={registerData.descriptionBoutique}
                    onChange={(event) =>
                      setRegisterData((current) => ({ ...current, descriptionBoutique: event.target.value }))
                    }
                  />
                </div>
                <div className="field-group">
                  <label htmlFor="logoBoutique">Logo image URL</label>
                  <input
                    id="logoBoutique"
                    className="field"
                    type="url"
                    value={registerData.logoBoutique}
                    onChange={(event) => setRegisterData((current) => ({ ...current, logoBoutique: event.target.value }))}
                  />
                </div>
              </>
            ) : null}
          </>
        ) : (
          <>
            <div className="field-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                className="field"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </div>

            <div className="field-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                className="field"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </div>
          </>
        )}

        {error ? <p style={{ color: "#8d2032" }}>{error}</p> : null}

        <button className="button" type="submit" disabled={pending}>
          {pending ? (mode === "login" ? "Signing in..." : "Creating account...") : mode === "login" ? "Login" : "Create account"}
        </button>
      </form>
    </div>
  );
}
