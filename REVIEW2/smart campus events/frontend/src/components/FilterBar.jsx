export default function FilterBar({ filters, setFilters }) {
  const onChange = (key, value) => setFilters((prev) => ({ ...prev, [key]: value }));
  return (
    <div className="glass grid gap-3 rounded-xl p-4 md:grid-cols-4">
      <input className="rounded-lg bg-slate-900 px-3 py-2" placeholder="Department" value={filters.department} onChange={(e) => onChange("department", e.target.value)} />
      <input className="rounded-lg bg-slate-900 px-3 py-2" placeholder="Type" value={filters.type} onChange={(e) => onChange("type", e.target.value)} />
      <input className="rounded-lg bg-slate-900 px-3 py-2" type="date" value={filters.date} onChange={(e) => onChange("date", e.target.value)} />
      <button className="rounded-lg bg-cyan-500 px-3 py-2 font-medium text-slate-900 transition hover:bg-cyan-400" onClick={() => setFilters({ department: "", type: "", date: "" })}>Clear</button>
    </div>
  );
}
