import { AlertCircle, Check, LoaderCircle, Plus, X } from 'lucide-react'

export function Card({ children, className = '', ...props }) { return <section className={`surface rounded-2xl ${className}`} {...props}>{children}</section> }

export function Button({ children, variant = 'primary', className = '', ...props }) {
  return <button className={`${variant === 'primary' ? 'btn-primary' : 'btn-secondary'} ${className}`} {...props}>{children}</button>
}

export function PageHeader({ eyebrow, title, description, action, actionLabel = 'Add new', icon: Icon = Plus }) {
  return <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
    <div>
      {eyebrow && <p className="mb-1 text-xs font-bold uppercase tracking-[.16em] text-blue-600 dark:text-blue-400">{eyebrow}</p>}
      <h1 className="text-2xl font-bold tracking-[-.03em] text-slate-900 dark:text-white sm:text-[28px]">{title}</h1>
      {description && <p className="mt-1.5 max-w-2xl text-sm text-slate-500 dark:text-slate-400">{description}</p>}
    </div>
    {action && <Button onClick={action}><Icon size={17} />{actionLabel}</Button>}
  </div>
}

export function Modal({ open, onClose, title, description, children, size = 'max-w-lg' }) {
  if (!open) return null
  return <div className="fixed inset-0 z-[80] flex items-end justify-center bg-slate-950/45 p-0 backdrop-blur-[2px] sm:items-center sm:p-5" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
    <div className={`slide-up max-h-[92vh] w-full overflow-y-auto rounded-t-3xl bg-white shadow-2xl dark:bg-[#151b27] sm:rounded-2xl ${size}`}>
      <div className="sticky top-0 z-10 flex items-start justify-between border-b border-slate-100 bg-white/95 px-6 py-5 backdrop-blur dark:border-slate-800 dark:bg-[#151b27]/95">
        <div><h2 className="font-bold text-slate-900 dark:text-white">{title}</h2>{description && <p className="mt-1 text-xs text-slate-500">{description}</p>}</div>
        <button className="icon-button -mr-2 -mt-2" onClick={onClose} aria-label="Close"><X size={19} /></button>
      </div>
      <div className="p-6">{children}</div>
    </div>
  </div>
}

export function Field({ label, hint, className = '', ...props }) {
  return <label className={`block ${className}`}><span className="label">{label}</span><input className="input" {...props} />{hint && <span className="mt-1 block text-[11px] text-slate-400">{hint}</span>}</label>
}

export function Select({ label, children, className = '', ...props }) {
  return <label className={`block ${className}`}><span className="label">{label}</span><select className="input" {...props}>{children}</select></label>
}

export function Textarea({ label, className = '', ...props }) {
  return <label className={`block ${className}`}><span className="label">{label}</span><textarea className="input min-h-24 resize-y py-3" {...props} /></label>
}

export function FormActions({ onCancel, saving, label = 'Save changes' }) {
  return <div className="mt-6 flex justify-end gap-2 border-t border-slate-100 pt-5 dark:border-slate-800"><Button type="button" variant="secondary" onClick={onCancel}>Cancel</Button><Button type="submit" disabled={saving}>{saving && <LoaderCircle className="animate-spin" size={16} />}{label}</Button></div>
}

export function EmptyState({ icon: Icon = AlertCircle, title = 'Nothing here yet', message, action, actionLabel = 'Add your first' }) {
  return <div className="flex min-h-64 flex-col items-center justify-center px-6 text-center"><span className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400"><Icon size={22} /></span><h3 className="font-semibold text-slate-800 dark:text-white">{title}</h3>{message && <p className="mt-1 max-w-sm text-sm text-slate-500">{message}</p>}{action && <Button onClick={action} className="mt-4"><Plus size={16} />{actionLabel}</Button>}</div>
}

export function Progress({ value = 0, color = '#2F80ED', className = '' }) {
  return <div className={`h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800 ${className}`}><div className="h-full rounded-full transition-all duration-700" style={{ width: `${Math.min(Math.max(Number(value), 0), 100)}%`, backgroundColor: color }} /></div>
}

export function Toast({ message, type = 'success', onClose }) {
  if (!message) return null
  return <div className="fade-in fixed bottom-5 right-5 z-[100] flex max-w-sm items-center gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 shadow-xl dark:border-slate-700 dark:bg-slate-800 dark:text-white"><span className={`flex h-7 w-7 items-center justify-center rounded-full ${type === 'success' ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-600'}`}>{type === 'success' ? <Check size={15} /> : <AlertCircle size={15} />}</span>{message}<button onClick={onClose}><X size={15} /></button></div>
}

export function LoadingScreen() { return <div className="flex min-h-[50vh] items-center justify-center"><LoaderCircle className="animate-spin text-blue-500" size={28} /></div> }
