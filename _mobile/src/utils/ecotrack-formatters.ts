import { EcoCategory, EcoLevel, EcoUnit } from '../types/ecotrack-api';

const CATEGORY_LABELS: Record<string, string> = {
  ALLE: 'Alle',
  MOBILITAET: 'Mobilitaet',
  RECYCLING: 'Recycling',
  ERNAEHRUNG: 'Ernaehrung',
  ENERGIE: 'Energie',
};

const LEVEL_LABELS: Record<string, string> = {
  SETZLING: 'Setzling',
  JUNGBAUM: 'Jungbaum',
  BAUM: 'Baum',
  ALTBAUM: 'Altbaum',
  LEGEND: 'Legende',
};

const UNIT_LABELS: Record<string, string> = {
  KM: 'km',
  STUECK: 'x',
  MINUTEN: 'min',
};

export function formatCategoryLabel(category?: EcoCategory) {
  if (!category) {
    return 'Aktion';
  }

  return CATEGORY_LABELS[category] ?? category;
}

export function formatLevelLabel(level?: EcoLevel) {
  if (!level) {
    return 'Setzling';
  }

  return LEVEL_LABELS[level] ?? level;
}

export function formatUnitLabel(unit?: EcoUnit) {
  if (!unit) {
    return 'Einheit';
  }

  return UNIT_LABELS[unit] ?? unit.toLowerCase();
}

export function formatQuantityLabel(quantity: number, unit?: EcoUnit) {
  const formattedQuantity = Number.isInteger(quantity) ? String(quantity) : quantity.toFixed(1);
  return `${formattedQuantity} ${formatUnitLabel(unit)}`;
}

export function formatActivityTimestamp(timestamp?: string) {
  if (!timestamp) {
    return 'Gerade eben';
  }

  const date = new Date(timestamp);
  if (Number.isNaN(date.getTime())) {
    return timestamp;
  }

  return date.toLocaleString('de-AT', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function getInitials(name: string) {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map(part => part.charAt(0).toUpperCase())
    .join('');
}

export function getFirstName(displayName?: string) {
  if (!displayName) {
    return 'EcoTrack';
  }

  return displayName.trim().split(' ')[0] || displayName;
}
