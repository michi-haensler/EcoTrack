import { Platform } from 'react-native';

const defaultHost = Platform.OS === 'android' ? '10.0.2.2' : 'localhost';

export const API_BASE_URL = `http://${defaultHost}:8080/api`;
export const KEYCLOAK_BASE_URL = `http://${defaultHost}:8180`;
