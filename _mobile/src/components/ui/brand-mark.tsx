import { StyleSheet, Text, View } from 'react-native';
import { Colors } from '../../theme/colors';

type BrandMarkSize = 'sm' | 'md' | 'lg';

interface BrandMarkProps {
  size?: BrandMarkSize;
}

const DIMENSIONS = {
  sm: { outer: 72, inner: 44, font: 18 },
  md: { outer: 96, inner: 56, font: 24 },
  lg: { outer: 120, inner: 68, font: 28 },
} as const;

export function BrandMark({ size = 'md' }: BrandMarkProps) {
  const dimensions = DIMENSIONS[size];

  return (
    <View
      style={[
        styles.outer,
        {
          width: dimensions.outer,
          height: dimensions.outer,
          borderRadius: dimensions.outer / 2,
        },
      ]}
    >
      <View
        style={[
          styles.inner,
          {
            width: dimensions.inner,
            height: dimensions.inner,
            borderRadius: dimensions.inner / 2,
          },
        ]}
      >
        <View style={styles.leafCluster}>
          <View style={[styles.leaf, styles.leafLeft]} />
          <View style={[styles.leaf, styles.leafRight]} />
        </View>
        <Text style={[styles.wordmark, { fontSize: dimensions.font }]}>ET</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  outer: {
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'rgba(15, 184, 108, 0.12)',
  },
  inner: {
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.surface,
    borderWidth: 1,
    borderColor: 'rgba(15, 184, 108, 0.18)',
  },
  leafCluster: {
    position: 'absolute',
    top: 10,
    flexDirection: 'row',
  },
  leaf: {
    width: 12,
    height: 18,
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
    borderBottomLeftRadius: 12,
    borderBottomRightRadius: 12,
    backgroundColor: Colors.primary,
  },
  leafLeft: {
    marginRight: 2,
    transform: [{ rotate: '-30deg' }],
  },
  leafRight: {
    marginLeft: 2,
    transform: [{ rotate: '30deg' }],
  },
  wordmark: {
    color: Colors.primaryDark,
    fontWeight: '800',
    letterSpacing: 1.5,
  },
});
