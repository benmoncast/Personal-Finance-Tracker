# Finance Tracker

A full-stack personal finance SaaS for tracking income, expenses, category budgets, accounts, subscriptions, transfers, savings goals, and reports.

## Stack

- React 19, Vite, Tailwind CSS 4, Axios, Lucide
- Spring Boot 3.5, Spring Security, stateless JWT authentication, Spring Data JPA
- MySQL 8+

## Run locally

1. Create `backend/.env` from `backend/.env.example` and update the MySQL credentials and JWT secret.
2. Start the API:

   ```powershell
   cd backend
   mvn spring-boot:run
   ```

3. Start the web app in another terminal:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

Open `http://localhost:5173`. With `APP_SEED_ENABLED=true`, the API creates sample categories and a demo workspace:

- Email: `demo@fintrack.app`
- Password: `Demo1234!`

The seeded administrator workspace uses `admin@fintrack.app` / `Admin1234!`. Change both demo passwords outside local development.

Hibernate can create/update the schema during development. The production-ready reference DDL is in [`backend/database/schema.sql`](backend/database/schema.sql).

## API

All endpoints except registration and login require `Authorization: Bearer <token>`. Finance records are always scoped to the authenticated owner.

| Area | Endpoints |
| --- | --- |
| Authentication | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me` |
| Dashboard | `GET /api/dashboard` |
| Income | `GET/POST /api/incomes`, `PUT/DELETE /api/incomes/{id}` |
| Expenses | `GET/POST /api/expenses`, `PUT/DELETE /api/expenses/{id}` |
| Budgets | `GET/POST /api/budgets`, `PUT/DELETE /api/budgets/{id}` |
| Accounts | `GET/POST /api/accounts`, `PUT/DELETE /api/accounts/{id}` |
| Transfers | `GET/POST /api/transfers` |
| Subscriptions | `GET/POST /api/subscriptions`, `PUT/DELETE /api/subscriptions/{id}` |
| Goals | `GET/POST /api/goals`, `PUT/DELETE /api/goals/{id}` |
| Categories | `GET/POST /api/categories`, `PUT/DELETE /api/categories/{id}` |
| Reports | `GET /api/reports?year=2026` |
| Profile | `PUT /api/profile` |
| Admin | `GET /api/admin/stats`, `GET /api/admin/users`, `PATCH/DELETE /api/admin/users/{id}` |

Income, expense, and transfer writes update account balances in the same database transaction. Deleting an account archives it so linked history remains intact.

## Verification

```powershell
cd backend
mvn -DskipTests compile

cd ../frontend
npm run lint
npm run build
```
