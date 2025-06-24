# E-commerce Application

A full-stack e-commerce application built with Spring Boot backend and React frontend, featuring product management, order processing, and VNPay payment integration.

## 🏗️ Architecture

- **Backend**: Spring Boot 3.4.4 with Java 21
- **Frontend**: React 19 with TypeScript and Vite
- **Database**: SQLite
- **UI Framework**: Tailwind CSS with Radix UI components
- **State Management**: Zustand
- **Payment Gateway**: VNPay (Vietnam)

## 📋 Prerequisites

Make sure you have the following installed on your system:

- **Java 21** or higher
- **Maven 3.6+** 
- **Node.js 18+** and **npm**
- **Git**

### Optional (Recommended)
- **mise** (for version management) - Install from [mise.jq.rs](https://mise.jq.rs/)

## 🚀 Quick Start

### Using mise (Recommended)

If you have mise installed, it will automatically manage Java and Maven versions:

```bash
# Clone the repository
git clone <repository-url>
cd Backend/demo

# mise will automatically install Java 21 and Maven based on mise.toml
mise install

# Verify installations
java --version
mvn --version
```

### Manual Setup

If not using mise:

```bash
# Ensure Java 21 is installed and active
java --version

# Ensure Maven is installed
mvn --version
```

## 🛠️ Installation & Setup

### 1. Backend Setup

```bash
# Navigate to the project root
cd Backend/demo

# Install Maven dependencies
mvn clean install

# Initialize the database (optional - will be created automatically)
# The database file will be created at ./data/ITSS_demo2.db
```

### 2. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install npm dependencies
npm install
```

## 🎯 Running the Application

### Development Mode

#### Option 1: Run Backend and Frontend Separately

**Terminal 1 - Backend:**
```bash
# From project root (Backend/demo)
mvn spring-boot:run
```
The backend will start on `http://localhost:8080`

**Terminal 2 - Frontend:**
```bash
# From frontend directory
cd frontend
npm run dev
```
The frontend will start on `http://localhost:3000`

#### Option 2: Run Both with a Single Command

```bash
# From project root, you can create a script to run both
# Backend in background
mvn spring-boot:run &

# Frontend in foreground
cd frontend && npm run dev
```

### Production Build

#### Backend:
```bash
# Create production JAR
mvn clean package

# Run the JAR file
java -jar target/merged-project-0.0.1-SNAPSHOT.jar
```

#### Frontend:
```bash
cd frontend

# Build for production
npm run build

# Preview the build (optional)
npm run preview
```

## 🗄️ Database Configuration

The application uses SQLite as the database:

- **Database file**: `./data/ITSS_demo2.db`
- **Auto-initialization**: Database schema is created automatically on first run
- **Sample data**: Located in `src/main/resources/data.sql`

### Database Schema

The database includes tables for:
- Users (with role-based access)
- Products (Books, CDs, DVDs)
- Orders and Order Lines
- Invoices
- Delivery Information

## 🔧 Configuration

### Backend Configuration

Key configuration files:
- `src/main/resources/application.properties` - Main application settings
- `pom.xml` - Maven dependencies and build configuration

Important settings:
```properties
# Database
spring.datasource.url=jdbc:sqlite:./data/ITSS_demo2.db

# Server
server.port=8080

# VNPay (for payment processing)
vnpay.pay-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8080/vnpay/return
```

### Frontend Configuration

- `vite.config.ts` - Vite build configuration
- `tailwind.config.js` - Tailwind CSS configuration
- `tsconfig.json` - TypeScript configuration

## 📡 API Endpoints

The backend provides REST API endpoints:

- **Products**: `/api/products/*`
- **Users**: `/api/users/*`
- **Orders**: `/api/orders/*`
- **Invoices**: `/api/invoices/*`
- **Payment**: `/api/payment/*`

## 🎨 Frontend Features

- **Product Catalog**: Browse books, CDs, and DVDs
- **Shopping Cart**: Add/remove items with Zustand state management
- **User Authentication**: Login/register functionality
- **Order Management**: Place and track orders
- **Payment Integration**: VNPay payment processing
- **Responsive Design**: Mobile-friendly UI with Tailwind CSS

## 🧪 Development

### Code Style & Linting

#### Frontend:
```bash
cd frontend

# Run ESLint
npm run lint

# Format code (if prettier is configured)
npm run format
```

#### Backend:
The project uses standard Java/Spring Boot conventions.

### Testing

```bash
# Backend tests
mvn test

# Frontend tests (if configured)
cd frontend
npm test
```

## 🔍 Troubleshooting

### Common Issues

1. **Port already in use**:
   - Backend (8080): Change `server.port` in `application.properties`
   - Frontend (3000): Vite will prompt for alternative port

2. **Database connection issues**:
   - Ensure `./data/` directory exists
   - Check file permissions for SQLite database

3. **Build failures**:
   - Verify Java 21 is being used: `java --version`
   - Clear Maven cache: `mvn clean`
   - Clear npm cache: `npm cache clean --force`

4. **CORS issues**:
   - Check `CorsConfig.java` in the backend
   - Ensure frontend URL is allowed in CORS configuration

### Logs

- **Backend logs**: Console output from Spring Boot
- **Frontend logs**: Browser console and terminal output

## 📦 Project Structure

```
Backend/demo/
├── src/main/java/com/itss/ecommerce/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA entities
│   ├── repository/     # Data repositories
│   └── service/        # Business logic
├── src/main/resources/
│   ├── application.properties
│   └── data.sql
├── frontend/
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── pages/      # Page components
│   │   ├── services/   # API services
│   │   ├── stores/     # Zustand stores
│   │   └── types/      # TypeScript types
│   └── public/         # Static assets
└── data/               # SQLite database
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Check the troubleshooting section above
- Review the logs for error details
- Create an issue in the project repository

---

**Happy coding! 🚀**
