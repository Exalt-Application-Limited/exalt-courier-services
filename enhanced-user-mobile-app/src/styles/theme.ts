import { MD3LightTheme, MD3DarkTheme } from 'react-native-paper';
import { DefaultTheme as NavigationLightTheme, DarkTheme as NavigationDarkTheme } from '@react-navigation/native';

// Exalt Courier Brand Colors
const brandColors = {
  primary: '#1976d2', // Exalt Courier Blue
  primaryVariant: '#1565c0',
  secondary: '#ff6b35', // Exalt Courier Orange
  secondaryVariant: '#e64a19',
  accent: '#4caf50', // Success Green
  background: '#ffffff',
  surface: '#f8fafc',
  error: '#f44336',
  warning: '#ff9800',
  info: '#2196f3',
  success: '#4caf50',
  
  // Neutral colors
  text: {
    primary: '#1a1a1a',
    secondary: '#666666',
    disabled: '#9e9e9e',
    hint: '#bdbdbd',
  },
  
  // Gray scale
  gray: {
    50: '#fafafa',
    100: '#f5f5f5',
    200: '#eeeeee',
    300: '#e0e0e0',
    400: '#bdbdbd',
    500: '#9e9e9e',
    600: '#757575',
    700: '#616161',
    800: '#424242',
    900: '#212121',
  },
};

// Light Theme
export const lightTheme = {
  ...MD3LightTheme,
  colors: {
    ...MD3LightTheme.colors,
    primary: brandColors.primary,
    primaryContainer: '#e3f2fd',
    secondary: brandColors.secondary,
    secondaryContainer: '#fff3e0',
    tertiary: brandColors.accent,
    tertiaryContainer: '#e8f5e8',
    surface: brandColors.surface,
    surfaceVariant: '#f5f5f5',
    background: brandColors.background,
    error: brandColors.error,
    errorContainer: '#ffebee',
    onPrimary: '#ffffff',
    onSecondary: '#ffffff',
    onTertiary: '#ffffff',
    onSurface: brandColors.text.primary,
    onSurfaceVariant: brandColors.text.secondary,
    onBackground: brandColors.text.primary,
    onError: '#ffffff',
    outline: brandColors.gray[300],
    outlineVariant: brandColors.gray[200],
    inverseSurface: brandColors.gray[800],
    inverseOnSurface: '#ffffff',
    inversePrimary: '#90caf9',
    shadow: '#000000',
    scrim: '#000000',
    surfaceDisabled: brandColors.gray[100],
    onSurfaceDisabled: brandColors.gray[400],
    backdrop: 'rgba(0, 0, 0, 0.5)',
  },
};

// Dark Theme
export const darkTheme = {
  ...MD3DarkTheme,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#90caf9',
    primaryContainer: '#1565c0',
    secondary: '#ffab40',
    secondaryContainer: '#e64a19',
    tertiary: '#81c784',
    tertiaryContainer: '#388e3c',
    surface: '#121212',
    surfaceVariant: '#1e1e1e',
    background: '#000000',
    error: '#ff5252',
    errorContainer: '#b71c1c',
    onPrimary: '#000000',
    onSecondary: '#000000',
    onTertiary: '#000000',
    onSurface: '#ffffff',
    onSurfaceVariant: '#cccccc',
    onBackground: '#ffffff',
    onError: '#000000',
    outline: '#666666',
    outlineVariant: '#444444',
    inverseSurface: '#f5f5f5',
    inverseOnSurface: '#1a1a1a',
    inversePrimary: brandColors.primary,
    shadow: '#000000',
    scrim: '#000000',
    surfaceDisabled: '#2c2c2c',
    onSurfaceDisabled: '#666666',
    backdrop: 'rgba(0, 0, 0, 0.7)',
  },
};

// Navigation Theme Light
export const navigationLightTheme = {
  ...NavigationLightTheme,
  colors: {
    ...NavigationLightTheme.colors,
    primary: brandColors.primary,
    background: brandColors.background,
    card: brandColors.surface,
    text: brandColors.text.primary,
    border: brandColors.gray[200],
    notification: brandColors.secondary,
  },
};

// Navigation Theme Dark
export const navigationDarkTheme = {
  ...NavigationDarkTheme,
  colors: {
    ...NavigationDarkTheme.colors,
    primary: '#90caf9',
    background: '#000000',
    card: '#121212',
    text: '#ffffff',
    border: '#333333',
    notification: '#ffab40',
  },
};

// Typography
export const typography = {
  fontFamily: {
    regular: 'Inter-Regular',
    medium: 'Inter-Medium',
    semiBold: 'Inter-SemiBold',
    bold: 'Inter-Bold',
  },
  fontSize: {
    xs: 12,
    sm: 14,
    base: 16,
    lg: 18,
    xl: 20,
    '2xl': 24,
    '3xl': 30,
    '4xl': 36,
    '5xl': 48,
  },
  lineHeight: {
    xs: 16,
    sm: 20,
    base: 24,
    lg: 28,
    xl: 32,
    '2xl': 36,
    '3xl': 42,
    '4xl': 48,
    '5xl': 60,
  },
};

// Spacing
export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  '2xl': 48,
  '3xl': 64,
  '4xl': 96,
};

// Border Radius
export const borderRadius = {
  none: 0,
  sm: 4,
  md: 8,
  lg: 12,
  xl: 16,
  '2xl': 24,
  full: 9999,
};

// Shadows
export const shadows = {
  none: {
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0,
    shadowRadius: 0,
    elevation: 0,
  },
  sm: {
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.18,
    shadowRadius: 1.0,
    elevation: 1,
  },
  md: {
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.23,
    shadowRadius: 2.62,
    elevation: 4,
  },
  lg: {
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.30,
    shadowRadius: 4.65,
    elevation: 8,
  },
  xl: {
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.37,
    shadowRadius: 7.49,
    elevation: 12,
  },
};

// Animation durations
export const animations = {
  duration: {
    fast: 150,
    normal: 250,
    slow: 350,
  },
  easing: {
    easeInOut: 'ease-in-out',
    easeIn: 'ease-in',
    easeOut: 'ease-out',
    linear: 'linear',
  },
};

// Common component styles
export const componentStyles = {
  button: {
    height: 48,
    borderRadius: borderRadius.md,
    paddingHorizontal: spacing.lg,
  },
  input: {
    height: 48,
    borderRadius: borderRadius.md,
    paddingHorizontal: spacing.md,
    fontSize: typography.fontSize.base,
  },
  card: {
    borderRadius: borderRadius.lg,
    padding: spacing.lg,
    ...shadows.md,
  },
  header: {
    height: 56,
    paddingHorizontal: spacing.md,
  },
};

// Export default theme (light)
export const theme = lightTheme;

// Theme type
export type Theme = typeof lightTheme;

// Status bar styles
export const statusBarStyles = {
  light: {
    barStyle: 'dark-content' as const,
    backgroundColor: brandColors.background,
  },
  dark: {
    barStyle: 'light-content' as const,
    backgroundColor: '#000000',
  },
};