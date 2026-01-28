// ============================================================
// Button Component Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung einer
// UI-Komponente mit cva (class-variance-authority).
// ============================================================

import { cn } from '@/lib/utils';
import { cva, type VariantProps } from 'class-variance-authority';
import { forwardRef } from 'react';
import { Spinner } from './spinner';

// -----------------------------
// Variants Definition mit cva
// -----------------------------
const buttonVariants = cva(
  // Base styles (immer angewendet)
  'inline-flex items-center justify-center rounded-md font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50',
  {
    variants: {
      variant: {
        primary: 'bg-green-600 text-white hover:bg-green-700 focus-visible:ring-green-500',
        secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300 focus-visible:ring-gray-500',
        outline: 'border border-gray-300 bg-white hover:bg-gray-50 focus-visible:ring-gray-500',
        ghost: 'hover:bg-gray-100 focus-visible:ring-gray-500',
        danger: 'bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-500',
      },
      size: {
        sm: 'h-8 px-3 text-sm',
        md: 'h-10 px-4 text-base',
        lg: 'h-12 px-6 text-lg',
      },
    },
    defaultVariants: {
      variant: 'primary',
      size: 'md',
    },
  }
);

// -----------------------------
// Props Interface
// -----------------------------
export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  /** Zeigt einen Loading-Spinner an */
  isLoading?: boolean;
}

// -----------------------------
// Component mit forwardRef
// -----------------------------
export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant, size, className, isLoading, disabled, children, ...props }, ref) => {
    return (
      <button
        ref={ref}
        className={cn(buttonVariants({ variant, size }), className)}
        disabled={disabled || isLoading}
        aria-busy={isLoading}
        {...props}
      >
        {isLoading && (
          <Spinner 
            className="mr-2 h-4 w-4" 
            aria-hidden="true"
          />
        )}
        {children}
      </button>
    );
  }
);

Button.displayName = 'Button';

// -----------------------------
// Verwendungsbeispiele
// -----------------------------
/*
<Button>Primary Button</Button>
<Button variant="secondary">Secondary</Button>
<Button variant="outline" size="sm">Small Outline</Button>
<Button variant="danger" isLoading>Deleting...</Button>
<Button disabled>Disabled</Button>
*/
