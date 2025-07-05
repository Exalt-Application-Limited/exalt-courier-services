import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/Ionicons';
import { Platform } from 'react-native';

import { useAuth } from '../contexts/AuthContext';

// Auth Screens
import LoginScreen from '../screens/auth/LoginScreen';
import RegisterScreen from '../screens/auth/RegisterScreen';
import ForgotPasswordScreen from '../screens/auth/ForgotPasswordScreen';
import OnboardingScreen from '../screens/auth/OnboardingScreen';

// Main Screens
import HomeScreen from '../screens/home/HomeScreen';
import ShipmentsScreen from '../screens/shipments/ShipmentsScreen';
import TrackingScreen from '../screens/tracking/TrackingScreen';
import ProfileScreen from '../screens/profile/ProfileScreen';

// Detailed Screens
import BookShipmentScreen from '../screens/shipments/BookShipmentScreen';
import ShipmentDetailScreen from '../screens/shipments/ShipmentDetailScreen';
import NotificationsScreen from '../screens/notifications/NotificationsScreen';
import SupportScreen from '../screens/support/SupportScreen';
import ChatScreen from '../screens/support/ChatScreen';
import TicketDetailScreen from '../screens/support/TicketDetailScreen';
import PaymentMethodsScreen from '../screens/profile/PaymentMethodsScreen';
import AddressBookScreen from '../screens/profile/AddressBookScreen';
import SettingsScreen from '../screens/profile/SettingsScreen';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const TabNavigator: React.FC = () => {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: string;

          switch (route.name) {
            case 'Home':
              iconName = focused ? 'home' : 'home-outline';
              break;
            case 'Shipments':
              iconName = focused ? 'cube' : 'cube-outline';
              break;
            case 'Tracking':
              iconName = focused ? 'location' : 'location-outline';
              break;
            case 'Profile':
              iconName = focused ? 'person' : 'person-outline';
              break;
            default:
              iconName = 'help-outline';
          }

          return <Icon name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#1976d2',
        tabBarInactiveTintColor: '#8e8e93',
        tabBarStyle: {
          backgroundColor: '#ffffff',
          borderTopColor: '#e0e0e0',
          borderTopWidth: 1,
          paddingTop: Platform.OS === 'ios' ? 10 : 5,
          height: Platform.OS === 'ios' ? 85 : 65,
        },
        headerShown: false,
      })}
    >
      <Tab.Screen 
        name="Home" 
        component={HomeScreen}
        options={{ tabBarLabel: 'Home' }}
      />
      <Tab.Screen 
        name="Shipments" 
        component={ShipmentsScreen}
        options={{ tabBarLabel: 'Shipments' }}
      />
      <Tab.Screen 
        name="Tracking" 
        component={TrackingScreen}
        options={{ tabBarLabel: 'Track' }}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{ tabBarLabel: 'Profile' }}
      />
    </Tab.Navigator>
  );
};

const RootNavigator: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return null; // Splash screen will be shown
  }

  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: {
          backgroundColor: '#1976d2',
        },
        headerTintColor: '#ffffff',
        headerTitleStyle: {
          fontWeight: '600',
        },
      }}
    >
      {!isAuthenticated ? (
        // Auth Stack
        <>
          <Stack.Screen 
            name="Login" 
            component={LoginScreen}
            options={{ headerShown: false }}
          />
          <Stack.Screen 
            name="Register" 
            component={RegisterScreen}
            options={{ 
              title: 'Create Account',
              headerBackTitle: 'Back'
            }}
          />
          <Stack.Screen 
            name="ForgotPassword" 
            component={ForgotPasswordScreen}
            options={{ 
              title: 'Reset Password',
              headerBackTitle: 'Back'
            }}
          />
          <Stack.Screen 
            name="Onboarding" 
            component={OnboardingScreen}
            options={{ headerShown: false }}
          />
        </>
      ) : (
        // Main App Stack
        <>
          <Stack.Screen 
            name="MainTabs" 
            component={TabNavigator}
            options={{ headerShown: false }}
          />
          <Stack.Screen 
            name="BookShipment" 
            component={BookShipmentScreen}
            options={{ 
              title: 'Book Shipment',
              presentation: 'modal'
            }}
          />
          <Stack.Screen 
            name="ShipmentDetail" 
            component={ShipmentDetailScreen}
            options={{ title: 'Shipment Details' }}
          />
          <Stack.Screen 
            name="Notifications" 
            component={NotificationsScreen}
            options={{ title: 'Notifications' }}
          />
          <Stack.Screen 
            name="Support" 
            component={SupportScreen}
            options={{ title: 'Support Center' }}
          />
          <Stack.Screen 
            name="Chat" 
            component={ChatScreen}
            options={{ title: 'Live Chat' }}
          />
          <Stack.Screen 
            name="TicketDetail" 
            component={TicketDetailScreen}
            options={{ title: 'Support Ticket' }}
          />
          <Stack.Screen 
            name="PaymentMethods" 
            component={PaymentMethodsScreen}
            options={{ title: 'Payment Methods' }}
          />
          <Stack.Screen 
            name="AddressBook" 
            component={AddressBookScreen}
            options={{ title: 'Address Book' }}
          />
          <Stack.Screen 
            name="Settings" 
            component={SettingsScreen}
            options={{ title: 'Settings' }}
          />
        </>
      )}
    </Stack.Navigator>
  );
};

export default RootNavigator;