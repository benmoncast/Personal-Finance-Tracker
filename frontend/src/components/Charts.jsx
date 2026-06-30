import { compactMoney, money } from '../lib/format'

export function TrendChart({ data = [] }) {
  const width = 720, height = 230, pad = 24
  const max = Math.max(...data.flatMap((d) => [Number(d.income), Number(d.expense)]), 1)
  const x = (i) => pad + (i * (width - pad * 2)) / Math.max(data.length - 1, 1)
  const y = (v) => height - 28 - (Number(v) / max) * (height - 52)
  const path = (key) => data.map((d, i) => `${i ? 'L' : 'M'}${x(i)},${y(d[key])}`).join(' ')
  return <div className="w-full overflow-hidden">
    <svg viewBox={`0 0 ${width} ${height}`} className="h-[230px] w-full" role="img" aria-label="Income and expense trend">
      <defs><linearGradient id="incomeFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stopColor="#27AE60" stopOpacity=".18"/><stop offset="1" stopColor="#27AE60" stopOpacity="0"/></linearGradient></defs>
      {[.25,.5,.75,1].map((n) => <line key={n} x1={pad} x2={width-pad} y1={y(max*n)} y2={y(max*n)} stroke="currentColor" className="text-slate-100 dark:text-slate-800" strokeDasharray="4 6" />)}
      {data.length > 1 && <path d={`${path('income')} L${x(data.length-1)},${height-28} L${x(0)},${height-28} Z`} fill="url(#incomeFill)" />}
      <path d={path('income')} fill="none" stroke="#27AE60" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
      <path d={path('expense')} fill="none" stroke="#EB5757" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
      {data.map((d,i) => <g key={d.month}><circle cx={x(i)} cy={y(d.income)} r="3.5" fill="#27AE60" stroke="white" strokeWidth="2"/><circle cx={x(i)} cy={y(d.expense)} r="3.5" fill="#EB5757" stroke="white" strokeWidth="2"/><text x={x(i)} y={height-7} textAnchor="middle" className="fill-slate-400 text-[11px]">{d.month}</text></g>)}
    </svg>
  </div>
}

export function BarChart({ data = [] }) {
  const max = Math.max(...data.flatMap((d) => [Number(d.income), Number(d.expense)]), 1)
  return <div className="flex h-64 items-end gap-2 border-b border-slate-100 px-2 dark:border-slate-800">
    {data.map((d) => <div key={d.month} className="flex h-full flex-1 flex-col justify-end gap-2 pt-4">
      <div className="flex flex-1 items-end justify-center gap-1.5"><div title={`Income ${money(d.income)}`} className="w-[36%] rounded-t-md bg-emerald-500/85 transition hover:bg-emerald-500" style={{height:`${Math.max(Number(d.income)/max*100,2)}%`}}/><div title={`Expenses ${money(d.expense)}`} className="w-[36%] rounded-t-md bg-red-400/85 transition hover:bg-red-500" style={{height:`${Math.max(Number(d.expense)/max*100,2)}%`}}/></div>
      <span className="pb-2 text-center text-[10px] text-slate-400">{d.month}</span>
    </div>)}
  </div>
}

export function DonutChart({ data = [], centerLabel = 'Spent' }) {
  const total = data.reduce((sum, item) => sum + Number(item.value), 0)
  let cursor = 0
  const stops = data.map((item) => { const start = cursor; cursor += total ? Number(item.value) / total * 100 : 0; return `${item.color || '#94a3b8'} ${start}% ${cursor}%` }).join(', ')
  return <div className="flex flex-col items-center gap-6 sm:flex-row">
    <div className="relative h-44 w-44 shrink-0 rounded-full" style={{background: total ? `conic-gradient(${stops})` : '#e2e8f0'}}><div className="absolute inset-[22px] flex flex-col items-center justify-center rounded-full bg-white text-center dark:bg-[#151b27]"><span className="text-[11px] text-slate-400">{centerLabel}</span><strong className="mt-0.5 text-lg text-slate-900 dark:text-white">{compactMoney(total)}</strong></div></div>
    <div className="grid w-full gap-3">{data.slice(0,6).map((item) => <div key={item.name} className="flex items-center justify-between gap-4 text-sm"><span className="flex min-w-0 items-center gap-2 text-slate-600 dark:text-slate-300"><i className="h-2.5 w-2.5 shrink-0 rounded-full" style={{background:item.color}} /> <span className="truncate">{item.name}</span></span><span className="font-semibold text-slate-800 dark:text-white">{total ? Math.round(Number(item.value)/total*100) : 0}%</span></div>)}</div>
  </div>
}
