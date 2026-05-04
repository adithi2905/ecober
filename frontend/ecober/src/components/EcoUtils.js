import React from 'react';

export const AVG_CAR_PER_TRIP = 2.31; // kg CO₂, US EPA urban average

// ── SVG donut ring: Gold ≥70, Silver ≥40, Bronze <40 ─────────────────────────
export function EcoRing({ score, size = 44 }) {
  const s    = typeof score === 'number' ? Math.max(0, Math.min(100, score)) : 0;
  const r    = 13;
  const circ = 2 * Math.PI * r;
  const dash = (s / 100) * circ;
  const color = s >= 70 ? '#16a34a' : s >= 40 ? '#eab308' : '#dc2626';
  const tier  = s >= 70 ? 'Gold'    : s >= 40 ? 'Silver'  : 'Bronze';

  return (
    <div className="relative flex-shrink-0" style={{ width: size, height: size }}>
      <svg width={size} height={size} viewBox="0 0 32 32" style={{ transform: 'rotate(-90deg)' }}>
        <circle cx="16" cy="16" r={r} fill="none" stroke="#e2e8f0" strokeWidth="3.5" />
        <circle
          cx="16" cy="16" r={r}
          fill="none"
          stroke={color}
          strokeWidth="3.5"
          strokeDasharray={`${dash} ${circ - dash}`}
          strokeLinecap="round"
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span style={{ fontSize: size >= 44 ? '11px' : '9px', fontWeight: 700, color, lineHeight: 1 }}>
          {score != null ? score : '—'}
        </span>
        <span style={{ fontSize: '6px', color, opacity: 0.75, lineHeight: 1, marginTop: '1px' }}>
          {tier}
        </span>
      </div>
    </div>
  );
}

// ── Inline real-world CO₂ equivalent line ─────────────────────────────────────
export function Co2Equiv({ kg, className = '' }) {
  if (!kg || kg <= 0) return null;
  const trees = (kg / 22).toFixed(1);
  const km    = Math.round(kg / 0.21);
  return (
    <p className={`text-xs text-gray-700 leading-snug ${className}`}>
      {kg.toFixed(1)} kg CO₂ = {trees} trees absorbed • {km} km car avoided
    </p>
  );
}

// ── Icon tile for Real-World Impact row ───────────────────────────────────────
export function RealWorldTile({ icon, label, value }) {
  return (
    <div className="bg-white border border-gray-300 rounded-lg p-4 text-center shadow-md hover:shadow-lg transition-shadow">
      {icon && <span className="text-xl text-emerald-600">{icon}</span>}
      <p className="text-base font-semibold text-gray-900 mt-3 leading-tight">{value}</p>
      <p className="text-xs text-gray-600 mt-1 leading-tight">{label}</p>
    </div>
  );
}

// ── Pure-SVG animated leaf (idle state illustration) ─────────────────────────
export function LeafIllustration() {
  return (
    <div style={{ animation: 'leafPulse 2.8s ease-in-out infinite' }}>
      <svg width="64" height="72" viewBox="0 0 64 72" fill="none">
        <path
          d="M32 68 C32 68 6 50 6 28 C6 12 18 3 32 3 C46 3 58 12 58 28 C58 50 32 68 32 68Z"
          fill="#6ee7b7"
          stroke="#10b981"
          strokeWidth="2"
        />
        {/* Leaf veins */}
        <line x1="32" y1="10" x2="32" y2="66" stroke="#059669" strokeWidth="1.5" strokeLinecap="round" opacity="0.45" />
        <line x1="32" y1="32" x2="20" y2="22" stroke="#059669" strokeWidth="1.2" strokeLinecap="round" opacity="0.55" />
        <line x1="32" y1="40" x2="44" y2="30" stroke="#059669" strokeWidth="1.2" strokeLinecap="round" opacity="0.55" />
        <line x1="32" y1="24" x2="23" y2="16" stroke="#059669" strokeWidth="1" strokeLinecap="round" opacity="0.4" />
        <line x1="32" y1="48" x2="42" y2="40" stroke="#059669" strokeWidth="1" strokeLinecap="round" opacity="0.4" />
      </svg>
    </div>
  );
}
