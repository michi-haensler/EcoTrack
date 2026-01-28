// ============================================================
// Button Component Test Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Component Tests mit Vitest und React Testing Library.
// ============================================================

import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { Button } from './button';

describe('Button', () => {
  // -------------------------
  // Rendering Tests
  // -------------------------
  
  it('should_renderChildren_when_provided', () => {
    // Arrange & Act
    render(<Button>Click me</Button>);
    
    // Assert
    expect(screen.getByRole('button')).toHaveTextContent('Click me');
  });
  
  it('should_applyPrimaryVariant_when_default', () => {
    // Arrange & Act
    render(<Button>Primary</Button>);
    
    // Assert
    expect(screen.getByRole('button')).toHaveClass('bg-green-600');
  });
  
  it('should_applySecondaryVariant_when_specified', () => {
    // Arrange & Act
    render(<Button variant="secondary">Secondary</Button>);
    
    // Assert
    expect(screen.getByRole('button')).toHaveClass('bg-gray-200');
  });
  
  it('should_applySize_when_specified', () => {
    // Arrange & Act
    render(<Button size="lg">Large</Button>);
    
    // Assert
    expect(screen.getByRole('button')).toHaveClass('h-12');
  });

  // -------------------------
  // Interaction Tests
  // -------------------------
  
  it('should_callOnClick_when_clicked', async () => {
    // Arrange
    const user = userEvent.setup();
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Click</Button>);
    
    // Act
    await user.click(screen.getByRole('button'));
    
    // Assert
    expect(onClick).toHaveBeenCalledOnce();
  });
  
  it('should_notCallOnClick_when_disabled', async () => {
    // Arrange
    const user = userEvent.setup();
    const onClick = vi.fn();
    render(<Button onClick={onClick} disabled>Disabled</Button>);
    
    // Act
    await user.click(screen.getByRole('button'));
    
    // Assert
    expect(onClick).not.toHaveBeenCalled();
  });

  // -------------------------
  // Loading State Tests
  // -------------------------
  
  it('should_showSpinner_when_loading', () => {
    // Arrange & Act
    render(<Button isLoading>Loading</Button>);
    
    // Assert
    expect(screen.getByRole('button')).toHaveAttribute('aria-busy', 'true');
  });
  
  it('should_beDisabled_when_loading', () => {
    // Arrange & Act
    render(<Button isLoading>Loading</Button>);
    
    // Assert
    expect(screen.getByRole('button')).toBeDisabled();
  });
  
  it('should_notCallOnClick_when_loading', async () => {
    // Arrange
    const user = userEvent.setup();
    const onClick = vi.fn();
    render(<Button onClick={onClick} isLoading>Loading</Button>);
    
    // Act
    await user.click(screen.getByRole('button'));
    
    // Assert
    expect(onClick).not.toHaveBeenCalled();
  });

  // -------------------------
  // Accessibility Tests
  // -------------------------
  
  it('should_haveFocusStyles_when_focused', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<Button>Focus me</Button>);
    
    // Act
    await user.tab();
    
    // Assert
    expect(screen.getByRole('button')).toHaveFocus();
  });
  
  it('should_haveAccessibleName_when_rendered', () => {
    // Arrange & Act
    render(<Button>Accessible</Button>);
    
    // Assert
    expect(screen.getByRole('button', { name: 'Accessible' })).toBeInTheDocument();
  });
});
