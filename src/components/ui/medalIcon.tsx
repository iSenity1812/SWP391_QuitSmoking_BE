import React from 'react';

interface MedalIconProps {
  size?: number;
  color?: string;
  locked?: boolean;
}

const MedalIcon: React.FC<MedalIconProps> = ({ size = 56, color = '#FFD700', locked = false }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 56 56"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    {/* Ribbon */}
    <rect x="22" y="38" width="4" height="14" rx="2" fill={locked ? '#BDBDBD' : '#1976D2'} />
    <rect x="30" y="38" width="4" height="14" rx="2" fill={locked ? '#BDBDBD' : '#D32F2F'} />
    {/* Medal Circle */}
    <circle
      cx="28"
      cy="28"
      r="16"
      fill={locked ? '#E0E0E0' : color}
      stroke={locked ? '#BDBDBD' : '#FFD700'}
      strokeWidth="3"
    />
    {/* Star */}
    <polygon
      points="28,18 30.472,24.472 37.5,25.236 32.25,29.764 33.944,36.764 28,33.236 22.056,36.764 23.75,29.764 18.5,25.236 25.528,24.472"
      fill={locked ? '#BDBDBD' : '#FFF59D'}
      stroke={locked ? '#BDBDBD' : '#FBC02D'}
      strokeWidth="1"
    />
    {/* Lock overlay if locked */}
    {locked && (
      <g>
        <circle cx="28" cy="28" r="16" fill="#BDBDBD" fillOpacity="0.3" />
        <rect x="22" y="22" width="12" height="10" rx="3" fill="#BDBDBD" />
        <rect x="26" y="18" width="4" height="6" rx="2" fill="#BDBDBD" />
      </g>
    )}
  </svg>
);

export default MedalIcon; 