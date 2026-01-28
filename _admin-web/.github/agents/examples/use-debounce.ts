// ============================================================
// useDebounce Hook Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung eines
// Utility Hooks für Debouncing.
// ============================================================

import { useEffect, useState } from 'react';

/**
 * Debounce Hook für verzögerte Wertaktualisierung
 * 
 * Nützlich für Suchfelder, um API-Calls zu reduzieren.
 * Der zurückgegebene Wert wird erst aktualisiert, wenn
 * der Input-Wert für die angegebene Zeit stabil bleibt.
 * 
 * @param value - Der zu debouncende Wert
 * @param delay - Verzögerung in Millisekunden (default: 300)
 * @returns Der debounced Wert
 * 
 * @example
 * ```tsx
 * function SearchInput() {
 *   const [search, setSearch] = useState('');
 *   const debouncedSearch = useDebounce(search, 300);
 *   
 *   // API wird nur aufgerufen wenn User 300ms nicht tippt
 *   const { data } = useSearchResults(debouncedSearch);
 *   
 *   return (
 *     <Input 
 *       value={search} 
 *       onChange={e => setSearch(e.target.value)}
 *       placeholder="Suchen..."
 *     />
 *   );
 * }
 * ```
 */
export function useDebounce<T>(value: T, delay: number = 300): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);
  
  useEffect(() => {
    // Timer setzen für verzögerte Aktualisierung
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    
    // Cleanup: Timer abbrechen wenn value sich ändert
    return () => {
      clearTimeout(timer);
    };
  }, [value, delay]);
  
  return debouncedValue;
}

// ============================================================
// useLocalStorage Hook Beispiel
// ============================================================

/**
 * Hook für persistenten State im localStorage
 * 
 * Synchronisiert automatisch mit localStorage und
 * reagiert auf Änderungen in anderen Tabs.
 * 
 * @param key - Der localStorage Key
 * @param initialValue - Initialer Wert wenn Key nicht existiert
 * @returns [value, setValue] Tuple wie useState
 * 
 * @example
 * ```tsx
 * function ThemeToggle() {
 *   const [theme, setTheme] = useLocalStorage('theme', 'light');
 *   
 *   return (
 *     <Button onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
 *       {theme === 'light' ? '🌙' : '☀️'}
 *     </Button>
 *   );
 * }
 * ```
 */
export function useLocalStorage<T>(
  key: string, 
  initialValue: T
): [T, (value: T | ((prev: T) => T)) => void] {
  // Initialen Wert aus localStorage laden
  const [storedValue, setStoredValue] = useState<T>(() => {
    if (typeof window === 'undefined') {
      return initialValue;
    }
    
    try {
      const item = window.localStorage.getItem(key);
      return item ? (JSON.parse(item) as T) : initialValue;
    } catch (error) {
      console.warn(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  });
  
  // Setter Funktion die auch localStorage aktualisiert
  const setValue = (value: T | ((prev: T) => T)) => {
    try {
      // Funktionale Updates unterstützen
      const valueToStore = value instanceof Function 
        ? value(storedValue) 
        : value;
      
      setStoredValue(valueToStore);
      
      if (typeof window !== 'undefined') {
        window.localStorage.setItem(key, JSON.stringify(valueToStore));
      }
    } catch (error) {
      console.warn(`Error setting localStorage key "${key}":`, error);
    }
  };
  
  // Auf Änderungen in anderen Tabs reagieren
  useEffect(() => {
    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === key && e.newValue) {
        try {
          setStoredValue(JSON.parse(e.newValue));
        } catch {
          // Invalid JSON - ignorieren
        }
      }
    };
    
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, [key]);
  
  return [storedValue, setValue];
}
