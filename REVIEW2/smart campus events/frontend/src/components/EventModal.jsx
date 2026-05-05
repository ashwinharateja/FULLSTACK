import { useEffect, useState } from "react";

const initialForm = { title: "", description: "", date: "", department: "", type: "", seats: 1, organizer: "", status: "PUBLISHED", featured: false };

export default function EventModal({ open, onClose, onSubmit, selected }) {
  const [form, setForm] = useState(initialForm);
  useEffect(() => {
    setForm(selected ? { ...selected } : initialForm);
  }, [selected]);
  if (!open) return null;

  const handle = (e) => setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  const submit = (e) => {
    e.preventDefault();
    onSubmit({ ...form, seats: Number(form.seats) });
  };

  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 p-4">
      <form className="glass w-full max-w-lg space-y-3 rounded-xl p-5" onSubmit={submit}>
        <h3 className="text-xl font-semibold">{selected ? "Edit Event" : "Create Event"}</h3>
        {["title", "description", "department", "type", "organizer"].map((field) => (
          <input key={field} name={field} value={form[field]} onChange={handle} required className="w-full rounded-lg bg-slate-900 px-3 py-2" placeholder={field} />
        ))}
        <div className="grid grid-cols-2 gap-2">
          <input name="date" type="date" value={form.date} onChange={handle} required className="rounded-lg bg-slate-900 px-3 py-2" />
          <input name="seats" type="number" min={1} value={form.seats} onChange={handle} required className="rounded-lg bg-slate-900 px-3 py-2" />
        </div>
        <div className="grid grid-cols-2 gap-2">
          <select name="status" value={form.status} onChange={handle} className="rounded-lg bg-slate-900 px-3 py-2">
            <option value="DRAFT">Draft</option>
            <option value="PUBLISHED">Published</option>
            <option value="CLOSED">Closed</option>
          </select>
          <label className="flex items-center gap-2 rounded-lg bg-slate-900 px-3 py-2">
            <input type="checkbox" checked={form.featured} onChange={(e) => setForm((prev) => ({ ...prev, featured: e.target.checked }))} />
            Featured Event
          </label>
        </div>
        <div className="flex justify-end gap-2">
          <button type="button" onClick={onClose} className="rounded-lg bg-white/10 px-3 py-2">Close</button>
          <button type="submit" className="rounded-lg bg-cyan-500 px-3 py-2 font-medium text-slate-900">Save</button>
        </div>
      </form>
    </div>
  );
}
