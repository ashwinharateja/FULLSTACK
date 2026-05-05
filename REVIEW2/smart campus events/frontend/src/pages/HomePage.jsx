import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import api from "../services/api";
import EventCard from "../components/EventCard";
import FilterBar from "../components/FilterBar";
import LoadingSpinner from "../components/LoadingSpinner";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import EventDetailsModal from "../components/EventDetailsModal";

export default function HomePage({ userId, userEmail }) {
  const [filters, setFilters] = useState({ department: "", type: "", date: "" });
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [compare, setCompare] = useState([]);
  const [quickView, setQuickView] = useState(null);

  const query = useMemo(() => ({ ...filters, userId, page, size: 6 }), [filters, userId, page]);

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const { data } = await api.get("/events", { params: query });
      setEvents(data.content);
      setTotalPages(data.totalPages || 1);
    } catch (e) {
      toast.error(e?.response?.data?.error || "Failed to load events");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchEvents(); }, [query]);

  const register = async (eventId) => {
    try {
      if (!userEmail) throw new Error("Missing logged-in user email");
      await api.post("/register", { email: userEmail, eventId });
      toast.success("Registered successfully");
      fetchEvents();
    } catch (e) {
      toast.error(e?.response?.data?.error || "Registration failed");
    }
  };

  const cancel = async (eventId) => {
    try {
      if (!userEmail) throw new Error("Missing logged-in user email");
      await api.delete("/register", { params: { email: userEmail, eventId } });
      toast.success("Registration cancelled");
      fetchEvents();
    } catch (e) {
      toast.error(e?.response?.data?.error || "Cancellation failed");
    }
  };

  const waitlist = async (eventId) => {
    try {
      if (!userEmail) throw new Error("Missing logged-in user email");
      await api.post("/waitlist", { email: userEmail, eventId });
      toast.success("Added to waitlist");
      fetchEvents();
    } catch (e) {
      toast.error(e?.response?.data?.error || "Waitlist failed");
    }
  };

  const toggleBookmark = async (eventId) => {
    try {
      const { data } = await api.post("/bookmark", null, { params: { userId, eventId } });
      toast.success(data.message);
      fetchEvents();
    } catch {
      toast.error("Bookmark action failed");
    }
  };

  const addCompare = (event) => {
    setCompare((prev) => {
      if (prev.find((e) => e.id === event.id) || prev.length >= 2) return prev;
      return [...prev, event];
    });
  };

  return (
    <main className="mx-auto max-w-7xl space-y-5 px-4 py-6">
      <FilterBar filters={filters} setFilters={setFilters} />
      {loading ? <LoadingSpinner /> : events.length === 0 ? (
        <div className="glass rounded-xl p-8 text-center text-slate-300">No upcoming events found.</div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {events.map((event) => (
            <EventCard
              key={event.id}
              event={event}
              onRegister={register}
              onCancel={cancel}
              onWaitlist={waitlist}
              onBookmark={toggleBookmark}
              onCompare={addCompare}
              onQuickView={setQuickView}
            />
          ))}
        </div>
      )}
      {compare.length > 0 && (
        <div className="glass rounded-xl p-4">
          <div className="mb-2 flex items-center justify-between">
            <h3 className="font-semibold">Compare Events</h3>
            <button onClick={() => setCompare([])} className="rounded bg-white/10 px-2 py-1 text-xs">Clear</button>
          </div>
          <div className="grid gap-3 md:grid-cols-2">
            {compare.map((item) => (
              <div key={item.id} className="rounded-lg bg-white/5 p-3 text-sm">
                <p className="font-semibold">{item.title}</p>
                <p>Seats: {item.seats}</p>
                <p>Registered: {item.registeredCount}</p>
                <p>Date: {item.date}</p>
              </div>
            ))}
          </div>
        </div>
      )}
      <div className="glass rounded-xl p-4">
        <h3 className="mb-3 font-semibold">Calendar View</h3>
        <Calendar className="!rounded-xl !border-white/10 !bg-slate-900 !text-slate-100" value={new Date()} />
      </div>
      <div className="flex items-center justify-center gap-2">
        <button disabled={page <= 0} onClick={() => setPage((p) => p - 1)} className="rounded bg-white/10 px-3 py-1 disabled:opacity-40">Prev</button>
        <span className="text-sm text-slate-300">Page {page + 1} / {totalPages}</span>
        <button disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)} className="rounded bg-white/10 px-3 py-1 disabled:opacity-40">Next</button>
      </div>
      <EventDetailsModal open={Boolean(quickView)} onClose={() => setQuickView(null)} event={quickView} />
    </main>
  );
}
