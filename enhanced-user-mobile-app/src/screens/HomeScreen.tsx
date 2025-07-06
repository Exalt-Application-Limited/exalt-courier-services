import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

interface QuickActionProps {
  title: string;
  icon: string;
  onPress: () => void;
  color: string;
}

const QuickAction: React.FC<QuickActionProps> = ({title, icon, onPress, color}) => (
  <TouchableOpacity style={[styles.quickAction, {backgroundColor: color}]} onPress={onPress}>
    <Icon name={icon} size={24} color="white" />
    <Text style={styles.quickActionText}>{title}</Text>
  </TouchableOpacity>
);

const HomeScreen = ({navigation}: any) => {
  const recentShipments = [
    {id: 'GX001', status: 'In Transit', destination: 'Lagos'},
    {id: 'GX002', status: 'Delivered', destination: 'Abuja'},
    {id: 'GX003', status: 'Pending', destination: 'Kano'},
  ];

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView>
        <View style={styles.header}>
          <Text style={styles.welcomeText}>Welcome to Gogidix Courier</Text>
          <Text style={styles.subText}>Fast, reliable delivery services</Text>
        </View>

        <View style={styles.quickActionsContainer}>
          <Text style={styles.sectionTitle}>Quick Actions</Text>
          <View style={styles.quickActionsGrid}>
            <QuickAction
              title="Book Shipment"
              icon="add-box"
              color="#007AFF"
              onPress={() => navigation.navigate('Book')}
            />
            <QuickAction
              title="Track Package"
              icon="search"
              color="#34C759"
              onPress={() => navigation.navigate('Track')}
            />
            <QuickAction
              title="Rate Calculator"
              icon="calculate"
              color="#FF9500"
              onPress={() => {/* Navigate to rate calculator */}}
            />
            <QuickAction
              title="Support"
              icon="support-agent"
              color="#AF52DE"
              onPress={() => {/* Navigate to support */}}
            />
          </View>
        </View>

        <View style={styles.recentContainer}>
          <Text style={styles.sectionTitle}>Recent Shipments</Text>
          {recentShipments.map((shipment, index) => (
            <TouchableOpacity
              key={index}
              style={styles.shipmentCard}
              onPress={() => navigation.navigate('ShipmentDetails', {shipmentId: shipment.id})}>
              <View style={styles.shipmentInfo}>
                <Text style={styles.shipmentId}>#{shipment.id}</Text>
                <Text style={styles.shipmentDestination}>To: {shipment.destination}</Text>
              </View>
              <View style={[
                styles.statusBadge,
                {backgroundColor: getStatusColor(shipment.status)}
              ]}>
                <Text style={styles.statusText}>{shipment.status}</Text>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Delivered': return '#34C759';
    case 'In Transit': return '#007AFF';
    case 'Pending': return '#FF9500';
    default: return '#8E8E93';
  }
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F2F2F7',
  },
  header: {
    padding: 20,
    backgroundColor: '#007AFF',
    marginBottom: 20,
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
    marginBottom: 5,
  },
  subText: {
    fontSize: 16,
    color: 'white',
    opacity: 0.9,
  },
  quickActionsContainer: {
    paddingHorizontal: 20,
    marginBottom: 30,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#1C1C1E',
  },
  quickActionsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  quickAction: {
    width: '48%',
    aspectRatio: 1,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 15,
  },
  quickActionText: {
    color: 'white',
    fontSize: 14,
    fontWeight: '600',
    marginTop: 8,
    textAlign: 'center',
  },
  recentContainer: {
    paddingHorizontal: 20,
  },
  shipmentCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 15,
    marginBottom: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  shipmentInfo: {
    flex: 1,
  },
  shipmentId: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1C1C1E',
    marginBottom: 4,
  },
  shipmentDestination: {
    fontSize: 14,
    color: '#8E8E93',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
  },
  statusText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
});

export default HomeScreen;