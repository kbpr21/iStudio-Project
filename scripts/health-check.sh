#!/usr/bin/env bash
# ==============================================================================
# Script: health-check.sh
# Purpose: Verifies application health endpoint and HTTP status
# ==============================================================================

TOMCAT_PORT="${TOMCAT_PORT:-8081}"
APP_NAME="${APP_NAME:-student-feedback-portal}"
HEALTH_URL="http://localhost:${TOMCAT_PORT}/${APP_NAME}/health"

echo "Checking Health Endpoint: ${HEALTH_URL}"
echo "----------------------------------------------------------"

HTTP_STATUS=$(curl -s -o /tmp/health_out.json -w "%{http_code}" "${HEALTH_URL}" || echo "000")

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo " [PASSED] Application is HEALTHY (HTTP 200 OK)"
    cat /tmp/health_out.json
    echo ""
    exit 0
else
    echo " [FAILED] Application returned HTTP ${HTTP_STATUS}"
    if [ -f /tmp/health_out.json ]; then
        cat /tmp/health_out.json
        echo ""
    fi
    exit 1
fi
