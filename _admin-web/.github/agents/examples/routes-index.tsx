// ============================================================
// Routes Configuration Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung der
// Route-Konfiguration mit React Router v6.
// ============================================================

import { ErrorPage } from '@/pages/error-page';
import { LoadingPage } from '@/pages/loading-page';
import { lazy, Suspense } from 'react';
import {
    createBrowserRouter,
    Navigate,
    Outlet,
    RouterProvider
} from 'react-router-dom';
import { AuthLayout } from './layouts/auth-layout';
import { DashboardLayout } from './layouts/dashboard-layout';
import { ProtectedRoute } from './protected-route';

// -----------------------------
// Lazy-loaded Pages
// -----------------------------
// Code Splitting: Pages werden erst bei Bedarf geladen
const DashboardPage = lazy(() => import('@/pages/dashboard'));
const UsersPage = lazy(() => import('@/pages/users'));
const UserDetailPage = lazy(() => import('@/pages/users/user-detail-page'));
const ChallengesPage = lazy(() => import('@/pages/challenges'));
const ActivitiesPage = lazy(() => import('@/pages/activities'));
const LoginPage = lazy(() => import('@/pages/auth/login-page'));

// -----------------------------
// Router Configuration
// -----------------------------
const router = createBrowserRouter([
  // Protected Routes (Dashboard)
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <DashboardLayout>
          <Suspense fallback={<LoadingPage />}>
            <Outlet />
          </Suspense>
        </DashboardLayout>
      </ProtectedRoute>
    ),
    errorElement: <ErrorPage />,
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'users',
        element: <UsersPage />,
      },
      {
        path: 'users/:userId',
        element: <UserDetailPage />,
      },
      {
        path: 'challenges',
        element: <ChallengesPage />,
      },
      {
        path: 'activities',
        element: <ActivitiesPage />,
      },
    ],
  },
  
  // Auth Routes (Public)
  {
    path: '/auth',
    element: (
      <AuthLayout>
        <Suspense fallback={<LoadingPage />}>
          <Outlet />
        </Suspense>
      </AuthLayout>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="login" replace />,
      },
      {
        path: 'login',
        element: <LoginPage />,
      },
    ],
  },
  
  // Catch-all: 404
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);

// -----------------------------
// Router Provider Component
// -----------------------------
export function AppRoutes() {
  return <RouterProvider router={router} />;
}
