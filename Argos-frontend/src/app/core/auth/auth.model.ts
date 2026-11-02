export type ProfileType = 'OPERATIONS_MANAGER' | 'FIELD_AGENT';

export interface CurrentUser {
  firstName: string;
  profile: ProfileType;
}

export const PROFILE_LABELS: Record<ProfileType, string> = {
  OPERATIONS_MANAGER: "Responsable d'exploitation",
  FIELD_AGENT: 'Agent de terrain',
};
