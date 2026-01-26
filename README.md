# Hotel Booking Microservices System

This project is a comprehensive Hotel Booking application built using a Microservices architecture. It features a responsive frontend for users and administrators, and a robust backend handling hotel management, bookings, reviews, and file storage.

## 📖 Overview

The system allows users to search for hotels, view details, book rooms, and manage their profiles. It includes a dedicated Admin Dashboard for hotel owners/administrators to manage hotel listings, room types, and view business statistics.

## ✨ Key Features

### 🏨 User Features

- **Search & Discovery:** Search hotels by location, check-in/out dates, and room count.
- **Hotel Details:** View comprehensive hotel information, star ratings, descriptions, and image galleries.
- **Booking System:** Real-time room availability checking and booking process.
- **User Accounts:** Registration, Login (JWT-based), and Profile management (update info, avatar, deactivate account).
- **My Bookings:** View booking history, status (Confirmed/Pending), and booking references.
- **Reviews:** Write and view reviews for hotels after a stay.
- **Chatbot:** Integrated AI chatbot for customer support.

### 🛠 Admin Features

- **Dashboard:** Overview of total hotels, bookings, and revenue.
- **Hotel Management:** Create, Update, and Delete hotel listings.
- **Room Management:** Manage room types, pricing, capacity, and availability.
- **Image Management:** Upload and manage images for hotels and room types.

## 🏗 Architecture & Tech Stack

### Frontend

- **Core:** HTML5, CSS3, Vanilla JavaScript (ES6 Modules).
- **Styling:** Tailwind CSS (Utility-first framework).
- **Icons:** Remix Icon, FontAwesome.
- **Maps:** Google Maps API integration.
- **State Management:** `sessionStorage` and `localStorage` for auth tokens and booking sessions.

### Backend (Microservices)

- **Framework:** Spring.
- **Database:** PostgreSQL, MongoDB, Redis.
- **Gateway:** API Gateway running on port `8888`.
- **Services Identified:**
  - `Hotel Service`: Manages hotel data, room types, and file storage.
  - `Booking Service`: Handles reservation logic.
  - `User/Auth Service`: Handles JWT authentication and user profiles.
  - `Review Service`: Manages user feedback.
  - `Chatbot Service`: AI integration.
- **File Storage:** Local file system storage (`uploads/` directory).

## 📂 Project Structure

```text
microservice/
├── front-end/
│   ├── assets/
│   │   ├── js/
│   │   │   ├── api/          # API service connectors
│   │   │   ├── pages/        # Page-specific logic (search, booking, admin)
│   │   │   └── utils/        # Utilities (JWT parsing, formatting)
│   └── ...
├── hotel_service/            # Spring Boot Service
│   ├── src/main/java/.../controller  # REST Controllers
│   ├── src/main/java/.../service     # Business Logic (FileStorage, etc.)
│   └── ...
└── ...
```

## 🚀 Getting Started

### Prerequisites

- **Java JDK 17+**
- **Maven**
- **Database** (PostgreSQL/MySQL configured in application.properties).

### Backend Setup

1.  **Navigate to the service directory:**
    ```bash
    cd hotel_service
    ```
2.  **Configure Database:**
    Update `src/main/resources/application.properties` with your database credentials.
3.  **Run the Service:**
    ```bash
    mvn spring-boot:run
    ```
    _Ensure the API Gateway is running on port `8888` as the frontend relies on `http://localhost:8888`._

### Frontend Setup

1.  **Navigate to the frontend directory:**
    ```bash
    cd front-end
    ```
2.  **Serve the files:**
    Since the project uses ES6 Modules (`import/export`), you cannot open `index.html` directly from the file system. You must use a local server.
    - **VS Code:** Use the "Live Server" extension.
    - **Python:** `python -m http.server 5500`
    - **Node:** `npx serve`

3.  **Access the App:**
    Open your browser at `http://localhost:5500/pages/main-page.html` (or your server's port).

## ⚙️ Configuration

### File Uploads

The backend is configured to store images locally. Ensure the root directory of the backend service has write permissions for creating the `uploads` folder.

- **Path:** `microservice/uploads/hotels` and `microservice/uploads/room_types`
- **Serving:** Static resources are mapped via `MvcConfig` to serve files from the absolute path.

### API Endpoints (Key Examples)

| Method | Endpoint                            | Description          |
| :----- | :---------------------------------- | :------------------- |
| `GET`  | `/api/v1/hotels/home`               | Get popular hotels   |
| `GET`  | `/api/v1/hotels/{id}`               | Get hotel details    |
| `POST` | `/api/v1/bookings/bookings`         | Create a new booking |
| `POST` | `/api/v1/files/hotel-images/upload` | Upload hotel image   |
| `POST` | `/auth/login`                       | User login           |

## 🤝 Contributing

1.  Fork the repository.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 📝 License

This project is part of a Graduate Thesis.

---

**Note:** Ensure the API Gateway is active before testing the frontend, as all API requests are routed through `http://localhost:8888`.
