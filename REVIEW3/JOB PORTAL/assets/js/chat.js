/**
 * NEXUS JOBS — Chat Module
 * REST-polling-style chat utilities
 */

const Chat = (() => {
  let pollInterval = null;

  function startPolling(userId, threadId, onNewMessage, intervalMs = 3000) {
    let lastCount = DB.Messages.getThread(threadId).length;
    pollInterval = setInterval(() => {
      const msgs = DB.Messages.getThread(threadId);
      if (msgs.length > lastCount) {
        lastCount = msgs.length;
        onNewMessage(msgs);
      }
    }, intervalMs);
  }

  function stopPolling() {
    if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
  }

  function formatTime(ts) {
    const d = new Date(ts);
    const now = new Date();
    const isToday = d.toDateString() === now.toDateString();
    if (isToday) return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    return d.toLocaleDateString([], { month: 'short', day: 'numeric' });
  }

  return { startPolling, stopPolling, formatTime };
})();
