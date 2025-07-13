
# EcoBer - Smart Ride-Sharing Backend

EcoBer is a backend system for a ride-sharing platform designed to match drivers with riders, calculate trip emissions, and promote eco-friendly transportation. This project demonstrates my ability to design and deploy a feature-rich backend with real-world considerations like environmental impact and driver scoring.

---

## 🌱 Core Innovation

EcoBer introduces features beyond basic ride requests:

* **Driver Matching**: Finds nearby drivers using geospatial queries and vehicle type filters.
* **Carbon Tracking**: Calculates CO₂ emissions per trip based on distance and vehicle type.
* **Driver Eco Report**: Tracks driver trip history, total CO₂ saved, and assigns an eco-badge based on their environmental impact.

---

## ✅ Key Features

### Intelligent Driver Matching

* Finds drivers within a radius using **Haversine distance**.
* Filters by **vehicle type** (e.g., Sedan, EV, Hybrid).
* Prevents multiple drivers from accepting the same ride.

### Smart Trip Management

* Trip lifecycle: **ACCEPTED → IN\_PROGRESS → COMPLETED**.
* Tracks trip details including distance, estimated/actual CO₂ emissions, and timestamps.

### Environmental Focus

* Estimates CO₂ emissions per trip.
* Assigns drivers a **carbon score** and badge (e.g., 🚗 *Standard Driver*).
* Monthly CO₂ savings and ride type distribution reports.

### Deployment Journey

* Deployed backend on **AWS Elastic Beanstalk** with **Aurora Serverless MySQL**.
* Configured health checks and debugged security/port issues during deployment.

---

## 🛠 Technology Stack

* **Backend**: Spring Boot (Java 17)
* **Database**: Aurora Serverless MySQL (Hibernate auto schema creation)
* **Cloud Deployment**: AWS Elastic Beanstalk
* **APIs**: RESTful endpoints for riders and drivers
* **Testing**: JUnit & Mockito

---

## 🚀 Backend Architecture

```
Rider Request → Driver Matching → Trip Creation
      ↓                    ↓
Carbon Emission Estimation → Trip Lifecycle Management
      ↓
Driver Eco Report & Analytics
```

---

## 🌍 API Examples

### Request a Ride

```http
POST /api/rides/request
{
  "pickupLatitude": 39.1696,
  "pickupLongitude": -86.5385,
  "dropoffLatitude": 39.7684,
  "dropoffLongitude": -86.1581,
  "vehicleType": "SEDAN"
}
```

### Driver Eco Report

```http
GET /driver/me/eco-report
Authorization: Bearer <token>
```

Response:

```json
{
  "tripCount": 2,
  "totalCO2": 78.52,
  "rideTypeDistribution": [
    { "name": "SEDAN", "value": 2 }
  ],
  "carbonRating": "🚗 Standard Driver",
  "monthlyCo2Savings": [
    { "co2": 64.25, "month": "JUN" }
  ],
  "carbonScore": 157.04
}
```

---

## 🌱 Deployment Highlights

1. **Port Binding Fix**: Configured Elastic Beanstalk environment properties to align with Spring Boot.
2. **Database Configuration**: Switched from manually created schema to Hibernate auto schema creation for foreign key constraints.
3. **Security Debugging**: Resolved `403 Forbidden` errors by relaxing Spring Security during deployment testing.
4. **Health Monitoring**: Added a `/health` API endpoint for Elastic Beanstalk health checks.

---

## ⚡ Future Enhancements

* Frontend hosting on AWS S3 + CloudFront.
* Real-time driver location updates using WebSockets.
* Event-driven architecture with Kafka for trip state changes.
* Advanced driver matching using trust scores and demand prediction.

---

## 🌟 Why EcoBer?

This project reflects a **real-world backend deployment journey** with debugging, optimization, and a clean architecture to scale future features. It highlights a **feature-rich backend-focused innovation** without placing more emphasis on the frontend presentation and system design.

