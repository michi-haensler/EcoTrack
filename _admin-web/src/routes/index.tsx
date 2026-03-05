/**
 * Routing-Konfiguration (MS-01)
 *
 * Routen:
 * - / → Redirect zu /dashboard (wenn eingeloggt) oder /login
 * - /login → LoginPage
 * - /dashboard → DashboardPage (geschützt)
 */

import { ProtectedRoute } from '@/components/common/ProtectedRoute';
import { DashboardPage } from '@/pages/DashboardPage';
import { LoginPage } from '@/pages/LoginPage';
import { createBrowserRouter, Navigate } from 'react-router-dom';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/dashboard" replace />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: '/dashboard',
        element: <DashboardPage />,
      },
    ],
  },
  {
    // Keycloak-Callback (Deep-Link nach Passwortänderung)
    path: '/callback',
    element: <Navigate to="/login" replace />,
  },
  {
    path: '*',
    element: <Navigate to="/login" replace />,
  },
]);
