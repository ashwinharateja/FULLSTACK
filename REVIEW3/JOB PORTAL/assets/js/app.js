/**
 * NEXUS JOBS — App Shell Utilities
 * Shared across all dashboard pages
 */

/* ── Toast System ── */
function showToast(type, title, message, duration = 4000) {
  const container = document.getElementById('toast-container');
  if (!container) return;
  const icons = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
  const el = document.createElement('div');
  el.className = `toast toast-${type}`;
  el.innerHTML = `
    <span class="toast-icon">${icons[type] || '🔔'}</span>
    <div class="toast-body">
      <div class="toast-title">${title}</div>
      ${message ? `<div class="toast-msg">${message}</div>` : ''}
    </div>
    <button class="toast-close" onclick="this.parentElement.remove()">✕</button>
  `;
  container.appendChild(el);
  setTimeout(() => {
    el.classList.add('removing');
    el.addEventListener('animationend', () => el.remove());
  }, duration);
}

/* ── Auth Guard ── */
function requireAuth(requiredRole) {
  const user = DB.Auth.getCurrentUser();
  if (!user) { window.location.href = 'login.html'; return null; }
  if (requiredRole && user.role !== requiredRole && user.role !== 'admin') {
    const dest = { admin: 'admin-dashboard.html', employer: 'employer-dashboard.html', seeker: 'jobseeker-dashboard.html' }[user.role];
    if (dest) window.location.href = dest;
    return null;
  }
  return user;
}

/* ── Ripple Effect on buttons ── */
document.addEventListener('click', (e) => {
  const btn = e.target.closest('.btn');
  if (!btn) return;
  const rect = btn.getBoundingClientRect();
  const ripple = document.createElement('span');
  const size = Math.max(rect.width, rect.height);
  ripple.className = 'ripple';
  ripple.style.cssText = `width:${size}px;height:${size}px;left:${e.clientX-rect.left-size/2}px;top:${e.clientY-rect.top-size/2}px`;
  btn.classList.add('ripple-wrap');
  btn.appendChild(ripple);
  ripple.addEventListener('animationend', () => ripple.remove());
});

/* ── Format helpers (fallback if DB not loaded) ── */
window.fmtSalary = (min, max) => {
  const f = n => n >= 1000 ? (n/1000).toFixed(0) + 'k' : n;
  return `$${f(min)} – $${f(max)}`;
};
