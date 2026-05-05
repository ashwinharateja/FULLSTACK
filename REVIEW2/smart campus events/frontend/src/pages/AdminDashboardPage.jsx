import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import api from "../services/api";
import EventModal from "../components/EventModal";
import FloatingButton from "../components/FloatingButton";
import { BarChart, Bar, PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from "recharts";

export default function AdminDashboardPage() {
  const [auth, setAuth] = useState(false);
  const [loginForm, setLoginForm] = useState({ email: "admin@campus.com", password: "admin123" });
  const [events, setEvents] = useState([]);
  const [stats, setStats] = useState(null);
  const [registrations, setRegistrations] = useState({});
  const [auditLogs, setAuditLogs] = useState([]);
  const [notifyForm, setNotifyForm] = useState({ scope: "all", message: "" });
  const [openModal, setOpenModal] = useState(false);
  const [selected, setSelected] = useState(null);
  const [filters, setFilters] = useState({ department: "", type: "", date: "" });

  const params = useMemo(() => ({ ...filters, page: 0, size: 100 }), [filters]);

  const load = async () => {
    const [eventsRes, statsRes, logsRes] = await Promise.all([
      api.get("/events", { params }),
      api.get("/stats"),
      api.get("/audit-logs"),
    ]);
    setEvents(eventsRes.data.content);
    setStats(statsRes.data);
    setAuditLogs(logsRes.data);
  };

  useEffect(() => { if (auth) load(); }, [auth, params]);

  const login = async (e) => {
    e.preventDefault();
    try {
      await api.post("/admin/login", loginForm);
      setAuth(true);
      toast.success("Welcome admin");
    } catch {
      toast.error("Invalid admin credentials");
    }
  };

  const saveEvent = async (payload) => {
    try {
      if (selected) await api.put(`/events/${selected.id}`, payload);
      else await api.post("/events", payload);
      toast.success(selected ? "Event updated" : "Event created");
      setOpenModal(false);
      setSelected(null);
      load();
    } catch {
      toast.error("Failed to save event");
    }
  };

  const deleteEvent = async (id) => {
    if (!window.confirm("Delete this event?")) return;
    try {
      await api.delete(`/events/${id}`);
      toast.success("Event deleted");
      load();
    } catch {
      toast.error("Delete failed");
    }
  };

  const showRegistrations = async (eventId) => {
    const { data } = await api.get(`/events/${eventId}/registrations`);
    setRegistrations((prev) => ({ ...prev, [eventId]: data }));
  };

  const approveParticipant = async (eventId, registrationId, approve) => {
    try {
      const { data } = await api.post(`/events/${eventId}/participants/decision`, null, { params: { registrationId, approve } });
      toast.success(data.message);
      showRegistrations(eventId);
      load();
    } catch {
      toast.error("Participant update failed");
    }
  };

  const exportCsv = async () => {
    const { data } = await api.get("/export/registrations");
    const blob = new Blob([data], { type: "text/csv" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "registrations.csv";
    link.click();
    URL.revokeObjectURL(link.href);
  };

  const sendNotification = async (e) => {
    e.preventDefault();
    try {
      const { data } = await api.post("/notifications", null, { params: notifyForm });
      toast.success(data.message);
      setNotifyForm({ scope: "all", message: "" });
    } catch {
      toast.error("Notification failed");
    }
  };

  if (!auth) {
    return (
      <main className="mx-auto max-w-md px-4 py-16">
        <form onSubmit={login} className="glass space-y-4 rounded-xl p-6">
          <h1 className="text-2xl font-bold">Admin Login</h1>
          <input className="w-full rounded bg-slate-900 px-3 py-2" value={loginForm.email} onChange={(e) => setLoginForm((p) => ({ ...p, email: e.target.value }))} />
          <input type="password" className="w-full rounded bg-slate-900 px-3 py-2" value={loginForm.password} onChange={(e) => setLoginForm((p) => ({ ...p, password: e.target.value }))} />
          <button className="w-full rounded bg-cyan-500 px-3 py-2 font-medium text-slate-900">Login</button>
        </form>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-7xl px-4 py-6">
      {stats && (
        <div className="mb-4 grid gap-3 md:grid-cols-4">
          <div className="glass rounded-xl p-4">Total Events: <strong>{stats.totalEvents}</strong></div>
          <div className="glass rounded-xl p-4">Total Registrations: <strong>{stats.totalRegistrations}</strong></div>
          <div className="glass rounded-xl p-4">Active Users: <strong>{stats.activeUsers}</strong></div>
          <div className="glass rounded-xl p-4">Most Popular: <strong>{stats.mostPopularEvent}</strong></div>
        </div>
      )}
      {stats ? (
        <div className="mb-4 grid gap-3 md:grid-cols-2">
          <div className="glass h-64 rounded-xl p-3">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={[{ name: "Events", value: stats.totalEvents }, { name: "Regs", value: stats.totalRegistrations }, { name: "Waitlist", value: stats.waitlistedCount }]}>
                <Tooltip />
                <Bar dataKey="value" fill="#22d3ee" />
              </BarChart>
            </ResponsiveContainer>
          </div>
          <div className="glass h-64 rounded-xl p-3">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie dataKey="value" data={[{ name: "Published", value: stats.publishedEvents }, { name: "Others", value: Math.max(0, stats.totalEvents - stats.publishedEvents) }]} outerRadius={90}>
                  <Cell fill="#60a5fa" />
                  <Cell fill="#818cf8" />
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      ) : (
        <div className="mb-4 grid gap-3 md:grid-cols-2"><div className="skeleton h-64" /><div className="skeleton h-64" /></div>
      )}
      <div className="mb-3 grid gap-2 md:grid-cols-3">
        <input className="rounded bg-slate-900 px-3 py-2" placeholder="Department" value={filters.department} onChange={(e) => setFilters((p) => ({ ...p, department: e.target.value }))} />
        <input className="rounded bg-slate-900 px-3 py-2" placeholder="Type" value={filters.type} onChange={(e) => setFilters((p) => ({ ...p, type: e.target.value }))} />
        <input className="rounded bg-slate-900 px-3 py-2" type="date" value={filters.date} onChange={(e) => setFilters((p) => ({ ...p, date: e.target.value }))} />
      </div>
      <div className="glass overflow-x-auto rounded-xl p-4">
        <table className="min-w-full text-sm">
          <thead className="text-left text-slate-300">
            <tr><th className="p-2">Title</th><th className="p-2">Date</th><th className="p-2">Department</th><th className="p-2">Type</th><th className="p-2">Actions</th></tr>
          </thead>
          <tbody>
            {events.map((event) => (
              <tr key={event.id} className="border-t border-white/10">
                <td className="p-2">{event.title}</td>
                <td className="p-2">{event.date}</td>
                <td className="p-2">{event.department}</td>
                <td className="p-2">{event.type}</td>
                <td className="flex gap-1 p-2">
                  <button className="rounded bg-indigo-500/30 px-2 py-1" onClick={() => { setSelected(event); setOpenModal(true); }}>Edit</button>
                  <button className="rounded bg-rose-500/30 px-2 py-1" onClick={() => deleteEvent(event.id)}>Delete</button>
                  <button className="rounded bg-emerald-500/30 px-2 py-1" onClick={() => showRegistrations(event.id)}>Registrations</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="mb-3 flex gap-2">
        <button onClick={exportCsv} className="rounded bg-emerald-500 px-3 py-2 text-sm font-medium text-slate-900">Export CSV</button>
      </div>
      <form onSubmit={sendNotification} className="glass mb-3 grid gap-2 rounded-xl p-3 md:grid-cols-3">
        <select className="rounded bg-slate-900 px-3 py-2" value={notifyForm.scope} onChange={(e) => setNotifyForm((p) => ({ ...p, scope: e.target.value }))}>
          <option value="all">All Users</option>
          <option value="department">Department</option>
          <option value="event">Event</option>
        </select>
        <input className="rounded bg-slate-900 px-3 py-2" placeholder="Message" value={notifyForm.message} onChange={(e) => setNotifyForm((p) => ({ ...p, message: e.target.value }))} />
        <button className="rounded bg-cyan-500 px-3 py-2 font-medium text-slate-900">Send Notification</button>
      </form>
      {Object.entries(registrations).map(([eventId, regs]) => (
        <div key={eventId} className="glass mt-3 rounded-xl p-3 text-sm">
          <h3 className="mb-2 font-semibold">Event #{eventId} Registrations</h3>
          {regs.length ? regs.map((r) => (
            <div key={r.registrationId} className="mb-2 flex items-center justify-between rounded bg-white/5 p-2">
              <p>{r.userName} ({r.userEmail}) - {r.status}</p>
              <div className="flex gap-2">
                <button onClick={() => approveParticipant(Number(eventId), r.registrationId, true)} className="rounded bg-emerald-500/30 px-2 py-1">Approve</button>
                <button onClick={() => approveParticipant(Number(eventId), r.registrationId, false)} className="rounded bg-rose-500/30 px-2 py-1">Reject</button>
              </div>
            </div>
          )) : <p>No registrations yet.</p>}
        </div>
      ))}
      <div className="glass mt-4 rounded-xl p-3 text-sm">
        <h3 className="mb-2 font-semibold">Audit Logs</h3>
        {auditLogs.length ? auditLogs.slice(-10).reverse().map((log) => (
          <p key={log.id} className="border-b border-white/10 py-1">{log.action} - {log.details}</p>
        )) : <p>No logs yet.</p>}
      </div>
      <FloatingButton onClick={() => { setSelected(null); setOpenModal(true); }} />
      <EventModal open={openModal} onClose={() => setOpenModal(false)} onSubmit={saveEvent} selected={selected} />
    </main>
  );
}
