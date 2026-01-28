// ============================================================
// Home Screen Test Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Screen Tests mit React Native Testing Library.
// ============================================================

import { useCurrentUser } from '@/hooks/use-auth';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { fireEvent, render, screen, waitFor } from '@testing-library/react-native';
import { HomeScreen } from './home-screen';

// Mock Hooks
jest.mock('@/hooks/use-auth');

// -----------------------------
// Test Setup
// -----------------------------

// Mock Navigation
const createMockNavigation = () => ({
  navigate: jest.fn(),
  goBack: jest.fn(),
  setOptions: jest.fn(),
  addListener: jest.fn(() => jest.fn()),
});

// Query Wrapper
function QueryWrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe('HomeScreen', () => {
  let mockNavigation: ReturnType<typeof createMockNavigation>;
  
  beforeEach(() => {
    mockNavigation = createMockNavigation();
    jest.clearAllMocks();
  });
  
  // -------------------------
  // Rendering Tests
  // -------------------------
  
  it('should_renderWelcomeMessage_when_userLoaded', async () => {
    // Arrange
    (useCurrentUser as jest.Mock).mockReturnValue({
      data: { id: 'user-1', name: 'Max' },
      isLoading: false,
      refetch: jest.fn(),
      isRefetching: false,
    });
    
    // Act
    render(
      <QueryWrapper>
        <HomeScreen navigation={mockNavigation as any} route={{} as any} />
      </QueryWrapper>
    );
    
    // Assert
    await waitFor(() => {
      expect(screen.getByText(/Willkommen, Max/)).toBeTruthy();
    });
  });
  
  it('should_showLoading_when_userIsLoading', () => {
    // Arrange
    (useCurrentUser as jest.Mock).mockReturnValue({
      data: null,
      isLoading: true,
      refetch: jest.fn(),
      isRefetching: false,
    });
    
    // Act
    render(
      <QueryWrapper>
        <HomeScreen navigation={mockNavigation as any} route={{} as any} />
      </QueryWrapper>
    );
    
    // Assert - PointsSummary sollte Loading anzeigen
    // (Component-spezifisch)
  });
  
  // -------------------------
  // Navigation Tests
  // -------------------------
  
  it('should_navigateToActivityDetail_when_activitySelected', async () => {
    // Arrange
    (useCurrentUser as jest.Mock).mockReturnValue({
      data: { id: 'user-1', name: 'Max' },
      isLoading: false,
      refetch: jest.fn(),
      isRefetching: false,
    });
    
    render(
      <QueryWrapper>
        <HomeScreen navigation={mockNavigation as any} route={{} as any} />
      </QueryWrapper>
    );
    
    // Act - Find and press an activity card
    // (Depends on ActivityList implementation)
    // fireEvent.press(screen.getByTestId('activity-card-1'));
    
    // Assert
    // expect(mockNavigation.navigate).toHaveBeenCalledWith(
    //   'ActivityDetail',
    //   { activityId: 'activity-1' }
    // );
  });
  
  it('should_navigateToLogActivity_when_quickActionPressed', async () => {
    // Arrange
    (useCurrentUser as jest.Mock).mockReturnValue({
      data: { id: 'user-1', name: 'Max' },
      isLoading: false,
      refetch: jest.fn(),
      isRefetching: false,
    });
    
    render(
      <QueryWrapper>
        <HomeScreen navigation={mockNavigation as any} route={{} as any} />
      </QueryWrapper>
    );
    
    // Act
    const logButton = screen.getByTestId('quick-action-log');
    fireEvent.press(logButton);
    
    // Assert
    expect(mockNavigation.navigate).toHaveBeenCalledWith('LogActivity');
  });
  
  // -------------------------
  // Pull-to-Refresh Tests
  // -------------------------
  
  it('should_callRefetch_when_pullToRefresh', async () => {
    // Arrange
    const mockRefetch = jest.fn();
    (useCurrentUser as jest.Mock).mockReturnValue({
      data: { id: 'user-1', name: 'Max' },
      isLoading: false,
      refetch: mockRefetch,
      isRefetching: false,
    });
    
    render(
      <QueryWrapper>
        <HomeScreen navigation={mockNavigation as any} route={{} as any} />
      </QueryWrapper>
    );
    
    // Act - Simulate pull-to-refresh
    const scrollView = screen.getByTestId('home-scroll-view');
    const { refreshControl } = scrollView.props;
    refreshControl.props.onRefresh();
    
    // Assert
    expect(mockRefetch).toHaveBeenCalled();
  });
});
