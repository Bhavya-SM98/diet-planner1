# 🥗 NutriAI - AI Personalized Diet Planner
### Full Stack Project | React + Spring Boot + MySQL + Claude AI

---

## 📁 Project Structure

```
diet-planner/
├── frontend/          ← React App (Port 3000)
├── backend/           ← Spring Boot API (Port 8080)
└── database/
    └── schema.sql     ← MySQL Schema
```

---

## ⚙️ Prerequisites

Install these before starting:
- ✅ Node.js v18+ → https://nodejs.org
- ✅ Java JDK 17+ → https://adoptium.net
- ✅ Maven 3.8+ → https://maven.apache.org
- ✅ MySQL 8.0+ → https://dev.mysql.com/downloads
- ✅ Claude API Key → https://console.anthropic.com

---

## 🗄️ Step 1: Database Setup

```sql
-- Open MySQL and run:
mysql -u root -p

-- Then paste contents of database/schema.sql
-- Or run:
mysql -u root -p < database/schema.sql
```

---

## ☕ Step 2: Backend Setup (Spring Boot)

### 2.1 Configure application.properties
Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
claude.api.key=YOUR_CLAUDE_API_KEY
```

### 2.2 Get Claude API Key
1. Go to https://console.anthropic.com
2. Click "API Keys" → "Create Key"
3. Copy and paste into application.properties

### 2.3 Run the Backend
```bash
cd backend
mvn spring-boot:run
```
Backend starts at → http://localhost:8080

---

## ⚛️ Step 3: Frontend Setup (React)

```bash
cd frontend
npm install
npm start
```
Frontend starts at → http://localhost:3000

---

## 🚀 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login user |

### Food Analysis
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/food/analyze | Upload food image → AI analysis |
| GET | /api/food/daily?date=YYYY-MM-DD | Get daily food summary |
| GET | /api/food/weekly?startDate=&endDate= | Get weekly food logs |
| DELETE | /api/food/{id} | Delete a food log |

### User & Diet
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/user/profile | Get user profile |
| PUT | /api/user/profile | Update profile + recalculate TDEE |
| POST | /api/user/diet-plan | Generate AI diet plan |
| GET | /api/user/diet-plans | Get all diet plans |
| POST | /api/user/weight | Log weight |
| GET | /api/user/weight | Get weight history |

---

## ✨ Features Implemented

- [x] JWT Authentication (Register/Login)
- [x] Food Photo Upload + AI Nutrition Analysis (Claude API)
- [x] Daily Food Tracker by meal type
- [x] Calorie & Macro tracking with progress bars
- [x] AI-Generated Weekly Diet Plans
- [x] TDEE/Calorie Target Calculation (Harris-Benedict formula)
- [x] Weight Logging & Trend Charts
- [x] Dashboard with Doughnut & Line charts
- [x] Responsive Design (Mobile + Desktop)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, TailwindCSS, Chart.js, React Router v6 |
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Database | MySQL 8.0 with JPA/Hibernate |
| AI | Claude claude-sonnet-4-20250514 API |
| Auth | JWT (JSON Web Tokens) |
| File Upload | Spring Multipart + Multer |

---

## 🔧 Troubleshooting

**CORS Error?**
→ Make sure backend is running on port 8080 and frontend on 3000

**MySQL Connection Failed?**
→ Check username/password in application.properties
→ Make sure MySQL service is running

**Claude API Error?**
→ Verify your API key is correct and has credits

**Maven Build Failed?**
→ Make sure Java 17+ is installed: `java -version`

---

## 📱 App Pages

| Page | Path | Description |
|------|------|-------------|
| Dashboard | / | Overview, charts, daily summary |
| Analyze Food | /analyze | Upload photo → AI analysis |
| Food Tracker | /tracker | View/delete daily food logs |
| Diet Plan | /diet-plan | Generate & view AI meal plans |
| Profile | /profile | Update health info, log weight |
