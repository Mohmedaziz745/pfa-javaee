"use client";

import { useEffect, useMemo, useState } from "react";

type CardState = {
  number: string;
  holder: string;
  expiry: string;
  cvv: string;
};

type FieldKey = keyof CardState;

type CreditCardFormProps = {
  onValidChange: (valid: boolean) => void;
};

function luhnCheck(cardNumber: string): boolean {
  const digits = cardNumber.replace(/\D/g, "");
  if (digits.length < 13 || digits.length > 19) {
    return false;
  }
  let sum = 0;
  let shouldDouble = false;
  for (let i = digits.length - 1; i >= 0; i -= 1) {
    let value = Number(digits[i]);
    if (shouldDouble) {
      value *= 2;
      if (value > 9) {
        value -= 9;
      }
    }
    sum += value;
    shouldDouble = !shouldDouble;
  }
  return sum % 10 === 0;
}

function expiryValid(expiry: string): boolean {
  const match = /^(0[1-9]|1[0-2])\/(\d{2})$/.exec(expiry);
  if (!match) {
    return false;
  }
  const month = Number(match[1]);
  const year = 2000 + Number(match[2]);
  const endOfMonth = new Date(year, month, 1);
  return endOfMonth > new Date();
}

function formatCardNumber(raw: string): string {
  const digits = raw.replace(/\D/g, "").slice(0, 19);
  return digits.replace(/(.{4})/g, "$1 ").trim();
}

function formatExpiry(raw: string): string {
  const digits = raw.replace(/\D/g, "").slice(0, 4);
  if (digits.length < 3) {
    return digits;
  }
  return `${digits.slice(0, 2)}/${digits.slice(2)}`;
}

function sanitizeCvv(raw: string): string {
  return raw.replace(/\D/g, "").slice(0, 4);
}

export function CreditCardForm({ onValidChange }: CreditCardFormProps) {
  const [card, setCard] = useState<CardState>({ number: "", holder: "", expiry: "", cvv: "" });
  const [touched, setTouched] = useState<Record<FieldKey, boolean>>({
    number: false,
    holder: false,
    expiry: false,
    cvv: false,
  });

  const errors = useMemo<Partial<Record<FieldKey, string>>>(() => {
    const result: Partial<Record<FieldKey, string>> = {};
    if (!luhnCheck(card.number)) {
      result.number = "Enter a valid card number.";
    }
    if (card.holder.trim().length < 2) {
      result.holder = "Enter the card holder name.";
    }
    if (!expiryValid(card.expiry)) {
      result.expiry = "Enter a valid MM/YY in the future.";
    }
    if (!/^\d{3,4}$/.test(card.cvv)) {
      result.cvv = "CVV must be 3 or 4 digits.";
    }
    return result;
  }, [card]);

  const valid = Object.keys(errors).length === 0;

  useEffect(() => {
    onValidChange(valid);
  }, [valid, onValidChange]);

  function showError(key: FieldKey) {
    return touched[key] ? errors[key] : undefined;
  }

  function markTouched(key: FieldKey) {
    setTouched((previous) => ({ ...previous, [key]: true }));
  }

  return (
    <div className="field-group" style={{ marginTop: 18 }}>
      <p className="eyebrow">Payment</p>
      <label htmlFor="card-number">Card number</label>
      <input
        id="card-number"
        className="field"
        inputMode="numeric"
        autoComplete="cc-number"
        placeholder="1234 5678 9012 3456"
        value={formatCardNumber(card.number)}
        onChange={(event) => setCard((previous) => ({ ...previous, number: event.target.value.replace(/\D/g, "").slice(0, 19) }))}
        onBlur={() => markTouched("number")}
      />
      {showError("number") ? <p className="small" style={{ color: "var(--accent-deep)" }}>{showError("number")}</p> : null}

      <label htmlFor="card-holder" style={{ marginTop: 12 }}>Card holder</label>
      <input
        id="card-holder"
        className="field"
        autoComplete="cc-name"
        placeholder="JANE DOE"
        value={card.holder}
        onChange={(event) => setCard((previous) => ({ ...previous, holder: event.target.value }))}
        onBlur={() => markTouched("holder")}
      />
      {showError("holder") ? <p className="small" style={{ color: "var(--accent-deep)" }}>{showError("holder")}</p> : null}

      <div className="inline-row" style={{ marginTop: 12, gap: 12 }}>
        <div style={{ flex: 1 }}>
          <label htmlFor="card-expiry">Expiration (MM/YY)</label>
          <input
            id="card-expiry"
            className="field"
            inputMode="numeric"
            autoComplete="cc-exp"
            placeholder="MM/YY"
            value={formatExpiry(card.expiry)}
            onChange={(event) => setCard((previous) => ({ ...previous, expiry: event.target.value }))}
            onBlur={() => markTouched("expiry")}
          />
          {showError("expiry") ? <p className="small" style={{ color: "var(--accent-deep)" }}>{showError("expiry")}</p> : null}
        </div>
        <div style={{ flex: 1 }}>
          <label htmlFor="card-cvv">CVV</label>
          <input
            id="card-cvv"
            className="field"
            inputMode="numeric"
            autoComplete="cc-csc"
            placeholder="123"
            value={card.cvv}
            onChange={(event) => setCard((previous) => ({ ...previous, cvv: sanitizeCvv(event.target.value) }))}
            onBlur={() => markTouched("cvv")}
          />
          {showError("cvv") ? <p className="small" style={{ color: "var(--accent-deep)" }}>{showError("cvv")}</p> : null}
        </div>
      </div>
    </div>
  );
}
