import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  Alert,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

const ShipmentTrackingScreen = () => {
  const [trackingNumber, setTrackingNumber] = useState('');
  const [trackingData, setTrackingData] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  const handleTrack = async () => {
    if (!trackingNumber.trim()) {
      Alert.alert('Error', 'Please enter a tracking number');
      return;
    }

    setLoading(true);
    
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock tracking data
      setTrackingData({
        id: trackingNumber,
        status: 'In Transit',
        origin: 'Lagos, Nigeria',
        destination: 'Abuja, Nigeria',
        estimatedDelivery: '2024-01-15',
        timeline: [
          {
            status: 'Package picked up',
            location: 'Lagos Warehouse',
            time: '2024-01-12 09:00 AM',
            completed: true,
          },
          {
            status: 'In transit',
            location: 'Lagos - Abuja Highway',
            time: '2024-01-12 02:00 PM',
            completed: true,
          },
          {
            status: 'Arrived at destination city',
            location: 'Abuja Distribution Center',
            time: '2024-01-13 08:00 AM',
            completed: true,
          },
          {
            status: 'Out for delivery',
            location: 'Abuja Delivery Hub',
            time: 'Expected: 2024-01-15 10:00 AM',
            completed: false,
          },
        ],
      });
    } catch (error) {
      Alert.alert('Error', 'Failed to track shipment. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView>
        <View style={styles.header}>
          <Text style={styles.title}>Track Your Shipment</Text>
          <Text style={styles.subtitle}>Enter your tracking number to get real-time updates</Text>
        </View>

        <View style={styles.trackingForm}>
          <View style={styles.inputContainer}>
            <Icon name="search" size={20} color="#8E8E93" style={styles.inputIcon} />
            <TextInput
              style={styles.input}
              placeholder="Enter tracking number (e.g., GX001234)"
              value={trackingNumber}
              onChangeText={setTrackingNumber}
              autoCapitalize="characters"
            />
          </View>
          
          <TouchableOpacity
            style={[styles.trackButton, loading && styles.trackButtonDisabled]}
            onPress={handleTrack}
            disabled={loading}>
            <Text style={styles.trackButtonText}>
              {loading ? 'Tracking...' : 'Track Package'}
            </Text>
          </TouchableOpacity>
        </View>

        {trackingData && (
          <View style={styles.trackingResults}>
            <View style={styles.statusCard}>
              <View style={styles.statusHeader}>
                <Icon name="local-shipping" size={24} color="#007AFF" />
                <Text style={styles.trackingId}>#{trackingData.id}</Text>
              </View>
              <Text style={styles.currentStatus}>{trackingData.status}</Text>
              <Text style={styles.routeInfo}>
                {trackingData.origin} → {trackingData.destination}
              </Text>
              <Text style={styles.estimatedDelivery}>
                Estimated Delivery: {trackingData.estimatedDelivery}
              </Text>
            </View>

            <View style={styles.timelineContainer}>
              <Text style={styles.timelineTitle}>Tracking Timeline</Text>
              {trackingData.timeline.map((item: any, index: number) => (
                <View key={index} style={styles.timelineItem}>
                  <View style={styles.timelineIcon}>
                    <View style={[
                      styles.timelineDot,
                      {backgroundColor: item.completed ? '#34C759' : '#E5E5EA'}
                    ]} />
                    {index < trackingData.timeline.length - 1 && (
                      <View style={[
                        styles.timelineLine,
                        {backgroundColor: item.completed ? '#34C759' : '#E5E5EA'}
                      ]} />
                    )}
                  </View>
                  <View style={styles.timelineContent}>
                    <Text style={[
                      styles.timelineStatus,
                      {color: item.completed ? '#1C1C1E' : '#8E8E93'}
                    ]}>
                      {item.status}
                    </Text>
                    <Text style={styles.timelineLocation}>{item.location}</Text>
                    <Text style={styles.timelineTime}>{item.time}</Text>
                  </View>
                </View>
              ))}
            </View>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F2F2F7',
  },
  header: {
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#1C1C1E',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#8E8E93',
    lineHeight: 22,
  },
  trackingForm: {
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'white',
    borderRadius: 12,
    marginBottom: 15,
    paddingHorizontal: 15,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  inputIcon: {
    marginRight: 12,
  },
  input: {
    flex: 1,
    height: 50,
    fontSize: 16,
    color: '#1C1C1E',
  },
  trackButton: {
    backgroundColor: '#007AFF',
    borderRadius: 12,
    height: 50,
    justifyContent: 'center',
    alignItems: 'center',
  },
  trackButtonDisabled: {
    backgroundColor: '#B0B0B0',
  },
  trackButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: '600',
  },
  trackingResults: {
    paddingHorizontal: 20,
  },
  statusCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  statusHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  trackingId: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#1C1C1E',
    marginLeft: 10,
  },
  currentStatus: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#007AFF',
    marginBottom: 8,
  },
  routeInfo: {
    fontSize: 16,
    color: '#8E8E93',
    marginBottom: 8,
  },
  estimatedDelivery: {
    fontSize: 14,
    color: '#FF9500',
    fontWeight: '600',
  },
  timelineContainer: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  timelineTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#1C1C1E',
    marginBottom: 20,
  },
  timelineItem: {
    flexDirection: 'row',
    marginBottom: 20,
  },
  timelineIcon: {
    alignItems: 'center',
    marginRight: 15,
  },
  timelineDot: {
    width: 12,
    height: 12,
    borderRadius: 6,
  },
  timelineLine: {
    width: 2,
    height: 40,
    marginTop: 5,
  },
  timelineContent: {
    flex: 1,
  },
  timelineStatus: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
  },
  timelineLocation: {
    fontSize: 14,
    color: '#8E8E93',
    marginBottom: 2,
  },
  timelineTime: {
    fontSize: 12,
    color: '#8E8E93',
  },
});

export default ShipmentTrackingScreen;