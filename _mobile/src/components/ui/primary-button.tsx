import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type ButtonVariant = 'filled' | 'outline' | 'ghost';

interface PrimaryButtonProps {
  label: string;
  onPress?: () => void;
  disabled?: boolean;
  variant?: ButtonVariant;
  accentText?: string;
}

export function PrimaryButton({
  label,
  onPress,
  disabled = false,
  variant = 'filled',
  accentText,
}: PrimaryButtonProps) {
  return (
    <Pressable
      accessibilityRole="button"
      disabled={disabled}
      onPress={onPress}
      style={({ pressed }) => [
        styles.base,
        variant === 'filled' && styles.filled,
        variant === 'outline' && styles.outline,
        variant === 'ghost' && styles.ghost,
        disabled && styles.disabled,
        pressed && !disabled && styles.pressed,
      ]}
    >
      <Text
        style={[
          styles.label,
          variant === 'filled' ? styles.labelFilled : styles.labelOutline,
          disabled && styles.labelDisabled,
        ]}
      >
        {label}
      </Text>
      {accentText ? (
        <View style={styles.accentBadge}>
          <Text style={styles.accentText}>{accentText}</Text>
        </View>
      ) : null}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    minHeight: 56,
    borderRadius: 18,
    alignItems: 'center',
    justifyContent: 'center',
    flexDirection: 'row',
    paddingHorizontal: Spacing.lg,
    borderWidth: 1,
  },
  filled: {
    backgroundColor: Colors.primary,
    borderColor: Colors.primary,
  },
  outline: {
    backgroundColor: Colors.surface,
    borderColor: Colors.border,
  },
  ghost: {
    backgroundColor: 'transparent',
    borderColor: 'transparent',
  },
  disabled: {
    backgroundColor: Colors.surfaceMuted,
    borderColor: Colors.border,
  },
  pressed: {
    opacity: 0.85,
    transform: [{ scale: 0.99 }],
  },
  label: {
    fontSize: 16,
    fontWeight: '700',
  },
  labelFilled: {
    color: Colors.surface,
  },
  labelOutline: {
    color: Colors.text,
  },
  labelDisabled: {
    color: Colors.textSoft,
  },
  accentBadge: {
    marginLeft: Spacing.sm,
    minWidth: 28,
    height: 28,
    paddingHorizontal: Spacing.sm,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.18)',
  },
  accentText: {
    color: Colors.surface,
    fontSize: 12,
    fontWeight: '800',
  },
});
