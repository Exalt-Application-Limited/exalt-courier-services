import React, { useEffect } from 'react';
import { StatusBar } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { Provider } from 'react-redux';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { ThemeProvider } from 'react-native-paper';
import SplashScreen from 'react-native-splash-screen';
import messaging from '@react-native-firebase/messaging';
import notifee, { EventType } from '@notifee/react-native';

import { store } from './src/store';
import { AuthProvider } from './src/contexts/AuthContext';
import { NotificationProvider } from './src/contexts/NotificationContext';
import { LocationProvider } from './src/contexts/LocationContext';
import RootNavigator from './src/navigation/RootNavigator';
import { setupNotifications } from './src/services/notification.service';
import { theme } from './src/styles/theme';

/**
 * Enhanced User Mobile App for Exalt Courier
 * 
 * Full-featured React Native application providing:
 * - Customer registration and onboarding
 * - Shipment booking and management
 * - Real-time tracking with maps
 * - Push notifications for updates
 * - Secure payment processing
 * - Document upload and management
 * - Live chat support
 * - Offline capabilities
 */

const App: React.FC = () => {
  useEffect(() => {
    // Hide splash screen
    SplashScreen.hide();

    // Setup push notifications
    setupNotifications();

    // Request notification permissions
    const requestPermissions = async () => {
      const authStatus = await messaging().requestPermission();
      const enabled =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;

      if (enabled) {
        console.log('Notification permissions granted');
      }
    };

    requestPermissions();

    // Handle background notification events
    notifee.onBackgroundEvent(async ({ type, detail }) => {
      const { notification, pressAction } = detail;

      if (type === EventType.ACTION_PRESS && pressAction?.id === 'track') {
        // Handle track shipment action
        console.log('User pressed track shipment');
      }
    });

    // Handle foreground notification events
    const unsubscribe = notifee.onForegroundEvent(({ type, detail }) => {
      switch (type) {
        case EventType.DISPLAYED:
          console.log('Notification displayed:', detail.notification);
          break;
        case EventType.PRESS:
          console.log('Notification pressed:', detail.notification);
          break;
      }
    });

    return () => {
      unsubscribe();
    };
  }, []);

  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <SafeAreaProvider>
          <AuthProvider>
            <NotificationProvider>
              <LocationProvider>
                <NavigationContainer>
                  <StatusBar
                    barStyle="dark-content"
                    backgroundColor="#ffffff"
                  />
                  <RootNavigator />
                </NavigationContainer>
              </LocationProvider>
            </NotificationProvider>
          </AuthProvider>
        </SafeAreaProvider>
      </ThemeProvider>
    </Provider>
  );
};

export default App;