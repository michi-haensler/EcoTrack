# EcoTrack Design Tokens

Design Tokens für konsistentes UI-Design über alle Plattformen hinweg.

## Verwendung

### Web (React Admin)

```typescript
import tokens from '../../shared-resources/design-tokens/tokens.json';

// In CSS-in-JS oder Tailwind Config
const primaryColor = tokens.colors.primary['500'];
```

### Mobile (React Native)

```typescript
import tokens from '../../shared-resources/design-tokens/tokens.json';

const styles = StyleSheet.create({
  primaryButton: {
    backgroundColor: tokens.colors.primary['500'],
  },
});
```

## Farbschema

### Primärfarbe (Grün - Nachhaltigkeit)
- `primary.500` - Hauptfarbe für Buttons, Links
- `primary.700` - Hover/Active States
- `primary.100` - Hintergründe

### Sekundärfarbe (Blau - Vertrauen)
- `secondary.500` - Sekundäre Aktionen
- `secondary.700` - Hover/Active States

### Akzentfarbe (Orange - Gamification)
- `accent.500` - Highlights, Badges
- `accent.700` - Achievements

### Kategoriefarben
Jede Nachhaltigkeitskategorie hat ihre eigene Farbe:
- 🚲 Mobilität: Grün
- 🛒 Konsum: Blau
- ♻️ Recycling: Orange
- ⚡ Energie: Violett
- 🥗 Ernährung: Pink
- 📦 Sonstiges: Grau

### Level-Farben
Visualisierung des Baum-Wachstums:
- 🌱 Setzling: Hellgrün
- 🌿 Jungbaum: Mittelgrün
- 🌳 Baum: Dunkelgrün
- 🌲 Altbaum: Tiefgrün
