import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import { LoadingScreen } from './components/UI'
import AuthPage from './pages/AuthPage'
import DashboardPage from './pages/DashboardPage'
import { AccountsPage, BudgetsPage, ExpensesPage, GoalsPage, IncomePage, SubscriptionsPage } from './pages/FinancePages'
import ReportsPage from './pages/ReportsPage'
import SettingsPage from './pages/SettingsPage'
import AdminPage from './pages/AdminPage'
import CategoriesPage from './pages/CategoriesPage'

export default function App() {
  const { user, loading } = useAuth()
  if (loading) return <LoadingScreen />
  return (
    <Routes>
      <Route path="/login" element={<AuthPage />} />
      <Route element={user ? <Layout /> : <Navigate to="/login" replace />}>
        <Route index element={<DashboardPage />} />
        <Route path="income" element={<IncomePage />} />
        <Route path="expenses" element={<ExpensesPage />} />
        <Route path="categories" element={<CategoriesPage />} />
        <Route path="budgets" element={<BudgetsPage />} />
        <Route path="accounts" element={<AccountsPage />} />
        <Route path="subscriptions" element={<SubscriptionsPage />} />
        <Route path="goals" element={<GoalsPage />} />
        <Route path="reports" element={<ReportsPage />} />
        <Route path="settings" element={<SettingsPage />} />
        <Route path="admin" element={<AdminPage />} />
      </Route>
      <Route path="*" element={<Navigate to={user ? '/' : '/login'} replace />} />
    </Routes>
  )
}
