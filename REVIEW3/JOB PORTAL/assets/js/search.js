/**
 * NEXUS JOBS — Search & Filter Engine
 * Thin wrapper used by the seeker dashboard
 */

// Already integrated inline in jobseeker-dashboard.html.
// This file provides standalone search utilities if needed externally.

const Search = (() => {
  function query(filters = {}) {
    return DB.Jobs.getAll(filters);
  }

  function highlight(text, term) {
    if (!term) return text;
    const escaped = term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    return text.replace(new RegExp(`(${escaped})`, 'gi'), '<mark style="background:rgba(108,99,255,0.3);border-radius:2px;padding:0 2px">$1</mark>');
  }

  return { query, highlight };
})();
