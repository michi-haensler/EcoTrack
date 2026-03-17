import { PropsWithChildren, createContext, useContext, useState } from 'react';
import { API_BASE_URL } from '../config/api';
import { mockActivities, type ActivitySummary } from '../components/features/activity-types';
import {
  createActivity,
  fetchActivities,
  fetchCatalog,
  fetchMyProfile,
  fetchMyProgress,
  loginStudent,
  logout,
  registerStudent,
  requestPasswordReset,
} from '../services/ecotrack-api';
import { getErrorMessage } from '../services/api-client';
import {
  ActionDefinitionResponse,
  ActivityEntryResponse,
  AuthUserInfo,
  EcoUserProfileResponse,
  LoginRequest,
  ProgressSnapshotResponse,
  RegistrationRequest,
  RegistrationResponse,
} from '../types/ecotrack-api';
import { formatActivityTimestamp, formatQuantityLabel } from '../utils/ecotrack-formatters';

interface EcoTrackAppContextValue {
  apiBaseUrl: string;
  isAuthenticated: boolean;
  user: AuthUserInfo | null;
  profile: EcoUserProfileResponse | null;
  progress: ProgressSnapshotResponse | null;
  activities: ActivitySummary[];
  catalog: ActionDefinitionResponse[];
  isAuthBusy: boolean;
  isDataBusy: boolean;
  isCreatingActivity: boolean;
  authError: string | null;
  dataError: string | null;
  signIn: (request: LoginRequest) => Promise<void>;
  registerUser: (request: RegistrationRequest) => Promise<RegistrationResponse>;
  requestPasswordResetEmail: (email: string) => Promise<void>;
  refreshData: () => Promise<void>;
  createActivityEntry: (actionDefinitionId: string, quantity?: number) => Promise<void>;
  signOut: () => Promise<void>;
}

const demoProfile: EcoUserProfileResponse = {
  ecoUserId: 'demo-eco-user',
  name: {
    firstName: 'Alex',
    lastName: 'Green',
    displayName: 'Alex Green',
  },
  email: 'alex.green@schueler.htl-leoben.at',
  classId: null,
  className: '4BHIF',
  schoolId: null,
  schoolName: 'HTL Leoben',
  totalPoints: 120,
  level: 'JUNGBAUM',
  role: 'SCHUELER',
};

const demoProgress: ProgressSnapshotResponse = {
  ecoUserId: 'demo-eco-user',
  totalPoints: 120,
  currentLevel: 'JUNGBAUM',
  pointsToNextLevel: 130,
  progressPercentage: 48,
  milestones: [],
  treeVisualization: {
    level: 'JUNGBAUM',
    assetName: 'jungbaum',
    growthPercentage: 48,
  },
};

const defaultContextValue: EcoTrackAppContextValue = {
  apiBaseUrl: API_BASE_URL,
  isAuthenticated: false,
  user: {
    userId: 'demo-user',
    ecoUserId: 'demo-eco-user',
    email: demoProfile.email,
    firstName: demoProfile.name.firstName,
    lastName: demoProfile.name.lastName,
    role: 'SCHUELER',
  },
  profile: demoProfile,
  progress: demoProgress,
  activities: mockActivities,
  catalog: [],
  isAuthBusy: false,
  isDataBusy: false,
  isCreatingActivity: false,
  authError: null,
  dataError: null,
  signIn: async () => undefined,
  registerUser: async () => ({
    userId: 'demo-user',
    email: demoProfile.email,
    message: 'Demo',
  }),
  requestPasswordResetEmail: async () => undefined,
  refreshData: async () => undefined,
  createActivityEntry: async () => undefined,
  signOut: async () => undefined,
};

const EcoTrackAppContext = createContext<EcoTrackAppContextValue>(defaultContextValue);

function mapActivity(entry: ActivityEntryResponse): ActivitySummary {
  return {
    id: entry.activityEntryId,
    name: entry.actionName,
    points: entry.points,
    category: entry.category,
    timestamp: formatActivityTimestamp(entry.timestamp),
    impact: `${formatQuantityLabel(entry.quantity, entry.unit)} erfasst`,
  };
}

export function EcoTrackAppProvider({ children }: PropsWithChildren) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [refreshToken, setRefreshToken] = useState<string | null>(null);
  const [user, setUser] = useState<AuthUserInfo | null>(null);
  const [profile, setProfile] = useState<EcoUserProfileResponse | null>(null);
  const [progress, setProgress] = useState<ProgressSnapshotResponse | null>(null);
  const [activities, setActivities] = useState<ActivitySummary[]>([]);
  const [catalog, setCatalog] = useState<ActionDefinitionResponse[]>([]);
  const [isAuthBusy, setIsAuthBusy] = useState(false);
  const [isDataBusy, setIsDataBusy] = useState(false);
  const [isCreatingActivity, setIsCreatingActivity] = useState(false);
  const [authError, setAuthError] = useState<string | null>(null);
  const [dataError, setDataError] = useState<string | null>(null);

  async function loadAuthenticatedData(token: string) {
    setIsDataBusy(true);
    setDataError(null);

    try {
      const [nextProfile, nextProgress, nextActivities, nextCatalog] = await Promise.all([
        fetchMyProfile(token),
        fetchMyProgress(token),
        fetchActivities(token),
        fetchCatalog(token),
      ]);

      setProfile(nextProfile);
      setProgress(nextProgress);
      setActivities(nextActivities.content.map(mapActivity));
      setCatalog(nextCatalog.filter(item => item.active));
    } catch (error) {
      setDataError(getErrorMessage(error));
      throw error;
    } finally {
      setIsDataBusy(false);
    }
  }

  async function signIn(request: LoginRequest) {
    setIsAuthBusy(true);
    setAuthError(null);

    try {
      const response = await loginStudent({
        email: request.email.trim().toLowerCase(),
        password: request.password,
      });

      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);
      setUser(response.user);
      await loadAuthenticatedData(response.accessToken);
    } catch (error) {
      setAccessToken(null);
      setRefreshToken(null);
      setUser(null);
      setProfile(null);
      setProgress(null);
      setActivities([]);
      setCatalog([]);
      setAuthError(getErrorMessage(error));
      throw error;
    } finally {
      setIsAuthBusy(false);
    }
  }

  async function registerUser(request: RegistrationRequest) {
    setIsAuthBusy(true);
    setAuthError(null);

    try {
      return await registerStudent({
        ...request,
        email: request.email.trim().toLowerCase(),
      });
    } catch (error) {
      setAuthError(getErrorMessage(error));
      throw error;
    } finally {
      setIsAuthBusy(false);
    }
  }

  async function requestPasswordResetEmail(email: string) {
    setIsAuthBusy(true);
    setAuthError(null);

    try {
      await requestPasswordReset({
        email: email.trim().toLowerCase(),
      });
    } catch (error) {
      setAuthError(getErrorMessage(error));
      throw error;
    } finally {
      setIsAuthBusy(false);
    }
  }

  async function refreshData() {
    if (!accessToken) {
      return;
    }

    await loadAuthenticatedData(accessToken);
  }

  async function createActivityEntry(actionDefinitionId: string, quantity = 1) {
    if (!accessToken) {
      return;
    }

    setIsCreatingActivity(true);
    setDataError(null);

    try {
      await createActivity(accessToken, {
        actionDefinitionId,
        quantity,
      });

      const [nextProgress, nextActivities] = await Promise.all([
        fetchMyProgress(accessToken),
        fetchActivities(accessToken),
      ]);

      setProgress(nextProgress);
      setActivities(nextActivities.content.map(mapActivity));
    } catch (error) {
      setDataError(getErrorMessage(error));
      throw error;
    } finally {
      setIsCreatingActivity(false);
    }
  }

  async function signOut() {
    try {
      if (accessToken) {
        await logout(refreshToken, accessToken);
      }
    } catch {
      // Lokales Logout soll auch dann funktionieren, wenn das Backend nicht mehr erreichbar ist.
    } finally {
      setAccessToken(null);
      setRefreshToken(null);
      setUser(null);
      setProfile(null);
      setProgress(null);
      setActivities([]);
      setCatalog([]);
      setAuthError(null);
      setDataError(null);
    }
  }

  return (
    <EcoTrackAppContext.Provider
      value={{
        apiBaseUrl: API_BASE_URL,
        isAuthenticated: Boolean(accessToken),
        user,
        profile,
        progress,
        activities,
        catalog,
        isAuthBusy,
        isDataBusy,
        isCreatingActivity,
        authError,
        dataError,
        signIn,
        registerUser,
        requestPasswordResetEmail,
        refreshData,
        createActivityEntry,
        signOut,
      }}
    >
      {children}
    </EcoTrackAppContext.Provider>
  );
}

export function useEcoTrackApp() {
  return useContext(EcoTrackAppContext);
}
