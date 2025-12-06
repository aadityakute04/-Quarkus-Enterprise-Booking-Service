<<<<<<< HEAD
# CSC8104 Quickstart Project

This quickstart project is provided for students on the CSC8104 Enterprise Middleware module and provides a foundation for starting their coursework. Students are expected to download and build their solution within the provided project and should not aim to create a new project from scratch.

Within the project there is an example REST service for creating and storing contacts which can be accessed via the Swagger UI endpoint (http://localhost:8080/q/swagger-ui). It is encouraged that students spend spend time reading through this code to gain a strong understanding of how the project works. Not only this, but students are also encouraged to follow a similar packaging structure.

Students are not required to remove the contact service and can leave this functionality in their submission.

Throughout the coursework specification there are many links to various guides to help you complete the coursework. It is strongly encouraged that you spend time working through these guides before attempting to implement the specification requirements.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -D"net.bytebuddy.experimental"
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.
=======
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

>>>>>>> origin/main
