import { Link, NavLink } from "react-router-dom";

export default function Navbar({ loggedInUser, onLogout }) {
  return (
    <header className="sticky top-0 z-20 border-b border-white/10 bg-slate-950/80 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
        <Link to="/" className="text-lg font-semibold text-cyan-300">Smart Campus</Link>
        <div className="flex items-center gap-3">
          {loggedInUser?.name && (
            <div className="rounded-md bg-slate-900 px-2 py-1 text-xs text-slate-200">
              {loggedInUser.name} ({loggedInUser.department})
            </div>
          )}
          <nav className="flex gap-3 text-sm">
          {[
            ["/", "Home"],
            ["/my-events", "My Events"],
            ["/admin", "Admin"],
          ].map(([to, label]) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `rounded-md px-3 py-2 transition ${isActive ? "bg-cyan-500/20 text-cyan-300" : "text-slate-300 hover:bg-white/10"}`
              }
            >
              {label}
            </NavLink>
          ))}
          </nav>
          {loggedInUser && (
            <button onClick={onLogout} className="rounded-md bg-white/10 px-3 py-2 text-xs text-slate-200 hover:bg-white/20">
              Logout
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
