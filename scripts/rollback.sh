#!/usr/bin/env bash
# ==============================================================================
# Script: rollback.sh
# Purpose: Implements disaster recovery rollback by restoring the previous WAR
# ==============================================================================

set -e

APP_NAME="student-feedback-portal"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/opt/tomcat/webapps}"
BACKUP_DIR="${BACKUP_DIR:-/opt/tomcat/backups}"
TOMCAT_PORT="${TOMCAT_PORT:-8081}"

echo "=========================================================="
echo " Initiating Disaster Recovery Rollback"
echo "=========================================================="

if [ -f "${BACKUP_DIR}/${APP_NAME}.war.bak" ]; then
    echo "Found backup at ${BACKUP_DIR}/${APP_NAME}.war.bak"
    echo "Restoring previous known-stable WAR artifact to ${TOMCAT_WEBAPPS}/${APP_NAME}.war..."
    
    # Remove faulty exploded folder and copy previous WAR
    rm -rf "${TOMCAT_WEBAPPS}/${APP_NAME}"
    cp "${BACKUP_DIR}/${APP_NAME}.war.bak" "${TOMCAT_WEBAPPS}/${APP_NAME}.war"
    
    echo "Restoration complete. Waiting 6 seconds for Tomcat redeployment..."
    sleep 6

    # Verify rollback health
    ./scripts/health-check.sh
    echo "=========================================================="
    echo " Rollback completed successfully!"
    echo "=========================================================="
else
    echo "ERROR: No backup found at ${BACKUP_DIR}/${APP_NAME}.war.bak"
    echo "Please check Jenkins archived artifacts or Git history to retrieve the previous release."
    exit 1
fi
