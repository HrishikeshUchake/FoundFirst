# FoundFirst App – Lost & Found + Study Group Planner for ISU

**FoundFirst** is a unified web application designed for Iowa State University (ISU) students. It combines two essential campus utilities: a **Lost and Found portal** and a **Study Group Planner** to streamline campus life and academic collaboration.

[![FoundFirst App Demo](https://img.youtube.com/vi/LxyOGoTdRuE/0.jpg)](https://www.youtube.com/watch?v=LxyOGoTdRuE)

---

## Features

### Lost & Found Portal
- Post lost or found items with descriptions and images
- Browse or search categorized listings
- Initiate real-time chat with the item poster to coordinate returns
- Mark items as resolved to keep listings clean
- Image upload support for better item identification
- Location-based filtering for campus areas

### Study Group Planner
- Create or join study groups for ISU courses
- Schedule meetings with time slots and locations
- Live group chat and comments for collaboration
- Notifications for group updates and upcoming sessions
- Course-specific group organization
- Meeting reminder system

---

## Tech Stack

- **Backend**: Spring Boot, Java
- **Database**: MySQL
- **Real-Time Communication**: WebSocket (Spring)
- **CI/CD**: GitHub Actions + Docker
- **Design Patterns**: Command Pattern for modular UI actions
- **Project Management**: Agile methodology with UML artifacts and Scrum roles

---

## Getting Started

### Prerequisites
- Java 11 or higher
- MySQL 8.0+
- Maven 3.6+

### Installation

1. Clone the repository
```bash
git clone https://github.com/HrishikeshUchake/FoundFirst.git
cd FoundFirst
```

2. Set up the database
```sql
CREATE DATABASE foundfirst;
```

3. Configure application properties
```bash
# Update Backend/src/main/resources/application.properties with your database credentials
```

4. Build and run the application
```bash
cd Backend
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

---

## Key Functionalities

* **Real-time Communication**: WebSocket-powered chat system for seamless user interaction
* **Structured Design**: Backed by comprehensive UML design and documentation
* **Modular Architecture**: Command design pattern implementation for maintainable code
* **Automated Deployment**: CI/CD pipelines ensuring reliable releases
* **User Authentication**: Secure login system for ISU students
* **Responsive Design**: Mobile-friendly interface for on-the-go access

---

## API Documentation

The application provides RESTful APIs for:
- User management and authentication
- Lost & found item operations
- Study group management
- Real-time messaging

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

---

## License

MIT License — free to use, share, and modify.

---

## Team

Created by:
- [Hrishikesh Uchake](https://github.com/HrishikeshUchake)
- Subhajit Guchhait
- Jared Wong  
- Adam Abdi

**Iowa State University Students**  
Open to collaboration and feedback!

---

## Support

For questions or issues, please open an issue on GitHub or contact the development team.
