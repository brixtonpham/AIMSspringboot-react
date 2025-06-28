# ITSS E-commerce Application

A full-stack e-commerce application built with **Spring Boot 3.4.4** backend and **React 19.1.0** frontend, featuring VNPay payment integration, user management, and modern UI components.

## 🚀 Features

### Core Features
- **User Management**: Registration, login, profile management with role-based access control
- **Product Management**: Browse products, detailed product views, search and filtering
- **Shopping Cart**: Add/remove items, quantity management, persistent cart state
- **Order Management**: Place orders, view order history, order status tracking
- **Payment Integration**: VNPay payment gateway for secure transactions
- **Admin Dashboard**: Product management, order management, user administration

### Technical Features
- **RESTful API**: Clean REST endpoints with proper HTTP status codes
- **Email Notifications**: Order confirmation emails
- **Data Validation**: Input validation on both frontend and backend
- **Error Handling**: Comprehensive error handling and user feedback
- **Responsive Design**: Mobile-first design with Tailwind CSS
- **State Management**: Zustand for efficient client-side state management
- **TypeScript**: Full TypeScript support for type safety

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Database**: SQLite with Hibernate JPA
- **Build Tool**: Maven
- **Security**: Spring Security with password encryption
- **Email**: Spring Mail integration
- **Payment**: VNPay integration
- **Testing**: JUnit 5, Mockito, TestContainers

### Frontend
- **Framework**: React 19.1.0 with TypeScript
- **Build Tool**: Vite 7.0.0
- **Styling**: Tailwind CSS 3.4.17
- **UI Components**: Radix UI
- **State Management**: Zustand 5.0.5
- **HTTP Client**: Axios with TanStack Query
- **Routing**: React Router DOM 7.6.2
- **Form Handling**: React Hook Form with Zod validation

### Development Tools
- **Hot Reload**: Both frontend and backend support hot reload
- **Linting**: ESLint for code quality
- **Type Checking**: TypeScript strict mode
- **API Documentation**: Comprehensive REST API endpoints

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+**
- **Node.js 18+** and **npm**
- **Git**

### Verify Installation
```bash
java --version
mvn --version
node --version
npm --version
```

## 🚀 Quick Start

### Method 1: Automated Setup (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AIMSspringboot-react
   ```

2. **Start the application**
   ```bash
   chmod +x start.sh
   ./start.sh
   ```

3. **Access the application**
   - Frontend: [http://localhost:5173](http://localhost:5173)
   - Backend API: [http://localhost:8080](http://localhost:8080)

### Method 2: Manual Setup

#### Backend Setup
```bash
# Navigate to project root
cd AIMSspringboot-react

# Install dependencies and start backend
mvn clean install
mvn spring-boot:run
```

#### Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

## 📁 Project Structure

```
AIMSspringboot-react/
├── src/main/java/com/itss/ecommerce/    # Spring Boot application
│   ├── controller/                      # REST controllers
│   ├── service/                         # Business logic services
│   ├── repository/                      # Data access layer
│   ├── entity/                          # JPA entities
│   ├── dto/                             # Data transfer objects
│   └── config/                          # Configuration classes
├── frontend/                            # React application
│   ├── src/
│   │   ├── components/                  # Reusable UI components
│   │   ├── pages/                       # Page components
│   │   ├── services/                    # API service layer
│   │   ├── stores/                      # Zustand state stores
│   │   └── types/                       # TypeScript type definitions
│   └── public/                          # Static assets
├── data/                                # Database files
├── start.sh                             # Automated startup script
├── stop.sh                              # Shutdown script
└── pom.xml                              # Maven configuration
```

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```

### Test Coverage
The project includes comprehensive unit tests for:
- **Service Layer**: Business logic validation
- **VNPay Integration**: Payment processing workflows
- **Cart Operations**: Add, remove, update cart items
- **Order Processing**: Order creation and status management

### Frontend Testing
```bash
cd frontend

# Run linting
npm run lint

# Build for production (includes type checking)
npm run build
```

## 🔌 API Documentation

### Authentication Endpoints
```http
POST /api/auth/login          # User login
POST /api/auth/register       # User registration
GET  /api/auth/profile        # Get user profile
PUT  /api/auth/profile        # Update user profile
```

### Product Endpoints
```http
GET    /api/products          # Get all products
GET    /api/products/{id}     # Get product by ID
POST   /api/products          # Create product (Admin)
PUT    /api/products/{id}     # Update product (Admin)
DELETE /api/products/{id}     # Delete product (Admin)
```

### Cart Endpoints
```http
GET    /api/cart              # Get user's cart
POST   /api/cart/add          # Add item to cart
PUT    /api/cart/update       # Update cart item quantity
DELETE /api/cart/remove/{id}  # Remove item from cart
DELETE /api/cart/clear        # Clear entire cart
```

### Order Endpoints
```http
GET  /api/orders              # Get user's orders
GET  /api/orders/{id}         # Get order details
POST /api/orders              # Create new order
PUT  /api/orders/{id}/status  # Update order status (Admin)
```

### Payment Endpoints
```http
POST /api/payment/vnpay       # Create VNPay payment
GET  /api/payment/vnpay/return # Handle payment return
POST /api/payment/query       # Query payment status
POST /api/payment/refund      # Process refund
```

## 💳 VNPay Integration

The application integrates with VNPay for secure payment processing:

### Configuration
VNPay settings are configured in `application.properties`:
```properties
vnpay.pay-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8080/api/payment/vnpay/return
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.secret-key=YOUR_SECRET_KEY
```

### Payment Flow
1. User initiates payment from checkout page
2. Application creates VNPay payment request
3. User redirected to VNPay payment gateway
4. After payment, user redirected back to application
5. Payment status validated and order confirmed
6. Email confirmation sent to user

## 🛠️ Development

### Adding New Features

1. **Backend Development**
   ```bash
   # Create new entity
   src/main/java/com/itss/ecommerce/entity/NewEntity.java
   
   # Create repository
   src/main/java/com/itss/ecommerce/repository/NewEntityRepository.java
   
   # Create service
   src/main/java/com/itss/ecommerce/service/NewEntityService.java
   
   # Create controller
   src/main/java/com/itss/ecommerce/controller/NewEntityController.java
   ```

2. **Frontend Development**
   ```bash
   # Create new page
   frontend/src/pages/NewPage.tsx
   
   # Create new component
   frontend/src/components/NewComponent.tsx
   
   # Add to routing
   frontend/src/App.tsx
   ```

### Database Management

The application uses SQLite database with automatic schema generation:

```bash
# Database file location
./data/ITSS_demo2.db

# Initial data is loaded from
src/main/resources/data.sql
```

### Environment Configuration

#### Development
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Database: SQLite file in `./data/`

#### Production
Update configuration in:
- `src/main/resources/application.properties` (Backend)
- `frontend/vite.config.ts` (Frontend)

## 🐛 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Kill process on port 8080
   lsof -ti:8080 | xargs kill -9
   
   # Kill process on port 5173
   lsof -ti:5173 | xargs kill -9
   ```

2. **Database Connection Issues**
   ```bash
   # Ensure database directory exists
   mkdir -p data
   
   # Check file permissions
   ls -la data/
   ```

3. **Maven Build Issues**
   ```bash
   # Clean and rebuild
   mvn clean install -U
   
   # Skip tests if needed
   mvn clean install -DskipTests
   ```

4. **Node Dependencies Issues**
   ```bash
   # Clear npm cache
   npm cache clean --force
   
   # Delete node_modules and reinstall
   rm -rf frontend/node_modules
   cd frontend && npm install
   ```

### Logs and Debugging

- **Backend logs**: `backend.log`
- **Frontend logs**: `frontend.log`
- **Application logs**: Check Spring Boot console output

```bash
# View real-time logs
tail -f backend.log
tail -f frontend.log
```

## 🚀 Deployment

### Production Build

1. **Build Frontend**
   ```bash
   cd frontend
   npm run build
   ```

2. **Package Backend**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Run Production**
   ```bash
   java -jar target/merged-project-0.0.1-SNAPSHOT.war
   ```

### Docker Deployment (Optional)

Create a `Dockerfile` for containerization:
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.war"]
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow existing code style and conventions
- Write unit tests for new features
- Update documentation for significant changes
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful frontend library
- VNPay for payment gateway integration
- All contributors and maintainers

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check existing documentation
- Review troubleshooting section

---

**Made with ❤️ for ITSS Project**
