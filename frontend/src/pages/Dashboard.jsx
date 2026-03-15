import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'
import TaskModal from '../components/TaskModal'
import TaskCard from '../components/TaskCard'
import './Dashboard.css'

const COLUMNS = [
  { key: 'TODO', label: '📋 To Do', color: '#a0a0b0' },
  { key: 'IN_PROGRESS', label: '🔄 In Progress', color: '#fbbf24' },
  { key: 'DONE', label: '✅ Done', color: '#4ade80' },
]

const Dashboard = () => {
  const { user, logout } = useAuth()
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editTask, setEditTask] = useState(null)
  const [users, setUsers] = useState([])
  const [filterPriority, setFilterPriority] = useState('ALL')

  const fetchTasks = async () => {
    try {
      const res = await api.get('/tasks')
      setTasks(res.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const fetchUsers = async () => {
    try {
      const res = await api.get('/users')
      // Ensure we always set an array
      setUsers(Array.isArray(res.data) ? res.data : [])
    } catch (err) {
      console.error(err)
      setUsers([])
    }
  }

  useEffect(() => {
    fetchTasks()
    fetchUsers()
  }, [])

  const handleStatusChange = async (taskId, newStatus) => {
    try {
      await api.patch(`/tasks/${taskId}/status`, { status: newStatus })
      setTasks(prev => prev.map(t => t.id === taskId ? { ...t, status: newStatus } : t))
    } catch (err) {
      console.error(err)
    }
  }

  const handleDelete = async (taskId) => {
    if (!confirm('Delete this task?')) return
    try {
      await api.delete(`/tasks/${taskId}`)
      setTasks(prev => prev.filter(t => t.id !== taskId))
    } catch (err) {
      console.error(err)
    }
  }

  const handleSave = async (taskData) => {
    try {
      if (editTask) {
        const res = await api.put(`/tasks/${editTask.id}`, taskData)
        setTasks(prev => prev.map(t => t.id === editTask.id ? res.data : t))
      } else {
        const res = await api.post('/tasks', taskData)
        setTasks(prev => [...prev, res.data])
      }
      setShowModal(false)
      setEditTask(null)
    } catch (err) {
      console.error(err)
    }
  }

  const openEdit = (task) => {
    setEditTask(task)
    setShowModal(true)
  }

  const openCreate = () => {
    setEditTask(null)
    setShowModal(true)
  }

  const filteredTasks = filterPriority === 'ALL'
    ? tasks
    : tasks.filter(t => t.priority === filterPriority)

  const getTasksByStatus = (status) => filteredTasks.filter(t => t.status === status)

  const stats = {
    total: tasks.length,
    todo: tasks.filter(t => t.status === 'TODO').length,
    inProgress: tasks.filter(t => t.status === 'IN_PROGRESS').length,
    done: tasks.filter(t => t.status === 'DONE').length,
  }

  return (
    <div className="dashboard">
      {/* Navbar */}
      <nav className="dash-nav">
        <div className="dash-nav-left">
          <span className="dash-logo">✅ TaskFlow</span>
        </div>
        <div className="dash-nav-right">
          <span className="dash-user">👤 {user?.name}</span>
          <button className="btn btn-outline btn-sm" onClick={logout}>Logout</button>
        </div>
      </nav>

      <div className="dash-content">
        {/* Header */}
        <div className="dash-header">
          <div>
            <h1 className="dash-title">Task Board</h1>
            <p className="dash-subtitle">Manage and track your tasks</p>
          </div>
          <button className="btn btn-primary" onClick={openCreate}>
            + New Task
          </button>
        </div>

        {/* Stats */}
        <div className="stats-row">
          <div className="stat-box">
            <span className="stat-num">{stats.total}</span>
            <span className="stat-label">Total Tasks</span>
          </div>
          <div className="stat-box">
            <span className="stat-num" style={{ color: 'var(--text-muted)' }}>{stats.todo}</span>
            <span className="stat-label">To Do</span>
          </div>
          <div className="stat-box">
            <span className="stat-num" style={{ color: 'var(--warning)' }}>{stats.inProgress}</span>
            <span className="stat-label">In Progress</span>
          </div>
          <div className="stat-box">
            <span className="stat-num" style={{ color: 'var(--success)' }}>{stats.done}</span>
            <span className="stat-label">Done</span>
          </div>
        </div>

        {/* Filter */}
        <div className="filter-bar">
          <span className="filter-label">Filter by priority:</span>
          {['ALL', 'HIGH', 'MEDIUM', 'LOW'].map(p => (
            <button
              key={p}
              className={`filter-pill ${filterPriority === p ? 'active' : ''}`}
              onClick={() => setFilterPriority(p)}
            >
              {p}
            </button>
          ))}
        </div>

        {/* Kanban Board */}
        {loading ? (
          <div className="loading">Loading tasks...</div>
        ) : (
          <div className="kanban-board">
            {COLUMNS.map(col => (
              <div className="kanban-col" key={col.key}>
                <div className="kanban-col-header" style={{ borderColor: col.color }}>
                  <span>{col.label}</span>
                  <span className="col-count" style={{ background: col.color + '22', color: col.color }}>
                    {getTasksByStatus(col.key).length}
                  </span>
                </div>
                <div className="kanban-cards">
                  {getTasksByStatus(col.key).length === 0 ? (
                    <div className="empty-col">No tasks here</div>
                  ) : (
                    getTasksByStatus(col.key).map(task => (
                      <TaskCard
                        key={task.id}
                        task={task}
                        onEdit={() => openEdit(task)}
                        onDelete={() => handleDelete(task.id)}
                        onStatusChange={handleStatusChange}
                      />
                    ))
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {showModal && (
        <TaskModal
          task={editTask}
          users={users}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditTask(null) }}
        />
      )}
    </div>
  )
}

export default Dashboard
