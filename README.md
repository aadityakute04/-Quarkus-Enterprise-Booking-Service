# CSC8104 — Quarkus Enterprise Booking Service

**Author:** Aaditya Kute  
**Module:** Enterprise Software & Services (CSC8104)  
**University:** Newcastle University  

This project is a Quarkus-based enterprise booking service built as part of the CSC8104 module.  
It provides REST APIs for managing customers, commodity bookings (taxi, hotel, flight), and coordinating multi-service bookings through a Travel Agent service.  
The system demonstrates clean architecture, dependency injection, REST design principles, and automated testing in Quarkus.

---

## 📘 Project Overview

This application simulates an enterprise travel booking platform:

✔ Customer registration & management  
✔ Hotel, Taxi, and Flight commodity bookings  
✔ Travel Agent service that performs **aggregate bookings**  
✔ Compensation logic if one service fails  
✔ Integration tests validating full booking workflows  

The application leverages **Quarkus**, offering extremely fast startup, hot reload, and a lightweight development environment.

---

## ✨ Features

### **REST Services**
- Customer service  
- Contact service  
- Hotel booking service  
- Taxi booking service  
- Flight booking service  
- Guest booking service  
- Travel Agent aggregate booking service  

### **Enterprise Logic**
- Data validation & domain exceptions  
- Multi-service orchestration  
- Compensation workflow on failures  
- Clean layering (REST → Service → Repository → Model)

### **Testing**
- Unit tests  
- REST-assured integration tests  
- End-to-end booking flow validation  

When running in dev mode:

