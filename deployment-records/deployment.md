# Ecober: AWS Serverless Deployment Retrospective

### Overview

Ecober is a backend system for a carbon-conscious ride-sharing platform. The goal was to design and deploy the backend on AWS using a **serverless architecture** that is cost-efficient (Free Tier) while supporting production-grade features like driver eco-scoring, trip management, and CO₂ tracking.

This document outlines my deployment journey, highlighting the challenges faced and the solutions implemented to achieve a functional serverless setup.

---

## Deployment Journey

### 1. Packaging the Backend for AWS

* Started with a standard Spring Boot monolith, which isn’t directly compatible with Lambda-based serverless environments due to cold starts and port binding issues.
* Refactored API endpoints to reduce startup times and packaged the backend as a **Lambda handler** using Spring Cloud Function.
* Used **API Gateway** to expose REST endpoints securely.

---

### 2. Database Configuration

* Initially set up a **MySQL RDS instance** for persistence.
* Encountered issues with Hibernate schema generation because of foreign key constraints in the existing schema.
* Solution: Dropped the manually created schema and allowed Hibernate to auto-create tables.
* Later migrated to **Aurora Serverless v2** to:

  * Enable auto-scaling to zero for cost efficiency.
  * Stay within AWS Free Tier limits while testing.

---

### 3. Handling Security and Routing Issues

* Faced **403 Forbidden errors** due to Spring Security blocking API Gateway requests.
* Solution:

  * Updated Spring Security configuration to whitelist API Gateway endpoints.
  * Temporarily relaxed CORS policies during debugging.

---

### 4. Debugging Deployment Errors

* Fixed **port binding issues** by updating Elastic Beanstalk environment variables and aligning them with Spring Boot’s application properties.
* Added a `/health` endpoint to enable Elastic Beanstalk health monitoring.
* Captured deployment logs and iteratively resolved errors like:

  * Foreign key constraint violations.
  * API Gateway integration mismatches.

---

### 5. Final Architecture

* **API Gateway:** Handles routing and SSL termination.
* **AWS Lambda:** Runs the backend APIs with auto-scaling.
* **Aurora Serverless v2 (MySQL):** Provides persistence with on-demand scaling.

This setup ensures scalability, resilience, and minimal operational overhead while staying cost-effective.

---

## Key Takeaways

* Designed and deployed a **production-grade backend** on AWS Free Tier.
* Debugged real-world issues like Hibernate schema creation, Spring Security misconfigurations, and Lambda cold starts.
* Achieved a serverless architecture that can scale to zero during idle times and automatically scale up under load.

---

## Artifacts Included

* Backend source code (Spring Boot + Serverless packaging).
* AWS deployment logs and screenshots showing key issues and fixes (e.g., 403 errors, port binding errors).
* Markdown documentation describing the deployment journey and architecture decisions.

