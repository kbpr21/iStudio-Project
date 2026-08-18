pipeline {
    agent any

    tools {
        maven 'Maven-3.9.11'
        jdk 'JDK-17'
    }

    environment {
        APP_NAME        = 'student-feedback-portal'
        WAR_NAME        = 'student-feedback-portal.war'
        TOMCAT_HOST     = 'localhost'
        TOMCAT_PORT     = '8081'
        TOMCAT_HOME     = '/opt/tomcat'
        TOMCAT_WEBAPPS  = '/opt/tomcat/webapps'
        BACKUP_DIR      = '/opt/tomcat/backups'
        HEALTH_ENDPOINT = "http://${env.TOMCAT_HOST}:${env.TOMCAT_PORT}/${env.APP_NAME}/health"
        APP_URL         = "http://${env.TOMCAT_HOST}:${env.TOMCAT_PORT}/${env.APP_NAME}/"
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

    stages {

        stage('Stage 1: Checkout') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 1] Checking out code from Git Repository"
                echo "=========================================================="
                checkout scm
                sh '''
                    echo "Git Commit Hash: $(git rev-parse --short HEAD)"
                    echo "Git Branch:      ${GIT_BRANCH:-main}"
                    echo "Commit Message:  $(git log -1 --pretty=%B)"
                    echo "Committer:       $(git log -1 --pretty=%an)"
                '''
            }
        }

        stage('Stage 2: Maven Clean & Compile') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 2] Maven Clean & Compile Source Code"
                echo "=========================================================="
                sh 'mvn clean compile'
            }
        }

        stage('Stage 3: Unit Test') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 3] Executing Unit Test Suite (Quality Gate)"
                echo "=========================================================="
                sh 'mvn test'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
                failure {
                    echo ">>> QUALITY GATE FAILED: Unit tests did not pass! Pipeline execution halted prior to deploy."
                }
            }
        }

        stage('Stage 4: Package WAR') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 4] Packaging Application into WAR file"
                echo "=========================================================="
                sh 'mvn package -DskipTests'
                sh '''
                    if [ ! -f target/${WAR_NAME} ]; then
                        echo "ERROR: Artifact target/${WAR_NAME} was not generated!"
                        exit 1
                    fi
                    echo "Successfully built target/${WAR_NAME} (Size: $(du -h target/${WAR_NAME} | cut -f1))"
                '''
            }
        }

        stage('Stage 5: Archive Artifact') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 5] Archiving Build Artifact in Jenkins"
                echo "=========================================================="
                archiveArtifacts artifacts: "target/${WAR_NAME}", fingerprint: true, onlyIfSuccessful: true
                echo "Archived artifact target/${WAR_NAME} for traceability and rollback points."
            }
        }

        stage('Stage 6: Deploy to Tomcat') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 6] Deploying WAR to Apache Tomcat Server (Port: ${TOMCAT_PORT})"
                echo "=========================================================="
                sh '''
                    # 1. Ensure backup directory exists
                    mkdir -p ${BACKUP_DIR}

                    # 2. Backup currently running WAR if it exists (for automated rollback)
                    if [ -f "${TOMCAT_WEBAPPS}/${WAR_NAME}" ]; then
                        echo "Creating backup of current active deployment..."
                        cp "${TOMCAT_WEBAPPS}/${WAR_NAME}" "${BACKUP_DIR}/${WAR_NAME}.previous"
                    fi

                    # 3. Copy newly packaged WAR into Tomcat webapps directory
                    echo "Deploying target/${WAR_NAME} to ${TOMCAT_WEBAPPS}/..."
                    cp target/${WAR_NAME} "${TOMCAT_WEBAPPS}/${WAR_NAME}"

                    # 4. Allow Tomcat auto-deploy to extract WAR
                    echo "Deployment copy complete. Waiting 8 seconds for Tomcat auto-deploy..."
                    sleep 8
                '''
            }
        }

        stage('Stage 7: Health Check Verification') {
            steps {
                echo "=========================================================="
                echo " [CI/CD Stage 7] Post-Deployment Health Check Verification"
                echo " Endpoint: ${HEALTH_ENDPOINT}"
                echo "=========================================================="
                sh '''
                    MAX_ATTEMPTS=6
                    ATTEMPT=1
                    SUCCESS=0

                    while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
                        echo "Attempt $ATTEMPT of $MAX_ATTEMPTS: Checking ${HEALTH_ENDPOINT}..."
                        HTTP_STATUS=$(curl -s -o /tmp/health_response.json -w "%{http_code}" "${HEALTH_ENDPOINT}" || echo "000")

                        if [ "$HTTP_STATUS" -eq 200 ]; then
                            echo "SUCCESS: Health Check returned HTTP 200 OK!"
                            cat /tmp/health_response.json
                            echo ""
                            SUCCESS=1
                            break
                        else
                            echo "Warning: Received HTTP status ${HTTP_STATUS}. Waiting 5 seconds before retry..."
                            sleep 5
                            ATTEMPT=$((ATTEMPT + 1))
                        fi
                    done

                    if [ $SUCCESS -ne 1 ]; then
                        echo "FATAL ERROR: Health check failed after $MAX_ATTEMPTS attempts!"
                        exit 1
                    fi
                '''
            }
        }

    }

    post {
        success {
            echo "=========================================================="
            echo "  BUILD & DEPLOYMENT SUCCESSFUL!"
            echo "  Application URL: ${APP_URL}"
            echo "  Health Endpoint: ${HEALTH_ENDPOINT}"
            echo "=========================================================="
        }
        failure {
            echo "=========================================================="
            echo "  PIPELINE FAILED! TRIGGERING ROLLBACK PROCEDURE..."
            echo "=========================================================="
            sh '''
                if [ -f "${BACKUP_DIR}/${WAR_NAME}.previous" ]; then
                    echo "Restoring previous known good WAR artifact from backup..."
                    cp "${BACKUP_DIR}/${WAR_NAME}.previous" "${TOMCAT_WEBAPPS}/${WAR_NAME}"
                    echo "Restored previous build to ${TOMCAT_WEBAPPS}/${WAR_NAME}"
                else
                    echo "No previous backup found at ${BACKUP_DIR}/${WAR_NAME}.previous"
                fi
            '''
        }
        always {
            cleanWs deleteDirs: false, notFailBuild: true
        }
    }
}
