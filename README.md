# Info5100 Final Project: Wellness Chain Supply System

A comprehensive Java Swing application for managing multi-location wellness clinic operations and supplier coordination.

**Recording** 

## 📋 Project Description

### Problem Statement
Small wellness businesses struggle to coordinate multi-location operations, staff scheduling, client appointments, and supply inventory. Communication between branches and suppliers relies on manual processes, leading to stock shortages, scheduling conflicts, and inefficient operations.

### Solution
A unified platform where:
- **Branch Admins** manage appointments, staff, and inventory
- **Therapists** view and update their schedules
- **Suppliers** fulfill inventory orders through a structured work request system

## 🏢 System Architecture

### Two Enterprises
| Enterprise              | Description                                    |
| ----------------------- | ---------------------------------------------- |
| **Wellness Enterprise** | Chain of massage/wellness clinics (3 branches) |
| **Supplier Enterprise** | Product supplier for wellness supplies         |

### Four Roles
| Role           | Type   | Enterprise | Responsibilities                                             |
| -------------- | ------ | ---------- | ------------------------------------------------------------ |
| Branch Admin   | Admin  | Wellness   | Manage branches, customers, therapists, inventory, POs, appointments |
| Therapist      | Normal | Wellness   | View and update appointments                                 |
| Supplier Admin | Admin  | Supplier   | Manage products, approve/reject POs                          |
| Supplier Staff | Normal | Supplier   | Pack, ship, deliver orders                                   |

## 🔄 Cross-Enterprise Work Request Flow

```
Branch Admin creates PO (SUBMITTED)
         ↓
    WorkRequest created (PURCHASE_ORDER, OPEN)
         ↓
Supplier Admin reviews
         ↓
    ┌────┴────┐
    ↓         ↓
APPROVED   REJECTED
    ↓
Supplier Staff: PACKED → SHIPPED → DELIVERED
    ↓
Branch Inventory automatically updated
```

## 🛠️ Tech Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing
- **Database**: MySQL 8.0 (Docker)
- **Connectivity**: JDBC (MySQL Connector/J 8+)
- **IDE**: NetBeans (Ant build)

## 📁 Project Structure

```
WellnessChainSupplySystem/
├── README.md
├── database/
│   ├── docker-compose.yml        # Docker MySQL setup
│   ├── schema.sql                # Database schema (10 tables)
│   └── seed_data.sql             # Sample test data
├── src/
│   └── wellnesschainsupplysystem/
│       ├── WellnessChainSupplySystem.java
│       ├── model/                # Data models & enums
│       ├── dao/                  # Database access layer
│       ├── ui/                   # Swing UI frames
│       └── util/                 # Utilities (DB connection)
├── diagrams/                     # UML diagrams
└── screenshots/                  # Application screenshots
```

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Docker Desktop
- NetBeans IDE (or any Java IDE)
- MySQL Connector/J 8.x

### Database Setup

1. **Start MySQL using Docker Compose:**
```bash
cd database
docker-compose up -d
```

2. **Create the schema:**
```bash
docker exec -i wellness-mysql mysql -uroot -prootpassword wellness_chain_db < schema.sql
```

3. **Load sample data:**
```bash
docker exec -i wellness-mysql mysql -uroot -prootpassword wellness_chain_db < seed_data.sql
```

### Running the Application

1. Open project in NetBeans
2. Ensure MySQL Connector/J is in the classpath
3. Run `WellnessChainSupplySystem.java`

### Test Accounts

| Username      | Password    | Role           |
| ------------- | ----------- | -------------- |
| branchadmin   | password123 | BRANCH_ADMIN   |
| therapist1    | password123 | THERAPIST      |
| therapist2    | password123 | THERAPIST      |
| supplieradmin | password123 | SUPPLIER_ADMIN |
| supplierstaff | password123 | SUPPLIER_STAFF |

## 📊 Database Schema

| Table                 | Description                               |
| --------------------- | ----------------------------------------- |
| `user_account`        | All system users with roles               |
| `clinic_branch`       | Wellness branch locations                 |
| `therapist_profile`   | Therapist details and branch assignment   |
| `customer`            | Client information                        |
| `appointment`         | Bookings between customers and therapists |
| `product`             | Supplier product catalog                  |
| `branch_inventory`    | Stock levels per branch                   |
| `purchase_order`      | Orders from branches to supplier          |
| `purchase_order_item` | Line items in purchase orders             |
| `work_request`        | Task tracking across enterprises          |

## ✅ Features Implemented

### Branch Admin
- [x] Manage Clinic Branches (CRUD)
- [x] Manage Customers (CRUD)
- [x] Manage Therapists (CRUD)
- [x] Manage Branch Inventory (CRUD)
- [x] Create/Delete Purchase Orders
- [x] Manage Appointments (CRUD)

### Therapist
- [x] View Assigned Appointments
- [x] Update Appointment Status (Complete/Cancel)

### Supplier Admin
- [x] Manage Products (CRUD)
- [x] View All Purchase Orders
- [x] Approve/Reject Purchase Orders
- [x] View Work Requests

### Supplier Staff
- [x] View Approved Orders
- [x] Mark Order as Packed
- [x] Mark Order as Shipped
- [x] Mark Order as Delivered (auto-updates inventory)

## 👤 Author

**Lei Luo**  
Northeastern University Toronto  
Master's in Information Systems

## 📄 License

This project is for educational purposes as part of the INFO 5100 Application Engineering and Development course.