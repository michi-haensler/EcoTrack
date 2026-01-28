// ============================================================
// Hook Test Beispiel - useActivities
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Hook Tests mit renderHook und QueryClient.
// ============================================================

import { activityApi } from '@/api/activity-api';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { renderHook, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useActivities, useCreateActivity } from './use-activities';

// Mock der API
vi.mock('@/api/activity-api');

describe('useActivities', () => {
  let queryClient: QueryClient;
  
  // -------------------------
  // Test Setup
  // -------------------------
  
  beforeEach(() => {
    // Frischer QueryClient für jeden Test
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { 
          retry: false,
          gcTime: 0,
        },
      },
    });
    
    // Mocks zurücksetzen
    vi.clearAllMocks();
  });
  
  // Wrapper für QueryClientProvider
  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
  
  // -------------------------
  // Query Tests
  // -------------------------
  
  it('should_loadActivities_when_userIdProvided', async () => {
    // Arrange
    const mockActivities = [
      { id: '1', name: 'Activity 1', points: 10 },
      { id: '2', name: 'Activity 2', points: 20 },
    ];
    vi.mocked(activityApi.getByUser).mockResolvedValue(mockActivities);
    
    // Act
    const { result } = renderHook(
      () => useActivities('user-1'),
      { wrapper }
    );
    
    // Assert - Initial Loading State
    expect(result.current.isLoading).toBe(true);
    
    // Assert - Data Loaded
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
      expect(result.current.data).toEqual(mockActivities);
    });
    
    // Assert - API called with correct params
    expect(activityApi.getByUser).toHaveBeenCalledWith('user-1');
  });
  
  it('should_handleError_when_apiFails', async () => {
    // Arrange
    const error = new Error('API Error');
    vi.mocked(activityApi.getByUser).mockRejectedValue(error);
    
    // Act
    const { result } = renderHook(
      () => useActivities('user-1'),
      { wrapper }
    );
    
    // Assert
    await waitFor(() => {
      expect(result.current.error).toBe(error);
      expect(result.current.isLoading).toBe(false);
    });
  });
  
  it('should_notFetch_when_userIdIsEmpty', () => {
    // Arrange & Act
    const { result } = renderHook(
      () => useActivities(''),
      { wrapper }
    );
    
    // Assert
    expect(result.current.isLoading).toBe(false);
    expect(activityApi.getByUser).not.toHaveBeenCalled();
  });
});

describe('useCreateActivity', () => {
  let queryClient: QueryClient;
  
  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        mutations: { retry: false },
      },
    });
    vi.clearAllMocks();
  });
  
  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
  
  // -------------------------
  // Mutation Tests
  // -------------------------
  
  it('should_createActivity_when_mutationCalled', async () => {
    // Arrange
    const newActivity = { 
      id: 'new-1', 
      ecoUserId: 'user-1',
      quantity: 5,
      points: 50 
    };
    vi.mocked(activityApi.create).mockResolvedValue(newActivity);
    
    const { result } = renderHook(
      () => useCreateActivity(),
      { wrapper }
    );
    
    // Act
    result.current.mutate({
      ecoUserId: 'user-1',
      actionDefinitionId: 'action-1',
      quantity: 5,
    });
    
    // Assert
    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
      expect(result.current.data).toEqual(newActivity);
    });
  });
  
  it('should_invalidateQueries_when_mutationSucceeds', async () => {
    // Arrange
    const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');
    vi.mocked(activityApi.create).mockResolvedValue({ id: 'new-1' });
    
    const { result } = renderHook(
      () => useCreateActivity(),
      { wrapper }
    );
    
    // Act
    result.current.mutate({
      ecoUserId: 'user-1',
      actionDefinitionId: 'action-1',
      quantity: 1,
    });
    
    // Assert
    await waitFor(() => {
      expect(invalidateSpy).toHaveBeenCalledWith({
        queryKey: ['activities', 'list'],
      });
    });
  });
  
  it('should_handleError_when_mutationFails', async () => {
    // Arrange
    const error = new Error('Create failed');
    vi.mocked(activityApi.create).mockRejectedValue(error);
    
    const { result } = renderHook(
      () => useCreateActivity(),
      { wrapper }
    );
    
    // Act
    result.current.mutate({
      ecoUserId: 'user-1',
      actionDefinitionId: 'action-1',
      quantity: 1,
    });
    
    // Assert
    await waitFor(() => {
      expect(result.current.isError).toBe(true);
      expect(result.current.error).toBe(error);
    });
  });
});
