# Backend - LabLabLab Management System

## 📋 Overview

A backend project for managing lab rooms, equipment, and tool rentals built with Spring MVC.

## 🛠️ Technologies Used

- **Framework**: Spring MVC 6.1.4
- **Java**: 21
- **Database**: MySQL (TiDB Cloud)
- **Build Tool**: Maven
- **Packaging**: WAR
- **Security**: Spring Security 6.2.2 + JWT
- **ORM**: Hibernate 6.4.4
- **Mapping**: MapStruct 1.6.3
- **Real-time**: WebSocket
- **Email**: Thymeleaf + Jakarta Mail
- **Excel**: Apache POI 5.2.5

## 📁 Project Directory Structure

```
backend/
├── src/main/
│   ├── java/com/example/springmvc/
│   │   ├── config/              # Application Configuration
│   │   │   ├── AppConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── WebMvcConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   ├── DatabaseCleanupListener.java
│   │   │   └── WebAppInitializer.java
│   │   │
│   │   ├── controller/          # REST Controllers
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── ItemController.java
│   │   │   ├── RentController.java
│   │   │   ├── RoomController.java
│   │   │   ├── TwoFactorController.java
│   │   │   └── UserController.java
│   │   │
│   │   ├── service/             # Business Logic Layer
│   │   │   ├── impl/           # Service Implementations
│   │   │   │   ├── AdminServiceImpl.java
│   │   │   │   ├── AssetServiceImpl.java
│   │   │   │   ├── AuthServiceImpl.java
│   │   │   │   ├── ChemicalServiceImpl.java
│   │   │   │   ├── ExcelServiceImpl.java
│   │   │   │   ├── ItemServiceImpl.java
│   │   │   │   ├── NotificationServiceImpl.java
│   │   │   │   ├── OtpServiceImpl.java
│   │   │   │   ├── RentServiceImpl.java
│   │   │   │   ├── RoomServiceImpl.java
│   │   │   │   ├── TwoFactorServiceImpl.java
│   │   │   │   └── UserServiceImpl.java
│   │   │   ├── AdminService.java
│   │   │   ├── AuthService.java
│   │   │   ├── ItemService.java
│   │   │   ├── RentService.java
│   │   │   ├── RoomService.java
│   │   │   ├── UserService.java
│   │   │   ├── TwoFactorService.java
│   │   │   ├── OtpService.java
│   │   │   ├── NotificationService.java
│   │   │   ├── ExcelService.java
│   │   │   ├── AssetService.java
│   │   │   └── ChemicalService.java
│   │   │
│   │   ├── repository/          # Data Access Layer (JPA)
│   │   │   ├── UserRepository.java
│   │   │   ├── ItemRepository.java
│   │   │   ├── RentTicketRepository.java
│   │   │   ├── RentTicketDetailRepository.java
│   │   │   ├── RoomRepository.java
│   │   │   ├── AssetRepository.java
│   │   │   ├── ChemicalRepository.java
│   │   │   ├── OtpLogRepository.java
│   │   │   └── UserSecuritySettingsRepository.java
│   │   │
│   │   ├── entity/             # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Profile.java
│   │   │   ├── Item.java
│   │   │   ├── Asset.java
│   │   │   ├── Chemical.java
│   │   │   ├── Tool.java
│   │   │   ├── RentTicket.java
│   │   │   ├── RentTicketDetail.java
│   │   │   ├── Room.java
│   │   │   ├── OtpLog.java
│   │   │   ├── OtpType.java
│   │   │   └── UserSecuritySettings.java
│   │   │
│   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── auth/           # Authentication DTOs
│   │   │   │   ├── ChangePasswordRequest.java
│   │   │   │   ├── ChangeTwoFaEmailRequest.java
│   │   │   │   ├── Enable2FaRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   └── VerifyOtpRequest.java
│   │   │   ├── user/           # User DTOs
│   │   │   │   ├── CreateUserRequest.java
│   │   │   │   ├── SecuritySettingsResponse.java
│   │   │   │   ├── UpdateProfileRequest.java
│   │   │   │   ├── UserProfileResponse.java
│   │   │   │   └── UserResponse.java
│   │   │   ├── item/           # Item DTOs
│   │   │   │   ├── AssetResponse.java
│   │   │   │   ├── ChemicalResponse.java
│   │   │   │   └── ItemResponse.java
│   │   │   ├── rent/           # Rent DTOs
│   │   │   │   ├── RealTimeRentInfo.java
│   │   │   │   ├── RentDetailResponse.java
│   │   │   │   ├── RentListResponse.java
│   │   │   │   ├── RentRequest.java
│   │   │   │   ├── RentTicketResponse.java
│   │   │   │   ├── ReturnRequest.java
│   │   │   │   └── UpdateRentTicketStatus.java
│   │   │   ├── room/           # Room DTOs
│   │   │   │   └── RoomResponse.java
│   │   │   ├── ApiResponse.java
│   │   │   └── DashboardStats.java
│   │   │
│   │   ├── mapper/             # MapStruct Mappers
│   │   │   ├── UserMapper.java
│   │   │   ├── ItemMapper.java
│   │   │   ├── RentMapper.java
│   │   │   └── RoomMapper.java
│   │   │
│   │   ├── security/           # Security Components
│   │   │   ├── JwtUtils.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   │
│   │   ├── constant/           # Constants
│   │   │   ├── RoleConst.java
│   │   │   ├── RentConst.java
│   │   │   └── ItemCondition.java
│   │   │
│   │   └── exception/          # Exception Handling
│   │       ├── BusinessException.java
│   │       └── GlobalExceptionHandler.java
│   │
│   ├── resources/
│   │   ├── app.properties      # Application configuration
│   │   ├── META-INF/
│   │   │   └── orm.xml         # ORM mapping
│   │   └── templates/
│   │       └── otp-email.html  # Email template
│   │
│   └── webapp/
│       └── WEB-INF/            # Web configuration
│
├── target/                     # Build output
├── uploads/                    # File uploads
│   └── avatars/               # User avatars
│
└── pom.xml                     # Maven configuration
```

## 🎯 Main Modules

### 1. Authentication & Security
- JWT-based authentication
- Spring Security integration
- Two-Factor Authentication (2FA) with OTP via email
- User security settings management

### 2. User Management
- User CRUD operations
- Profile management
- Avatar upload
- User roles and permissions

### 3. Item Management
- Asset, Chemical, and Tool management
- Item conditions tracking
- Excel import/export functionality

### 4. Rent Management
- Rent ticket creation and management
- Real-time rent information (WebSocket)
- Status updates and tracking

### 5. Room Management
- Room CRUD operations
- Room assignments

### 6. Admin Dashboard
- Dashboard statistics
- Admin operations and management

## 🔧 Configuration

Main configuration is located in `src/main/resources/app.properties`:
- Database connection (TiDB Cloud)
- JWT secret and expiration
- CORS allowed origins
- File upload directory
- SMTP configuration (SendGrid)

## 📝 Notes

- Project uses Java 21
- Build output: `target/`
- File uploads: `uploads/`
- WAR file is generated in `target/backend-mvc.war`
