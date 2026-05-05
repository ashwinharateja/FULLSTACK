import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../services/api";
import LoadingSpinner from "../components/LoadingSpinner";
import { QRCodeCanvas } from "qrcode.react";
import jsPDF from "jspdf";

export default function EventDetailsPage({ userId }) {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(false);
  const [participants, setParticipants] = useState([]);
  const [feedback, setFeedback] = useState({ rating: 5, comment: "" });
  const [eventFeedback, setEventFeedback] = useState([]);
  const [attendanceToken, setAttendanceToken] = useState("");

  useEffect(() => {
    const fetchDetails = async () => {
      setLoading(true);
      try {
        const { data } = await api.get(`/events/${id}`, { params: { userId } });
        setEvent(data);
        const [pRes, fRes] = await Promise.all([
          api.get(`/events/${id}/registrations`),
          api.get(`/events/${id}/feedback`),
        ]);
        setParticipants(pRes.data);
        setEventFeedback(fRes.data);
      } catch {
        toast.error("Failed to fetch event details");
      } finally {
        setLoading(false);
      }
    };
    fetchDetails();
  }, [id, userId]);

  if (loading) return <LoadingSpinner />;
  if (!event) return <div className="mx-auto max-w-4xl px-4 py-8">Event not found.</div>;
  const millis = new Date(event.date).getTime() - Date.now();
  const days = Math.max(0, Math.floor(millis / (1000 * 60 * 60 * 24)));
  const myRegistration = participants.find((p) => Number(p.userId) === Number(userId));

  const markAttendance = async () => {
    try {
      await api.post("/attendance", { token: attendanceToken || myRegistration?.attendanceToken });
      toast.success("Attendance marked");
    } catch {
      toast.error("Attendance failed");
    }
  };

  const submitFeedback = async (e) => {
    e.preventDefault();
    try {
      await api.post("/feedback", { ...feedback, userId, eventId: Number(id) });
      toast.success("Feedback submitted");
      setFeedback({ rating: 5, comment: "" });
      const { data } = await api.get(`/events/${id}/feedback`);
      setEventFeedback(data);
    } catch {
      toast.error("Feedback failed");
    }
  };

  const downloadCertificate = () => {
    if (!myRegistration?.attendanceMarked) {
      toast.error("Attend event first");
      return;
    }
    const doc = new jsPDF();
    doc.setFontSize(20);
    doc.text("Certificate of Participation", 20, 30);
    doc.setFontSize(12);
    doc.text(`Student ID: ${userId}`, 20, 50);
    doc.text(`Event: ${event.title}`, 20, 60);
    doc.text("Status: Attended", 20, 70);
    doc.save(`certificate-${event.id}.pdf`);
  };

  return (
    <main className="mx-auto max-w-4xl px-4 py-8">
      <div className="glass gradient-border space-y-3 rounded-xl p-6">
        <h1 className="text-2xl font-bold">{event.title}</h1>
        <p className="text-slate-300">{event.description}</p>
        <p><span className="text-slate-400">Date:</span> {event.date}</p>
        <p><span className="text-slate-400">Countdown:</span> {days} days</p>
        <p><span className="text-slate-400">Department:</span> {event.department}</p>
        <p><span className="text-slate-400">Type:</span> {event.type}</p>
        <p><span className="text-slate-400">Organizer:</span> {event.organizer}</p>
        <p><span className="text-slate-400">Participants:</span> {participants.length}</p>
        <p><span className="text-slate-400">Seats Available:</span> {event.seatsAvailable}</p>
        <p><span className="text-slate-400">Status:</span> {event.registered ? "Registered" : event.waitlisted ? "Waitlisted" : "Not Registered"}</p>
        {myRegistration?.attendanceToken && (
          <div className="rounded-lg bg-white/5 p-3">
            <p className="mb-2 text-sm">Your attendance QR</p>
            <QRCodeCanvas value={myRegistration.attendanceToken} />
          </div>
        )}
        <div className="flex gap-2">
          <input value={attendanceToken} onChange={(e) => setAttendanceToken(e.target.value)} placeholder="Scan/Paste token" className="rounded bg-slate-900 px-3 py-2 text-sm" />
          <button onClick={markAttendance} className="rounded bg-emerald-500 px-3 py-2 text-sm font-medium text-slate-900">Mark Attendance</button>
          <button onClick={downloadCertificate} className="rounded bg-indigo-500 px-3 py-2 text-sm font-medium">Download Certificate</button>
        </div>
      </div>
      <div className="glass mt-4 rounded-xl p-4">
        <h3 className="mb-2 font-semibold">Feedback</h3>
        <form onSubmit={submitFeedback} className="mb-3 flex flex-col gap-2">
          <select value={feedback.rating} onChange={(e) => setFeedback((prev) => ({ ...prev, rating: Number(e.target.value) }))} className="rounded bg-slate-900 px-3 py-2">
            {[5, 4, 3, 2, 1].map((r) => <option key={r} value={r}>{r} Stars</option>)}
          </select>
          <textarea value={feedback.comment} onChange={(e) => setFeedback((prev) => ({ ...prev, comment: e.target.value }))} className="rounded bg-slate-900 px-3 py-2" placeholder="Share your feedback" />
          <button className="self-start rounded bg-cyan-500 px-3 py-2 text-sm font-medium text-slate-900">Submit</button>
        </form>
        <div className="space-y-2 text-sm">
          {eventFeedback.map((item) => (
            <div key={item.id} className="rounded bg-white/5 p-2">
              <p>{item.user} - {item.rating}/5</p>
              <p className="text-slate-300">{item.comment}</p>
            </div>
          ))}
        </div>
      </div>
    </main>
  );
}
