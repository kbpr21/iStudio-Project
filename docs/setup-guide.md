# Comprehensive Environment Setup Guide (Ubuntu Linux / VM / Cloud)

This guide provides end-to-end setup instructions for installing and configuring **Java, Maven, Git, Apache Tomcat, and Jenkins** on an Ubuntu Linux server or VM instance, as required by the DevOps Internship Project specification.

---

## 1. System Update & Prerequisites

Update package repositories and install essential CLI tools:

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git unzip apt-transport-https ca-certificates gnupg lsb-release
```

---

## 2. Java (OpenJDK 17) Installation

Install OpenJDK 17 LTS:

```bash
sudo apt install -y openjdk-17-jdk openjdk-17-jre
```

### Verification:
```bash
java -version
javac -version
```

*Expected Output:*
```text
openjdk version "17.0.x" ...
OpenJDK Runtime Environment ...
```

Set `JAVA_HOME` in `/etc/environment`:
```bash
echo 'JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"' | sudo tee -a /etc/environment
source /etc/environment
echo $JAVA_HOME
```

---

## 3. Apache Maven Installation

Install Maven:

```bash
sudo apt install -y maven
```

### Verification:
```bash
mvn -version
```

*Expected Output:*
```text
Apache Maven 3.x.x
Maven home: /usr/share/maven
Java version: 17.0.x ...
```

---

## 4. Git Installation & Branching Setup

```bash
sudo apt install -y git
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### Git Branching Model:
Initialize repository and establish the 3-tier branch hierarchy:
```bash
# Initialize local repo
git init

# Main branch (production ready)
git branch -M main

# Create integration branch
git checkout -b dev

# Create feature branches for tasks
git checkout -b feature/feedback-form
```

---

## 5. Apache Tomcat Installation & Port Configuration (Port 8081)

To prevent port collision with Jenkins (`8080`), Tomcat is configured on port **`8081`**.

### 5.1 Create Tomcat User & Download Tomcat 10
```bash
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat
cd /tmp
wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.20/bin/apache-tomcat-10.1.20.tar.gz
sudo tar -xf apache-tomcat-10.1.20.tar.gz -C /opt/tomcat/ --strip-components=1
```

### 5.2 Configure Tomcat Port to 8081
Open `/opt/tomcat/conf/server.xml`:
```bash
sudo nano /opt/tomcat/conf/server.xml
```
Locate the Connector block (around line 69) and change `port="8080"` to `port="8081"`:
```xml
<Connector port="8081" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443"
           maxParameterCount="1000" />
```

### 5.3 Configure Tomcat Users & Roles
Edit `/opt/tomcat/conf/tomcat-users.xml`:
```xml
<tomcat-users>
    <role rolename="manager-gui"/>
    <role rolename="manager-script"/>
    <role rolename="admin-gui"/>
    <user username="admin" password="AdminPassword123!" roles="manager-gui,manager-script,admin-gui"/>
</tomcat-users>
```

### 5.4 Create Systemd Service for Tomcat
Create `/etc/systemd/system/tomcat.service`:
```ini
[Unit]
Description=Apache Tomcat 10 Web Application Container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom"
Environment="CATALINA_BASE=/opt/tomcat"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"
Environment="CATALINA_OPTS=-Xms256m -Xmx512m -server -XX:+UseParallelGC"

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

RestartSec=10
Restart=always

[Install]
WantedBy=multi-user.target
```

### 5.5 Set Permissions and Start Tomcat
```bash
sudo chown -R tomcat:tomcat /opt/tomcat
sudo chmod -R u+x /opt/tomcat/bin
sudo systemctl daemon-reload
sudo systemctl enable --now tomcat
```

### Verification:
```bash
sudo systemctl status tomcat
curl -I http://localhost:8081
```

---

## 6. Jenkins Installation & Configuration (Port 8080)

### 6.1 Install Jenkins
```bash
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/" | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

sudo apt update
sudo apt install -y jenkins
sudo systemctl enable --now jenkins
```

### 6.2 Unlock Jenkins
```bash
sudo systemctl status jenkins
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```
1. Open browser at `http://<server-ip>:8080`.
2. Paste the initial admin password.
3. Select **Install suggested plugins**.
4. Create your administrator account.

---

## 7. Permissions & Deployment Access

To allow Jenkins to deploy files directly into Tomcat's webapps folder:

```bash
# 1. Add jenkins user to the tomcat group
sudo usermod -aG tomcat jenkins

# 2. Grant write permissions to webapps and backup directories
sudo chmod -R 775 /opt/tomcat/webapps
sudo mkdir -p /opt/tomcat/backups
sudo chown -R tomcat:tomcat /opt/tomcat/backups
sudo chmod -R 775 /opt/tomcat/backups

# 3. Restart Jenkins to apply group changes
sudo systemctl restart jenkins
```

---

## 8. Jenkins Global Tool Configuration

Navigate to **Manage Jenkins** &rarr; **Tools**:
1. **JDK**:
   - Name: `JDK-17`
   - JAVA_HOME: `/usr/lib/jvm/java-17-openjdk-amd64`
2. **Maven**:
   - Name: `Maven-3.9.11`
   - MAVEN_HOME: `/usr/share/maven` (or check *Install automatically*)

---

## 9. Creating the CI/CD Pipeline Job

1. Go to Jenkins Dashboard &rarr; **New Item**.
2. Enter name: `student-feedback-portal-pipeline`.
3. Select **Pipeline** &rarr; Click **OK**.
4. Scroll to **Pipeline** section:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `<Your Git Repository URL>`
   - Branch Specifier: `*/main` (or `*/dev`)
   - Script Path: `Jenkinsfile`
5. Click **Save** and trigger **Build Now**.

---

## 10. Local Alternative: Docker Compose

If running locally on Docker Desktop, launch both services with:

```bash
docker compose up -d
```

- **Jenkins**: `http://localhost:8080`
- **Tomcat**: `http://localhost:8081/student-feedback-portal/`
- **Health API**: `http://localhost:8081/student-feedback-portal/health`
