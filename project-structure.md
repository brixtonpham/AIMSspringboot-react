# E-commerce System Architecture

## System Overview Diagram

```mermaid
graph TB
    subgraph "Frontend Layer - React + TypeScript"
        React[React 19.1.0 + TypeScript]
        Vite[Vite 7.0.0 Build Tool]
        TailwindCSS[Tailwind CSS 3.4.17]
        RadixUI[Radix UI Components]
        TanStackQuery[TanStack Query 5.81.2]
        Zustand[Zustand 5.0.5 State Management]
        ReactRouter[React Router DOM 7.6.2]
        
        React --> TailwindCSS
        React --> RadixUI
        React --> TanStackQuery
        React --> Zustand
        React --> ReactRouter
        Vite --> React
    end
    
    subgraph "Backend Layer - Spring Boot 3.4.4"
        SpringBoot[Spring Boot Application]
        Controllers[REST Controllers]
        Services[Business Services]
        Repositories[JPA Repositories]
        Entities[JPA Entities]
        DTOs[DTOs & Mappers]
        Config[Configuration Classes]
        Exception[Exception Handling]
        
        SpringBoot --> Controllers
        Controllers --> Services
        Services --> Repositories
        Repositories --> Entities
        Controllers --> DTOs
        Services --> DTOs
        SpringBoot --> Config
        SpringBoot --> Exception
    end
    
    subgraph "Database Layer"
        SQLite[(SQLite Database)]
        InitData[data.sql - Initial Data]
        HibernateDialect[SQLite Hibernate Dialect]
    end
    
    subgraph "External Services"
        VNPay[VNPay Payment Gateway]
        EmailService[Gmail SMTP Service]
    end
    
    subgraph "Development Tools"
        Maven[Maven Build Tool]
        Lombok[Lombok Annotations]
        DevTools[Spring Boot DevTools]
        TestContainers[TestContainers for Testing]
    end
    
    Frontend --> Controllers
    Repositories --> SQLite
    InitData --> SQLite
    HibernateDialect --> SQLite
    Services --> VNPay
    Services --> EmailService
    
    Maven --> SpringBoot
    Lombok --> Entities
    DevTools --> SpringBoot
    TestContainers --> SpringBoot
    
    classDef frontend fill:#e1f5fe
    classDef backend fill:#f3e5f5
    classDef database fill:#e8f5e8
    classDef external fill:#fff3e0
    classDef tools fill:#f5f5f5
    
    class React,Vite,TailwindCSS,RadixUI,TanStackQuery,Zustand,ReactRouter frontend
    class SpringBoot,Controllers,Services,Repositories,Entities,DTOs,Config,Exception backend
    class SQLite,InitData,HibernateDialect database
    class VNPay,EmailService external
    class Maven,Lombok,DevTools,TestContainers tools
```

## Frontend Architecture Details

```mermaid
graph TD
    subgraph "Frontend Structure"
        App[App.tsx - Main Application]
        
        subgraph "Pages"
            HomePage[HomePage.tsx]
            ProductsPage[ProductsPage.tsx]
            ProductDetailPage[ProductDetailPage.tsx]
            CartPage[CartPage.tsx]
            CheckoutPage[CheckoutPage.tsx]
            LoginPage[LoginPage.tsx]
            RegisterPage[RegisterPage.tsx]
            ProfilePage[ProfilePage.tsx]
            OrderHistoryPage[OrderHistoryPage.tsx]
            OrderConfirmationPage[OrderConfirmationPage.tsx]
            AdminDashboard[AdminDashboard.tsx]
        end
        
        subgraph "Components"
            Header[Header.tsx]
            ProductCard[ProductCard.tsx]
            ProductGrid[ProductGrid.tsx]
        end
        
        subgraph "UI Components (Radix UI)"
            Button[button.tsx]
            Card[card.tsx]
            Form[form.tsx]
            Input[input.tsx]
            Label[label.tsx]
        end
        
        subgraph "Services & State"
            ApiService[api.ts - HTTP Client]
            AuthStore[authStore.ts - Authentication]
            CartStore[cartStore.ts - Shopping Cart]
        end
        
        subgraph "Utils & Types"
            Utils[utils.ts - Utility Functions]
            ApiTypes[api.ts - Type Definitions]
        end
    end
    
    App --> Pages
    App --> Components
    Components --> UI
    Pages --> Services
    Services --> ApiService
    ApiService --> Utils
    Services --> ApiTypes
```

## Backend Architecture Details

```mermaid
graph TD
    subgraph "Backend Spring Boot Architecture"
        Main[EcommerceApplication.java]
        
        subgraph "Controllers Layer"
            ProductController[ProductController.java]
            UserController[UserController.java]
            OrderController[OrderController.java]
            InvoiceController[InvoiceController.java]
        end
        
        subgraph "Services Layer"
            ProductService[ProductService.java]
            UserService[UserService.java]
            OrderService[OrderService.java]
            InvoiceService[InvoiceService.java]
            AuditLogService[AuditLogService.java]
        end
        
        subgraph "Repository Layer"
            ProductRepository[ProductRepository.java]
            UserRepository[UserRepository.java]
            OrderRepository[OrderRepository.java]
            InvoiceRepository[InvoiceRepository.java]
            BookRepository[BookRepository.java]
            CDRepository[CDRepository.java]
            DVDRepository[DVDRepository.java]
            OrderLineRepository[OrderLineRepository.java]
            DeliveryInformationRepository[DeliveryInformationRepository.java]
            AuditLogRepository[AuditLogRepository.java]
        end
        
        subgraph "Entity Layer"
            Product[Product.java]
            Book[Book.java - extends Product]
            CD[CD.java - extends Product]
            DVD[DVD.java - extends Product]
            User[User.java]
            Order[Order.java]
            OrderLine[OrderLine.java]
            Invoice[Invoice.java]
            DeliveryInformation[DeliveryInformation.java]
            AuditLog[AuditLog.java]
        end
        
        subgraph "DTO Layer"
            ProductDTO[ProductDTO.java]
            BookDTO[BookDTO.java]
            CDDTO[CDDTO.java]
            DVDDTO[DVDDTO.java]
            UserDTO[UserDTO.java]
            OrderDTO[OrderDTO.java]
            OrderLineDTO[OrderLineDTO.java]
            InvoiceDTO[InvoiceDTO.java]
            DeliveryInformationDTO[DeliveryInformationDTO.java]
            ApiResponse[ApiResponse.java]
            CreateOrderRequest[CreateOrderRequest.java]
            CreateInvoiceRequest[CreateInvoiceRequest.java]
            ProcessPaymentRequest[ProcessPaymentRequest.java]
            UpdateInvoiceRequest[UpdateInvoiceRequest.java]
            UpdateUserProfileRequest[UpdateUserProfileRequest.java]
        end
        
        subgraph "Mappers"
            ProductMapper[ProductMapper.java]
            UserMapper[UserMapper.java]
            OrderMapper[OrderMapper.java]
        end
        
        subgraph "Configuration"
            CorsConfig[CorsConfig.java]
            EcommerceConfig[EcommerceConfig.java]
        end
        
        subgraph "Exception Handling"
            GlobalExceptionHandler[GlobalExceptionHandler.java]
            BusinessException[BusinessException.java]
            InsufficientStockException[InsufficientStockException.java]
            PaymentProcessingException[PaymentProcessingException.java]
            ResourceAlreadyExistsException[ResourceAlreadyExistsException.java]
        end
    end
    
    Main --> Controllers
    Controllers --> Services
    Services --> Repository
    Repository --> Entity
    Controllers --> DTO
    Services --> DTO
    DTO --> Mappers
    Main --> Configuration
    Main --> Exception
    
    Product --> Book
    Product --> CD
    Product --> DVD
```

## Database Schema (Entity Relationships)

```mermaid
erDiagram
    User ||--o{ Order : places
    User ||--o{ DeliveryInformation : has
    Order ||--o{ OrderLine : contains
    Order ||--o| Invoice : generates
    Product ||--o{ OrderLine : included_in
    Product ||--|| Book : "inheritance"
    Product ||--|| CD : "inheritance"
    Product ||--|| DVD : "inheritance"
    
    User {
        Long id PK
        String name
        String email
        String phone
        String address
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    Product {
        Long id PK
        String title
        String category
        BigDecimal price
        Integer quantity
        String description
        String imageUrl
        Double weight
        String rushSupportType
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    Book {
        Long id PK
        String author
        String coverType
        String publisher
        String publishDate
        Integer numOfPages
        String language
        String bookCategory
    }
    
    CD {
        Long id PK
        String artist
        String recordLabel
        String musicType
        String releasedDate
    }
    
    DVD {
        Long id PK
        String discType
        String director
        Integer runtime
        String studio
        String subtitle
        String releasedDate
        String filmType
    }
    
    Order {
        Long id PK
        BigDecimal totalCost
        BigDecimal shippingFees
        String status
        LocalDateTime createdAt
        LocalDateTime updatedAt
        Long userId FK
    }
    
    OrderLine {
        Long id PK
        Integer quantity
        BigDecimal price
        Long orderId FK
        Long productId FK
    }
    
    Invoice {
        Long id PK
        BigDecimal totalCost
        LocalDateTime createdAt
        LocalDateTime updatedAt
        Long orderId FK
    }
    
    DeliveryInformation {
        Long id PK
        String name
        String province
        String instructions
        Long userId FK
    }
    
    AuditLog {
        Long id PK
        String action
        String entityType
        String entityId
        String details
        LocalDateTime timestamp
    }
```

## Technology Stack Overview

```mermaid
mindmap
  root((E-commerce System))
    Frontend
      React 19.1.0
        TypeScript 5.8.3
        Vite 7.0.0
        Tailwind CSS 3.4.17
        Radix UI Components
      State Management
        Zustand 5.0.5
        TanStack Query 5.81.2
      Routing
        React Router DOM 7.6.2
      Forms
        React Hook Form 7.58.1
        Zod 3.25.67 validation
      HTTP Client
        Axios 1.10.0
    Backend
      Spring Boot 3.4.4
        Java 21
        Maven Build Tool
      Spring Modules
        Spring Web
        Spring Data JPA
        Spring Boot DevTools
        Spring Validation
        Spring Security Crypto
        Spring Mail
      ORM
        Hibernate 6.x
        SQLite Dialect
      Utilities
        Lombok
        Gson 2.9.0
        Commons Codec 1.15
    Database
      SQLite
        JDBC Driver
        Hibernate Community Dialects
        JPA Integration
    External Services
      VNPay Payment Gateway
        Sandbox Environment
        Payment Processing
        Return URL Handling
      Email Service
        Gmail SMTP
        Spring Mail Integration
    Development Tools
      Testing
        JUnit Jupiter 5.8.2
        Mockito 4.5.1
        TestContainers 1.19.3
        Spring Boot Test
      Development
        Spring Boot DevTools
        H2 Console (development)
        Logging Configuration
```

## Request Flow Architecture

```mermaid
sequenceDiagram
    participant User as User Browser
    participant Frontend as React Frontend
    participant Controller as Spring Controller
    participant Service as Business Service
    participant Repository as JPA Repository
    participant Database as SQLite Database
    participant VNPay as VNPay Gateway
    participant Email as Email Service
    
    User->>Frontend: User Action (e.g., Place Order)
    Frontend->>Controller: HTTP Request (REST API)
    Controller->>Service: Business Logic Call
    Service->>Repository: Data Access
    Repository->>Database: SQL Query (JPA/Hibernate)
    Database-->>Repository: Query Result
    Repository-->>Service: Entity Objects
    Service->>Service: Business Rule Processing
    
    alt Payment Required
        Service->>VNPay: Payment Request
        VNPay-->>Service: Payment Response
        Service->>Email: Send Confirmation Email
        Email-->>Service: Email Sent
    end
    
    Service-->>Controller: DTO Response
    Controller-->>Frontend: JSON Response
    Frontend-->>User: UI Update
    
    Note over Service,Database: Audit logging for all operations
    Service->>Repository: Log Audit Entry
    Repository->>Database: Insert Audit Log
```

## Deployment Configuration

```mermaid
graph LR
    subgraph "Development Environment"
        FrontendDev[Frontend Dev Server<br/>Vite - Port 3000]
        BackendDev[Backend Dev Server<br/>Spring Boot - Port 8080]
        DatabaseDev[SQLite Database<br/>./data/ITSS_demo2.db]
        
        FrontendDev -->|HTTP Requests| BackendDev
        BackendDev -->|JDBC| DatabaseDev
    end
    
    subgraph "External Services"
        VNPaySandbox[VNPay Sandbox<br/>sandbox.vnpayment.vn]
        GmailSMTP[Gmail SMTP<br/>smtp.gmail.com:587]
    end
    
    BackendDev -->|Payment API| VNPaySandbox
    BackendDev -->|Email Service| GmailSMTP
    
    subgraph "Build Tools"
        MavenBuild[Maven<br/>Backend Build]
        ViteBuild[Vite<br/>Frontend Build]
    end
    
    MavenBuild --> BackendDev
    ViteBuild --> FrontendDev
```

## Key Features & Capabilities

- **Product Management**: Books, CDs, DVDs with inheritance hierarchy
- **User Management**: Registration, authentication, profile management
- **Shopping Cart**: Add/remove items, quantity management
- **Order Processing**: Complete order workflow with order lines
- **Payment Integration**: VNPay payment gateway integration
- **Invoice Generation**: Automated invoice creation
- **Email Notifications**: Order confirmations and notifications
- **Admin Dashboard**: Administrative interface for management
- **Audit Logging**: Complete system activity tracking
- **Responsive Design**: Modern UI with Tailwind CSS and Radix UI
- **State Management**: Zustand for client-side state
- **Data Fetching**: TanStack Query for server state management
- **Form Handling**: React Hook Form with Zod validation
- **Type Safety**: Full TypeScript implementation