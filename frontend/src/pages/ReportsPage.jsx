import { useEffect, useState } from 'react'
import { ArrowUpRight, Download, Lightbulb, PiggyBank, ReceiptText, TrendingUp } from 'lucide-react'
import { api, errorMessage } from '../lib/api'
import { money } from '../lib/format'
import { BarChart, DonutChart } from '../components/Charts'
import { Card, LoadingScreen, PageHeader } from '../components/UI'

export default function ReportsPage(){
  const [year,setYear]=useState(new Date().getFullYear());const [data,setData]=useState(null);const [error,setError]=useState('')
  useEffect(()=>{setData(null);api.get(`/reports?year=${year}`).then(r=>setData(r.data)).catch(e=>setError(errorMessage(e)))},[year])
  const download=()=>{if(!data)return;const rows=['Month,Income,Expenses,Savings',...data.monthly.map(m=>`${m.month},${m.income},${m.expense},${m.savings}`)];const blob=new Blob([rows.join('\n')],{type:'text/csv'});const a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download=`finance-report-${year}.csv`;a.click();URL.revokeObjectURL(a.href)}
  if(!data&&!error)return <LoadingScreen/>
  if(error)return <Card className="p-8 text-center text-red-500">{error}</Card>
  const top=data.categoryBreakdown?.[0]
  return <div className="fade-in"><PageHeader eyebrow="The bigger picture" title="Reports & analytics" description="Zoom out, spot patterns, and turn your history into better next-month decisions."/>
    <div className="mb-5 flex flex-wrap items-center justify-between gap-3"><select className="input h-10 w-32" value={year} onChange={e=>setYear(Number(e.target.value))}>{[0,1,2,3].map(i=><option key={i}>{new Date().getFullYear()-i}</option>)}</select><button className="btn-secondary" onClick={download}><Download size={16}/>Export CSV</button></div>
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">{[
      ['Annual income',data.income,TrendingUp,'text-emerald-600 bg-emerald-50 dark:bg-emerald-500/10'],['Annual expenses',data.expenses,ReceiptText,'text-red-500 bg-red-50 dark:bg-red-500/10'],['Net savings',data.savings,PiggyBank,'text-blue-600 bg-blue-50 dark:bg-blue-500/10'],['Savings rate',`${data.savingsRate}%`,ArrowUpRight,'text-violet-600 bg-violet-50 dark:bg-violet-500/10']
    ].map(([label,value,Icon,color])=><Card key={label} className="p-5"><span className={`flex h-9 w-9 items-center justify-center rounded-xl ${color}`}><Icon size={17}/></span><p className="mt-4 text-xs text-slate-400">{label}</p><strong className="mt-1 block text-xl tracking-[-.03em] dark:text-white">{typeof value==='string'?value:money(value)}</strong></Card>)}</div>
    <div className="mt-4 grid gap-4 xl:grid-cols-[1.55fr_1fr]"><Card className="p-5 sm:p-6"><div className="flex items-start justify-between"><div><h2 className="text-sm font-bold dark:text-white">Yearly cash flow</h2><p className="mt-1 text-xs text-slate-400">Monthly income and expenses</p></div><div className="flex gap-3 text-[10px] text-slate-400"><span className="flex items-center gap-1"><i className="h-2 w-2 rounded-full bg-emerald-500"/>Income</span><span className="flex items-center gap-1"><i className="h-2 w-2 rounded-full bg-red-400"/>Expenses</span></div></div><div className="mt-3"><BarChart data={data.monthly}/></div></Card><Card className="p-5 sm:p-6"><div className="mb-6"><h2 className="text-sm font-bold dark:text-white">Annual spending</h2><p className="mt-1 text-xs text-slate-400">By category</p></div><DonutChart data={data.categoryBreakdown} centerLabel="Total expenses"/></Card></div>
    <Card className="mt-4 p-5 sm:p-6"><div className="flex items-start gap-3"><span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-amber-50 text-amber-600 dark:bg-amber-500/10"><Lightbulb size={18}/></span><div><h2 className="text-sm font-bold dark:text-white">A useful read on {year}</h2><p className="mt-1.5 text-sm leading-6 text-slate-500 dark:text-slate-400">Your savings rate is <strong className="text-slate-700 dark:text-slate-200">{data.savingsRate}%</strong>. {top?<>Your largest spending category is <strong className="text-slate-700 dark:text-slate-200">{top.name}</strong> at {money(top.value)}.</>:<>Add more expenses to unlock category insights.</>} {Number(data.savingsRate)>=20?'You are maintaining a healthy margin between what comes in and what goes out.':'A small recurring expense review could create more room for your goals.'}</p></div></div></Card>
  </div>
}
