/**
 * NEXUS JOBS — Mock Database + Data Layer
 * Persists to localStorage, seeds demo data on first run
 */

const DB = (() => {
  const KEYS = {
    users: 'nj_users',
    jobs: 'nj_jobs',
    applications: 'nj_applications',
    messages: 'nj_messages',
    notifications: 'nj_notifications',
    session: 'nj_session',
  };

  /* ── Utilities ── */
  const uid = () => '_' + Math.random().toString(36).slice(2, 11);
  const now = () => new Date().toISOString();
  const daysAgo = (n) => new Date(Date.now() - n * 86400000).toISOString();

  const get = (k) => JSON.parse(localStorage.getItem(k) || '[]');
  const getObj = (k, def = {}) => JSON.parse(localStorage.getItem(k) || JSON.stringify(def));
  const set = (k, v) => localStorage.setItem(k, JSON.stringify(v));

  /* ── Seed Data ── */
  function seed() {
    if (localStorage.getItem('nj_seeded')) return;

    // Users
    const users = [
      { id: 'u1', role: 'admin', name: 'Alex Admin', email: 'admin@nexusjobs.com', password: 'admin123', avatar: '👑', avatarClass: 'avatar-gradient-3', createdAt: daysAgo(60) },
      { id: 'u2', role: 'employer', name: 'Sarah Chen', email: 'employer@techcorp.com', password: 'employer123', avatar: '🏢', avatarClass: 'avatar-gradient-1', company: 'TechCorp Inc.', industry: 'Technology', website: 'https://techcorp.com', about: 'Leading tech company building next-gen solutions.', createdAt: daysAgo(45) },
      { id: 'u3', role: 'employer', name: 'Mark Rivera', email: 'mark@designco.io', password: 'employer123', avatar: '🎨', avatarClass: 'avatar-gradient-2', company: 'DesignCo', industry: 'Design', website: 'https://designco.io', about: 'Award-winning design agency.', createdAt: daysAgo(30) },
      { id: 'u4', role: 'seeker', name: 'Jordan Lee', email: 'seeker@email.com', password: 'seeker123', avatar: '👤', avatarClass: 'avatar-gradient-1', title: 'Full Stack Developer', location: 'San Francisco, CA', skills: ['React', 'Node.js', 'Python', 'TypeScript', 'PostgreSQL'], experience: 3, bio: 'Passionate developer with 3 years of experience building scalable web applications.', resume: 'Jordan_Lee_Resume.pdf', education: 'B.S. Computer Science, Stanford University', salary: 130000, createdAt: daysAgo(20) },
      { id: 'u5', role: 'seeker', name: 'Priya Sharma', email: 'priya@email.com', password: 'seeker123', avatar: '👤', avatarClass: 'avatar-gradient-2', title: 'UX Designer', location: 'New York, NY', skills: ['Figma', 'User Research', 'Prototyping', 'CSS', 'Design Systems'], experience: 4, bio: 'Creative UX designer focused on building delightful user experiences.', resume: 'Priya_Sharma_Resume.pdf', education: 'M.A. HCI, Carnegie Mellon', salary: 110000, createdAt: daysAgo(15) },
      { id: 'u6', role: 'seeker', name: 'Carlos Mendez', email: 'carlos@email.com', password: 'seeker123', avatar: '👤', avatarClass: 'avatar-gradient-4', title: 'Data Scientist', location: 'Austin, TX', skills: ['Python', 'Machine Learning', 'TensorFlow', 'SQL', 'Tableau'], experience: 5, bio: 'Data scientist specializing in ML models and analytics pipelines.', resume: 'Carlos_Mendez_Resume.pdf', education: 'PhD, Data Science, UC Berkeley', salary: 155000, createdAt: daysAgo(10) },
    ];

    // Jobs
    const jobs = [
      {
        id: 'j1', employerId: 'u2', title: 'Senior Full Stack Engineer', company: 'TechCorp Inc.', companyLogo: '🚀', location: 'San Francisco, CA', type: 'Full-time', remote: true,
        salary: { min: 120000, max: 160000, currency: 'USD' }, skills: ['React', 'Node.js', 'TypeScript', 'PostgreSQL', 'AWS'],
        description: 'We are looking for a Senior Full Stack Engineer to join our growing team...', experience: '3-5 years', category: 'Engineering',
        status: 'active', views: 342, applications: 18, createdAt: daysAgo(5),
      },
      {
        id: 'j2', employerId: 'u2', title: 'Product Designer', company: 'TechCorp Inc.', companyLogo: '🚀', location: 'Remote', type: 'Full-time', remote: true,
        salary: { min: 100000, max: 130000, currency: 'USD' }, skills: ['Figma', 'User Research', 'Prototyping', 'Design Systems'],
        description: 'Join our design team to craft beautiful, user-centric interfaces...', experience: '2-4 years', category: 'Design',
        status: 'active', views: 215, applications: 11, createdAt: daysAgo(8),
      },
      {
        id: 'j3', employerId: 'u3', title: 'UX/UI Designer', company: 'DesignCo', companyLogo: '🎨', location: 'New York, NY', type: 'Full-time', remote: false,
        salary: { min: 90000, max: 115000, currency: 'USD' }, skills: ['Figma', 'Sketch', 'CSS', 'User Research', 'Wireframing'],
        description: 'We need a talented UX/UI Designer to lead design projects...', experience: '2-5 years', category: 'Design',
        status: 'active', views: 178, applications: 9, createdAt: daysAgo(12),
      },
      {
        id: 'j4', employerId: 'u2', title: 'Machine Learning Engineer', company: 'TechCorp Inc.', companyLogo: '🚀', location: 'Austin, TX', type: 'Full-time', remote: true,
        salary: { min: 150000, max: 190000, currency: 'USD' }, skills: ['Python', 'TensorFlow', 'PyTorch', 'MLOps', 'SQL'],
        description: 'Help us build ML models powering millions of users...', experience: '4-7 years', category: 'Data Science',
        status: 'active', views: 302, applications: 14, createdAt: daysAgo(3),
      },
      {
        id: 'j5', employerId: 'u3', title: 'Frontend Developer', company: 'DesignCo', companyLogo: '🎨', location: 'Remote', type: 'Contract', remote: true,
        salary: { min: 80000, max: 100000, currency: 'USD' }, skills: ['React', 'CSS', 'TypeScript', 'Jest', 'Storybook'],
        description: 'Build beautiful, performant UIs for our clients...', experience: '1-3 years', category: 'Engineering',
        status: 'active', views: 141, applications: 7, createdAt: daysAgo(7),
      },
      {
        id: 'j6', employerId: 'u2', title: 'DevOps Engineer', company: 'TechCorp Inc.', companyLogo: '🚀', location: 'Seattle, WA', type: 'Full-time', remote: false,
        salary: { min: 130000, max: 165000, currency: 'USD' }, skills: ['Kubernetes', 'Docker', 'Terraform', 'AWS', 'CI/CD'],
        description: 'Manage our cloud infrastructure and deployment pipelines...', experience: '3-6 years', category: 'Engineering',
        status: 'active', views: 198, applications: 8, createdAt: daysAgo(10),
      },
    ];

    // Applications
    const applications = [
      { id: 'a1', jobId: 'j1', seekerId: 'u4', status: 'interview', appliedAt: daysAgo(4), coverLetter: 'I am excited about this role...', steps: ['applied', 'screening', 'interview'] },
      { id: 'a2', jobId: 'j3', seekerId: 'u5', status: 'screening', appliedAt: daysAgo(6), coverLetter: 'My design work aligns with...', steps: ['applied', 'screening'] },
      { id: 'a3', jobId: 'j4', seekerId: 'u6', status: 'applied', appliedAt: daysAgo(2), coverLetter: 'My ML experience matches...', steps: ['applied'] },
      { id: 'a4', jobId: 'j2', seekerId: 'u4', status: 'offered', appliedAt: daysAgo(14), coverLetter: 'I would love to join...', steps: ['applied', 'screening', 'interview', 'offered'] },
    ];

    // Messages
    const messages = [
      { id: 'm1', fromId: 'u2', toId: 'u4', thread: 't1', text: 'Hi Jordan! We reviewed your application for the Senior Full Stack Engineer role.', ts: daysAgo(3) },
      { id: 'm2', fromId: 'u4', toId: 'u2', thread: 't1', text: 'Thank you! I am very excited about the opportunity.', ts: daysAgo(3) },
      { id: 'm3', fromId: 'u2', toId: 'u4', thread: 't1', text: 'We would like to schedule a technical interview. Are you available Thursday at 2PM PST?', ts: daysAgo(2) },
      { id: 'm4', fromId: 'u4', toId: 'u2', thread: 't1', text: 'Thursday works perfectly! Looking forward to it.', ts: daysAgo(2) },
      { id: 'm5', fromId: 'u3', toId: 'u5', thread: 't2', text: 'Hi Priya, your portfolio is impressive. Would you like to discuss the UX role?', ts: daysAgo(5) },
      { id: 'm6', fromId: 'u5', toId: 'u3', thread: 't2', text: 'Absolutely! I have been following DesignCo for a while and love the work.', ts: daysAgo(5) },
    ];

    // Notifications
    const notifications = [
      { id: 'n1', userId: 'u4', type: 'success', title: 'Interview Scheduled', message: 'TechCorp wants to interview you for Sr. Full Stack Engineer', ts: daysAgo(2), read: false },
      { id: 'n2', userId: 'u4', type: 'info', title: 'Application Viewed', message: 'DesignCo viewed your profile', ts: daysAgo(4), read: false },
      { id: 'n3', userId: 'u4', type: 'success', title: 'Offer Received!', message: 'You have an offer for Product Designer at TechCorp', ts: daysAgo(1), read: false },
      { id: 'n4', userId: 'u2', type: 'info', title: 'New Application', message: 'Jordan Lee applied to Sr. Full Stack Engineer', ts: daysAgo(4), read: false },
      { id: 'n5', userId: 'u2', type: 'info', title: 'New Application', message: 'Jordan Lee applied to Product Designer', ts: daysAgo(14), read: true },
    ];

    set(KEYS.users, users);
    set(KEYS.jobs, jobs);
    set(KEYS.applications, applications);
    set(KEYS.messages, messages);
    set(KEYS.notifications, notifications);
    localStorage.setItem('nj_seeded', '1');
  }

  /* ── Auth ── */
  const Auth = {
    login(email, password) {
      const users = get(KEYS.users);
      const user = users.find(u => u.email === email && u.password === password);
      if (!user) return null;
      const session = { userId: user.id, role: user.role, name: user.name, ts: now() };
      set(KEYS.session, session);
      return user;
    },
    register(data) {
      const users = get(KEYS.users);
      if (users.find(u => u.email === data.email)) return null;
      const user = { id: uid(), createdAt: now(), avatarClass: 'avatar-gradient-' + ((users.length % 4) + 1), ...data };
      users.push(user);
      set(KEYS.users, users);
      const session = { userId: user.id, role: user.role, name: user.name, ts: now() };
      set(KEYS.session, session);
      return user;
    },
    logout() { localStorage.removeItem(KEYS.session); },
    getSession() { return getObj(KEYS.session, null); },
    getCurrentUser() {
      const s = Auth.getSession();
      if (!s) return null;
      return get(KEYS.users).find(u => u.id === s.userId) || null;
    },
    updateProfile(updates) {
      const user = Auth.getCurrentUser();
      if (!user) return false;
      const users = get(KEYS.users).map(u => u.id === user.id ? { ...u, ...updates } : u);
      set(KEYS.users, users);
      return true;
    },
  };

  /* ── Jobs ── */
  const Jobs = {
    getAll(filters = {}) {
      let jobs = get(KEYS.jobs).filter(j => j.status === 'active');
      if (filters.query) {
        const q = filters.query.toLowerCase();
        jobs = jobs.filter(j => j.title.toLowerCase().includes(q) || j.company.toLowerCase().includes(q) || (j.skills || []).some(s => s.toLowerCase().includes(q)));
      }
      if (filters.category && filters.category !== 'all') jobs = jobs.filter(j => j.category === filters.category);
      if (filters.type && filters.type !== 'all') jobs = jobs.filter(j => j.type === filters.type);
      if (filters.remote === true) jobs = jobs.filter(j => j.remote);
      if (filters.location) jobs = jobs.filter(j => j.location.toLowerCase().includes(filters.location.toLowerCase()));
      if (filters.salaryMin) jobs = jobs.filter(j => j.salary.max >= filters.salaryMin);
      if (filters.salaryMax) jobs = jobs.filter(j => j.salary.min <= filters.salaryMax);
      return jobs;
    },
    getByEmployer(employerId) { return get(KEYS.jobs).filter(j => j.employerId === employerId); },
    getById(id) { return get(KEYS.jobs).find(j => j.id === id) || null; },
    create(data) {
      const jobs = get(KEYS.jobs);
      const job = { id: uid(), createdAt: now(), status: 'active', views: 0, applications: 0, ...data };
      jobs.push(job);
      set(KEYS.jobs, jobs);
      return job;
    },
    update(id, updates) {
      const jobs = get(KEYS.jobs).map(j => j.id === id ? { ...j, ...updates } : j);
      set(KEYS.jobs, jobs);
    },
    delete(id) {
      set(KEYS.jobs, get(KEYS.jobs).filter(j => j.id !== id));
    },
  };

  /* ── Applications ── */
  const Applications = {
    apply(jobId, seekerId, data) {
      const apps = get(KEYS.applications);
      if (apps.find(a => a.jobId === jobId && a.seekerId === seekerId)) return null;
      const app = { id: uid(), jobId, seekerId, status: 'applied', appliedAt: now(), steps: ['applied'], ...data };
      apps.push(app);
      set(KEYS.applications, apps);
      // Increment job applications count
      const jobs = get(KEYS.jobs).map(j => j.id === jobId ? { ...j, applications: (j.applications || 0) + 1 } : j);
      set(KEYS.jobs, jobs);
      return app;
    },
    getBySeeker(seekerId) {
      const apps = get(KEYS.applications).filter(a => a.seekerId === seekerId);
      return apps.map(a => ({ ...a, job: Jobs.getById(a.jobId) }));
    },
    getByJob(jobId) {
      const apps = get(KEYS.applications).filter(a => a.jobId === jobId);
      return apps.map(a => ({ ...a, seeker: get(KEYS.users).find(u => u.id === a.seekerId) }));
    },
    getByEmployer(employerId) {
      const myJobs = Jobs.getByEmployer(employerId).map(j => j.id);
      const apps = get(KEYS.applications).filter(a => myJobs.includes(a.jobId));
      return apps.map(a => ({ ...a, job: Jobs.getById(a.jobId), seeker: get(KEYS.users).find(u => u.id === a.seekerId) }));
    },
    updateStatus(id, status, step) {
      const apps = get(KEYS.applications).map(a => {
        if (a.id !== id) return a;
        const steps = a.steps || ['applied'];
        if (step && !steps.includes(step)) steps.push(step);
        return { ...a, status, steps };
      });
      set(KEYS.applications, apps);
    },
    hasApplied(jobId, seekerId) { return get(KEYS.applications).some(a => a.jobId === jobId && a.seekerId === seekerId); },
    getAll() {
      const apps = get(KEYS.applications);
      return apps.map(a => ({ ...a, job: Jobs.getById(a.jobId), seeker: get(KEYS.users).find(u => u.id === a.seekerId) }));
    },
    deleteApp(id) { set(KEYS.applications, get(KEYS.applications).filter(a => a.id !== id)); },
  };

  /* ── Messages ── */
  const Messages = {
    getThreads(userId) {
      const msgs = get(KEYS.messages);
      const threadIds = [...new Set(msgs.filter(m => m.fromId === userId || m.toId === userId).map(m => m.thread))];
      return threadIds.map(tid => {
        const threadMsgs = msgs.filter(m => m.thread === tid).sort((a, b) => new Date(a.ts) - new Date(b.ts));
        const last = threadMsgs[threadMsgs.length - 1];
        const otherId = last.fromId === userId ? last.toId : last.fromId;
        const other = get(KEYS.users).find(u => u.id === otherId);
        return { thread: tid, other, last, messages: threadMsgs };
      });
    },
    getThread(threadId) {
      return get(KEYS.messages).filter(m => m.thread === threadId).sort((a, b) => new Date(a.ts) - new Date(b.ts));
    },
    send(fromId, toId, text, thread = null) {
      const msgs = get(KEYS.messages);
      const threadId = thread || ('t_' + fromId + '_' + toId + '_' + Date.now());
      const msg = { id: uid(), fromId, toId, thread: threadId, text, ts: now() };
      msgs.push(msg);
      set(KEYS.messages, msgs);
      return msg;
    },
  };

  /* ── Notifications ── */
  const Notifications = {
    getForUser(userId) { return get(KEYS.notifications).filter(n => n.userId === userId).sort((a, b) => new Date(b.ts) - new Date(a.ts)); },
    add(userId, type, title, message) {
      const notifs = get(KEYS.notifications);
      notifs.push({ id: uid(), userId, type, title, message, ts: now(), read: false });
      set(KEYS.notifications, notifs);
    },
    markRead(userId) {
      const notifs = get(KEYS.notifications).map(n => n.userId === userId ? { ...n, read: true } : n);
      set(KEYS.notifications, notifs);
    },
    getUnreadCount(userId) { return get(KEYS.notifications).filter(n => n.userId === userId && !n.read).length; },
  };

  /* ── Stats ── */
  const Stats = {
    getAdmin() {
      const users = get(KEYS.users);
      const jobs = get(KEYS.jobs);
      const apps = get(KEYS.applications);
      return {
        totalUsers: users.length,
        seekers: users.filter(u => u.role === 'seeker').length,
        employers: users.filter(u => u.role === 'employer').length,
        activeJobs: jobs.filter(j => j.status === 'active').length,
        totalApplications: apps.length,
        hireRate: Math.round((apps.filter(a => a.status === 'offered').length / Math.max(apps.length, 1)) * 100),
      };
    },
    getEmployer(employerId) {
      const jobs = Jobs.getByEmployer(employerId);
      const apps = Applications.getByEmployer(employerId);
      return {
        activeJobs: jobs.filter(j => j.status === 'active').length,
        totalApplications: apps.length,
        shortlisted: apps.filter(a => a.status === 'interview' || a.status === 'offered').length,
        totalViews: jobs.reduce((s, j) => s + (j.views || 0), 0),
      };
    },
    getSeeker(seekerId) {
      const apps = Applications.getBySeeker(seekerId);
      return {
        applied: apps.length,
        interviews: apps.filter(a => a.status === 'interview').length,
        offers: apps.filter(a => a.status === 'offered').length,
      };
    },
  };

  /* ── Recommendations ── */
  const Recommendations = {
    forSeeker(seekerId) {
      const user = get(KEYS.users).find(u => u.id === seekerId);
      if (!user || !user.skills) return [];
      const jobs = Jobs.getAll();
      const applied = get(KEYS.applications).filter(a => a.seekerId === seekerId).map(a => a.jobId);
      return jobs
        .filter(j => !applied.includes(j.id))
        .map(j => {
          const jSkills = j.skills || [];
          const match = jSkills.filter(s => user.skills.includes(s)).length;
          const score = Math.round((match / Math.max(jSkills.length, 1)) * 100);
          return { ...j, matchScore: score };
        })
        .sort((a, b) => b.matchScore - a.matchScore)
        .filter(j => j.matchScore > 0)
        .slice(0, 5);
    },
  };

  /* ── Users (admin) ── */
  const Users = {
    getAll() { return get(KEYS.users); },
    getById(id) { return get(KEYS.users).find(u => u.id === id) || null; },
    delete(id) { set(KEYS.users, get(KEYS.users).filter(u => u.id !== id)); },
    update(id, updates) { set(KEYS.users, get(KEYS.users).map(u => u.id === id ? { ...u, ...updates } : u)); },
  };

  /* ── Helpers ── */
  const Helpers = {
    formatSalary(min, max, currency = 'USD') {
      const fmt = n => n >= 1000 ? (n / 1000).toFixed(0) + 'k' : n;
      return `$${fmt(min)} – $${fmt(max)}`;
    },
    timeAgo(ts) {
      const diff = (Date.now() - new Date(ts)) / 1000;
      if (diff < 60) return 'just now';
      if (diff < 3600) return Math.floor(diff / 60) + 'm ago';
      if (diff < 86400) return Math.floor(diff / 3600) + 'h ago';
      if (diff < 2592000) return Math.floor(diff / 86400) + 'd ago';
      return Math.floor(diff / 2592000) + 'mo ago';
    },
    statusColor(status) {
      return { applied: 'badge-muted', screening: 'badge-info', interview: 'badge-warning', offered: 'badge-accent', rejected: 'badge-danger' }[status] || 'badge-muted';
    },
    statusLabel(status) {
      return { applied: 'Applied', screening: 'Screening', interview: 'Interview', offered: 'Offered 🎉', rejected: 'Rejected' }[status] || status;
    },
    scoreColor(score) {
      if (score >= 80) return 'var(--accent)';
      if (score >= 50) return 'var(--warning)';
      return 'var(--danger)';
    },
    scoreBg(score) {
      if (score >= 80) return 'rgba(0,212,170,0.12)';
      if (score >= 50) return 'rgba(255,217,61,0.12)';
      return 'rgba(255,107,107,0.12)';
    },
  };

  return { seed, Auth, Jobs, Applications, Messages, Notifications, Stats, Recommendations, Users, Helpers };
})();

// Seed on load
DB.seed();
