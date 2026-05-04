import React from 'react';

export const BADGE_TIERS = [
  { name: 'Green Starter',  fill: '#4ade80', stroke: '#16a34a', dark: '#14532d' },
  { name: 'Eco Rider',      fill: '#34d399', stroke: '#059669', dark: '#064e3b' },
  { name: 'Green Champion', fill: '#2dd4bf', stroke: '#0d9488', dark: '#134e4a' },
  { name: 'Eco Warrior',    fill: '#60a5fa', stroke: '#2563eb', dark: '#1e3a8a' },
  { name: 'Eco Legend',     fill: '#c084fc', stroke: '#9333ea', dark: '#4c1d95' },
];

export const BADGE_LABELS = {
  'Green Starter':  'Just starting the eco journey',
  'Eco Rider':      'Building green habits',
  'Green Champion': 'Making a real difference',
  'Eco Warrior':    'A true eco champion',
  'Eco Legend':     'Top tier sustainability',
};

export const BADGE_ORDER = BADGE_TIERS.map(t => t.name);

function getTier(badgeName) {
  const i = BADGE_ORDER.indexOf(badgeName);
  return i === -1 ? 0 : i;
}

function TierIcon({ tier }) {
  // Sprout
  if (tier === 0) return (
    <>
      <line x1="40" y1="66" x2="40" y2="44" stroke="white" strokeWidth="2.5" strokeLinecap="round" />
      <path d="M40 56 C40 46 27 42 26 33 C25 27 31 23 36 28 C39 31 40 46 40 56Z" fill="white" opacity="0.85" />
      <path d="M40 50 C40 40 53 36 54 27 C55 21 49 17 44 22 C41 25 40 40 40 50Z" fill="white" opacity="0.75" />
    </>
  );
  // Single leaf
  if (tier === 1) return (
    <>
      <path d="M40 67 C40 67 23 54 23 41 C23 29 31 21 40 21 C49 21 57 29 57 41 C57 54 40 67 40 67Z" fill="white" opacity="0.9" />
      <line x1="40" y1="46" x2="32" y2="37" stroke="#059669" strokeWidth="2" strokeLinecap="round" />
      <line x1="40" y1="54" x2="49" y2="45" stroke="#059669" strokeWidth="2" strokeLinecap="round" />
    </>
  );
  // Star / champion
  if (tier === 2) return (
    <polygon
      points="40,23 44,33 55,33 46,40 49,51 40,44 31,51 34,40 25,33 36,33"
      fill="white"
      opacity="0.92"
    />
  );
  // Globe
  if (tier === 3) return (
    <>
      <circle cx="40" cy="44" r="19" fill="none" stroke="white" strokeWidth="2.5" />
      <ellipse cx="40" cy="44" rx="9.5" ry="19" fill="none" stroke="white" strokeWidth="1.75" />
      <line x1="21" y1="44" x2="59" y2="44" stroke="white" strokeWidth="2" />
      <line x1="25" y1="34" x2="55" y2="34" stroke="white" strokeWidth="1.5" opacity="0.65" />
      <line x1="25" y1="54" x2="55" y2="54" stroke="white" strokeWidth="1.5" opacity="0.65" />
    </>
  );
  // Lightning bolt
  return (
    <polygon points="46,22 31,47 42,47 35,67 56,40 44,40" fill="white" opacity="0.95" />
  );
}

export function BadgeShield({ badgeName, size = 56 }) {
  const tier = getTier(badgeName);
  const { fill, stroke } = BADGE_TIERS[tier];
  const gradId = `sg-${tier}`;

  return (
    <svg width={size} height={Math.round(size * 1.12)} viewBox="0 0 80 90" fill="none">
      <defs>
        <linearGradient id={gradId} x1="0.3" y1="0" x2="0.7" y2="1">
          <stop offset="0%" stopColor="white" stopOpacity="0.35" />
          <stop offset="100%" stopColor="black" stopOpacity="0.12" />
        </linearGradient>
      </defs>
      {/* Drop shadow */}
      <path
        d="M40 8 L74 21 V53 C74 73 58 85 40 90 C22 85 6 73 6 53 V21 Z"
        fill={stroke}
        opacity="0.18"
        transform="translate(2,3)"
      />
      {/* Shield fill */}
      <path d="M40 8 L74 21 V53 C74 73 58 85 40 90 C22 85 6 73 6 53 V21 Z" fill={fill} />
      {/* Gloss */}
      <path d="M40 8 L74 21 V53 C74 73 58 85 40 90 C22 85 6 73 6 53 V21 Z" fill={`url(#${gradId})`} />
      {/* Border */}
      <path d="M40 8 L74 21 V53 C74 73 58 85 40 90 C22 85 6 73 6 53 V21 Z" stroke={stroke} strokeWidth="2.5" />
      {/* Inner icon */}
      <TierIcon tier={tier} />
    </svg>
  );
}

/** Small pill for sidebars and trip cards */
export function EcoBadgePill({ badgeName, co2Saved }) {
  const tier  = getTier(badgeName);
  const { stroke } = BADGE_TIERS[tier];
  return (
    <div className="flex items-center gap-2.5 bg-white rounded-xl border border-slate-100 px-3 py-2.5 shadow-sm">
      <BadgeShield badgeName={badgeName} size={34} />
      <div className="min-w-0">
        <p className="text-xs font-bold text-slate-700 leading-tight truncate" style={{ color: stroke }}>
          {badgeName || 'Eco Rider'}
        </p>
        {co2Saved != null && (
          <p className="text-xs text-slate-500 leading-tight mt-0.5">{co2Saved.toFixed(1)} kg CO₂ saved</p>
        )}
      </div>
    </div>
  );
}

/** Large display for profile pages */
export function EcoBadgeFull({ badgeName, size = 88 }) {
  const tier  = getTier(badgeName);
  const label = BADGE_LABELS[badgeName] || '';
  const { stroke } = BADGE_TIERS[tier];

  return (
    <div className="flex flex-col items-center gap-3">
      <BadgeShield badgeName={badgeName} size={size} />
      <div className="text-center">
        <p className="font-bold text-slate-800 text-base leading-tight">{badgeName}</p>
        <p className="text-xs text-slate-500 mt-0.5">{label}</p>
      </div>
      <div className="flex gap-1.5">
        {BADGE_ORDER.map((_, i) => (
          <div
            key={i}
            className="w-2 h-2 rounded-full transition-all"
            style={{ backgroundColor: i <= tier ? stroke : '#e2e8f0' }}
          />
        ))}
      </div>
    </div>
  );
}
