// ============================================================
// Dashboard Page Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung einer
// Page-Komponente mit Helmet und Feature Components.
// ============================================================

import { ActivityList } from '@/components/features/activities';
import { RecentChallenges } from '@/components/features/challenges';
import { PointsSummary } from '@/components/features/scoring';
import { Card, CardBody, CardHeader } from '@/components/ui/card';
import { useCurrentUser } from '@/hooks/use-auth';
import { Helmet } from 'react-helmet-async';

// -----------------------------
// Page Component
// -----------------------------
export function DashboardPage() {
  const { data: user } = useCurrentUser();
  
  return (
    <>
      {/* SEO Meta Tags */}
      <Helmet>
        <title>Dashboard | EcoTrack Admin</title>
        <meta name="description" content="EcoTrack Admin Dashboard - Übersicht deiner Nachhaltigkeitsaktivitäten" />
      </Helmet>
      
      <div className="space-y-6">
        {/* Page Header */}
        <header>
          <h1 className="text-2xl font-bold text-gray-900">
            Willkommen zurück, {user?.name}! 🌱
          </h1>
          <p className="text-gray-600 mt-1">
            Hier ist deine Nachhaltigkeits-Übersicht
          </p>
        </header>
        
        {/* Points Summary - Full Width */}
        <PointsSummary userId={user?.id} />
        
        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Recent Activities */}
          <Card>
            <CardHeader className="flex justify-between items-center">
              <h2 className="text-lg font-semibold">Letzte Aktivitäten</h2>
              <a 
                href="/activities" 
                className="text-sm text-green-600 hover:underline"
              >
                Alle anzeigen
              </a>
            </CardHeader>
            <CardBody>
              <ActivityList userId={user?.id} limit={5} />
            </CardBody>
          </Card>
          
          {/* Active Challenges */}
          <Card>
            <CardHeader className="flex justify-between items-center">
              <h2 className="text-lg font-semibold">Aktive Challenges</h2>
              <a 
                href="/challenges" 
                className="text-sm text-green-600 hover:underline"
              >
                Alle anzeigen
              </a>
            </CardHeader>
            <CardBody>
              <RecentChallenges limit={3} />
            </CardBody>
          </Card>
        </div>
      </div>
    </>
  );
}

// Default Export für Lazy Loading
export default DashboardPage;
