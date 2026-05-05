/**
 * NEXUS JOBS — Auth helpers
 * Shared across dashboard pages
 */

// Redirect if not authenticated
(function checkSession() {
  // Only runs on dashboard pages (pages/ dir)
  const path = window.location.pathname;
  if (!path.includes('/pages/')) return;
  if (path.includes('login') || path.includes('register')) return;
  if (!DB.Auth.getSession()) {
    window.location.href = 'login.html';
  }
})();
