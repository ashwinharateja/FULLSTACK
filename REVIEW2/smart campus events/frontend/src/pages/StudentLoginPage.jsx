import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import api from "../services/api";

export default function StudentLoginPage({ onLogin }) {
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadStudents = async () => {
      try {
        const { data } = await api.get("/users/students");
        setStudents(data);
      } catch {
        toast.error("Unable to load students");
      }
    };
    loadStudents();
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { data } = await api.post("/student/login", form);
      onLogin(data);
      toast.success("Student login successful");
    } catch (err) {
      toast.error(err?.response?.data?.error || "Invalid credentials");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="mx-auto max-w-lg px-4 py-12">
      <div className="glass rounded-xl p-6">
        <h1 className="mb-4 text-2xl font-bold">Student Login</h1>
        <form onSubmit={submit} className="space-y-3">
          <input className="w-full rounded bg-slate-900 px-3 py-2" placeholder="Email" value={form.email} onChange={(e) => setForm((p) => ({ ...p, email: e.target.value }))} />
          <input className="w-full rounded bg-slate-900 px-3 py-2" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm((p) => ({ ...p, password: e.target.value }))} />
          <button disabled={loading} className="w-full rounded bg-cyan-500 px-3 py-2 font-medium text-slate-900 disabled:opacity-50">{loading ? "Signing in..." : "Login"}</button>
        </form>
        <div className="mt-4 text-xs text-slate-300">
          <p className="mb-1 font-semibold">Sample students:</p>
          {students.map((s) => (
            <p key={s.id}>{s.name} ({s.department}) - {s.email}</p>
          ))}
          <p className="mt-2">Passwords: `cse123`, `ece123`, `mba123`, `arts123`</p>
        </div>
      </div>
    </main>
  );
}
