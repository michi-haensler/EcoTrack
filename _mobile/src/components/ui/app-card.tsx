import { ReactNode } from 'react';
import {
  Platform,
  StyleProp,
  StyleSheet,
  View,
  ViewProps,
  ViewStyle,
} from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

interface AppCardProps extends ViewProps {
  children: ReactNode;
  style?: StyleProp<ViewStyle>;
}

export function AppCard({ children, style, ...rest }: AppCardProps) {
  return (
    <View {...rest} style={[styles.card, style]}>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: Colors.surface,
    borderRadius: 24,
    borderWidth: 1,
    borderColor: Colors.border,
    padding: Spacing.lg,
    ...Platform.select({
      android: {
        elevation: 3,
      },
      ios: {
        shadowColor: Colors.shadow,
        shadowOffset: { width: 0, height: 12 },
        shadowOpacity: 0.08,
        shadowRadius: 24,
      },
      default: {},
    }),
  },
});
