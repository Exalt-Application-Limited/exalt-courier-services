# Multi-stage build for Regional Admin React Application

# Stage 1: Build stage
FROM node:18-alpine AS build

# Set working directory
WORKDIR /app

# Install dependencies for better caching
COPY package*.json ./
RUN npm ci --legacy-peer-deps --silent

# Copy source code
COPY . .

# Set build environment variables
ENV REACT_APP_ENVIRONMENT=production
ENV GENERATE_SOURCEMAP=false
ENV CI=true

# Build the application
RUN npm run build

# Stage 2: Production stage with Nginx
FROM nginx:alpine AS production

# Install security updates
RUN apk update && apk upgrade && apk add --no-cache ca-certificates

# Create nginx user and group
RUN addgroup -g 1001 -S nginx && \
    adduser -S -D -H -u 1001 -h /var/cache/nginx -s /sbin/nologin -G nginx -g nginx nginx

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built application from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy additional nginx configurations if they exist
COPY nginx.conf /etc/nginx/conf.d/default.conf || echo "No custom nginx config found"

# Set proper permissions
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d

# Switch to non-root user
USER nginx

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Expose port
EXPOSE 80

# Labels for better image management
LABEL maintainer="Exalt Application Limited"
LABEL description="Regional Admin Dashboard for Branch and Courier Management"
LABEL version="1.0.0"

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
