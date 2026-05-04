import React from 'react';

function Svg({ size, className = '', children, viewBox = '0 0 24 24' }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox={viewBox}
      fill="none"
      stroke="currentColor"
      strokeWidth="1.75"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
    >
      {children}
    </svg>
  );
}

export const LeafIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10z" />
    <path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12" />
  </Svg>
);

export const MapPinIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
    <circle cx="12" cy="10" r="3" />
  </Svg>
);

export const NavigationIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <polygon points="3 11 22 2 13 21 11 13 3 11" />
  </Svg>
);

export const HistoryIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <polyline points="1 4 1 10 7 10" />
    <path d="M3.51 15a9 9 0 1 0 .49-4.52" />
    <polyline points="12 7 12 12 15 15" />
  </Svg>
);

export const UserIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
    <circle cx="12" cy="7" r="4" />
  </Svg>
);

export const CarIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <path d="M5 17H3a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h13l3 4v6a2 2 0 0 1-2 2h-2" />
    <circle cx="7" cy="17" r="2" />
    <circle cx="15" cy="17" r="2" />
  </Svg>
);

export const ZapIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
  </Svg>
);

export const CloudIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <path d="M18 10h-1.26A8 8 0 1 0 9 20h9a5 5 0 0 0 0-10z" />
  </Svg>
);

export const StarIcon = ({ size = 20, className = '', filled = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill={filled ? 'currentColor' : 'none'}
    stroke="currentColor"
    strokeWidth="1.75"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
  >
    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
  </svg>
);

export const ClockIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <circle cx="12" cy="12" r="10" />
    <polyline points="12 6 12 12 16 14" />
  </Svg>
);

export const CheckCircleIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
    <polyline points="22 4 12 14.01 9 11.01" />
  </Svg>
);

export const TrendingDownIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <polyline points="23 18 13.5 8.5 8.5 13.5 1 6" />
    <polyline points="17 18 23 18 23 12" />
  </Svg>
);

export const AwardIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <circle cx="12" cy="8" r="7" />
    <polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88" />
  </Svg>
);

export const TruckIcon = ({ size = 20, className = '' }) => (
  <Svg size={size} className={className}>
    <rect x="1" y="3" width="15" height="13" />
    <polygon points="16 8 20 8 23 11 23 16 16 16 16 8" />
    <circle cx="5.5" cy="18.5" r="2.5" />
    <circle cx="18.5" cy="18.5" r="2.5" />
  </Svg>
);
