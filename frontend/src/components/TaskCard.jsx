import './TaskCard.css'

const NEXT_STATUS = {
  TODO: 'IN_PROGRESS',
  IN_PROGRESS: 'DONE',
  DONE: null,
}

const STATUS_LABELS = {
  TODO: '▶ Start',
  IN_PROGRESS: '✅ Complete',
  DONE: null,
}

const TaskCard = ({ task, onEdit, onDelete, onStatusChange }) => {
  const nextStatus = NEXT_STATUS[task.status]
  const nextLabel = STATUS_LABELS[task.status]

  const formatDate = (dateStr) => {
    if (!dateStr) return null
    const date = new Date(dateStr)
    const today = new Date()
    const isOverdue = date < today && task.status !== 'DONE'
    return { text: date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }), overdue: isOverdue }
  }

  const due = formatDate(task.dueDate)

  return (
    <div className="task-card">
      <div className="task-card-top">
        <div className="task-badges">
          <span className={`badge badge-${task.priority?.toLowerCase()}`}>
            {task.priority}
          </span>
        </div>
        <div className="task-actions">
          <button className="icon-btn" onClick={onEdit} title="Edit">✏️</button>
          <button className="icon-btn danger" onClick={onDelete} title="Delete">🗑️</button>
        </div>
      </div>

      <h4 className="task-title">{task.title}</h4>

      {task.description && (
        <p className="task-desc">{task.description}</p>
      )}

      <div className="task-meta">
        {task.assignedToName && (
          <span className="task-meta-item">
            👤 {task.assignedToName}
          </span>
        )}
        {due && (
          <span className={`task-meta-item ${due.overdue ? 'overdue' : ''}`}>
            📅 {due.text}{due.overdue ? ' (Overdue)' : ''}
          </span>
        )}
      </div>

      {nextStatus && (
        <button
          className="task-advance-btn"
          onClick={() => onStatusChange(task.id, nextStatus)}
        >
          {nextLabel}
        </button>
      )}
    </div>
  )
}

export default TaskCard
