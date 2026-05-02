# Shift Scheduling

基於 Spring Boot 的班表管理系統，整合 PostgreSQL、Redis 快取與 JWT 認證，透過 Docker Compose 一鍵部署。

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=flat-square&logo=springboot) ![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?style=flat-square&logo=postgresql) ![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=flat-square&logo=redis) ![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)

---

## 架構概覽

| 容器名稱 | 服務 | 連接埠 |
|---|---|---|
| `shift-app` | Spring Boot | `:8080` |
| `shift-postgres` | PostgreSQL | `:5432` |
| `shift-redis` | Redis | `:6379` |

## 前置需求

- ✅ Docker Desktop（Mac / Windows / Linux）
- ❌ 無需安裝 Java、Maven、PostgreSQL、Redis

---

## 快速開始

### 1. 克隆專案

```bash
git clone https://github.com/chipangao/shift_scheduling.git
cd shift-scheduling
```

### 2. 啟動所有服務

```bash
docker compose up -d
```

### 3. 查看容器狀態

```bash
docker ps
```

預期看到三個容器：

- `shift-postgres` — PostgreSQL（5432）
- `shift-redis` — Redis（6379）
- `shift-app` — Spring Boot（8080）

### 4. 資料庫建立

進入 PostgreSQL 容器：

```bash
docker exec -it shift-postgres psql -U postgres -d shift_db
```

建立員工表（`employee`）：

```sql
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    position VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

建立班表表（`shift`）：

```sql
CREATE TABLE IF NOT EXISTS shift (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL REFERENCES employee(id),
    shift_date DATE NOT NULL,
    shift_type VARCHAR(20) NOT NULL CHECK (shift_type IN ('morning', 'afternoon', 'night')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(employee_id, shift_date)
);
```

建立會員表（`app_user`）：

```sql
CREATE TABLE IF NOT EXISTS app_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

確認資料表建立成功：

```sql
\dt
```

預期輸出：

```
 Schema |   Name    | Type  |  Owner
--------+-----------+-------+----------
 public | app_user  | table | postgres
 public | employee  | table | postgres
 public | shift     | table | postgres
```

離開 psql：

```sql
\q
```

---

## API 文件

### 公開 API（無需 JWT Token）

| Method | Endpoint | 說明 |
|---|---|---|
| `GET` | `/api/health` | 健康檢查 |
| `POST` | `/api/auth/register` | 註冊會員 |
| `POST` | `/api/auth/login` | 登入取得 Token |

### 員工管理 API（需要 JWT Token）🔒

| Method | Endpoint | 說明 |
|---|---|---|
| `GET` | `/api/employees` | 查詢所有員工 |
| `GET` | `/api/employees/{id}` | 查詢單一員工 |
| `POST` | `/api/employees` | 新增員工 |
| `PUT` | `/api/employees/{id}` | 修改員工 |
| `DELETE` | `/api/employees/{id}` | 刪除員工 |

### 班表管理 API（需要 JWT Token）🔒

| Method | Endpoint | 說明 |
|---|---|---|
| `GET` | `/api/shifts` | 查詢所有班表 |
| `GET` | `/api/shifts/{id}` | 查詢單一班表 |
| `GET` | `/api/shifts/employee/{employeeId}` | 查詢特定員工的班表 |
| `GET` | `/api/shifts/date/{date}` | 查詢特定日期的班表 |
| `POST` | `/api/shifts` | 新增班表（含重複檢查） |
| `DELETE` | `/api/shifts/{id}` | 刪除班表 |

---

## 完整測試流程

### 步驟 1：健康檢查

```bash
curl http://localhost:8080/api/health
```

```json
{"message":"Spring Boot is running","status":"ok"}
```

### 步驟 2：註冊會員

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456","email":"admin@example.com"}'
```

```
註冊成功
```

### 步驟 3：登入取得 JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

```json
{"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NzcwNDYxNCwiZXhwIjoxNzc3NzkxMDE0fQ.xxx"}
```

### 步驟 4：儲存 Token 為環境變數

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NzcwNDYxNCwiZXhwIjoxNzc3NzkxMDE0fQ.xxx"
```

### 步驟 5：新增員工

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"John Doe","email":"john@example.com","position":"Engineer"}'
```

```json
{"id":1,"name":"John Doe","email":"john@example.com","position":"Engineer","createdAt":"2026-05-02T06:00:00"}
```

### 步驟 6：查詢所有員工

```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer $TOKEN"
```

```json
[{"id":1,"name":"John Doe","email":"john@example.com","position":"Engineer","createdAt":"2026-05-02T06:00:00"}]
```

### 步驟 7：查詢單一員工

```bash
curl -X GET http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $TOKEN"
```

```json
{"id":1,"name":"John Doe","email":"john@example.com","position":"Engineer","createdAt":"2026-05-02T06:00:00"}
```

### 步驟 8：修改員工

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"John Updated","email":"john@example.com","position":"Senior Engineer"}'
```

```json
{"id":1,"name":"John Updated","email":"john@example.com","position":"Senior Engineer","createdAt":"2026-05-02T06:00:00"}
```

### 步驟 9：新增班表

```bash
curl -X POST http://localhost:8080/api/shifts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"employeeId":1,"shiftDate":"2026-05-01","shiftType":"morning"}'
```

```json
{"id":1,"employeeId":1,"shiftDate":"2026-05-01","shiftType":"morning","createdAt":"2026-05-02T06:00:00"}
```

### 步驟 10：查詢所有班表

```bash
curl -X GET http://localhost:8080/api/shifts \
  -H "Authorization: Bearer $TOKEN"
```

```json
[{"id":1,"employeeId":1,"shiftDate":"2026-05-01","shiftType":"morning","createdAt":"2026-05-02T06:00:00"}]
```

### 步驟 11：測試重複排班（預期失敗）⚠️

```bash
curl -X POST http://localhost:8080/api/shifts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"employeeId":1,"shiftDate":"2026-05-01","shiftType":"afternoon"}'
```

```json
{"error":"員工當天已有班表，不可重複排班"}
```

### 步驟 12：查詢員工的班表

```bash
curl -X GET http://localhost:8080/api/shifts/employee/1 \
  -H "Authorization: Bearer $TOKEN"
```

```json
[{"id":1,"employeeId":1,"shiftDate":"2026-05-01","shiftType":"morning","createdAt":"2026-05-02T06:00:00"}]
```

### 步驟 13：查詢特定日期的班表

```bash
curl -X GET http://localhost:8080/api/shifts/date/2026-05-01 \
  -H "Authorization: Bearer $TOKEN"
```

```json
[{"id":1,"employeeId":1,"shiftDate":"2026-05-01","shiftType":"morning","createdAt":"2026-05-02T06:00:00"}]
```

### 步驟 14：刪除班表

```bash
curl -X DELETE http://localhost:8080/api/shifts/1 \
  -H "Authorization: Bearer $TOKEN"
```

> 無內容，HTTP 204

### 步驟 15：刪除員工

```bash
curl -X DELETE http://localhost:8080/api/employees/1 \
  -H "Authorization: Bearer $TOKEN"
```

> 無內容，HTTP 204

### 步驟 16：確認員工已被刪除

```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer $TOKEN"
```

```json
[]
```

---

## Redis 快取說明

### 為什麼使用 Redis？

| 優勢 | 說明 |
|---|---|
| 🚀 提升查詢效能 | 員工資料頻繁查詢，使用快取減少資料庫負擔 |
| 📉 降低連線數 | 相同資料重複查詢直接從 Redis 返回 |
| 📖 讀多寫少場景 | 班表系統查詢次數遠大於修改次數 |

### 快取策略

| 操作 | 快取行為 | 說明 |
|---|---|---|
| 查詢員工 | `@Cacheable` | 第一次查詢存入 Redis，後續直接讀取 |
| 修改員工 | `@CacheEvict` | 清除該員工快取（下次查詢重新載入） |
| 刪除員工 | `@CacheEvict` | 清除該員工快取 |

### 驗證快取是否生效

```bash
# 第一次查詢（會查資料庫）
curl -X GET http://localhost:8080/api/employees/1 -H "Authorization: Bearer $TOKEN"

# 查看日誌，應該出現 "查資料庫: employee id=1"
docker logs shift-app --tail 10

# 第二次查詢（不會查資料庫，直接從 Redis 讀取）
curl -X GET http://localhost:8080/api/employees/1 -H "Authorization: Bearer $TOKEN"

# 查看日誌，不應該再出現 "查資料庫"
docker logs shift-app --tail 10

# 直接查看 Redis 中的快取
docker exec -it shift-redis redis-cli
keys *
get "employee::1"
exit
```

---

## JWT 認證流程

### API 權限對照表

| API 路徑 | 需要 JWT | 說明 |
|---|---|---|
| `GET /api/health` | — | 健康檢查 |
| `POST /api/auth/register` | — | 註冊 |
| `POST /api/auth/login` | — | 登入 |
| `GET /api/employees/*` | 🔒 | 員工查詢 |
| `POST /api/employees` | 🔒 | 新增員工 |
| `PUT /api/employees/*` | 🔒 | 修改員工 |
| `DELETE /api/employees/*` | 🔒 | 刪除員工 |
| `GET /api/shifts/*` | 🔒 | 班表查詢 |
| `POST /api/shifts` | 🔒 | 新增班表 |
| `DELETE /api/shifts/*` | 🔒 | 刪除班表 |

### JWT Filter 運作流程

```
請求進入 → JwtAuthenticationFilter
    │
    ├── 公開 API（/api/auth/*, /api/health）→ 直接放行
    │
    └── 其他 API → 檢查 Authorization Header
                      │
                      ├── Token 有效 → 設定 SecurityContext，放行
                      │
                      └── Token 無效/過期 → 401 Unauthorized
```

---

## 專案結構

```
shift-scheduling/
├── src/main/java/com/example/shift_scheduling/
│   ├── ShiftSchedulingApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── RedisCacheConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── EmployeeController.java
│   │   └── ShiftController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── EmployeeService.java
│   │   └── ShiftService.java
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   ├── EmployeeMapper.java
│   │   └── ShiftMapper.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Employee.java
│   │   └── Shift.java
│   └── util/
│       └── JwtUtil.java
├── src/test/java/
│   └── com/example/shift_scheduling/service/
│       ├── EmployeeServiceTest.java
│       └── ShiftServiceTest.java
├── src/main/resources/
│   └── application.properties
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 單元測試

### 測試覆蓋範圍

| 測試類別 | 測試數量 | 測試內容 |
|---|---|---|
| `EmployeeServiceTest` | 5 | getById、getAll、create、update、delete |
| `ShiftServiceTest` | 5 | getById、getByEmployee、create、duplicate、delete |
| `ShiftSchedulingApplicationTests` | 1 | Spring Context 載入測試 |
| **總計** | **11** | **全部通過** |

### 執行測試

```bash
# 本機執行（需安裝 Java）
./mvnw test

# 或用 Docker 執行（不需安裝 Java）
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 mvn test
```

### 測試結果範例

```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- EmployeeServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- ShiftServiceTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- ShiftSchedulingApplicationTests
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Docker 指令

| 用途 | 指令 |
|---|---|
| 啟動所有服務 | `docker compose up -d` |
| 停止所有服務 | `docker compose down` |
| 重新啟動 | `docker compose restart` |
| 查看容器狀態 | `docker ps` |
| 查看應用程式日誌 | `docker logs shift-app -f` |
| 查看資料庫日誌 | `docker logs shift-postgres -f` |
| 進入資料庫 | `docker exec -it shift-postgres psql -U postgres -d shift_db` |
| 進入 Redis | `docker exec -it shift-redis redis-cli` |

> ⚠️ `docker compose down -v` 會清除所有容器和資料，執行前請確認不需要保留資料。
