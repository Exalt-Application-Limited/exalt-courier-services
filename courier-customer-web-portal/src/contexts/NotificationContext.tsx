import React, { createContext, useContext, useState, ReactNode, useCallback } from 'react';
import { Snackbar, Alert, AlertColor, Slide, SlideProps } from '@mui/material';

export interface NotificationOptions {
  type?: AlertColor;
  duration?: number;
  position?: {
    vertical: 'top' | 'bottom';
    horizontal: 'left' | 'center' | 'right';
  };
  action?: React.ReactNode;
  persistent?: boolean;
}

export interface Notification {
  id: string;
  message: string;
  type: AlertColor;
  duration: number;
  position: {
    vertical: 'top' | 'bottom';
    horizontal: 'left' | 'center' | 'right';
  };
  action?: React.ReactNode;
  persistent: boolean;
  timestamp: number;
}

export interface NotificationContextType {
  notifications: Notification[];
  showNotification: (message: string, type?: AlertColor, options?: NotificationOptions) => string;
  hideNotification: (id: string) => void;
  clearAllNotifications: () => void;
  showSuccess: (message: string, options?: Omit<NotificationOptions, 'type'>) => string;
  showError: (message: string, options?: Omit<NotificationOptions, 'type'>) => string;
  showWarning: (message: string, options?: Omit<NotificationOptions, 'type'>) => string;
  showInfo: (message: string, options?: Omit<NotificationOptions, 'type'>) => string;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const useNotification = (): NotificationContextType => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within a NotificationProvider');
  }
  return context;
};

function SlideTransition(props: SlideProps) {
  return <Slide {...props} direction="down" />;
}

interface NotificationProviderProps {
  children: ReactNode;
  maxNotifications?: number;
}

export const NotificationProvider: React.FC<NotificationProviderProps> = ({ 
  children, 
  maxNotifications = 5 
}) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  const generateId = useCallback(() => {
    return `notification_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }, []);

  const showNotification = useCallback((
    message: string,
    type: AlertColor = 'info',
    options: NotificationOptions = {}
  ): string => {
    const id = generateId();
    
    const notification: Notification = {
      id,
      message,
      type,
      duration: options.duration ?? (type === 'error' ? 6000 : 4000),
      position: options.position ?? { vertical: 'top', horizontal: 'right' },
      action: options.action,
      persistent: options.persistent ?? false,
      timestamp: Date.now(),
    };

    setNotifications(prev => {
      const newNotifications = [notification, ...prev];
      // Limit the number of notifications
      return newNotifications.slice(0, maxNotifications);
    });

    // Auto-hide non-persistent notifications
    if (!notification.persistent && notification.duration > 0) {
      setTimeout(() => {
        hideNotification(id);
      }, notification.duration);
    }

    return id;
  }, [generateId, maxNotifications]);

  const hideNotification = useCallback((id: string) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id));
  }, []);

  const clearAllNotifications = useCallback(() => {
    setNotifications([]);
  }, []);

  const showSuccess = useCallback((message: string, options?: Omit<NotificationOptions, 'type'>) => {
    return showNotification(message, 'success', options);
  }, [showNotification]);

  const showError = useCallback((message: string, options?: Omit<NotificationOptions, 'type'>) => {
    return showNotification(message, 'error', options);
  }, [showNotification]);

  const showWarning = useCallback((message: string, options?: Omit<NotificationOptions, 'type'>) => {
    return showNotification(message, 'warning', options);
  }, [showNotification]);

  const showInfo = useCallback((message: string, options?: Omit<NotificationOptions, 'type'>) => {
    return showNotification(message, 'info', options);
  }, [showNotification]);

  const value: NotificationContextType = {
    notifications,
    showNotification,
    hideNotification,
    clearAllNotifications,
    showSuccess,
    showError,
    showWarning,
    showInfo,
  };

  // Group notifications by position
  const notificationsByPosition = notifications.reduce((acc, notification) => {
    const key = `${notification.position.vertical}-${notification.position.horizontal}`;
    if (!acc[key]) {
      acc[key] = [];
    }
    acc[key].push(notification);
    return acc;
  }, {} as Record<string, Notification[]>);

  return (
    <NotificationContext.Provider value={value}>
      {children}
      
      {/* Render notifications grouped by position */}
      {Object.entries(notificationsByPosition).map(([positionKey, positionNotifications]) => {
        const [vertical, horizontal] = positionKey.split('-') as ['top' | 'bottom', 'left' | 'center' | 'right'];
        
        return positionNotifications.map((notification, index) => (
          <Snackbar
            key={notification.id}
            open={true}
            anchorOrigin={{ vertical, horizontal }}
            TransitionComponent={SlideTransition}
            style={{
              position: 'fixed',
              [vertical]: vertical === 'top' ? 24 + (index * 60) : 24 + (index * 60),
              zIndex: 9999 - index,
            }}
            onClose={() => !notification.persistent && hideNotification(notification.id)}
          >
            <Alert
              severity={notification.type}
              variant="filled"
              onClose={() => hideNotification(notification.id)}
              action={notification.action}
              sx={{
                minWidth: 300,
                maxWidth: 500,
                boxShadow: '0px 4px 12px rgba(0, 0, 0, 0.15)',
                '& .MuiAlert-message': {
                  wordBreak: 'break-word',
                },
              }}
            >
              {notification.message}
            </Alert>
          </Snackbar>
        ));
      })}
    </NotificationContext.Provider>
  );
};