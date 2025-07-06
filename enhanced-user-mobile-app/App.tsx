import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import Icon from 'react-native-vector-icons/MaterialIcons';
import {SafeAreaProvider} from 'react-native-safe-area-context';

// Import screens
import HomeScreen from './src/screens/HomeScreen';
import ShipmentTrackingScreen from './src/screens/ShipmentTrackingScreen';
import BookingScreen from './src/screens/BookingScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import LoginScreen from './src/screens/auth/LoginScreen';
import RegisterScreen from './src/screens/auth/RegisterScreen';
import ShipmentDetailsScreen from './src/screens/ShipmentDetailsScreen';
import AddressBookScreen from './src/screens/AddressBookScreen';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const TabNavigator = () => {
  return (
    <Tab.Navigator
      screenOptions={({route}) => ({
        tabBarIcon: ({focused, color, size}) => {
          let iconName = '';

          if (route.name === 'Home') {
            iconName = 'home';
          } else if (route.name === 'Track') {
            iconName = 'local-shipping';
          } else if (route.name === 'Book') {
            iconName = 'add-box';
          } else if (route.name === 'Profile') {
            iconName = 'person';
          }

          return <Icon name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#007AFF',
        tabBarInactiveTintColor: 'gray',
      })}>
      <Tab.Screen 
        name="Home" 
        component={HomeScreen} 
        options={{title: 'Home'}}
      />
      <Tab.Screen 
        name="Track" 
        component={ShipmentTrackingScreen} 
        options={{title: 'Track'}}
      />
      <Tab.Screen 
        name="Book" 
        component={BookingScreen} 
        options={{title: 'Book Shipment'}}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen} 
        options={{title: 'Profile'}}
      />
    </Tab.Navigator>
  );
};

const App = () => {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Stack.Navigator 
          initialRouteName="Login"
          screenOptions={{headerShown: false}}>
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
          <Stack.Screen name="Main" component={TabNavigator} />
          <Stack.Screen 
            name="ShipmentDetails" 
            component={ShipmentDetailsScreen}
            options={{headerShown: true, title: 'Shipment Details'}}
          />
          <Stack.Screen 
            name="AddressBook" 
            component={AddressBookScreen}
            options={{headerShown: true, title: 'Address Book'}}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
};

export default App;