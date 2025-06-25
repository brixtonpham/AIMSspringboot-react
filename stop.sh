#!/bin/bash

# ITSS E-commerce Application Shutdown Script
# This script stops both the Java Spring Boot backend and React frontend

echo "🛑 Stopping ITSS E-commerce Application..."
echo "================================================"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Navigate to project root
cd "$(dirname "$0")"

# Stop backend service
stop_backend() {
    echo -e "${BLUE}🔧 Stopping Spring Boot Backend...${NC}"
    
    if [ -f ".backend.pid" ]; then
        BACKEND_PID=$(cat .backend.pid)
        if ps -p $BACKEND_PID > /dev/null 2>&1; then
            echo -e "${YELLOW}⏹️  Stopping backend process (PID: $BACKEND_PID)...${NC}"
            kill $BACKEND_PID
            
            # Wait for graceful shutdown
            for i in {1..10}; do
                if ! ps -p $BACKEND_PID > /dev/null 2>&1; then
                    echo -e "${GREEN}✅ Backend stopped gracefully${NC}"
                    break
                fi
                if [ $i -eq 10 ]; then
                    echo -e "${YELLOW}⚠️  Force stopping backend...${NC}"
                    kill -9 $BACKEND_PID 2>/dev/null
                fi
                sleep 1
            done
        else
            echo -e "${YELLOW}⚠️  Backend process not running${NC}"
        fi
        rm -f .backend.pid
    else
        echo -e "${YELLOW}⚠️  Backend PID file not found${NC}"
    fi
    
    # Also try to kill any remaining Spring Boot processes
    SPRING_PIDS=$(pgrep -f "spring-boot:run")
    if [ ! -z "$SPRING_PIDS" ]; then
        echo -e "${YELLOW}🧹 Cleaning up remaining Spring Boot processes...${NC}"
        echo $SPRING_PIDS | xargs kill 2>/dev/null
    fi
}

# Stop frontend service
stop_frontend() {
    echo -e "${BLUE}🎨 Stopping React Frontend...${NC}"
    
    if [ -f ".frontend.pid" ]; then
        FRONTEND_PID=$(cat .frontend.pid)
        if ps -p $FRONTEND_PID > /dev/null 2>&1; then
            echo -e "${YELLOW}⏹️  Stopping frontend process (PID: $FRONTEND_PID)...${NC}"
            kill $FRONTEND_PID
            
            # Wait for graceful shutdown
            for i in {1..5}; do
                if ! ps -p $FRONTEND_PID > /dev/null 2>&1; then
                    echo -e "${GREEN}✅ Frontend stopped gracefully${NC}"
                    break
                fi
                if [ $i -eq 5 ]; then
                    echo -e "${YELLOW}⚠️  Force stopping frontend...${NC}"
                    kill -9 $FRONTEND_PID 2>/dev/null
                fi
                sleep 1
            done
        else
            echo -e "${YELLOW}⚠️  Frontend process not running${NC}"
        fi
        rm -f .frontend.pid
    else
        echo -e "${YELLOW}⚠️  Frontend PID file not found${NC}"
    fi
    
    # Also try to kill any remaining Vite/Node processes on port 5173
    VITE_PIDS=$(lsof -ti:5173 2>/dev/null)
    if [ ! -z "$VITE_PIDS" ]; then
        echo -e "${YELLOW}🧹 Cleaning up processes on port 5173...${NC}"
        echo $VITE_PIDS | xargs kill 2>/dev/null
    fi
}

# Kill any processes using the application ports
cleanup_ports() {
    echo -e "${BLUE}🧹 Cleaning up ports...${NC}"
    
    # Check and kill processes on port 8080 (backend)
    BACKEND_PORT_PIDS=$(lsof -ti:8080 2>/dev/null)
    if [ ! -z "$BACKEND_PORT_PIDS" ]; then
        echo -e "${YELLOW}🔧 Freeing port 8080...${NC}"
        echo $BACKEND_PORT_PIDS | xargs kill 2>/dev/null
    fi
    
    # Check and kill processes on port 5173 (frontend)
    FRONTEND_PORT_PIDS=$(lsof -ti:5173 2>/dev/null)
    if [ ! -z "$FRONTEND_PORT_PIDS" ]; then
        echo -e "${YELLOW}🎨 Freeing port 5173...${NC}"
        echo $FRONTEND_PORT_PIDS | xargs kill 2>/dev/null
    fi
}

# Clean up log files (optional)
cleanup_logs() {
    if [ "$1" = "--clean-logs" ]; then
        echo -e "${BLUE}🧹 Cleaning up log files...${NC}"
        rm -f backend.log frontend.log
        echo -e "${GREEN}✅ Log files removed${NC}"
    fi
}

# Show final status
show_status() {
    echo -e "\n${GREEN}✅ ITSS E-commerce Application Stopped!${NC}"
    echo "================================================"
    echo -e "${BLUE}📊 Port Status:${NC}"
    
    # Check if ports are free
    if ! lsof -i:8080 &>/dev/null; then
        echo -e "   🔧 Port 8080: ${GREEN}Free${NC}"
    else
        echo -e "   🔧 Port 8080: ${RED}Still in use${NC}"
    fi
    
    if ! lsof -i:5173 &>/dev/null; then
        echo -e "   🎨 Port 5173: ${GREEN}Free${NC}"
    else
        echo -e "   🎨 Port 5173: ${RED}Still in use${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}📝 Log Files:${NC}"
    if [ -f "backend.log" ]; then
        echo -e "   🔧 Backend logs: backend.log ($(wc -l < backend.log) lines)"
    fi
    if [ -f "frontend.log" ]; then
        echo -e "   🎨 Frontend logs: frontend.log ($(wc -l < frontend.log) lines)"
    fi
    
    echo ""
    echo -e "${BLUE}🛠️  Commands:${NC}"
    echo -e "   🚀 Start services: ./start.sh"
    echo -e "   🧹 Clean logs: ./stop.sh --clean-logs"
    echo "================================================"
}

# Display usage information
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --clean-logs    Remove log files after stopping services"
    echo "  --help         Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                  # Stop services, keep logs"
    echo "  $0 --clean-logs     # Stop services and remove logs"
}

# Main execution
main() {
    # Parse command line arguments
    case "$1" in
        --help)
            show_usage
            exit 0
            ;;
        --clean-logs)
            CLEAN_LOGS="--clean-logs"
            ;;
        "")
            # No arguments, proceed normally
            ;;
        *)
            echo -e "${RED}❌ Unknown option: $1${NC}"
            show_usage
            exit 1
            ;;
    esac
    
    stop_backend
    stop_frontend
    cleanup_ports
    cleanup_logs $CLEAN_LOGS
    show_status
}

# Handle script interruption
trap 'echo -e "\n${RED}🛑 Shutdown interrupted${NC}"; exit 1' INT

# Run main function with all arguments
main "$@"