/**
 * DashboardPage — Platzhalter (MS-01)
 *
 * Wird nach erfolgreichem Login angezeigt.
 * Volle Implementierung in MS-02 (M-US-6 Dashboard für Lehrer).
 */

import { useAuthStore } from '@/stores/authStore';
import { useNavigate } from 'react-router-dom';

export function DashboardPage() {
  const navigate = useNavigate();
  const user = useAuthStore((s) => s.session?.user);
  const logout = useAuthStore((s) => s.logout);

  async function handleLogout() {
    await logout();
    navigate('/login', { replace: true });
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-2 font-bold text-primary-700 text-lg">
          <span aria-hidden="true">🌿</span> EcoTrack Admin
        </div>
        <div className="flex items-center gap-4">
          {user && (
            <span className="text-sm text-gray-600">
              {user.firstName} {user.lastName}
              <span className="ml-1 text-xs text-gray-400">({user.role})</span>
            </span>
          )}
          <button
            onClick={handleLogout}
            className="text-sm text-red-600 hover:text-red-700 font-medium transition"
          >
            Abmelden
          </button>
        </div>
      </header>

      {/* Inhalt */}
      <main className="max-w-4xl mx-auto px-6 py-12 text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">
          Willkommen, {user?.firstName ?? ''}!
        </h1>
        <p className="text-gray-500">
          Das Dashboard wird in einem späteren Meilenstein implementiert.
        </p>
      </main>
    </div>
  );
}
