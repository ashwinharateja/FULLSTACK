import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import api from "../services/api";
import LoadingSpinner from "../components/LoadingSpinner";

export default function MyEventsPage({ userId, userEmail }) {
  const [events, setEvents] = useState([]);
  const [dashboard, setDashboard] = useState([]);
  const [bookmarks, setBookmarks] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!userEmail || !userId) return;
    const fetchMyEvents = async () => {
      setLoading(true);
      try {
        const [eventsRes, dashboardRes] = await Promise.all([
          api.get("/my-events", { params: { email: userEmail } }),
          api.get("/my-events-dashboard", { params: { userId } }),
        ]);
        setEvents(eventsRes.data);
        setDashboard(dashboardRes.data);
        const bookmarksRes = await api.get("/bookmarks", { params: { userId } });
        setBookmarks(bookmarksRes.data);
      } catch {
        toast.error("Unable to load registered events");
      } finally {
        setLoading(false);
      }
    };
    fetchMyEvents();
  }, [userId, userEmail]);

  return (
    <main className="mx-auto max-w-7xl px-4 py-6">
      <h1 className="mb-4 text-2xl font-bold">My Events</h1>
      <div className="mb-4 grid gap-3 md:grid-cols-3">
        <div className="glass rounded-xl p-4">Upcoming: <strong>{dashboard.filter((d) => d.registrationStatus === "REGISTERED").length}</strong></div>
        <div className="glass rounded-xl p-4">Completed: <strong>{dashboard.filter((d) => d.registrationStatus === "ATTENDED").length}</strong></div>
        <div className="glass rounded-xl p-4">Cancelled: <strong>{dashboard.filter((d) => d.registrationStatus === "CANCELLED").length}</strong></div>
      </div>
      {loading ? <LoadingSpinner /> : events.length === 0 ? (
        <div className="glass rounded-xl p-8 text-center">You have not registered for any event yet.</div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {events.map((event) => (
            <div key={event.id} className="glass rounded-xl p-4">
              <h3 className="text-lg font-semibold">{event.title}</h3>
              <p className="text-sm text-slate-300">{event.date} • {event.department}</p>
              <p className="mt-1 text-xs text-cyan-300">{event.registered ? "Registered" : event.waitlisted ? "Waitlisted" : "Inactive"}</p>
            </div>
          ))}
        </div>
      )}
      <div className="mt-5">
        <h2 className="mb-3 text-xl font-semibold">Bookmarked Events</h2>
        <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-3">
          {bookmarks.map((item) => (
            <div key={item.id} className="glass rounded-xl p-3">
              <p className="font-semibold">{item.title}</p>
              <p className="text-sm text-slate-300">{item.date}</p>
            </div>
          ))}
          {!bookmarks.length && <div className="glass rounded-xl p-4 text-sm text-slate-300">No bookmarks yet.</div>}
        </div>
      </div>
    </main>
  );
}
