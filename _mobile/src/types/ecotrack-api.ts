export type EcoRole = 'ADMIN' | 'LEHRER' | 'SCHUELER';

export type EcoLevel = 'SETZLING' | 'JUNGBAUM' | 'BAUM' | 'ALTBAUM' | 'LEGEND';

export type EcoCategory =
  | 'MOBILITAET'
  | 'RECYCLING'
  | 'ERNAEHRUNG'
  | 'ENERGIE'
  | string;

export type EcoUnit = 'KM' | 'STUECK' | 'MINUTEN' | string;

export interface ApiErrorResponse {
  code: string;
  message: string;
  timestamp?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegistrationRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  classId?: string | null;
}

export interface PasswordResetRequest {
  email: string;
}

export interface AuthUserInfo {
  userId: string;
  ecoUserId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: EcoRole;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: AuthUserInfo;
}

export interface RegistrationResponse {
  userId: string;
  email: string;
  message: string;
}

export interface EcoUserProfileResponse {
  ecoUserId: string;
  name: {
    firstName: string;
    lastName: string;
    displayName: string;
  };
  email: string;
  classId: string | null;
  className: string | null;
  schoolId: string | null;
  schoolName: string | null;
  totalPoints: number;
  level: EcoLevel;
  role: EcoRole;
}

export interface MilestoneResponse {
  milestoneId: string;
  name: string;
  requiredPoints: number;
  reached: boolean;
  reachedAt: string | null;
}

export interface TreeVisualizationResponse {
  level: EcoLevel;
  assetName: string;
  growthPercentage: number;
}

export interface ProgressSnapshotResponse {
  ecoUserId: string;
  totalPoints: number;
  currentLevel: EcoLevel;
  pointsToNextLevel: number;
  progressPercentage: number;
  milestones: MilestoneResponse[];
  treeVisualization: TreeVisualizationResponse;
}

export interface ActionDefinitionResponse {
  actionDefinitionId: string;
  name: string;
  description: string;
  category: EcoCategory;
  unit: EcoUnit;
  basePoints: number;
  active: boolean;
}

export interface CreateActivityRequest {
  actionDefinitionId: string;
  quantity: number;
  date?: string;
}

export interface ActivityEntryResponse {
  activityEntryId: string;
  actionDefinitionId: string;
  actionName: string;
  category: EcoCategory;
  quantity: number;
  unit: EcoUnit;
  points: number;
  timestamp: string;
  source: string;
}

export interface ActivityPageResponse {
  content: ActivityEntryResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
