require("dotenv").config();
const express = require("express");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");
const { eurekaClient } = require("./eureka-client");

// Initialize Express app
const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan("dev"));

// Health check endpoint
app.get("/health", (req, res) => {
  res.status(200).json({ status: "UP" });
});

// Routes
app.get("/", (req, res) => {
  res.status(200).json({
    service: "courier-location-tracker",
    version: "1.0.0",
    status: "running"
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    error: "Internal Server Error",
    message: err.message
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`courier-location-tracker service running on port ${PORT}`);
  // Register with Eureka if in production
  if (process.env.NODE_ENV === "production") {
    eurekaClient.start();
  }
});

module.exports = app; // For testing
