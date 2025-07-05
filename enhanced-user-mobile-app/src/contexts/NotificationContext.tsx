import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import messaging from '@react-native-firebase/messaging';
import notifee, { AndroidImportance, AndroidVisibility } from '@notifee/react-native';
import { Platform, Alert } from 'react-native';
import { notificationService } from '../services/notification.service';
import { Notification } from '../types';

interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  markAsRead: (notificationId: string) => void;
  markAllAsRead: () => void;
  clearNotification: (notificationId: string) => void;
  clearAllNotifications: () => void;
  requestPermissions: () => Promise<boolean>;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

interface NotificationProviderProps {
  children: ReactNode;
}

export const NotificationProvider: React.FC<NotificationProviderProps> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [fcmToken, setFcmToken] = useState<string | null>(null);

  const unreadCount = notifications.filter(n => !n.isRead).length;

  useEffect(() => {
    initializeNotifications();
    setupMessageHandlers();
    return () => {
      // Cleanup listeners
    };
  }, []);

  const initializeNotifications = async () => {
    try {
      // Request permissions
      const hasPermission = await requestPermissions();
      
      if (hasPermission) {
        // Get FCM token
        const token = await messaging().getToken();
        setFcmToken(token);
        
        // Register token with backend
        await notificationService.registerDevice(token);
        
        // Load existing notifications
        await loadNotifications();
      }
    } catch (error) {
      console.error('Failed to initialize notifications:', error);
    }
  };

  const requestPermissions = async (): Promise<boolean> => {
    try {
      if (Platform.OS === 'android') {
        await notifee.requestPermission();
      }
      
      const authStatus = await messaging().requestPermission();
      const enabled =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;

      if (!enabled) {
        Alert.alert(
          'Notifications Disabled',
          'Please enable notifications to receive shipment updates and important alerts.',
          [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Settings', onPress: () => notifee.openNotificationSettings() },
          ]
        );
      }

      return enabled;
    } catch (error) {
      console.error('Permission request failed:', error);
      return false;
    }
  };

  const setupMessageHandlers = () => {
    // Handle foreground messages
    const unsubscribeOnMessage = messaging().onMessage(async (remoteMessage) => {
      console.log('Foreground message received:', remoteMessage);
      
      // Show local notification
      await displayNotification(remoteMessage);
      
      // Add to notifications list
      if (remoteMessage.data) {
        const notification = createNotificationFromMessage(remoteMessage);
        setNotifications(prev => [notification, ...prev]);
      }
    });

    // Handle background/quit state messages
    messaging().setBackgroundMessageHandler(async (remoteMessage) => {
      console.log('Background message received:', remoteMessage);
      await displayNotification(remoteMessage);
    });

    // Handle notification opened from quit state
    messaging().getInitialNotification().then((remoteMessage) => {
      if (remoteMessage) {
        console.log('App opened from quit state by notification:', remoteMessage);
        handleNotificationPress(remoteMessage);
      }
    });

    // Handle notification opened from background state
    const unsubscribeOnNotificationOpenedApp = messaging().onNotificationOpenedApp((remoteMessage) => {
      console.log('App opened from background by notification:', remoteMessage);
      handleNotificationPress(remoteMessage);
    });

    return () => {
      unsubscribeOnMessage();
      unsubscribeOnNotificationOpenedApp();
    };
  };

  const displayNotification = async (remoteMessage: any) => {
    try {
      await notifee.displayNotification({
        title: remoteMessage.notification?.title || 'Exalt Courier',
        body: remoteMessage.notification?.body || 'You have a new update',
        data: remoteMessage.data,
        android: {
          channelId: 'default',
          importance: AndroidImportance.HIGH,
          visibility: AndroidVisibility.PUBLIC,
          smallIcon: 'ic_notification',
          largeIcon: remoteMessage.notification?.android?.imageUrl,
          actions: [
            {
              title: 'Track Shipment',
              pressAction: {
                id: 'track',
                launchActivity: 'default',
              },
            },
            {
              title: 'View Details',
              pressAction: {
                id: 'view',
                launchActivity: 'default',
              },
            },
          ],
        },
        ios: {
          sound: 'default',
          badgeCount: unreadCount + 1,
        },
      });
    } catch (error) {
      console.error('Failed to display notification:', error);
    }
  };

  const createNotificationFromMessage = (remoteMessage: any): Notification => {
    return {
      id: remoteMessage.messageId || Date.now().toString(),
      title: remoteMessage.notification?.title || 'Notification',
      body: remoteMessage.notification?.body || '',
      data: remoteMessage.data || {},
      timestamp: new Date(),
      isRead: false,
      type: remoteMessage.data?.type || 'general',
    };
  };

  const handleNotificationPress = (remoteMessage: any) => {
    const { data } = remoteMessage;
    
    if (data?.type === 'shipment_update' && data?.shipmentId) {
      // Navigate to shipment detail
      // NavigationService.navigate('ShipmentDetail', { id: data.shipmentId });
    } else if (data?.type === 'support_message' && data?.ticketId) {
      // Navigate to support ticket
      // NavigationService.navigate('TicketDetail', { id: data.ticketId });
    }
  };

  const loadNotifications = async () => {
    try {
      const response = await notificationService.getNotifications();
      if (response.success && response.data) {
        setNotifications(response.data);
      }
    } catch (error) {
      console.error('Failed to load notifications:', error);
    }
  };

  const markAsRead = async (notificationId: string) => {
    try {
      setNotifications(prev => 
        prev.map(n => 
          n.id === notificationId ? { ...n, isRead: true } : n
        )
      );
      
      await notificationService.markAsRead(notificationId);
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      setNotifications(prev => 
        prev.map(n => ({ ...n, isRead: true }))
      );
      
      await notificationService.markAllAsRead();
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error);
    }
  };

  const clearNotification = async (notificationId: string) => {
    try {
      setNotifications(prev => 
        prev.filter(n => n.id !== notificationId)
      );
      
      await notificationService.deleteNotification(notificationId);
    } catch (error) {
      console.error('Failed to clear notification:', error);
    }
  };

  const clearAllNotifications = async () => {
    try {
      setNotifications([]);
      await notificationService.clearAllNotifications();
    } catch (error) {
      console.error('Failed to clear all notifications:', error);
    }
  };

  const value: NotificationContextType = {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead,
    clearNotification,
    clearAllNotifications,
    requestPermissions,
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotifications = (): NotificationContextType => {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
};