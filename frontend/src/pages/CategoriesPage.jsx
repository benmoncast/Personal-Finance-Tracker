import { useCallback, useEffect, useMemo, useState } from 'react'
import { Edit3, Layers3, Search, Tag, Trash2, TrendingDown, TrendingUp } from 'lucide-react'
import { useLocation } from 'react-router-dom'
import { api, errorMessage } from '../lib/api'
import { Card, EmptyState, Field, FormActions, LoadingScreen, Modal, PageHeader, Select, Toast } from '../components/UI'

const emptyCategory = { name: '', type: 'EXPENSE', color: '#2F80ED', icon: 'tag' }

export default function CategoriesPage() {
  const location = useLocation()
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(emptyCategory)
  const [query, setQuery] = useState('')
  const [filter, setFilter] = useState('ALL')
  const [notice, setNotice] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const { data } = await api.get('/categories')
      setCategories(data)
    } catch (error) {
      setNotice({ message: errorMessage(error), type: 'error' })
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  const filtered = useMemo(() => categories.filter((category) => {
    const matchesType = filter === 'ALL' || category.type === filter
    const matchesQuery = category.name.toLowerCase().includes(query.trim().toLowerCase())
    return matchesType && matchesQuery
  }), [categories, filter, query])

  const start = (category) => {
    setEditing(category || null)
    setForm(category ? {
      name: category.name,
      type: category.type,
      color: category.color || '#2F80ED',
      icon: category.icon || 'tag',
    } : emptyCategory)
    setOpen(true)
  }

  useEffect(() => {
    if (new URLSearchParams(location.search).get('new')) {
      setEditing(null)
      setForm(emptyCategory)
      setOpen(true)
    }
  }, [location.search])

  const save = async (event) => {
    event.preventDefault()
    setSaving(true)
    try {
      await api[editing ? 'put' : 'post'](`/categories${editing ? `/${editing.id}` : ''}`, form)
      await load()
      setOpen(false)
      setNotice({ message: `Category ${editing ? 'updated' : 'created'} successfully.` })
    } catch (error) {
      setNotice({ message: errorMessage(error), type: 'error' })
    } finally {
      setSaving(false)
    }
  }

  const remove = async (category) => {
    if (!window.confirm(`Delete “${category.name}”?`)) return
    try {
      await api.delete(`/categories/${category.id}`)
      await load()
      setNotice({ message: 'Category deleted.' })
    } catch (error) {
      setNotice({ message: errorMessage(error), type: 'error' })
    }
  }

  if (loading) return <LoadingScreen />

  const incomeCount = categories.filter((category) => category.type === 'INCOME').length
  const expenseCount = categories.filter((category) => category.type === 'EXPENSE').length

  return <div className="fade-in">
    <PageHeader eyebrow="Organize your money" title="Categories" description="Create a clean category system for transactions, budgets, subscriptions, and reports." action={() => start()} actionLabel="Add category" />

    <div className="mb-5 grid gap-4 sm:grid-cols-3">
      {[
        ['Total categories', categories.length, Layers3, 'bg-blue-50 text-blue-600 dark:bg-blue-500/10'],
        ['Income categories', incomeCount, TrendingUp, 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10'],
        ['Expense categories', expenseCount, TrendingDown, 'bg-red-50 text-red-500 dark:bg-red-500/10'],
      ].map(([label, value, Icon, color]) => <Card key={label} className="flex items-center gap-4 p-5"><span className={`flex h-11 w-11 items-center justify-center rounded-xl ${color}`}><Icon size={19} /></span><div><p className="text-xs text-slate-400">{label}</p><strong className="mt-1 block text-xl dark:text-white">{value}</strong></div></Card>)}
    </div>

    <Card className="overflow-hidden">
      <div className="flex flex-col gap-3 border-b border-slate-100 p-4 dark:border-slate-800 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex rounded-xl bg-slate-100 p-1 dark:bg-slate-900">
          {['ALL', 'INCOME', 'EXPENSE'].map((type) => <button key={type} onClick={() => setFilter(type)} className={`rounded-lg px-3 py-2 text-[11px] font-bold transition ${filter === type ? 'bg-white text-blue-600 shadow-sm dark:bg-slate-700 dark:text-blue-400' : 'text-slate-400'}`}>{type === 'ALL' ? 'All' : type === 'INCOME' ? 'Income' : 'Expenses'}</button>)}
        </div>
        <div className="relative"><Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" /><input className="input h-10 w-full pl-9 sm:w-64" placeholder="Search categories..." value={query} onChange={(event) => setQuery(event.target.value)} /></div>
      </div>

      {filtered.length === 0 ? <EmptyState icon={Tag} title="No matching categories" message="Try another filter or create a category for your money flow." action={() => start()} actionLabel="Add category" /> : <div className="grid gap-px bg-slate-100 dark:bg-slate-800 sm:grid-cols-2 xl:grid-cols-3">
        {filtered.map((category) => <article key={category.id} className="group bg-white p-5 dark:bg-[#151b27]">
          <div className="flex items-start justify-between gap-3">
            <span className="flex h-11 w-11 items-center justify-center rounded-2xl" style={{ backgroundColor: `${category.color}18`, color: category.color }}><Tag size={19} /></span>
            {category.system ? <span className="rounded-full bg-slate-100 px-2.5 py-1 text-[9px] font-bold uppercase tracking-wider text-slate-500 dark:bg-slate-800">System</span> : <div className="flex opacity-70 transition group-hover:opacity-100"><button className="icon-button h-8 w-8" onClick={() => start(category)} aria-label={`Edit ${category.name}`}><Edit3 size={14} /></button><button className="icon-button h-8 w-8 hover:text-red-500" onClick={() => remove(category)} aria-label={`Delete ${category.name}`}><Trash2 size={14} /></button></div>}
          </div>
          <h2 className="mt-4 text-sm font-bold text-slate-800 dark:text-white">{category.name}</h2>
          <p className="mt-1 flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-wider text-slate-400"><i className="h-1.5 w-1.5 rounded-full" style={{ backgroundColor: category.color }} />{category.type}</p>
        </article>)}
      </div>}
    </Card>

    <Modal open={open} onClose={() => setOpen(false)} title={`${editing ? 'Edit' : 'Create'} category`} description="Categories shape your budgets and reports.">
      <form onSubmit={save} className="space-y-4">
        <Field label="Category name" required maxLength={80} value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="e.g. Education" />
        <Select label="Transaction type" value={form.type} onChange={(event) => setForm({ ...form, type: event.target.value })}><option value="EXPENSE">Expense</option><option value="INCOME">Income</option></Select>
        <Select label="Icon" value={form.icon} onChange={(event) => setForm({ ...form, icon: event.target.value })}><option value="tag">Tag</option><option value="briefcase">Briefcase</option><option value="house">House</option><option value="car">Transport</option><option value="receipt">Receipt</option><option value="heart">Health</option><option value="sparkles">Lifestyle</option></Select>
        <label className="block"><span className="label">Color</span><div className="flex items-center gap-3"><input type="color" className="h-11 w-16 rounded-xl border border-slate-200 bg-white p-1.5 dark:border-slate-700 dark:bg-slate-900" value={form.color} onChange={(event) => setForm({ ...form, color: event.target.value })} /><span className="text-xs font-medium uppercase text-slate-400">{form.color}</span></div></label>
        <FormActions onCancel={() => setOpen(false)} saving={saving} label={editing ? 'Save changes' : 'Create category'} />
      </form>
    </Modal>
    <Toast {...notice} onClose={() => setNotice(null)} />
  </div>
}
