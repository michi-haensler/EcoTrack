// ============================================================
// Activities Hooks Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Query und Mutation Hooks mit TanStack Query.
// ============================================================

import { activityApi } from '@/api/activity-api';
import type { CreateActivityRequest } from '@/types/activity';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

// -----------------------------
// Query Keys Factory
// -----------------------------
// Zentrale Definition aller Query Keys für Type Safety
// und einfache Invalidierung
export const activityKeys = {
  all: ['activities'] as const,
  lists: () => [...activityKeys.all, 'list'] as const,
  list: (userId: string) => [...activityKeys.lists(), userId] as const,
  details: () => [...activityKeys.all, 'detail'] as const,
  detail: (id: string) => [...activityKeys.details(), id] as const,
};

// -----------------------------
// Query Hook: Liste laden
// -----------------------------
/**
 * Hook zum Laden aller Aktivitäten eines Benutzers
 * 
 * @param userId - Die ID des Benutzers
 * @returns TanStack Query Result mit Activity[]
 * 
 * @example
 * ```tsx
 * function ActivityList({ userId }) {
 *   const { data, isLoading, error } = useActivities(userId);
 *   
 *   if (isLoading) return <Spinner />;
 *   if (error) return <ErrorMessage error={error} />;
 *   
 *   return data.map(a => <ActivityCard key={a.id} activity={a} />);
 * }
 * ```
 */
export function useActivities(userId: string) {
  return useQuery({
    queryKey: activityKeys.list(userId),
    queryFn: () => activityApi.getByUser(userId),
    staleTime: 5 * 60 * 1000, // 5 Minuten - Daten als "fresh" behandeln
    enabled: !!userId, // Nur laden wenn userId vorhanden
  });
}

// -----------------------------
// Query Hook: Einzelnes Item
// -----------------------------
/**
 * Hook zum Laden einer einzelnen Aktivität
 * 
 * @param id - Die ID der Aktivität
 * @returns TanStack Query Result mit Activity
 */
export function useActivity(id: string) {
  return useQuery({
    queryKey: activityKeys.detail(id),
    queryFn: () => activityApi.getById(id),
    enabled: !!id,
  });
}

// -----------------------------
// Mutation Hook: Erstellen
// -----------------------------
/**
 * Hook zum Erstellen einer neuen Aktivität
 * 
 * Invalidiert automatisch alle Listen-Queries nach Erfolg.
 * 
 * @returns TanStack Mutation
 * 
 * @example
 * ```tsx
 * function CreateButton() {
 *   const createActivity = useCreateActivity();
 *   
 *   const handleCreate = () => {
 *     createActivity.mutate({
 *       ecoUserId: 'user-123',
 *       actionDefinitionId: 'action-456',
 *       quantity: 1,
 *     });
 *   };
 *   
 *   return (
 *     <Button 
 *       onClick={handleCreate} 
 *       isLoading={createActivity.isPending}
 *     >
 *       Erstellen
 *     </Button>
 *   );
 * }
 * ```
 */
export function useCreateActivity() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateActivityRequest) => activityApi.create(data),
    onSuccess: (newActivity) => {
      // Listen invalidieren → werden neu geladen
      queryClient.invalidateQueries({ queryKey: activityKeys.lists() });
      
      // Optional: Neue Aktivität direkt in Cache speichern
      queryClient.setQueryData(
        activityKeys.detail(newActivity.id),
        newActivity
      );
    },
  });
}

// -----------------------------
// Mutation Hook: Löschen
// -----------------------------
/**
 * Hook zum Löschen einer Aktivität
 * 
 * @returns TanStack Mutation
 */
export function useDeleteActivity() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => activityApi.delete(id),
    onSuccess: (_, deletedId) => {
      // Aus Cache entfernen
      queryClient.removeQueries({ queryKey: activityKeys.detail(deletedId) });
      // Listen invalidieren
      queryClient.invalidateQueries({ queryKey: activityKeys.lists() });
    },
  });
}

// -----------------------------
// Mutation Hook: Aktualisieren
// -----------------------------
/**
 * Hook zum Aktualisieren einer Aktivität
 * 
 * @returns TanStack Mutation
 */
export function useUpdateActivity() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<CreateActivityRequest> }) => 
      activityApi.update(id, data),
    onSuccess: (updatedActivity) => {
      // Cache aktualisieren
      queryClient.setQueryData(
        activityKeys.detail(updatedActivity.id),
        updatedActivity
      );
      // Listen invalidieren
      queryClient.invalidateQueries({ queryKey: activityKeys.lists() });
    },
  });
}
