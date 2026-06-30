import { useState } from 'react'
import { ArrowRight, BarChart3, CheckCircle2, Eye, EyeOff, LoaderCircle, LockKeyhole, Mail, ShieldCheck, Sparkles } from 'lucide-react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { errorMessage } from '../lib/api'

export default function AuthPage() {
  const { user, login, register } = useAuth()
  const [mode, setMode] = useState('login'); const [show, setShow] = useState(false); const [busy, setBusy] = useState(false); const [error, setError] = useState('')
  const [form, setForm] = useState({ firstName:'', lastName:'', username:'', email:'', password:'' })
  if (user) return <Navigate to="/" replace />
  const submit = async (e) => { e.preventDefault(); setBusy(true); setError(''); try { await (mode === 'login' ? login(form) : register(form)) } catch (err) { setError(errorMessage(err)) } finally { setBusy(false) } }
  return <main className="grid min-h-screen bg-white dark:bg-[#0d121c] lg:grid-cols-[1.08fr_.92fr]">
    <section className="relative hidden overflow-hidden bg-[#102044] p-14 text-white lg:flex lg:flex-col">
      <div className="absolute -right-24 -top-24 h-96 w-96 rounded-full bg-blue-500/20 blur-3xl"/><div className="absolute -bottom-36 -left-24 h-[420px] w-[420px] rounded-full bg-violet-500/15 blur-3xl"/>
      <div className="relative flex items-center gap-3"><span className="flex h-11 w-11 items-center justify-center rounded-xl bg-blue-500 shadow-lg shadow-blue-500/30"><BarChart3 size={23}/></span><div><strong className="block tracking-tight">Finance Tracker</strong><span className="text-[10px] uppercase tracking-[.2em] text-blue-200/60">Plan. Track. Grow.</span></div></div>
      <div className="relative my-auto max-w-xl"><span className="mb-6 inline-flex items-center gap-2 rounded-full border border-blue-300/20 bg-white/5 px-3 py-1.5 text-xs text-blue-100"><Sparkles size={13}/> A calmer way to manage money</span><h1 className="text-5xl font-bold leading-[1.08] tracking-[-.045em]">See where your money goes. <span className="text-blue-400">Shape where it takes you.</span></h1><p className="mt-6 max-w-lg text-base leading-7 text-blue-100/65">Income, spending, budgets, goals, and recurring bills—one clean workspace built for everyday financial clarity.</p>
        <div className="mt-10 grid grid-cols-2 gap-4"><div className="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur"><ShieldCheck className="mb-4 text-emerald-400" size={22}/><p className="text-sm font-semibold">Private by design</p><p className="mt-1 text-xs leading-5 text-blue-100/50">Secure JWT sessions and user-scoped financial data.</p></div><div className="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur"><BarChart3 className="mb-4 text-blue-400" size={22}/><p className="text-sm font-semibold">Insights that click</p><p className="mt-1 text-xs leading-5 text-blue-100/50">Understand habits with friendly, useful reports.</p></div></div>
      </div><p className="relative text-xs text-blue-100/35">Built for better money habits, one month at a time.</p>
    </section>
    <section className="flex items-center justify-center px-5 py-10 sm:px-10"><div className="w-full max-w-[430px] fade-in">
      <div className="mb-9 lg:hidden"><div className="mb-8 flex items-center gap-2.5"><span className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 text-white"><BarChart3 size={21}/></span><strong>Finance Tracker</strong></div></div>
      <p className="text-xs font-bold uppercase tracking-[.16em] text-blue-600">{mode === 'login' ? 'Welcome back' : 'Start your journey'}</p><h2 className="mt-2 text-3xl font-bold tracking-[-.04em] text-slate-900 dark:text-white">{mode === 'login' ? 'Sign in to your workspace' : 'Create your free account'}</h2><p className="mt-2 text-sm text-slate-500">{mode === 'login' ? 'Your financial picture is ready when you are.' : 'Everything you need to build calmer money habits.'}</p>
      <form onSubmit={submit} className="mt-8 space-y-4">
        {mode === 'register' && <div className="grid grid-cols-2 gap-3"><label><span className="label">First name</span><input className="input" required value={form.firstName} onChange={(e)=>setForm({...form,firstName:e.target.value})}/></label><label><span className="label">Last name</span><input className="input" required value={form.lastName} onChange={(e)=>setForm({...form,lastName:e.target.value})}/></label><label className="col-span-2"><span className="label">Username</span><input className="input" required minLength={3} value={form.username} onChange={(e)=>setForm({...form,username:e.target.value})}/></label></div>}
        <label className="block"><span className="label">Email address</span><div className="relative"><Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={17}/><input type="email" className="input pl-10" required value={form.email} onChange={(e)=>setForm({...form,email:e.target.value})}/></div></label>
        <label className="block"><span className="label">Password</span><div className="relative"><LockKeyhole className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={17}/><input type={show?'text':'password'} className="input px-10" required minLength={8} value={form.password} onChange={(e)=>setForm({...form,password:e.target.value})}/><button type="button" className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400" onClick={()=>setShow(!show)}>{show?<EyeOff size={17}/>:<Eye size={17}/>}</button></div></label>
        {mode === 'register' && <p className="flex items-center gap-2 text-xs text-slate-500"><CheckCircle2 size={14} className="text-emerald-500"/>Use at least 8 characters.</p>}
        {error && <p className="rounded-xl bg-red-50 px-3 py-2.5 text-xs font-medium text-red-600 dark:bg-red-500/10 dark:text-red-400">{error}</p>}
        <button className="btn-primary mt-2 h-12 w-full" disabled={busy}>{busy?<LoaderCircle className="animate-spin" size={18}/>:<>{mode==='login'?'Sign in':'Create account'}<ArrowRight size={17}/></>}</button>
      </form>
      <p className="mt-7 text-center text-sm text-slate-500">{mode==='login'?"New to Finance Tracker?":"Already have an account?"} <button className="font-semibold text-blue-600 hover:underline" onClick={()=>{setMode(mode==='login'?'register':'login');setError('')}}>{mode==='login'?'Create an account':'Sign in'}</button></p>
    </div></section>
  </main>
}
