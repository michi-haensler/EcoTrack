# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS build

WORKDIR /app

# Abhängigkeiten vorab cachen
COPY package.json package-lock.json* ./
RUN npm ci --silent

# Quellcode kopieren
COPY . .

# Umgebungsvariablen für den Build
# Relativ /api → wird von nginx auf backend weitergeleitet
ARG VITE_API_BASE_URL=/api
ARG VITE_KEYCLOAK_URL=http://localhost:8180
ARG VITE_KEYCLOAK_REALM=ecotrack
ARG VITE_KEYCLOAK_CLIENT_ID=ecotrack-admin-web

ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_KEYCLOAK_URL=$VITE_KEYCLOAK_URL
ENV VITE_KEYCLOAK_REALM=$VITE_KEYCLOAK_REALM
ENV VITE_KEYCLOAK_CLIENT_ID=$VITE_KEYCLOAK_CLIENT_ID

RUN npm run build

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime (nginx)
# ─────────────────────────────────────────────────────────────────────────────
FROM nginx:1.27-alpine AS runtime

# Nginx-Konfiguration einfügen
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Gebaute App-Dateien
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
