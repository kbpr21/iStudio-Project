#!/usr/bin/env bash
# ==============================================================================
# Script: deploy-to-tomcat.sh
# Purpose: Builds, packages, backs up, and deploys the WAR to Apache Tomcat
# ==============================================================================

set -e

APP_NAME="student-feedback-portal"
WAR_FILE="target/${APP_NAME}.war"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/opt/tomcat/webapps}"
BACKUP_DIR="${BACKUP_DIR:-/opt/tomcat/backups}"
TOMCAT_PORT="${TOMCAT_PORT:-8081}"
HEALTH_URL="http://localhost:${TOMCAT_PORT}/${APP_NAME}/health"

echo "=========================================================="
echo " Starting Build & Automated Tomcat Deployment"
echo "=========================================================="

# Step 1: Run Maven Build and Test
echo "[1/5] Compiling and running unit tests with Maven..."
mvn clean test

# Step 2: Package WAR
echo "[2/5] Packaging application into WAR artifact..."
mvn package -DskipTests

if [ ! -f "${WAR_FILE}" ]; then
    echo "ERROR: ${WAR_FILE} was not generated!"
    exit 1
fi

# Step 3: Create Backup
echo "[3/5] Backing up existing deployment if present..."
mkdir -p "${BACKUP_DIR}"
if [ -f "${TOMCAT_WEBAPPS}/${APP_NAME}.war" ]; then
    cp "${TOMCAT_WEBAPPS}/${APP_NAME}.war" "${BACKUP_DIR}/${APP_NAME}.war.bak"
    echo "Saved backup to ${BACKUP_DIR}/${APP_NAME}.war.bak"
fi

# Step 4: Deploy WAR
echo "[4/5] Deploying ${WAR_FILE} to ${TOMCAT_WEBAPPS}/..."
cp "${WAR_FILE}" "${TOMCAT_WEBAPPS}/${APP_NAME}.war"
echo "WAR copied successfully. Waiting for Tomcat extraction..."
sleep 6

# Step 5: Post-Deployment Health Check
echo "[5/5] Executing post-deployment health check against ${HEALTH_URL}..."
HTTP_STATUS=$(curl -s -o /tmp/health_check.json -w "%{http_code}" "${HEALTH_URL}" || echo "000")

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo "SUCCESS: Deployment verified! Health Check returned HTTP 200 OK."
    cat /tmp/health_check.json
    echo ""
else
    echo "DEPLOYMENT ERROR: Health check failed with HTTP status ${HTTP_STATUS}"
    echo "Attempting automatic rollback..."
    if [ -f "${BACKUP_DIR}/${APP_NAME}.war.bak" ]; then
        cp "${BACKUP_DIR}/${APP_NAME}.war.bak" "${TOMCAT_WEBAPPS}/${APP_NAME}.war"
        echo "Rolled back to previous version."
    fi
    exit 1
fi

echo "=========================================================="
echo " Application is live at: http://localhost:${TOMCAT_PORT}/${APP_NAME}/"
echo "=========================================================="
