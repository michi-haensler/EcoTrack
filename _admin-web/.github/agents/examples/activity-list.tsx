// ============================================================
// Activity List Feature Component Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung einer
// List Feature Component mit TanStack Query.
// ============================================================

import { Button } from '@/components/ui/button';
import { Card, CardBody } from '@/components/ui/card';
import { Spinner } from '@/components/ui/spinner';
import { useActivities, useDeleteActivity } from '@/hooks/use-activities';
import { cn } from '@/lib/utils';
import type { Activity } from '@/types/activity';

// -----------------------------
// Props Interface
// -----------------------------
interface ActivityListProps {
  /** User ID für Aktivitäten-Abfrage */
  userId: string;
  /** Callback wenn Aktivität ausgewählt wird */
  onSelectActivity?: (activity: Activity) => void;
  /** Maximale Anzahl (optional) */
  limit?: number;
  /** Zusätzliche CSS-Klassen */
  className?: string;
}

// -----------------------------
// Feature Component
// -----------------------------
export function ActivityList({ 
  userId, 
  onSelectActivity,
  limit,
  className 
}: ActivityListProps) {
  // Data Fetching via Hook
  const { data: activities, isLoading, error, refetch } = useActivities(userId);
  const deleteActivity = useDeleteActivity();

  // -----------------------------
  // Loading State
  // -----------------------------
  if (isLoading) {
    return (
      <div className="flex justify-center py-8" role="status">
        <Spinner size="lg" />
        <span className="sr-only">Aktivitäten werden geladen...</span>
      </div>
    );
  }

  // -----------------------------
  // Error State
  // -----------------------------
  if (error) {
    return (
      <Card className="p-6 text-center">
        <p className="text-red-600 mb-4">
          Fehler beim Laden der Aktivitäten: {error.message}
        </p>
        <Button variant="outline" onClick={() => refetch()}>
          Erneut versuchen
        </Button>
      </Card>
    );
  }

  // -----------------------------
  // Empty State
  // -----------------------------
  if (!activities?.length) {
    return (
      <Card className="p-6 text-center">
        <p className="text-gray-500">Noch keine Aktivitäten geloggt.</p>
        <p className="text-sm text-gray-400 mt-2">
          Beginne deine Nachhaltigkeitsreise!
        </p>
      </Card>
    );
  }

  // -----------------------------
  // Data State
  // -----------------------------
  const displayedActivities = limit 
    ? activities.slice(0, limit) 
    : activities;

  return (
    <div className={cn('space-y-4', className)}>
      {displayedActivities.map((activity) => (
        <ActivityCard
          key={activity.id}
          activity={activity}
          onSelect={() => onSelectActivity?.(activity)}
          onDelete={() => deleteActivity.mutate(activity.id)}
          isDeleting={deleteActivity.isPending}
        />
      ))}
    </div>
  );
}

// -----------------------------
// Sub-Component: ActivityCard
// -----------------------------
interface ActivityCardProps {
  activity: Activity;
  onSelect?: () => void;
  onDelete?: () => void;
  isDeleting?: boolean;
}

function ActivityCard({
  activity,
  onSelect,
  onDelete,
  isDeleting,
}: ActivityCardProps) {
  return (
    <Card 
      className="hover:shadow-md transition-shadow cursor-pointer"
      onClick={onSelect}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => e.key === 'Enter' && onSelect?.()}
    >
      <CardBody className="flex justify-between items-start">
        <div className="flex-1">
          {/* Title & Points Badge */}
          <div className="flex items-center gap-2">
            <h3 className="font-semibold">{activity.action.name}</h3>
            <span className="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-800">
              +{activity.points} Punkte
            </span>
          </div>
          
          {/* Description */}
          <p className="text-sm text-gray-600 mt-1">
            {activity.action.description}
          </p>
          
          {/* Metadata */}
          <div className="flex items-center gap-4 mt-3 text-sm text-gray-500">
            <span>Menge: {activity.quantity}</span>
            <time dateTime={activity.loggedAt}>
              {new Date(activity.loggedAt).toLocaleDateString('de-DE')}
            </time>
          </div>
        </div>
        
        {/* Delete Button */}
        {onDelete && (
          <Button
            variant="ghost"
            size="sm"
            onClick={(e) => {
              e.stopPropagation();
              onDelete();
            }}
            isLoading={isDeleting}
            aria-label="Aktivität löschen"
          >
            🗑️
          </Button>
        )}
      </CardBody>
    </Card>
  );
}
