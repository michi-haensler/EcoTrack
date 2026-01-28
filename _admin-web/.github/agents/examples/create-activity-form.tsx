// ============================================================
// Create Activity Form Feature Component Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung einer
// Form Feature Component mit React Hook Form + Zod.
// ============================================================

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select } from '@/components/ui/select';
import { useActionDefinitions } from '@/hooks/use-action-definitions';
import { useCreateActivity } from '@/hooks/use-activities';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

// -----------------------------
// Validation Schema (Zod)
// -----------------------------
const createActivitySchema = z.object({
  actionDefinitionId: z
    .string()
    .min(1, 'Bitte wähle eine Aktion'),
  quantity: z
    .number({ invalid_type_error: 'Bitte eine Zahl eingeben' })
    .min(1, 'Mindestens 1')
    .max(100, 'Maximal 100'),
  notes: z
    .string()
    .max(500, 'Maximal 500 Zeichen')
    .optional(),
});

type CreateActivityFormData = z.infer<typeof createActivitySchema>;

// -----------------------------
// Props Interface
// -----------------------------
interface CreateActivityFormProps {
  /** User ID für die neue Aktivität */
  userId: string;
  /** Callback nach erfolgreicher Erstellung */
  onSuccess?: () => void;
  /** Callback für Abbrechen */
  onCancel?: () => void;
}

// -----------------------------
// Feature Component
// -----------------------------
export function CreateActivityForm({ 
  userId, 
  onSuccess, 
  onCancel 
}: CreateActivityFormProps) {
  // Hooks für Daten und Mutation
  const { data: actions, isLoading: actionsLoading } = useActionDefinitions();
  const createActivity = useCreateActivity();
  
  // React Hook Form Setup
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<CreateActivityFormData>({
    resolver: zodResolver(createActivitySchema),
    defaultValues: {
      quantity: 1,
      notes: '',
    },
  });

  // -----------------------------
  // Form Submit Handler
  // -----------------------------
  const onSubmit = async (data: CreateActivityFormData) => {
    try {
      await createActivity.mutateAsync({
        ecoUserId: userId,
        ...data,
      });
      
      // Form zurücksetzen und Success-Callback
      reset();
      onSuccess?.();
    } catch (error) {
      // Error wird von TanStack Query gehandled
      console.error('Failed to create activity:', error);
    }
  };

  // -----------------------------
  // Render
  // -----------------------------
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {/* Action Select */}
      <Select
        label="Aktion"
        {...register('actionDefinitionId')}
        error={errors.actionDefinitionId?.message}
        disabled={actionsLoading}
      >
        <option value="">Aktion wählen...</option>
        {actions?.map((action) => (
          <option key={action.id} value={action.id}>
            {action.name} (+{action.points} Punkte)
          </option>
        ))}
      </Select>

      {/* Quantity Input */}
      <Input
        type="number"
        label="Menge"
        {...register('quantity', { valueAsNumber: true })}
        error={errors.quantity?.message}
        min={1}
        max={100}
      />

      {/* Notes Input (optional) */}
      <Input
        label="Notizen (optional)"
        {...register('notes')}
        error={errors.notes?.message}
        placeholder="z.B. Mit dem Rad zur Schule gefahren"
      />

      {/* Action Buttons */}
      <div className="flex gap-2 justify-end pt-4">
        {onCancel && (
          <Button 
            type="button" 
            variant="ghost" 
            onClick={onCancel}
          >
            Abbrechen
          </Button>
        )}
        <Button 
          type="submit" 
          isLoading={isSubmitting || createActivity.isPending}
        >
          Aktivität loggen
        </Button>
      </div>
      
      {/* Error Message */}
      {createActivity.isError && (
        <p className="text-sm text-red-600" role="alert">
          Fehler: {createActivity.error.message}
        </p>
      )}
    </form>
  );
}
