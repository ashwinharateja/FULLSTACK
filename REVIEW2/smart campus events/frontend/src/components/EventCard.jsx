import { CalendarDays, Users, CheckCircle2 } from "lucide-react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";

export default function EventCard({ event, onRegister, onCancel, onWaitlist, onBookmark, onCompare, onQuickView }) {
  return (
    <motion.div whileHover={{ y: -4, scale: 1.01 }} className="glass gradient-border rounded-xl p-4 transition hover:bg-white/15">
      <div className="mb-2 flex items-start justify-between gap-2">
        <h3 className="text-lg font-semibold">{event.title}</h3>
        <span className="rounded-full bg-indigo-500/20 px-2 py-1 text-xs">{event.type}</span>
      </div>
      <p className="text-sm text-slate-300">{event.description}</p>
      <div className="mt-3 space-y-2 text-sm text-slate-200">
        <p className="flex items-center gap-2"><CalendarDays size={16} /> {event.date}</p>
        <p className="flex items-center gap-2"><Users size={16} /> Seats left: {event.seatsAvailable}</p>
        <p className="flex items-center gap-2"><CheckCircle2 size={16} /> {event.registered ? "Registered" : event.waitlisted ? "Waitlisted" : "Not Registered"}</p>
      </div>
      <div className="mt-4 flex gap-2">
        <Link to={`/events/${event.id}`} className="rounded-lg bg-white/10 px-3 py-2 text-sm hover:bg-white/20">Details</Link>
        <button onClick={() => onQuickView(event)} className="rounded-lg bg-white/10 px-3 py-2 text-sm hover:bg-white/20">Quick View</button>
        {event.registered ? (
          <button onClick={() => onCancel(event.id)} className="rounded-lg bg-rose-500 px-3 py-2 text-sm font-medium text-white hover:bg-rose-400">Cancel</button>
        ) : (
          <button
            disabled={event.seatsAvailable <= 0}
            onClick={() => onRegister(event.id)}
            className="rounded-lg bg-cyan-500 px-3 py-2 text-sm font-medium text-slate-900 hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Register
          </button>
        )}
        {!event.registered && !event.waitlisted && (
          <button onClick={() => onWaitlist(event.id)} className="rounded-lg bg-amber-500 px-3 py-2 text-sm font-medium text-slate-900 hover:bg-amber-400">Waitlist</button>
        )}
      </div>
      <div className="mt-3 flex gap-2 text-xs">
        <button onClick={() => onBookmark(event.id)} className="rounded bg-white/10 px-2 py-1">{event.bookmarked ? "Bookmarked" : "Bookmark"}</button>
        <button onClick={() => onCompare(event)} className="rounded bg-white/10 px-2 py-1">Compare</button>
      </div>
    </motion.div>
  );
}
