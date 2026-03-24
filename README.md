# Secure File Statement Delivery System

## 📌 Overview

This project is a **secure document delivery system** that allows:

* **Admins** to upload customer statements (PDFs)
* **Admins** to generate secure, time-limited download links
* **Customers** to download their statements using a secure token

The system is built using:

* Java (Jetty embedded server)
* Servlets (no heavy frameworks)
* JWT-style token validation
* File-based storage
* Docker for containerization

---

## 🏗️ Architecture

The application follows a simplified MVC-style structure:

* **Boundary (UI):**

  * `admin.html` – Admin dashboard (upload + generate link)
  * `download.html` – Customer download page

* **Control (Servlets):**

  * `UploadServlet`
  * `GenerateLinkServlet`
  * `DownloadServlet`

* **Entity:**

  * `Statement`
  * `Token`

* **Service Layer:**

  * `FileStorageService`
  * `TokenService`

---

## 🚀 Features

* Secure file upload (PDF statements)
* Token-based secure download links
* Token expiration support
* File storage isolation
* Embedded Jetty server (no external server needed)
* Fully Dockerized (no local Java/Maven required to run)

---

## 📁 Project Structure

```
secure-statements/
│
├── docker/
│   └── Dockerfile
│
├── src/
│   ├── main/
│   │   ├── java/com/securestatements/
│   │   └── resources/webapp/
│   │       ├── admin.html
│   │       └── download.html
│
├── target/
│   └── secure-statements.jar
│
├── pom.xml
└── README.md
```

---

## 🐳 Running with Docker (Recommended)

### ✅ Prerequisites

Install:

* Docker Desktop

Verify installation:

```
docker --version
```

---

## 🔧 Pull and run the project


```
docker pull nkosi/secure-statements:latest
docker run -p 8080:8080 nkosi/secure-statements

```
---
## 🧪 Running Without Docker (Optional)

```
mvn clean package
java -jar target/secure-statements.jar
```

---

## 🌐 Access the Application

Open in browser:

```
http://localhost:8080
```

You will be redirected to:

👉 Admin Dashboard

---

## 🔐 How It Works

### 1. Upload Statement

Admin uploads a PDF and provides:

* Customer ID

The system:

* Saves file to storage
* Stores metadata

---

### 2. Generate Secure Link

Admin generates a tokenized link:

```
http://localhost:8080/download?token=XYZ
```

---

### 3. Download Statement

Customer:

* Clicks secure link
* Token is validated
* File is returned if valid

---

## ⚠️ Security Notes

* Tokens expire after a defined time
* Tokens can be single-use (optional enhancement)
* No authentication layer (can be extended)

---


## 🚀 Notable Future Improvements

* Add authentication (admin login)
* Replace file storage with cloud storage (S3)
* Use database (PostgreSQL)
* Add HTTPS support
* Implement audit logging
* Upgrade to Spring Boot

---

## 👨‍💻 Author

Secure Statements Demo Project
Built for skills demo and showcase understanding of secure file delivery patterns

---

## 📄 License

This project is for skills demo purposes.
