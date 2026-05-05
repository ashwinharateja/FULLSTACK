export default function EventDetailsModal({ open, onClose, event }) {
  if (!open || !event) return null;
  const millis = new Date(event.date).getTime() - Date.now();
  const days = Math.max(0, Math.floor(millis / (1000 * 60 * 60 * 24)));
  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 p-4">
      <div className="glass gradient-border w-full max-w-2xl rounded-xl p-5">
        <div className="mb-2 flex items-center justify-between">
          <h3 className="text-xl font-semibold">{event.title}</h3>
          <button onClick={onClose} className="rounded bg-white/10 px-2 py-1 text-sm">Close</button>
        </div>
        <p className="mb-2 text-slate-300">{event.description}</p>
        <div className="grid gap-2 text-sm md:grid-cols-2">
          <p>Date: {event.date}</p>
          <p>Department: {event.department}</p>
          <p>Type: {event.type}</p>
          <p>Organizer: {event.organizer}</p>
          <p>Participants: {event.registeredCount}</p>
          <p>Countdown: {days} days</p>
        </div>
      </div>
    </div>
  );
}
