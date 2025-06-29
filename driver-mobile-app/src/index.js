/**
 * Driver Mobile App Backend Server
 * This is the entry point for the Node.js backend that supports the driver mobile app.
 * It implements resilience patterns, including circuit breakers.
 */

const express = require('express');
const morgan = require('morgan');
const config = require('./config');
const logger = require('./utils/logger');
const { resetAllBreakers } = require('./utils/circuitBreaker');

// Create Express app
const app = express();

// Middleware
app.use(express.json());
app.use(morgan('combined', { stream: { write: message => logger.info(message.trim()) } }));

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'UP', timestamp: new Date().toISOString() });
});

// Circuit breaker admin endpoint
app.post('/admin/circuit-breakers/reset', (req, res) => {
  logger.info('Resetting all circuit breakers');
  resetAllBreakers();
  res.status(200).json({ message: 'All circuit breakers have been reset' });
});

// TODO: Add API routes
// app.use('/api/routes', require('./routes/routeRoutes'));
// app.use('/api/couriers', require('./routes/courierRoutes'));
// app.use('/api/tracking', require('./routes/trackingRoutes'));

// Error handling middleware
app.use((err, req, res, next) => {
  logger.error(`Error processing request: ${err.message}`, { 
    stack: err.stack,
    path: req.path,
    method: req.method
  });
  
  res.status(err.status || 500).json({
    error: {
      message: err.message || 'Internal Server Error',
      status: err.status || 500
    }
  });
});

// Start the server
const { port, host } = config.server;
app.listen(port, host, () => {
  logger.info(`Driver Mobile App backend server started on ${host}:${port}`);
  logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`);
  logger.info(`Circuit breaker default settings: ${JSON.stringify(config.circuitBreaker.default)}`);
});

// Handle graceful shutdown
process.on('SIGTERM', () => {
  logger.info('SIGTERM signal received. Shutting down gracefully.');
  // Close resources, DB connections, etc.
  process.exit(0);
});

process.on('SIGINT', () => {
  logger.info('SIGINT signal received. Shutting down gracefully.');
  // Close resources, DB connections, etc.
  process.exit(0);
});

process.on('unhandledRejection', (reason, promise) => {
  logger.error('Unhandled Promise Rejection', { reason });
});

process.on('uncaughtException', (error) => {
  logger.error('Uncaught Exception', { error });
  // For uncaught exceptions, it's often safest to exit
  process.exit(1);
}); 