# Wellness Chain Supply System

A Java Swing desktop application for managing a **multi-location wellness clinic chain** and its **supplier**, designed as a mini ecosystem to demonstrate:

- Multi-enterprise collaboration (Wellness Chain ↔ Supplier)
- Multiple user roles (admin + staff)
- Work requests across and within enterprises
- Full JDBC-based CRUD with MySQL (via Docker)
- Java OOP, Swing UI, and layered architecture

> **Enterprises**
>
> - **Wellness Chain** (multi-location clinics)
> - **Supplier** (massage oil, linen, wellness supplies)

------

## 1. Features Overview

### 1.1 Enterprises & Roles

**Enterprises**

1. **Wellness Chain**
   - Branches / locations
   - Customers / clients
   - Therapists
   - Appointment management
   - Branch inventory (products at each location)
2. **Supplier**
   - Products catalog (e.g., massage oils, linen)
   - Order approval
   - Warehouse shipment workflow

**Roles (4 total)**

1. **Branch Admin** (Wellness Chain)
   - Manage **Clinic Branches**
   - Manage **Customers**
   - Manage **Branch Inventory**
   - Create **Purchase Orders** to Supplier
   - Manage **Appointments** for Therapists
2. **Therapist** (Wellness Chain)
   - View **My Appointments**
   - Mark appointments as **COMPLETED** or **CANCELLED**
3. **Supplier Admin**
   - Manage **Products**
   - View **Purchase Orders**
   - **Approve / Reject** purchase orders
4. **Supplier Staff** (Warehouse / Fulfillment)
   - See **Approved** purchase orders
   - Progress shipments through:
     - **PACKED → SHIPPED → DELIVERED**
   - On **DELIVERED**:
     - Updates **branch inventory**
     - Completes **Shipment Work Request**

------

## 2. Work Requests 

The system implements **work requests** as real functional flows that change application state and are stored in the `work_request` table.

### 2.1 Cross-Enterprise Work Requests (≥ 2)

1. **Purchase Order Work Request**
   - Type: `PURCHASE_ORDER`
   - Flow:
     - Branch Admin creates a **Purchase Order** to Supplier
     - Automatically creates a `work_request` row
     - Supplier Admin **approves / rejects**
   - Tables involved:
     - `purchase_order`
     - `purchase_order_item`
     - `work_request`
2. **Shipment Work Request**
   - Type: `SHIPMENT`
   - Flow:
     - Supplier Staff sees **APPROVED** POs
     - Marks status **PACKED → SHIPPED → DELIVERED**
     - Creates/updates `work_request` of type `SHIPMENT`
     - On **DELIVERED**:
       - Updates `branch_inventory`
       - Marks shipment work request as `COMPLETED`
   - Tables involved:
     - `purchase_order`
     - `purchase_order_item`
     - `work_request`
     - `branch_inventory`

### 2.2 Internal Work Requests (within Wellness Chain)

1. **Appointment Booking (Branch → Therapist)**
   - Branch Admin creates **appointments**:
     - Branch, Therapist, Customer, start/end time, notes
   - Changes system state in `appointment` table.
2. **Appointment Completion / Cancellation (Therapist workflow)**
   - Therapist logs in and sees **“My Appointments”**
   - Marks them as **COMPLETED** or **CANCELLED**
   - Updates `appointment.status` (and is presented in the UI for reporting).

> In the **presentation**, you can explicitly name these 4 flows as “Work Request 1–4”.



------

## 3. Database Design

Main tables (summary):

- `user_account` – login accounts with role
- `clinic_branch` – wellness clinic locations
- `customer` – wellness clients
- `product` – supplier products
- `branch_inventory` – stock per branch per product
- `purchase_order` – PO header (branch → supplier)
- `purchase_order_item` – PO items (currently 1 product per PO in UI, but table supports many)
- `work_request` – generic work requests for POs & shipments
- `therapist_profile` – associates therapist users with branches & specialties
- `appointment` – therapist appointments with customers

------

## 4. Default Login Accounts

> **Note:** You can change passwords and usernames in `user_account`, but ensure they match your demo script.

### 4.1 Wellness Chain – Branch Admin

- **Role**: `BRANCH_ADMIN`
- **Username**: `branchadmin`
- **Password**: `password123`

**Capabilities**:

- Manage **Clinic Branches** (CRUD)
- Manage **Customers** (CRUD)
- Manage **Branch Inventory** (CRUD per branch/product)
- Create & view **Purchase Orders**
- Manage **Appointments**:
  - Select Branch
  - Select Therapist
  - Select Customer
  - Set start/end time, notes

### 4.2 Wellness Chain – Therapists

1. **Therapist 1**
   - Role: `THERAPIST`
   - Username: `therapist1`
   - Password: `password123`
2. **Therapist 2**
   - Role: `THERAPIST`
   - Username: `therapist2`
   - Password: `password123`

**Capabilities**:

- View **“My Appointments”**
- Change appointment status:
  - `SCHEDULED → COMPLETED`
  - `SCHEDULED → CANCELLED`

### 4.3 Supplier – Admin

- **Role**: `SUPPLIER_ADMIN`
- **Username**: `supplieradmin`
- **Password**: `password123`

**Capabilities**:

- Manage **Products** (CRUD)
- View all **Purchase Orders**
- Approve or reject POs:
  - `SUBMITTED → APPROVED`
  - `SUBMITTED → REJECTED`

> Approved orders become visible for **Supplier Staff** to handle shipments.

### 4.4 Supplier – Staff (Warehouse / Fulfillment)

- **Role**: `SUPPLIER_STAFF`
- **Username**: `supplierstaff`
- **Password**: `password123`

**Capabilities**:

- View all purchase orders in states:
  - `APPROVED`, `PACKED`, `SHIPPED`, `DELIVERED`
- Update shipment status:
  - `APPROVED → PACKED`
  - `PACKED → SHIPPED`
  - `SHIPPED → DELIVERED`

On **DELIVERED**:

- Increases **branch inventory** in `branch_inventory`
- Updates the `SHIPMENT` work request to `COMPLETED`
- Marks PO as `DELIVERED`



------

## 