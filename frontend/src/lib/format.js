export const money = (value = 0, currency = 'PHP') => new Intl.NumberFormat('en-PH', {
  style: 'currency', currency, maximumFractionDigits: 0,
}).format(Number(value || 0))

export const compactMoney = (value = 0) => new Intl.NumberFormat('en-PH', {
  style: 'currency', currency: 'PHP', notation: 'compact', maximumFractionDigits: 1,
}).format(Number(value || 0))

export const niceDate = (date) => date ? new Intl.DateTimeFormat('en-PH', {
  month: 'short', day: 'numeric', year: 'numeric',
}).format(new Date(`${String(date).slice(0, 10)}T00:00:00`)) : '—'

export const shortDate = (date) => date ? new Intl.DateTimeFormat('en-PH', {
  month: 'short', day: 'numeric',
}).format(new Date(`${String(date).slice(0, 10)}T00:00:00`)) : '—'

export const today = () => new Date().toISOString().slice(0, 10)
export const currentMonth = () => `${today().slice(0, 7)}-01`
