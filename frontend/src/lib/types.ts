export type SellerSummary = {
  id: number;
  nomBoutique: string;
  description: string | null;
  logo: string | null;
  note: number;
};

export type Review = {
  id: number;
  customerId: number;
  customerName: string;
  note: number;
  commentaire: string;
  approuve: boolean;
  dateCreation: string;
};

export type ProductVariant = {
  id: number;
  attribut: string;
  valeur: string;
  stockSupplementaire: number;
  prixDelta: number;
};

export type Product = {
  id: number;
  nom: string;
  description: string;
  prix: number;
  prixPromo: number | null;
  stock: number;
  actif: boolean;
  salesCount: number;
  averageRating: number;
  reviewCount: number;
  dateCreation: string;
  categories: string[];
  images: string[];
  seller: SellerSummary;
  variants: ProductVariant[];
  reviews: Review[];
};

export type ProductPage = {
  content: Product[];
  totalElements: number;
  totalPages: number;
  number: number;
};

export type CartItem = {
  itemId: number;
  productId: number;
  variantId: number | null;
  productName: string;
  variantLabel: string | null;
  quantite: number;
  prixUnitaire: number;
  totalLigne: number;
};

export type Cart = {
  id: number;
  lignes: CartItem[];
  couponCode: string | null;
  sousTotal: number;
  remise: number;
  fraisLivraison: number;
  totalTtc: number;
  dateModification: string;
};

export type Address = {
  id: number;
  rue: string;
  ville: string;
  codePostal: string;
  pays: string;
  principal: boolean;
};

export type OrderItem = {
  id: number;
  productId: number;
  variantId: number | null;
  productName: string;
  variantLabel: string | null;
  quantite: number;
  prixUnitaire: number;
};

export type Order = {
  id: number;
  numeroCommande: string;
  statut: string;
  sousTotal: number;
  fraisLivraison: number;
  totalTTC: number;
  isNew: boolean;
  dateCommande: string;
  adresseLivraison: Address;
  lignes: OrderItem[];
};

export type AuthPayload = {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  role: "ADMIN" | "SELLER" | "CUSTOMER";
};

export type RegisterPayload = {
  email: string;
  motDePasse: string;
  prenom: string;
  nom: string;
  role: "CUSTOMER" | "SELLER";
  nomBoutique?: string;
  descriptionBoutique?: string;
  logoBoutique?: string;
};
