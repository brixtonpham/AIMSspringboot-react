# ITSS E-commerce Application Management Scripts

This document explains how to use the startup and shutdown scripts for the ITSS E-commerce application.

## 📋 Prerequisites

Before running the scripts, ensure you have the following installed:

- **Java 11+** - For running the Spring Boot backend
- **Maven 3.6+** - For building and running the Spring Boot application
- **Node.js 16+** - For running the React frontend
- **npm** - For managing frontend dependencies

## 🚀 Starting the Application

### Quick Start
```bash
./start.sh
```

### What the start script does:
1. **Checks prerequisites** - Verifies Java, Maven, Node.js, and npm are installed
2. **Starts backend** - Builds and runs the Spring Boot application on port 8080
3. **Starts frontend** - Installs dependencies (if needed) and runs React dev server on port 5173
4. **Health checks** - Waits for both services to be ready
5. **Shows status** - Displays URLs and management information

### Expected Output:
```
🚀 Starting ITSS E-commerce Application...
================================================
📋 Checking requirements...
✅ All requirements met

🔧 Starting Spring Boot Backend...
📦 Building and starting backend server...
✅ Backend starting with PID: 12345
📝 Backend logs: backend.log
🌐 Backend URL: http://localhost:8080

🎨 Starting React Frontend...
🚀 Starting frontend development server...
✅ Frontend starting with PID: 12346
📝 Frontend logs: frontend.log
🌐 Frontend URL: http://localhost:5173

⏳ Waiting for services to start...
🔍 Checking backend service...
✅ Backend is ready!
🔍 Checking frontend service...
✅ Frontend is ready!

🎉 ITSS E-commerce Application Started!
================================================
📊 Service Status:
   🔧 Backend:  http://localhost:8080
   🎨 Frontend: http://localhost:5173

📝 Log Files:
   🔧 Backend:  backend.log
   🎨 Frontend: frontend.log

🛠️  Management:
   ⏹️  Stop services: ./stop.sh
   📊 View backend logs: tail -f backend.log
   📊 View frontend logs: tail -f frontend.log

💡 Access the application at: http://localhost:5173
================================================
```

## 🛑 Stopping the Application

### Quick Stop
```bash
./stop.sh
```

### Stop with Log Cleanup
```bash
./stop.sh --clean-logs
```

### What the stop script does:
1. **Stops backend** - Gracefully shuts down Spring Boot application
2. **Stops frontend** - Terminates React development server
3. **Cleans ports** - Ensures ports 8080 and 5173 are freed
4. **Optional cleanup** - Removes log files if `--clean-logs` flag is used
5. **Shows status** - Displays final status and available commands

### Expected Output:
```
🛑 Stopping ITSS E-commerce Application...
================================================
🔧 Stopping Spring Boot Backend...
⏹️  Stopping backend process (PID: 12345)...
✅ Backend stopped gracefully

🎨 Stopping React Frontend...
⏹️  Stopping frontend process (PID: 12346)...
✅ Frontend stopped gracefully

🧹 Cleaning up ports...

✅ ITSS E-commerce Application Stopped!
================================================
📊 Port Status:
   🔧 Port 8080: Free
   🎨 Port 5173: Free

📝 Log Files:
   🔧 Backend logs: backend.log (150 lines)
   🎨 Frontend logs: frontend.log (75 lines)

🛠️  Commands:
   🚀 Start services: ./start.sh
   🧹 Clean logs: ./stop.sh --clean-logs
================================================
```

## 📊 Monitoring and Debugging

### View Live Logs
```bash
# Backend logs
tail -f backend.log

# Frontend logs
tail -f frontend.log
```

### Check Service Status
```bash
# Check if ports are in use
lsof -i:8080  # Backend
lsof -i:5173  # Frontend

# Check running processes
ps aux | grep spring-boot
ps aux | grep node
```

### Manual Service Management
```bash
# Start only backend
mvn spring-boot:run

# Start only frontend
cd frontend && npm run dev
```

## 🔧 Troubleshooting

### Common Issues

**1. Port Already in Use**
```bash
# Kill processes on port 8080
lsof -ti:8080 | xargs kill

# Kill processes on port 5173
lsof -ti:5173 | xargs kill
```

**2. Backend Won't Start**
- Check Java version: `java -version`
- Check Maven version: `mvn -version`
- View backend logs: `tail -f backend.log`
- Check database file exists: `ls -la data/ITSS_demo2.db`

**3. Frontend Won't Start**
- Check Node.js version: `node -version`
- Check npm version: `npm -version`
- Clear npm cache: `npm cache clean --force`
- Delete node_modules: `rm -rf frontend/node_modules && cd frontend && npm install`

**4. Services Start But Don't Respond**
- Wait longer for startup (especially backend on first run)
- Check firewall settings
- Verify no proxy/VPN interference

### Script Options

**Start Script:**
- No options available (automatically handles all setup)

**Stop Script:**
- `--clean-logs` - Remove log files after stopping
- `--help` - Show usage information

## 🌐 Application URLs

Once started, access the application at:

- **Frontend (Main App)**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/api
- **Database**: SQLite file at `data/ITSS_demo2.db`

## 📁 File Structure

```
Backend/demo/
├── start.sh              # Startup script
├── stop.sh               # Shutdown script
├── backend.log           # Backend logs (created on first run)
├── frontend.log          # Frontend logs (created on first run)
├── .backend.pid          # Backend process ID (temporary)
├── .frontend.pid         # Frontend process ID (temporary)
├── pom.xml               # Maven configuration
├── src/                  # Java source code
├── frontend/             # React application
└── data/                 # Database files
```

## 🔐 Security Notes

- Scripts use process IDs for service management
- Log files may contain sensitive information
- Default ports (8080, 5173) are accessible from localhost only
- Use `./stop.sh --clean-logs` to remove logs containing sensitive data