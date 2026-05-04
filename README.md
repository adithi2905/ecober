# EcoBer — Eco-First Ride-Sharing Platform

EcoBer is a full-stack ride-sharing platform built around environmental accountability. Every trip is scored, every driver is ranked by carbon impact, and every rider earns badges for greener choices. The backend is Spring Boot (Java 17) deployed on AWS; the frontend is React 19 with Tailwind CSS.

---

## Table of Contents

- [Core Innovation](#core-innovation)
- [Eco Scoring System](#eco-scoring-system)
  - [Fuel-Based Eco Score](#fuel-based-eco-score)
  - [Carbon Emission Calculation](#carbon-emission-calculation)
  - [Driver Ranking Algorithm](#driver-ranking-algorithm)
  - [Carbon Rating (Letter Grade)](#carbon-rating-letter-grade)
  - [Eco Badges](#eco-badges)
  - [Carbon Cost](#carbon-cost)
  - [Daily Score Refresh](#daily-score-refresh)
  - [Rider Carbon Analytics](#rider-carbon-analytics)
- [Key Features](#key-features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [External APIs](#external-apis)
- [Deployment](#deployment)
- [Future Enhancements](#future-enhancements)

---

## Screenshots

### Driver: Eco Impact Dashboard
<img width="944" height="491" alt="image" src="https://github.com/user-attachments/assets/c2a990e1-9c27-4c3f-bbab-a97133477e12" />

### Rider: Eco Report Dashboard
<img width="959" height="505" alt="image" src="https://github.com/user-attachments/assets/b4bcce25-b564-4495-a6a0-8cb1dc412291" />

---

## Core Innovation

EcoBer goes beyond standard ride-sharing by treating carbon impact as a first-class metric:

- Every trip gets an **eco score** derived from the driver's vehicle fuel type (decoded from VIN).
- Drivers are **ranked and matched** using a weighted composite of proximity, trust score, and cumulative CO2 saved.
- CO2 emissions are calculated two ways — a **local estimate** and a **live Climatiq API** call — and both are stored per trip.
- Riders and drivers each earn **eco badges** that reflect their environmental footprint over time.
- The platform surfaces **monthly CO2 savings**, real-world impact equivalents (trees saved, flights offset), and progress toward green goals.

---

## Eco Scoring System

### Fuel-Based Eco Score

**Service:** `FuelScoringService.java`

When a trip ends, the driver's VIN is decoded via the NHTSA API to determine fuel type. The fuel type maps directly to a 0–100 eco score stored on `Trip.ecoScore`:

| Fuel Type | Eco Score |
|-----------|-----------|
| Electric | 100 |
| Hybrid / Plug-in Hybrid | 80 |
| Diesel | 40 |
| Gasoline | 30 |
| Unknown | 20 |

### Carbon Emission Calculation

**Utility:** `EmissionUtils.java`  
**Service:** `CarbonScoringService.java`

Emissions are computed two ways per trip:

**Estimated (local formula):**
```
estimatedCO2 = distanceKm × emissionFactor
```

**Emission factors (kg CO2 per km):**

| Vehicle Type | Factor (kg/km) |
|---|---|
| EV | 0.18 |
| Hybrid | 0.104 |
| Sedan | 0.173 |
| SUV | 0.231 |
| Bike | 0.0 |
| Default | 0.21 |

**Actual (live API):** The Climatiq API is called with the vehicle's activity ID and distance to return a real-world `co2e` value. Both `estimatedEmission` and `carbonEmission` (actual) are stored on `Route` and `Trip` respectively.

**Efficiency-based score:**
```
efficiency = min(1.0, expectedEmission / actualEmissions)
ecoScore   = efficiency × 100
```

### Driver Ranking Algorithm

**Service:** `DriverScoringService.java`

When a rider requests a ride, available drivers are scored and ranked. The composite score weights three factors:

```
driverScore = 0.5 × distanceScore
            + 0.3 × trustScore
            + 0.2 × co2SavedScore
```

| Component | Formula | What it rewards |
|---|---|---|
| `distanceScore` | `1 / (1 + distanceKm)` | Proximity to pickup |
| `trustScore` | `min(1.0, driver.trustScore / 5)` | Historical eco rating (0–5 scale) |
| `co2SavedScore` | `min(1.0, totalCO2Saved / 100)` | Lifetime CO2 avoided (caps at 100 kg) |

Drivers are sorted descending — so a nearby EV driver with strong eco history consistently outranks a distant gasoline-powered driver.

### Carbon Rating (Letter Grade)

**Service:** `CarbonScoringService.java`

The 0–100 eco score is mapped to a letter grade displayed on the driver eco report:

| Score | Rating |
|---|---|
| ≥ 90 | A+ |
| ≥ 80 | A |
| ≥ 70 | B |
| ≥ 60 | C |
| ≥ 50 | D |
| < 50 | F |

### Eco Badges

**Driver badges** — based on average CO2 per trip (`DriverService.java`):

| Badge | Threshold |
|---|---|
| Eco Champion | ≤ 10 kg / trip |
| Sustainable Driver | ≤ 25 kg / trip |
| Standard Driver | above 25 kg |

**Rider badges** — based on total cumulative CO2 across all trips (`Co2AnalyticsService.java`):

| Badge | Threshold |
|---|---|
| Eco Champion | < 20 kg total |
| Sustainable Commuter | 20–50 kg total |
| Active Explorer | > 50 kg total |

The frontend renders badges in three variants — shield, pill, and full — across five visual tiers: Green Starter, Eco Rider, Green Champion, Eco Warrior, and Eco Legend (`EcoBadge.js`).

### Carbon Cost

**Service:** `CarbonScoringService.java`

Each trip's carbon footprint is converted to a financial cost using a $100/ton carbon price:

```
carbonCost (USD) = actualCO2_kg × (100 / 1000) = actualCO2_kg × 0.10
```

This value is stored as `Route.carbonCost` and surfaced in the eco report.

### Daily Score Refresh

**Scheduler:** `FuelScoringScheduler.java`

A cron job runs every day at midnight (`0 0 0 * * ?`). It iterates all drivers, decodes their VIN via the NHTSA API, computes the fuel-based eco score, and updates `driver.trustScore`. This keeps the driver ranking algorithm current without requiring per-trip recalculation.

### Rider Carbon Analytics

**Service:** `Co2AnalyticsService.java`  
**Frontend:** `UserEcoReport.js`, `EcoUtils.js`

The rider-facing eco dashboard uses a baseline of **2.31 kg CO2 per average car trip**. Monthly savings are computed as:

```
monthlySavings = AVG_CAR_KG - tripCarbonCost   (per trip, summed monthly)
```

Real-world equivalents displayed to the rider:
- Trees saved (1 year of growth)
- Car kilometers offset
- Household energy days equivalent

The `EcoRing` component in `EcoUtils.js` renders a circular score visualization — gold (≥ 70), silver (≥ 40), bronze (< 40).

---

## Key Features

### Intelligent Driver Matching
- Haversine-formula distance calculation between rider pickup and all available drivers
- Composite eco-weighted ranking (see Driver Ranking Algorithm above)
- Vehicle type filtering (EV, Hybrid, Sedan, SUV, Bike)
- Prevents double-accepting the same ride request

### Trip Lifecycle Management
- State machine: `ACCEPTED → IN_PROGRESS → COMPLETED`
- Estimated and actual CO2 emissions computed and stored at each stage
- Trip eco score finalized on `endTrip` via VIN lookup

### Driver Eco Dashboard (`GET /driver/me/eco-report`)
- Total CO2 emitted, trip count, average per trip
- Carbon score (0–100) and letter rating (A+ to F)
- Monthly CO2 savings breakdown
- Ride type distribution (pooled vs solo, vehicle mix)
- Eco badge assignment

### Rider Eco Dashboard (`GET /user/eco/ecoReport`)
- Monthly savings vs average car baseline
- Progress bar toward monthly green goal
- Real-world impact equivalents
- Trip-level carbon cost breakdown

### Authentication & Security
- JWT-based auth for riders and drivers (separate flows)
- Spring Security with role-aware routing
- Redis-backed session caching

### Emission Estimate Endpoint
- Pre-ride CO2 estimate without creating a trip: `GET /ride/emissionEstimate?distanceKm=&vehicleType=`

---

## Architecture

```
Rider Request
    │
    ▼
RideRequestService ──► DriverScoringService (rank by distance + eco score)
    │
    ▼
Nearest Eco-Ranked Driver Notified
    │
    ▼
Driver Accepts ──► EmissionUtils (estimated + actual CO2 via Climatiq)
                ──► CarbonScoringService (efficiency score, carbon cost)
                ──► Route & Trip persisted
    │
    ▼
Trip In Progress ──► Real-time tracking
    │
    ▼
Driver Ends Trip ──► FuelScoringService (VIN → fuel type → eco score)
                 ──► DriverService.updateDriverFuelStats()
                 ──► Trip.ecoScore saved
    │
    ▼
FuelScoringScheduler (daily midnight) ──► trustScore updated on Driver
    │
    ▼
Driver Eco Report / Rider Carbon Dashboard
```

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Backend framework | Spring Boot | 3.4.5 |
| Language | Java | 17 |
| Build | Maven | 3.x |
| Database | MySQL / Aurora Serverless v2 | 8.0+ |
| ORM | Hibernate + JPA | (Spring Boot 3.4.5) |
| Caching | Redis | 6.x+ |
| Authentication | JWT (jjwt) | 0.11.5 |
| DTO mapping | MapStruct | 1.5.5 |
| Boilerplate | Lombok | 1.18.28 |
| API docs | SpringDoc OpenAPI (Swagger) | 2.5.0 |
| Testing | JUnit + Testcontainers + Mockito | — |
| Frontend | React | 19.1.0 |
| Routing | React Router DOM | 7.6.1 |
| Styling | Tailwind CSS | 3.4.17 |
| HTTP client | Axios | 1.9.0 |
| Charts | Recharts | 2.15.4 |
| Cloud hosting | AWS Elastic Beanstalk | — |
| Database hosting | Aurora Serverless v2 | — |

---

## API Reference

### Authentication

```http
POST /user/login
POST /user/register
POST /driver/login
POST /driver/register
```

### Ride Requests (Rider)

```http
POST /ride/request
{
  "pickupLatitude": 39.1696,
  "pickupLongitude": -86.5385,
  "dropoffLatitude": 39.7684,
  "dropoffLongitude": -86.1581,
  "vehicleType": "EV"
}

GET /ride/emissionEstimate?distanceKm=12.5&vehicleType=SEDAN
# Response: { "vehicleType": "SEDAN", "distanceKm": 12.5, "estimatedCO2kg": 2.16 }
```

### Driver Operations

```http
POST /driver/acceptRide/{rideRequestId}   # Accept + compute emissions
POST /driver/endTrip/{tripId}             # End trip + compute eco score
GET  /driver/me/eco-report                # Full eco dashboard
GET  /driver/me/carbon-impact             # Total CO2 impact
GET  /driver/me/trip-count                # Trip statistics
POST /driver/fuelScoring                  # Compute eco score from VIN
```

**Eco Report response:**
```json
{
  "tripCount": 12,
  "totalCO2": 42.8,
  "carbonScore": 85.4,
  "carbonRating": "A",
  "monthlyCo2Savings": [
    { "month": "APR", "co2": 18.3 },
    { "month": "MAY", "co2": 24.5 }
  ],
  "rideTypeDistribution": [
    { "name": "EV", "value": 9 },
    { "name": "HYBRID", "value": 3 }
  ]
}
```

### User Profile & Eco

```http
GET /user/profile         # Profile with eco badge
GET /user/eco/ecoReport   # Rider carbon dashboard
GET /user/tripsHistory    # Trip history with per-trip emissions
```

### Health Check

```http
GET /health   # Returns 200 OK (used by Elastic Beanstalk)
```

---

## Project Structure

```
ecober/
├── backend/
│   └── src/main/java/com/ecober/
│       ├── adapter/
│       │   ├── controller/          # DriverController, RideController, UserController
│       │   ├── Dto/                 # Request/response DTOs (CarbonDTO, TripDTO, DriverDTO...)
│       │   └── mapper/              # MapStruct mappers
│       ├── application/config/      # Security, Redis, Swagger config
│       ├── domain/
│       │   ├── model/               # JPA entities (Driver, Trip, Route, User, RideRequest...)
│       │   └── service/             # Business logic
│       │       ├── CarbonScoringService.java    # Letter grades, CO2 savings, carbon cost
│       │       ├── Co2AnalyticsService.java     # Rider carbon analytics & badges
│       │       ├── DriverScoringService.java    # Composite driver ranking algorithm
│       │       ├── DriverService.java           # Trip lifecycle, eco score on endTrip
│       │       ├── FuelScoringService.java      # Fuel type → eco score (0-100)
│       │       ├── FuelScoringScheduler.java    # Daily midnight trustScore refresh
│       │       ├── RideRequestService.java      # Driver matching & broadcasting
│       │       └── UserService.java             # User profile with carbon stats
│       ├── infrastructure/repository/           # Spring Data JPA repositories
│       ├── security/                            # JWT filter, CustomUserDetails
│       └── util/
│           ├── EmissionUtils.java   # Estimated + actual CO2 (Climatiq API)
│           ├── FuelMappingUtil.java # VIN → fuel type (NHTSA API)
│           └── GeoUtils.java        # Haversine distance, travel time estimate
│
├── frontend/ecober/src/
│   ├── components/
│   │   ├── ecoReport.js            # Driver eco dashboard
│   │   ├── UserEcoReport.js        # Rider carbon dashboard
│   │   ├── EcoBadge.js             # Badge rendering (shield, pill, full)
│   │   ├── EcoUtils.js             # EcoRing, Co2Equiv, real-world equivalents
│   │   ├── Profile.js              # User profile with eco metrics
│   │   ├── DriverProfile.js        # Driver profile with trust score
│   │   ├── riderBooking.js         # Ride request with vehicle type selection
│   │   └── ...                     # Auth, trip tracking, history components
│   ├── dashboards/
│   │   ├── dashboardLayout.js      # Rider shell
│   │   └── DriverDashboardLayout.js
│   └── App.js                      # Route definitions
│
└── deployment-records/
    └── deployment.md               # AWS deployment journey & troubleshooting log
```

---

## External APIs

| API | Purpose | Used In |
|---|---|---|
| [Climatiq](https://www.climatiq.io/) | Live CO2 emissions by vehicle type and distance | `EmissionUtils.java` |
| [NHTSA VPIC](https://vpic.nhtsa.dot.gov/api/) | VIN decoding to extract fuel type | `FuelMappingUtil.java` |
| Google Maps | Geocoding, routing, distance/duration | `GeocodingService.java` |
| OpenRouter | LLM-based route optimization | `RouteOptimizingService.java` |

---

## Deployment

Production runs on AWS with a serverless-first setup:

```
Client → AWS Elastic Beanstalk (Spring Boot JAR)
              │
              └── Aurora Serverless v2 MySQL (auto-scaling, cost-efficient)
              └── Redis (ElastiCache) for session caching
```

Key deployment notes (see `deployment-records/deployment.md` for full log):
- Port binding configured via Elastic Beanstalk environment properties to match Spring Boot
- Hibernate `ddl-auto=update` handles schema migrations automatically
- `/health` endpoint added for Elastic Beanstalk health checks
- Spring Security relaxed during initial deployment testing, then tightened
- CORS configured for frontend origin

---

## Future Enhancements

- Real-time driver location updates via WebSockets
- Kafka event streaming for trip state changes
- Frontend hosting on AWS S3 + CloudFront
- Pooled/shared ride CO2 splitting between multiple riders
- Advanced demand prediction for driver pre-positioning
- Carbon offset marketplace integration
