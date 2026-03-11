/**
 * ProtectedRoute — schützt alle Routen hinter dem Login
 *
 * Nicht authentifizierte Nutzer werden zur /login-Seite weitergeleitet.
 */

import { isAuthenticated, useAuthStore } from '@/stores/authStore';
import { Navigate, Outlet } from 'react-router-dom';

export function ProtectedRoute() {
  const authenticated = useAuthStore(isAuthenticated);

  if (!authenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
