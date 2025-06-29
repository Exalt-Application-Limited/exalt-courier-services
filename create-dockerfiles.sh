#!/bin/bash

echo "=== CREATING DOCKERFILE CONFIGURATIONS FOR FRONTEND APPS ==="
echo ""

# React app Dockerfile template
create_react_dockerfile() {
    local app_path=$1
    cat > "$app_path/Dockerfile" << 'EOF'
# Build stage
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci --legacy-peer-deps
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
EOF
    echo "✅ Created Dockerfile for $app_path"
}

# Node.js app Dockerfile template
create_node_dockerfile() {
    local app_path=$1
    cat > "$app_path/Dockerfile" << 'EOF'
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --production
COPY . .
EXPOSE 3001
CMD ["node", "src/index.js"]
EOF
    echo "✅ Created Dockerfile for $app_path"
}

# Create Dockerfiles for React apps
for app in corporate-admin global-hq-admin regional-admin; do
    create_react_dockerfile "$app"
done

# Create Dockerfile for Node.js backend
create_node_dockerfile "driver-mobile-app"

# Create nginx config for React apps
for app in corporate-admin global-hq-admin regional-admin; do
    cat > "$app/nginx.conf" << 'EOF'
server {
    listen 80;
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /usr/share/nginx/html;
    }
}
EOF
    echo "✅ Created nginx.conf for $app"
done

echo ""
echo "🎉 All Dockerfile configurations have been created!"