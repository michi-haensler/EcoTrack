// ============================================================
// Protected Route Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung eines
// Route Guards für authentifizierte Routen.
// ============================================================

import { useAuth } from '@/hooks/use-auth';
import { LoadingPage } from '@/pages/loading-page';
import { Navigate, useLocation } from 'react-router-dom';

// -----------------------------
// Props Interface
// -----------------------------
interface ProtectedRouteProps {
  children: React.ReactNode;
  /** Optional: Erforderliche Rollen für Zugriff */
  requiredRoles?: string[];
}

// -----------------------------
// Protected Route Component
// -----------------------------
export function ProtectedRoute({ 
  children, 
  requiredRoles 
}: ProtectedRouteProps) {
  const { user, isLoading, isAuthenticated } = useAuth();
  const location = useLocation();

  // Während Auth-Status geladen wird: Loading anzeigen
  if (isLoading) {
    return <LoadingPage />;
  }

  // Nicht authentifiziert: Redirect zu Login
  if (!isAuthenticated) {
    // Location speichern für Redirect nach Login
    return (
      <Navigate 
        to="/auth/login" 
        state={{ from: location }} 
        replace 
      />
    );
  }

  // Rollen-Check (wenn requiredRoles angegeben)
  if (requiredRoles && requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.some(
      role => user?.roles?.includes(role)
    );
    
    if (!hasRequiredRole) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  // Alles OK: Children rendern
  return <>{children}</>;
}

// -----------------------------
// Verwendungsbeispiele
// -----------------------------
/*
// Einfacher Auth-Check
<ProtectedRoute>
  <DashboardPage />
</ProtectedRoute>

// Mit Rollen-Check
<ProtectedRoute requiredRoles={['ADMIN', 'TEACHER']}>
  <AdminPage />
</ProtectedRoute>
*/
