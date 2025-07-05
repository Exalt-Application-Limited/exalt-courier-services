import messaging from '@react-native-firebase/messaging';
import notifee, { AndroidImportance, AndroidVisibility, AndroidChannel } from '@notifee/react-native';
import { Platform } from 'react-native';
import { apiClient } from './api.service';
import { ApiResponse, Notification, PaginatedResponse } from '../types';

class NotificationService {
  private baseUrl = '/api/v1/notifications';
  private defaultChannelId = 'default';

  async setupNotifications(): Promise<void> {
    try {
      // Create notification channels for Android
      if (Platform.OS === 'android') {
        await this.createNotificationChannels();
      }

      // Initialize FCM
      await this.initializeFCM();
    } catch (error) {
      console.error('Failed to setup notifications:', error);
    }
  }

  private async createNotificationChannels(): Promise<void> {
    const channels: AndroidChannel[] = [
      {
        id: 'default',
        name: 'General Notifications',
        description: 'General app notifications',
        importance: AndroidImportance.HIGH,
        visibility: AndroidVisibility.PUBLIC,
        sound: 'default',
        vibration: true,
      },
      {
        id: 'shipment_updates',
        name: 'Shipment Updates',
        description: 'Notifications about your shipment status',
        importance: AndroidImportance.HIGH,
        visibility: AndroidVisibility.PUBLIC,
        sound: 'shipment_update',
        vibration: true,
      },
      {
        id: 'promotions',
        name: 'Promotions & Offers',
        description: 'Special offers and promotional notifications',
        importance: AndroidImportance.DEFAULT,
        visibility: AndroidVisibility.PUBLIC,
        sound: 'promotion',
        vibration: false,
      },
      {
        id: 'support',
        name: 'Customer Support',
        description: 'Messages from customer support',
        importance: AndroidImportance.HIGH,
        visibility: AndroidVisibility.PUBLIC,
        sound: 'support_message',
        vibration: true,
      },
    ];

    await Promise.all(
      channels.map(channel => notifee.createChannel(channel))
    );
  }

  private async initializeFCM(): Promise<void> {
    try {
      // Get FCM token
      const token = await messaging().getToken();
      console.log('FCM Token:', token);

      // Listen for token refresh
      messaging().onTokenRefresh(async (newToken) => {
        console.log('FCM Token refreshed:', newToken);
        await this.registerDevice(newToken);
      });

      // Register device with backend
      await this.registerDevice(token);
    } catch (error) {
      console.error('FCM initialization failed:', error);
    }
  }

  async registerDevice(fcmToken: string): Promise<ApiResponse> {
    try {
      const deviceInfo = {
        fcmToken,
        platform: Platform.OS,
        appVersion: '1.0.0', // Get from package.json or react-native-device-info
        osVersion: Platform.Version,
        deviceModel: 'unknown', // Get from react-native-device-info
      };

      const response = await apiClient.post(`${this.baseUrl}/register-device`, deviceInfo);
      return response.data;
    } catch (error: any) {
      console.error('Failed to register device:', error);
      return {
        success: false,
        error: 'Failed to register device for notifications',
      };
    }
  }

  async unregisterDevice(): Promise<ApiResponse> {
    try {
      const response = await apiClient.delete(`${this.baseUrl}/unregister-device`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to unregister device:', error);
      return {
        success: false,
        error: 'Failed to unregister device',
      };
    }
  }

  async getNotifications(
    page: number = 1,
    limit: number = 20,
    type?: string
  ): Promise<ApiResponse<PaginatedResponse<Notification>>> {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: limit.toString(),
        ...(type && { type }),
      });

      const response = await apiClient.get(`${this.baseUrl}?${params}`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to get notifications:', error);
      return {
        success: false,
        error: 'Failed to load notifications',
      };
    }
  }

  async markAsRead(notificationId: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.patch(`${this.baseUrl}/${notificationId}/read`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to mark notification as read:', error);
      return {
        success: false,
        error: 'Failed to mark notification as read',
      };
    }
  }

  async markAllAsRead(): Promise<ApiResponse> {
    try {
      const response = await apiClient.patch(`${this.baseUrl}/read-all`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to mark all notifications as read:', error);
      return {
        success: false,
        error: 'Failed to mark notifications as read',
      };
    }
  }

  async deleteNotification(notificationId: string): Promise<ApiResponse> {
    try {
      const response = await apiClient.delete(`${this.baseUrl}/${notificationId}`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to delete notification:', error);
      return {
        success: false,
        error: 'Failed to delete notification',
      };
    }
  }

  async clearAllNotifications(): Promise<ApiResponse> {
    try {
      const response = await apiClient.delete(`${this.baseUrl}/clear-all`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to clear notifications:', error);
      return {
        success: false,
        error: 'Failed to clear notifications',
      };
    }
  }

  async updatePreferences(preferences: {
    email: boolean;
    sms: boolean;
    push: boolean;
    categories: string[];
  }): Promise<ApiResponse> {
    try {
      const response = await apiClient.put(`${this.baseUrl}/preferences`, preferences);
      return response.data;
    } catch (error: any) {
      console.error('Failed to update notification preferences:', error);
      return {
        success: false,
        error: 'Failed to update preferences',
      };
    }
  }

  async getPreferences(): Promise<ApiResponse<{
    email: boolean;
    sms: boolean;
    push: boolean;
    categories: string[];
  }>> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/preferences`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to get notification preferences:', error);
      return {
        success: false,
        error: 'Failed to load preferences',
      };
    }
  }

  async displayLocalNotification(notification: {
    title: string;
    body: string;
    data?: any;
    channelId?: string;
    actions?: Array<{
      title: string;
      pressAction: {
        id: string;
        launchActivity?: string;
      };
    }>;
  }): Promise<void> {
    try {
      await notifee.displayNotification({
        title: notification.title,
        body: notification.body,
        data: notification.data,
        android: {
          channelId: notification.channelId || this.defaultChannelId,
          importance: AndroidImportance.HIGH,
          visibility: AndroidVisibility.PUBLIC,
          smallIcon: 'ic_notification',
          actions: notification.actions,
        },
        ios: {
          sound: 'default',
        },
      });
    } catch (error) {
      console.error('Failed to display local notification:', error);
    }
  }

  async cancelNotification(notificationId: string): Promise<void> {
    try {
      await notifee.cancelNotification(notificationId);
    } catch (error) {
      console.error('Failed to cancel notification:', error);
    }
  }

  async cancelAllNotifications(): Promise<void> {
    try {
      await notifee.cancelAllNotifications();
    } catch (error) {
      console.error('Failed to cancel all notifications:', error);
    }
  }

  async setBadgeCount(count: number): Promise<void> {
    try {
      if (Platform.OS === 'ios') {
        await notifee.setBadgeCount(count);
      }
    } catch (error) {
      console.error('Failed to set badge count:', error);
    }
  }

  async incrementBadgeCount(): Promise<void> {
    try {
      if (Platform.OS === 'ios') {
        await notifee.incrementBadgeCount();
      }
    } catch (error) {
      console.error('Failed to increment badge count:', error);
    }
  }

  async decrementBadgeCount(): Promise<void> {
    try {
      if (Platform.OS === 'ios') {
        await notifee.decrementBadgeCount();
      }
    } catch (error) {
      console.error('Failed to decrement badge count:', error);
    }
  }

  async scheduleNotification(notification: {
    title: string;
    body: string;
    date: Date;
    data?: any;
  }): Promise<string> {
    try {
      const notificationId = await notifee.createTriggerNotification(
        {
          title: notification.title,
          body: notification.body,
          data: notification.data,
          android: {
            channelId: this.defaultChannelId,
          },
        },
        {
          type: notifee.TriggerType.TIMESTAMP,
          timestamp: notification.date.getTime(),
        }
      );

      return notificationId;
    } catch (error) {
      console.error('Failed to schedule notification:', error);
      throw error;
    }
  }

  async cancelScheduledNotification(notificationId: string): Promise<void> {
    try {
      await notifee.cancelTriggerNotification(notificationId);
    } catch (error) {
      console.error('Failed to cancel scheduled notification:', error);
    }
  }
}

// Global setup function
export const setupNotifications = async (): Promise<void> => {
  await notificationService.setupNotifications();
};

export const notificationService = new NotificationService();