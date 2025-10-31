# 🍽️ Distribution System – Zero Hunger Operations

> **A ScalaFX desktop app empowering Zero Hunger efforts.**  
> Manage food inventory, plan distributions, track nutrition, and coordinate communities — all in one unified system built to support **United Nations Sustainable Development Goal 2 (Zero Hunger)**.

---

## 🌍 Overview

The **Distribution System** is a **Scala-based desktop application** built with the **ScalaFX GUI toolkit**.  
It provides an integrated workspace for **food banks**, **shelters**, and **community charities** to streamline daily operations, improve efficiency, and reduce waste — directly contributing to **SDG 2: End Hunger, Achieve Food Security, and Promote Sustainable Agriculture**.

This system combines powerful backend logic with an intuitive, data-rich interface that supports:
- 📦 **Inventory Management**
- 🚚 **Distribution Tracking**
- 🥗 **Nutrition Intelligence**
- 💬 **Community Collaboration**
- 📊 **Impact Reporting**

---

## ✨ Key Features

### 🔐 Access & Roles
- Secure login for **Admin** and **User (Courier)** roles.  
- Task-oriented toolbar with session-safe navigation (Inventory, Distributions, Nutrition, Reports, Community).

---

### 📦 Inventory Management
- Manage food items with ID, name, category, expiry, unit (kg/L/pcs), and quantity.
- Built-in validation for ID format, expiry date, and quantity.
- Live KPIs:
  - 🧮 Total stock
  - ⚠️ Low-stock alerts
  - 🔥 Total calories in inventory
- One-click **“Refresh Nutrition”** updates all nutritional data.
- Integrated with the **OpenFoodFacts-style nutrition service** for real kcal/unit accuracy.

---

### 🥗 Nutrition Intelligence
- Realistic per-unit nutrition lookup (kg/L/pcs) powered by **OpenFoodFactsService**.
- Displays food image, calories, macros (protein, carbs, fat), and micronutrients.
- Supports alias and plural matching (e.g., “beans (kidney)” → “kidney beans”).
- Directly attach nutrition profiles to inventory items.

---

### 🚚 Distribution Management
- Record, plan, and manage food distributions to shelters or recipients.
- Track status changes (**Planned → Delivered → Canceled**).
- KPIs show **households served** and **deliveries completed**.

---

### 💬 Community Collaboration
- Built-in **Community Tab** for communication between shelters and charities.
- Create **channels** with names, locations, and organizations.
- Post needs (item, unit, quantity) and discussion updates in each channel.
- Encourages sharing, coordination, and transparency across partners.

---

### 📊 Reporting & Insights
- “Impact Snapshot” dashboard summarizing:
  - 📦 Distributions completed
  - 🔥 Total calories currently in stock
  - ⚠️ Items below stock threshold
- Designed for future export to PDF or Excel.

---

## 🧠 Architecture Highlights

| Concept | Implementation |
|----------|----------------|
| **Language & GUI** | Scala 3 + ScalaFX |
| **Design Approach** | Object-Oriented Programming (Encapsulation, Composition, Polymorphism) |
| **Data Handling** | In-memory repositories (can be extended to DB) |
| **Nutrition Data Source** | OpenFoodFactsService (offline API simulation) |
| **Views** | Modular ScalaFX components for each feature |
| **Visual Theme** | SDG 2 – *Zero Hunger* inspired palette & icons |

---

## 🧩 Core Classes

- `Inventory` → Handles food items and total calorie computation.  
- `FoodItem` → Represents each stock item (ID, category, expiry, quantity, nutrition).  
- `NutritionService` / `OpenFoodFactsService` → Strategy pattern for flexible nutrition lookup.  
- `DistributionService` → Manages delivery records and status.  
- `CommunityBoard` → Manages shelters, needs, and discussions.  
- `SdgShellView` → Main GUI shell linking all modules.  

---

## 🛠️ Technologies Used

| Category | Tools |
|-----------|-------|
| **Language** | Scala 3 |
| **UI Framework** | ScalaFX |
| **Architecture** | MVC / Layered OOP Design |
| **Data Model** | Custom in-memory repositories |
| **Styling** | CSS with SDG-2 color palette |
| **Version Control** | Git + GitHub |

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/whotiss/DistributionSystem.git
cd DistributionSystem
