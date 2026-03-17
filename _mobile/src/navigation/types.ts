export type RootStackParamList = {
  Welcome: undefined;
  Login: undefined;
  Register: undefined;
  ResetPassword: undefined;
  EmailStatus: {
    email: string;
    mode: 'verify' | 'reset';
  };
  Main: undefined;
  ActivityDetail: { activityId: string };
};

export type MainTabParamList = {
  Home: undefined;
  Activities: undefined;
  Profile: undefined;
};
