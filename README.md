## Overview

This application is designed for managing **foreign exchange rates** and handling currency conversion transactions. It consists of the following key components:

- **Scheduler**: The scheduler service is responsible for fetching up-to-date foreign exchange rate data at regular intervals. The frequency of updates can be customized using a cron expression (`CRON_JOB` environment variable). The scheduler ensures that the system always has the latest exchange rates to work with.
  
- **Gateway**: The gateway service handles currency conversion operations. It allows users to exchange one currency for another based on the latest exchange rate. Additionally, it stores transaction details, which can be later searched for auditing and reporting purposes.

### How It Works:

1. **Scheduler** periodically fetches the latest exchange rate data from an external source (e.g., an API) and updates the system with the new rates. The frequency of these updates can be controlled via the `CRON_JOB` environment variable.
   
2. **Gateway** provides the functionality to convert currencies by referencing the most up-to-date exchange rates. All currency exchange transactions are stored in a database, allowing users to search through past conversions and track their exchange activity.

## Configuration and Running the Application

To run the application, you can configure the environment variables in a way that best fits your setup. This flexibility allows you to manage the configuration for the services (e.g., **Gateway**, **Scheduler**, **Database**) as needed.

### 1. **Setting Environment Variables**

You can set environment variables directly in the `docker-compose.yml` file, use an `.env` file, or set them inline when running Docker Compose. This flexibility allows you to configure the application to suit your environment and preferences.

### 2. **Building and Running the Application**
1. **Clean and Build the Application**:
   Run the following command to clean and build the application:

   ```bash
   ./gradlew clean build

2. **Run the Containers with Docker Compose**
 After the build, start the application using Docker Compose:
    ```bash
     docker-compose up --build

3. **Open Swagger UI**
To access the Swagger UI, navigate to the following URL:
http://localhost:8081/swagger-ui/index.html#/currency-exchange

This will allow you to interact with the API and test the available endpoints for the currency exchange service.

