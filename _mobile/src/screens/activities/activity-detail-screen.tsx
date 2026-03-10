import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { RootStackParamList } from '../../navigation/types';
import { Colors } from '../../theme/colors';
import { Spacing } from '../../theme/spacing';

type ActivityDetailScreenProps = NativeStackScreenProps<
  RootStackParamList,
  'ActivityDetail'
>;

export function ActivityDetailScreen({ route }: ActivityDetailScreenProps) {
  const { activityId } = route.params;

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.content}>
        <Text style={styles.title} accessibilityRole="header">Aktivitaet Details</Text>
        <Text style={styles.text} accessibilityRole="text">{`ID: ${activityId}`}</Text>
        <Text style={styles.text} accessibilityRole="text">
          Hier stehen die Details zur Aktivitaet.
        </Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  content: {
    padding: Spacing.lg,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: Colors.primary,
    marginBottom: Spacing.sm,
  },
  text: {
    fontSize: 16,
    color: Colors.text,
    marginBottom: Spacing.xs,
  },
});
