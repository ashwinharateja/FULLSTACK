import { Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Navbar from "./components/Navbar";
import HomePage from "./pages/HomePage";
import EventDetailsPage from "./pages/EventDetailsPage";
import MyEventsPage from "./pages/MyEventsPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";
import StudentLoginPage from "./pages/StudentLoginPage";

export default function App() {
  const [loggedInUser, setLoggedInUser] = useState(() => {
    const raw = localStorage.getItem("smartCampusUser");
    return raw ? JSON.parse(raw) : null;
  });

  useEffect(() => {
    if (loggedInUser?.id && loggedInUser?.email) {
      localStorage.setItem("smartCampusUser", JSON.stringify(loggedInUser));
    }
  }, [loggedInUser]);

  const handleLogout = () => {
    localStorage.removeItem("smartCampusUser");
    setLoggedInUser(null);
    toast.success("Logged out");
  };

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_right,_#0f172a,_#020617)]">
      <Navbar loggedInUser={loggedInUser} onLogout={handleLogout} />
      <Routes>
        <Route path="/login" element={loggedInUser ? <Navigate to="/" /> : <StudentLoginPage onLogin={setLoggedInUser} />} />
        <Route path="/" element={loggedInUser ? <HomePage userId={loggedInUser?.id} userEmail={loggedInUser?.email} /> : <Navigate to="/login" />} />
        <Route path="/events/:id" element={loggedInUser ? <EventDetailsPage userId={loggedInUser?.id} /> : <Navigate to="/login" />} />
        <Route path="/my-events" element={loggedInUser ? <MyEventsPage userId={loggedInUser?.id} userEmail={loggedInUser?.email} /> : <Navigate to="/login" />} />
        <Route path="/admin" element={<AdminDashboardPage />} />
        <Route path="*" element={<Navigate to={loggedInUser ? "/" : "/login"} />} />
      </Routes>
    </div>
  );
}
