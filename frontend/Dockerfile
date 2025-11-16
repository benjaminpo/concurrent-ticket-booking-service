# Multi-stage build for Angular application

# Stage 1: Build stage
FROM node:18 AS build

WORKDIR /app

# Copy package files and install dependencies (layer caching optimization)
COPY package*.json ./
RUN npm install && npm cache clean --force

# Copy source code
COPY . .

# Build the Angular application for production
RUN npm run build

# Stage 2: Runtime stage with Nginx
FROM nginx:1.25

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built Angular app from build stage (Angular 17+ outputs to browser subdirectory)
COPY --from=build /app/dist/ticket-booking-frontend/browser /usr/share/nginx/html

# Set permissions for nginx user
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

# Switch to nginx user
USER nginx

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:80 || exit 1

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]

