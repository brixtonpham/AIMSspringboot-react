#!/bin/bash

# ITSS E-commerce Application Startup Script
# This script starts both the Java Spring Boot backend and React frontend

echo "🚀 Starting ITSS E-commerce Application..."
echo "================================================"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if required tools are installed
check_requirements() {
    echo -e "${BLUE}📋 Checking requirements...${NC}"
    
    # Check Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java is not installed or not in PATH${NC}"
        exit 1
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven is not installed or not in PATH${NC}"
        exit 1
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js is not installed or not in PATH${NC}"
        exit 1
    fi
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        echo -e "${RED}❌ npm is not installed or not in PATH${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ All requirements met${NC}"
}

# Start the Spring Boot backend
start_backend() {
    echo -e "\n${BLUE}🔧 Starting Spring Boot Backend...${NC}"
    
    # Navigate to project root (where pom.xml is located)
    cd "$(dirname "$0")"
    
    # Check if database file exists
    if [ ! -f "data/ITSS_demo2.db" ]; then
        echo -e "${YELLOW}⚠️  Database file not found. Creating database...${NC}"
        mkdir -p data
    fi
    
    # Start Spring Boot application in background
    echo -e "${BLUE}📦 Building and starting backend server...${NC}"
    nohup mvn spring-boot:run > backend.log 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > .backend.pid
    
    echo -e "${GREEN}✅ Backend starting with PID: $BACKEND_PID${NC}"
    echo -e "${BLUE}📝 Backend logs: backend.log${NC}"
    echo -e "${BLUE}🌐 Backend URL: http://localhost:8080${NC}"
}

# Start the React frontend
start_frontend() {
    echo -e "\n${BLUE}🎨 Starting React Frontend...${NC}"
    
    # Navigate to frontend directory
    cd frontend
    
    # Install dependencies if node_modules doesn't exist
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}📦 Installing frontend dependencies...${NC}"
        npm install
    fi
    
    # Start React development server in background
    echo -e "${BLUE}🚀 Starting frontend development server...${NC}"
    nohup npm run dev > ../frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > ../.frontend.pid
    
    echo -e "${GREEN}✅ Frontend starting with PID: $FRONTEND_PID${NC}"
    echo -e "${BLUE}📝 Frontend logs: frontend.log${NC}"
    echo -e "${BLUE}🌐 Frontend URL: http://localhost:5173${NC}"
    
    cd ..
}

# Wait for services to start
wait_for_services() {
    echo -e "\n${YELLOW}⏳ Waiting for services to start...${NC}"
    
    # Wait for backend (check port 8080)
    echo -e "${BLUE}🔍 Checking backend service...${NC}"
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/products > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Backend is ready!${NC}"
            break
        fi
        if [ $i -eq 30 ]; then
            echo -e "${RED}❌ Backend failed to start after 30 seconds${NC}"
            echo -e "${YELLOW}📝 Check backend.log for errors${NC}"
        fi
        sleep 1
    done
    
    # Wait for frontend (check port 5173)
    echo -e "${BLUE}🔍 Checking frontend service...${NC}"
    for i in {1..20}; do
        if curl -s http://localhost:5173 > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Frontend is ready!${NC}"
            break
        fi
        if [ $i -eq 20 ]; then
            echo -e "${RED}❌ Frontend failed to start after 20 seconds${NC}"
            echo -e "${YELLOW}📝 Check frontend.log for errors${NC}"
        fi
        sleep 1
    done
}

# Show status and instructions
show_status() {
    echo -e "\n${GREEN}🎉 ITSS E-commerce Application Started!${NC}"
    echo "================================================"
    echo -e "${BLUE}📊 Service Status:${NC}"
    echo -e "   🔧 Backend:  http://localhost:8080"
    echo -e "   🎨 Frontend: http://localhost:5173"
    echo ""
    echo -e "${BLUE}📝 Log Files:${NC}"
    echo -e "   🔧 Backend:  backend.log"
    echo -e "   🎨 Frontend: frontend.log"
    echo ""
    echo -e "${BLUE}🛠️  Management:${NC}"
    echo -e "   ⏹️  Stop services: ./stop.sh"
    echo -e "   📊 View backend logs: tail -f backend.log"
    echo -e "   📊 View frontend logs: tail -f frontend.log"
    echo ""
    echo -e "${YELLOW}💡 Access the application at: http://localhost:5173${NC}"
    echo "================================================"
}

# Main execution
main() {
    check_requirements
    start_backend
    start_frontend
    wait_for_services
    show_status
}

# Handle script interruption
trap 'echo -e "\n${RED}🛑 Startup interrupted. Cleaning up...${NC}"; ./stop.sh; exit 1' INT

# Run main function
main