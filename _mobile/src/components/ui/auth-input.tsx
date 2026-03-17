import { ReactNode, useState } from 'react';
import {
  KeyboardTypeOptions,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TextInputProps,
  View,
} from 'react-native';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

interface AuthInputProps {
  label: string;
  value: string;
  placeholder: string;
  onChangeText: (value: string) => void;
  keyboardType?: KeyboardTypeOptions;
  secureTextEntry?: boolean;
  autoCapitalize?: TextInputProps['autoCapitalize'];
  trailingActionLabel?: string;
  onTrailingActionPress?: () => void;
  leadingAccessory?: ReactNode;
}

export function AuthInput({
  label,
  value,
  placeholder,
  onChangeText,
  keyboardType = 'default',
  secureTextEntry = false,
  autoCapitalize = 'none',
  trailingActionLabel,
  onTrailingActionPress,
  leadingAccessory,
}: AuthInputProps) {
  const [isFocused, setIsFocused] = useState(false);

  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <View style={[styles.field, isFocused && styles.fieldFocused]}>
        {leadingAccessory ? <View style={styles.leading}>{leadingAccessory}</View> : null}
        <TextInput
          autoCapitalize={autoCapitalize}
          keyboardType={keyboardType}
          onBlur={() => setIsFocused(false)}
          onChangeText={onChangeText}
          onFocus={() => setIsFocused(true)}
          placeholder={placeholder}
          placeholderTextColor={Colors.textSoft}
          secureTextEntry={secureTextEntry}
          style={styles.input}
          value={value}
        />
        {trailingActionLabel && onTrailingActionPress ? (
          <Pressable onPress={onTrailingActionPress} style={styles.trailingButton}>
            <Text style={styles.trailingText}>{trailingActionLabel}</Text>
          </Pressable>
        ) : null}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: Spacing.md,
  },
  label: {
    marginBottom: Spacing.sm,
    marginLeft: Spacing.xs,
    color: Colors.text,
    fontSize: 14,
    fontWeight: '700',
  },
  field: {
    minHeight: 56,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: Colors.border,
    backgroundColor: Colors.surface,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.md,
  },
  fieldFocused: {
    borderColor: Colors.primary,
    backgroundColor: Colors.surfaceMuted,
  },
  leading: {
    marginRight: Spacing.sm,
  },
  input: {
    flex: 1,
    color: Colors.text,
    fontSize: 16,
    paddingVertical: Spacing.md,
  },
  trailingButton: {
    marginLeft: Spacing.sm,
    paddingVertical: Spacing.xs,
    paddingHorizontal: Spacing.sm,
  },
  trailingText: {
    color: Colors.primaryDark,
    fontSize: 13,
    fontWeight: '700',
  },
});
