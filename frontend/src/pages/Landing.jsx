import { Link } from 'react-router-dom'
import './Landing.css'

const features = [
  {
    icon: '📋',
    title: 'Kanban Board',
    desc: 'Visualize your workflow with a clean To Do → In Progress → Done board',
  },
  {
    icon: '🔐',
    title: 'Secure Authentication',
    desc: 'JWT-based auth with Spring Security — your data stays private and protected',
  },
  {
    icon: '🎯',
    title: 'Priority Management',
    desc: 'Tag tasks as High, Medium, or Low priority and filter your board instantly',
  },
  {
    icon: '👥',
    title: 'Team Collaboration',
    desc: 'Assign tasks to team members and track who is working on what',
  },
  {
    icon: '📊',
    title: 'Live Dashboard Stats',
    desc: 'See real-time counts of total, in-progress, done, and overdue tasks',
  },
  {
    icon: '🗓️',
    title: 'Due Date Tracking',
    desc: 'Set deadlines on tasks — overdue items are highlighted automatically',
  },
]

const stack = [
  { name: 'Java 17', color: '#f89820' },
  { name: 'Spring Boot', color: '#6db33f' },
  { name: 'Spring Security', color: '#6db33f' },
  { name: 'JWT', color: '#a855f7' },
  { name: 'Hibernate / JPA', color: '#59666c' },
  { name: 'MySQL', color: '#4479a1' },
  { name: 'React 18', color: '#61dafb' },
  { name: 'Axios', color: '#5a29e4' },
  { name: 'REST API', color: '#ff6584' },
]

const Landing = () => {
  return (
    <div className="landing">
      {/* Navbar */}
      <nav className="landing-nav">
        <div className="landing-nav-inner">
          <span className="landing-logo">✅ TaskFlow</span>
          <div className="landing-nav-links">
            <Link to="/login" className="btn-nav-outline">Sign In</Link>
            <Link to="/register" className="btn-nav-primary">Get Started →</Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="hero-section">
        <div className="hero-blob blob-a"></div>
        <div className="hero-blob blob-b"></div>
        <div className="hero-inner">
          <div className="hero-badge">🚀 Full Stack Java + React App</div>
          <h1 className="hero-heading">
            Manage Tasks Like a<br />
            <span className="gradient-text">Pro Developer</span>
          </h1>
          <p className="hero-sub">
            A production-ready Task Management app built with <strong>Java Spring Boot</strong> backend and <strong>React</strong> frontend.
            Features JWT auth, Kanban boards, priority tracking, and real-time stats.
          </p>
          <div className="hero-ctas">
            <Link to="/register" className="cta-primary">Get Started Free →</Link>
            <Link to="/login" className="cta-secondary">Sign In</Link>
          </div>

          {/* Mock preview card */}
          <div className="hero-preview">
            <div className="preview-bar">
              <span className="dot red"></span>
              <span className="dot yellow"></span>
              <span className="dot green"></span>
              <span className="preview-title">TaskFlow — Task Board</span>
            </div>
            <div className="preview-board">
              <div className="preview-col">
                <div className="preview-col-header" style={{ borderColor: '#a0a0b0' }}>📋 To Do <span className="preview-count">3</span></div>
                <div className="preview-card">
                  <span className="preview-badge high">HIGH</span>
                  <div className="preview-card-title">Design REST API</div>
                </div>
                <div className="preview-card">
                  <span className="preview-badge medium">MEDIUM</span>
                  <div className="preview-card-title">Write unit tests</div>
                </div>
              </div>
              <div className="preview-col">
                <div className="preview-col-header" style={{ borderColor: '#fbbf24' }}>🔄 In Progress <span className="preview-count">2</span></div>
                <div className="preview-card">
                  <span className="preview-badge high">HIGH</span>
                  <div className="preview-card-title">Spring Boot setup</div>
                </div>
                <div className="preview-card">
                  <span className="preview-badge medium">MEDIUM</span>
                  <div className="preview-card-title">JWT Auth flow</div>
                </div>
              </div>
              <div className="preview-col">
                <div className="preview-col-header" style={{ borderColor: '#4ade80' }}>✅ Done <span className="preview-count">4</span></div>
                <div className="preview-card done">
                  <span className="preview-badge low">LOW</span>
                  <div className="preview-card-title">Database schema</div>
                </div>
                <div className="preview-card done">
                  <span className="preview-badge medium">MEDIUM</span>
                  <div className="preview-card-title">React frontend</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="features-section">
        <h2 className="section-heading">Everything you need to <span className="gradient-text">ship faster</span></h2>
        <p className="section-sub">Built with enterprise patterns used in real Java backend jobs</p>
        <div className="features-grid">
          {features.map((f) => (
            <div className="feature-card" key={f.title}>
              <div className="feature-icon">{f.icon}</div>
              <h3>{f.title}</h3>
              <p>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Tech Stack */}
      <section className="stack-section">
        <h2 className="section-heading">Built with <span className="gradient-text">enterprise tech</span></h2>
        <p className="section-sub">The same stack used at top Java backend companies</p>
        <div className="stack-badges">
          {stack.map((t) => (
            <span
              className="stack-badge"
              key={t.name}
              style={{ borderColor: t.color + '55', color: t.color, background: t.color + '11' }}
            >
              {t.name}
            </span>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="cta-section">
        <h2>Ready to take control of your tasks?</h2>
        <p>Create a free account and start managing your projects today.</p>
        <Link to="/register" className="cta-primary">Get Started for Free →</Link>
      </section>

      {/* Footer */}
      <footer className="landing-footer">
        <span>✅ TaskFlow — Built with Java Spring Boot + React</span>
      </footer>
    </div>
  )
}

export default Landing
