import { ReactNode } from 'react';
import { StyleSheet, View } from 'react-native';
import { Colors } from '../../theme/colors';

interface MockupBackgroundProps {
  children: ReactNode;
}

export function MockupBackground({ children }: MockupBackgroundProps) {
  return (
    <View style={styles.container}>
      <View style={[styles.blob, styles.blobTop]} />
      <View style={[styles.blob, styles.blobBottom]} />
      <View style={styles.content}>{children}</View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
    overflow: 'hidden',
  },
  content: {
    flex: 1,
    zIndex: 1,
  },
  blob: {
    position: 'absolute',
    borderRadius: 999,
    backgroundColor: Colors.primary,
    opacity: 0.08,
  },
  blobTop: {
    width: 260,
    height: 260,
    top: -120,
    right: -80,
  },
  blobBottom: {
    width: 220,
    height: 220,
    left: -90,
    bottom: -80,
  },
});
