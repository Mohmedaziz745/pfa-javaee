import type { Metadata } from "next";
import { DM_Sans, Space_Grotesk } from "next/font/google";
import Link from "next/link";
import "./globals.css";
import { HeaderClient } from "@/components/header-client";

const bodyFont = DM_Sans({
  variable: "--font-body",
  subsets: ["latin"],
});

const displayFont = Space_Grotesk({
  variable: "--font-display",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "ShopFlow",
  description: "Modern e-commerce storefront for ShopFlow",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="fr" className={`${bodyFont.variable} ${displayFont.variable}`}>
      <body>
        <div className="site-shell">
          <HeaderClient />
          <main>{children}</main>
          <footer className="site-footer">
            <div>
              <p className="eyebrow">ShopFlow</p>
              <h3>Marketplace frontend for the Spring Boot API.</h3>
            </div>
            <div className="footer-links">
              <Link href="/">Accueil</Link>
              <Link href="/catalog">Catalogue</Link>
              <Link href="/cart">Panier</Link>
              <a href="http://localhost:8081/swagger-ui" target="_blank" rel="noreferrer">
                Swagger
              </a>
            </div>
          </footer>
        </div>
      </body>
    </html>
  );
}
