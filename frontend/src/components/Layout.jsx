import { useEffect, useRef, useState } from 'react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  ArrowLeftRight, BarChart3, Bell, ChevronDown, CreditCard,
  Goal, LayoutDashboard, LogOut, Menu, Moon, PiggyBank, Plus, ReceiptText,
  PhilippinePeso, Search, Settings, Sun, Tags, UserRound, WalletCards, X,
} from 'lucide-react'

const navigation = [
  { label: 'Dashboard', path: '/', icon: LayoutDashboard },
  { label: 'Income', path: '/income', icon: PhilippinePeso },
  { label: 'Expenses', path: '/expenses', icon: ReceiptText },
  { label: 'Categories', path: '/categories', icon: Tags },
  { label: 'Budgets', path: '/budgets', icon: PiggyBank },
  { label: 'Accounts', path: '/accounts', icon: WalletCards },
  { label: 'Subscriptions', path: '/subscriptions', icon: CreditCard },
  { label: 'Goals', path: '/goals', icon: Goal },
  { label: 'Reports', path: '/reports', icon: BarChart3 },
]

function Brand() {
  return <div className="flex items-center gap-3"><span className="relative flex h-10 w-10 items-center justify-center overflow-hidden rounded-xl bg-gradient-to-br from-blue-500 to-blue-700 text-white shadow-lg shadow-blue-500/20"><svg viewBox="0 0 28 28" className="h-6 w-6"><path d="M6 18.5h4V22H6zM12 12h4v10h-4zM18 6h4v16h-4z" fill="currentColor" /><path d="M5 8.5c5 2 9 1.2 16-4" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg></span><span><strong className="block text-[15px] tracking-[-.02em] text-slate-900 dark:text-white">Finance Tracker</strong><span className="block text-[10px] font-semibold uppercase tracking-[.14em] text-slate-400">Plan. Track. Grow.</span></span></div>
}

function Sidebar({ open, close }) {
  const { user } = useAuth()
  return <>
    {open && <button className="fixed inset-0 z-40 bg-slate-950/40 backdrop-blur-sm lg:hidden" onClick={close} aria-label="Close menu" />}
    <aside className={`fixed inset-y-0 left-0 z-50 flex w-[264px] flex-col border-r border-slate-200/80 bg-white px-4 py-5 transition-transform duration-300 dark:border-slate-800 dark:bg-[#111722] lg:translate-x-0 ${open ? 'translate-x-0' : '-translate-x-full'}`}>
      <div className="flex items-center justify-between px-2"><Brand /><button className="icon-button lg:hidden" onClick={close}><X size={18} /></button></div>
      <nav className="mt-8 flex-1 space-y-1">
        <p className="mb-3 px-3 text-[10px] font-bold uppercase tracking-[.18em] text-slate-400">Workspace</p>
        {navigation.map(({ label, path, icon: Icon }) => <NavLink end={path === '/'} key={path} to={path} onClick={close} className={({ isActive }) => `group flex h-11 items-center gap-3 rounded-xl px-3 text-[13px] font-semibold transition ${isActive ? 'bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400' : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900 dark:text-slate-400 dark:hover:bg-slate-800/70 dark:hover:text-white'}`}><Icon size={18} strokeWidth={2} />{label}</NavLink>)}
        {user?.role === 'ADMIN' && <NavLink to="/admin" onClick={close} className={({ isActive }) => `flex h-11 items-center gap-3 rounded-xl px-3 text-[13px] font-semibold ${isActive ? 'bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400' : 'text-slate-500 hover:bg-slate-50 dark:text-slate-400 dark:hover:bg-slate-800'}`}><UserRound size={18} />Admin</NavLink>}
      </nav>
      <div className="rounded-2xl bg-gradient-to-br from-[#192a52] to-[#254a88] p-4 text-white shadow-lg shadow-blue-950/10">
        <div className="mb-3 flex h-8 w-8 items-center justify-center rounded-lg bg-white/10"><ArrowLeftRight size={16} /></div><p className="text-xs font-bold">Make money feel simple.</p><p className="mt-1 text-[11px] leading-relaxed text-blue-100/70">Review your weekly flow and keep every peso intentional.</p>
      </div>
    </aside>
  </>
}

export default function Layout() {
  const { user, logout } = useAuth()
  const [menu, setMenu] = useState(false)
  const [quick, setQuick] = useState(false)
  const [profile, setProfile] = useState(false)
  const [dark, setDark] = useState(() => localStorage.getItem('finance_theme') === 'dark')
  const profileRef = useRef(null)
  const location = useLocation(); const navigate = useNavigate()
  useEffect(() => { document.documentElement.classList.toggle('dark', dark); localStorage.setItem('finance_theme', dark ? 'dark' : 'light') }, [dark])
  useEffect(() => { setQuick(false); setProfile(false) }, [location.pathname])
  useEffect(() => {
    const closeProfile = (event) => {
      if (!profileRef.current?.contains(event.target)) setProfile(false)
    }
    const closeOnEscape = (event) => {
      if (event.key === 'Escape') setProfile(false)
    }
    document.addEventListener('pointerdown', closeProfile)
    document.addEventListener('keydown', closeOnEscape)
    return () => {
      document.removeEventListener('pointerdown', closeProfile)
      document.removeEventListener('keydown', closeOnEscape)
    }
  }, [])
  const goToSettings = (section) => {
    setProfile(false)
    navigate(`/settings#${section}`)
    window.setTimeout(() => document.getElementById(section)?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50)
  }
  const title = navigation.find((item) => item.path === location.pathname)?.label || 'Finance Tracker'
  return <div className="min-h-screen bg-[#f6f8fc] text-slate-900 transition-colors dark:bg-[#0d121c] dark:text-white">
    <Sidebar open={menu} close={() => setMenu(false)} />
    <div className="lg:pl-[264px]">
      <header className="sticky top-0 z-30 flex h-[72px] items-center gap-3 border-b border-slate-200/70 bg-white/85 px-4 backdrop-blur-xl dark:border-slate-800 dark:bg-[#111722]/85 sm:px-6 lg:px-8">
        <button className="icon-button lg:hidden" onClick={() => setMenu(true)}><Menu size={20} /></button><h2 className="hidden text-sm font-bold text-slate-800 dark:text-white sm:block lg:hidden">{title}</h2>
        <div className="relative hidden max-w-sm flex-1 lg:block"><Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" /><input className="h-10 w-full rounded-xl border border-transparent bg-slate-100/80 pl-10 pr-4 text-xs outline-none transition focus:border-blue-300 focus:bg-white dark:bg-slate-800/70 dark:focus:border-blue-600 dark:focus:bg-slate-900" placeholder="Search transactions, accounts..." /></div>
        <div className="ml-auto flex items-center gap-1 sm:gap-2"><button className="icon-button relative" aria-label="Notifications"><Bell size={18} /><i className="absolute right-2.5 top-2.5 h-1.5 w-1.5 rounded-full bg-red-500 ring-2 ring-white dark:ring-slate-900" /></button>
          <div className="relative"><button className="btn-primary ml-1 h-10 px-3 sm:px-4" onClick={() => { setQuick(!quick); setProfile(false) }}><Plus size={17} /><span className="hidden sm:inline">Quick add</span><ChevronDown size={14} /></button>{quick && <div className="slide-up absolute right-0 top-12 w-52 rounded-2xl border border-slate-200 bg-white p-2 shadow-xl dark:border-slate-700 dark:bg-slate-800">{[['Add income', '/income?new=1'], ['Add expense', '/expenses?new=1'], ['Add category', '/categories?new=1'], ['Transfer money', '/accounts?transfer=1'], ['Create goal', '/goals?new=1']].map(([label, path]) => <button key={path} onClick={() => navigate(path)} className="block w-full rounded-xl px-3 py-2.5 text-left text-xs font-semibold text-slate-600 hover:bg-slate-50 dark:text-slate-300 dark:hover:bg-slate-700">{label}</button>)}</div>}</div>
          <div className="relative ml-1" ref={profileRef}>
            <button className="flex h-11 items-center gap-2 rounded-xl px-1.5 transition hover:bg-slate-100 dark:hover:bg-slate-800 sm:pr-2.5" onClick={() => { setProfile(!profile); setQuick(false) }} aria-label="Open account menu" aria-expanded={profile}>
              <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-blue-500 to-blue-700 text-xs font-bold text-white shadow-sm">{user?.firstName?.[0]}{user?.lastName?.[0]}</span>
              <span className="hidden max-w-28 text-left lg:block"><strong className="block truncate text-xs text-slate-800 dark:text-white">{user?.firstName}</strong><span className="block truncate text-[10px] text-slate-400">{user?.role}</span></span>
              <ChevronDown size={14} className={`hidden text-slate-400 transition sm:block ${profile ? 'rotate-180' : ''}`} />
            </button>
            {profile && <div className="slide-up absolute right-0 top-13 z-50 w-72 overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-2xl shadow-slate-900/10 dark:border-slate-700 dark:bg-slate-800">
              <div className="flex items-center gap-3 border-b border-slate-100 p-4 dark:border-slate-700"><span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-blue-100 text-sm font-bold text-blue-700 dark:bg-blue-500/15 dark:text-blue-300">{user?.firstName?.[0]}{user?.lastName?.[0]}</span><div className="min-w-0"><p className="truncate text-sm font-bold text-slate-900 dark:text-white">{user?.firstName} {user?.lastName}</p><p className="mt-0.5 truncate text-xs text-slate-400">{user?.email}</p></div></div>
              <div className="p-2"><button className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm font-medium text-slate-600 transition hover:bg-slate-50 hover:text-slate-900 dark:text-slate-300 dark:hover:bg-slate-700 dark:hover:text-white" onClick={() => goToSettings('profile')}><UserRound size={17} />My Profile</button><button className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm font-medium text-slate-600 transition hover:bg-slate-50 hover:text-slate-900 dark:text-slate-300 dark:hover:bg-slate-700 dark:hover:text-white" onClick={() => goToSettings('preferences')}><Settings size={17} />Account Settings</button><button className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm font-medium text-slate-600 transition hover:bg-slate-50 dark:text-slate-300 dark:hover:bg-slate-700" onClick={() => setDark(!dark)}>{dark ? <Sun size={17} /> : <Moon size={17} />}<span className="flex-1">Dark Mode</span><span className={`relative h-5 w-9 rounded-full transition ${dark ? 'bg-blue-600' : 'bg-slate-200 dark:bg-slate-600'}`}><i className={`absolute top-0.5 h-4 w-4 rounded-full bg-white shadow-sm transition ${dark ? 'left-[18px]' : 'left-0.5'}`} /></span></button></div>
              <div className="border-t border-slate-100 p-2 dark:border-slate-700"><button className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm font-semibold text-red-500 transition hover:bg-red-50 dark:hover:bg-red-500/10" onClick={logout}><LogOut size={17} />Sign Out</button></div>
            </div>}
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-[1500px] p-4 sm:p-6 lg:p-8"><Outlet /></main>
    </div>
  </div>
}
