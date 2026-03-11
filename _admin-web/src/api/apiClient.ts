/**
 * Axios-Client — Zentrale HTTP-Konfiguration
 *
 * Verwendet den Vite-Proxy (/api → Backend) im Dev-Modus.
 * In Produktion wird VITE_API_BASE_URL genutzt.
 */

import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15_000,
});

/**
 * Request-Interceptor: Fügt den Authorization-Header hinzu,
 * wenn ein Token im sessionStorage vorhanden ist.
 */
apiClient.interceptors.request.use((config) => {
  const raw = sessionStorage.getItem('ecotrack-admin-auth');
  if (raw) {
    try {
      const parsed = JSON.parse(raw);
      const token = parsed?.state?.session?.accessToken;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch {
      // Ungültiger sessionStorage-Eintrag — ignorieren
    }
  }
  return config;
});

export default apiClient;
