import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import Geolocation from 'react-native-geolocation-service';
import { Platform, Alert, Linking, PermissionsAndroid } from 'react-native';
import { PERMISSIONS, request, check, RESULTS } from 'react-native-permissions';

interface LocationContextType {
  location: {
    latitude: number;
    longitude: number;
  } | null;
  address: string | null;
  isLoading: boolean;
  hasPermission: boolean;
  requestLocationPermission: () => Promise<boolean>;
  getCurrentLocation: () => Promise<void>;
  watchLocation: () => () => void;
  geocodeAddress: (address: string) => Promise<{ lat: number; lng: number } | null>;
  reverseGeocode: (lat: number, lng: number) => Promise<string | null>;
}

const LocationContext = createContext<LocationContextType | undefined>(undefined);

interface LocationProviderProps {
  children: ReactNode;
}

export const LocationProvider: React.FC<LocationProviderProps> = ({ children }) => {
  const [location, setLocation] = useState<{ latitude: number; longitude: number } | null>(null);
  const [address, setAddress] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [hasPermission, setHasPermission] = useState(false);

  useEffect(() => {
    checkLocationPermission();
  }, []);

  const checkLocationPermission = async () => {
    try {
      let permission;
      
      if (Platform.OS === 'ios') {
        permission = PERMISSIONS.IOS.LOCATION_WHEN_IN_USE;
      } else {
        permission = PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION;
      }

      const result = await check(permission);
      const granted = result === RESULTS.GRANTED;
      
      setHasPermission(granted);
      
      if (granted) {
        getCurrentLocation();
      }
    } catch (error) {
      console.error('Error checking location permission:', error);
    }
  };

  const requestLocationPermission = async (): Promise<boolean> => {
    try {
      if (Platform.OS === 'android') {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
          {
            title: 'Location Permission',
            message: 'Exalt Courier needs access to your location to provide accurate delivery services.',
            buttonNeutral: 'Ask Me Later',
            buttonNegative: 'Cancel',
            buttonPositive: 'OK',
          }
        );
        
        const hasPermission = granted === PermissionsAndroid.RESULTS.GRANTED;
        setHasPermission(hasPermission);
        
        if (hasPermission) {
          getCurrentLocation();
        } else {
          showPermissionAlert();
        }
        
        return hasPermission;
      } else {
        const result = await request(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        const hasPermission = result === RESULTS.GRANTED;
        
        setHasPermission(hasPermission);
        
        if (hasPermission) {
          getCurrentLocation();
        } else {
          showPermissionAlert();
        }
        
        return hasPermission;
      }
    } catch (error) {
      console.error('Error requesting location permission:', error);
      return false;
    }
  };

  const showPermissionAlert = () => {
    Alert.alert(
      'Location Access Required',
      'To provide the best delivery experience, we need access to your location. You can enable this in your device settings.',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Settings', onPress: () => Linking.openSettings() },
      ]
    );
  };

  const getCurrentLocation = async (): Promise<void> => {
    if (!hasPermission) {
      console.warn('Location permission not granted');
      return;
    }

    setIsLoading(true);
    
    try {
      Geolocation.getCurrentPosition(
        async (position) => {
          const { latitude, longitude } = position.coords;
          setLocation({ latitude, longitude });
          
          // Get address from coordinates
          const addressText = await reverseGeocode(latitude, longitude);
          setAddress(addressText);
          
          setIsLoading(false);
        },
        (error) => {
          console.error('Error getting location:', error);
          setIsLoading(false);
          
          if (error.code === 1) {
            showPermissionAlert();
          } else {
            Alert.alert(
              'Location Error',
              'Unable to get your current location. Please check your GPS settings.'
            );
          }
        },
        {
          enableHighAccuracy: true,
          timeout: 15000,
          maximumAge: 10000,
        }
      );
    } catch (error) {
      console.error('Error in getCurrentLocation:', error);
      setIsLoading(false);
    }
  };

  const watchLocation = (): (() => void) => {
    if (!hasPermission) {
      console.warn('Location permission not granted');
      return () => {};
    }

    const watchId = Geolocation.watchPosition(
      async (position) => {
        const { latitude, longitude } = position.coords;
        setLocation({ latitude, longitude });
        
        // Update address if location changed significantly
        const addressText = await reverseGeocode(latitude, longitude);
        setAddress(addressText);
      },
      (error) => {
        console.error('Error watching location:', error);
      },
      {
        enableHighAccuracy: true,
        distanceFilter: 10, // Update every 10 meters
        interval: 5000, // Update every 5 seconds
        fastestInterval: 2000, // Fastest update every 2 seconds
      }
    );

    return () => {
      Geolocation.clearWatch(watchId);
    };
  };

  const geocodeAddress = async (address: string): Promise<{ lat: number; lng: number } | null> => {
    try {
      // Using Google Geocoding API
      const encodedAddress = encodeURIComponent(address);
      const response = await fetch(
        `https://maps.googleapis.com/maps/api/geocode/json?address=${encodedAddress}&key=YOUR_GOOGLE_MAPS_API_KEY`
      );
      
      const data = await response.json();
      
      if (data.results && data.results.length > 0) {
        const { lat, lng } = data.results[0].geometry.location;
        return { lat, lng };
      }
      
      return null;
    } catch (error) {
      console.error('Error geocoding address:', error);
      return null;
    }
  };

  const reverseGeocode = async (lat: number, lng: number): Promise<string | null> => {
    try {
      // Using Google Reverse Geocoding API
      const response = await fetch(
        `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=YOUR_GOOGLE_MAPS_API_KEY`
      );
      
      const data = await response.json();
      
      if (data.results && data.results.length > 0) {
        return data.results[0].formatted_address;
      }
      
      return null;
    } catch (error) {
      console.error('Error reverse geocoding:', error);
      return null;
    }
  };

  const value: LocationContextType = {
    location,
    address,
    isLoading,
    hasPermission,
    requestLocationPermission,
    getCurrentLocation,
    watchLocation,
    geocodeAddress,
    reverseGeocode,
  };

  return (
    <LocationContext.Provider value={value}>
      {children}
    </LocationContext.Provider>
  );
};

export const useLocation = (): LocationContextType => {
  const context = useContext(LocationContext);
  if (context === undefined) {
    throw new Error('useLocation must be used within a LocationProvider');
  }
  return context;
};