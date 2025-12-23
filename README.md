# OOAD Project

## Docker Setup

Hướng dẫn chạy project **OOAD** bằng Docker và Docker Compose.

## 📦 Getting Started

To get a local copy of this project up and running, follow these steps.

### 🚀 Prerequisites

- Docker: [Cài đặt Docker](https://docs.docker.com/get-docker/)
- Docker Compose: [Cài đặt Docker Compose](https://docs.docker.com/compose/install/)

## 🛠️ Installation

1. **Clone the repository:**

   ```bash
   git clone git@github.com:Phongdnh-uit/SE110.git
   cd SE110
   ```

2. **Get into docker directory for production**

   ```bash
   cd docker/prod
   ```

3. **Set up environment variables:**

   Create a `.env` file in the directory and use example.env to configure your environment variables.:

   ```env
   ....
   ```

4. **Run docker compose for server and database MYSQL**

   ```bash
   docker compose up --build
   ```

Open [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to view the SWAGGER API docs.
