import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import { getMyTickets, getTicketHistory, updateTicketStatus } from "../../services/ticketService";

function MyTickets() {
  const role = localStorage.getItem("role");
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [history, setHistory] = useState({});
  const [historyOpen, setHistoryOpen] = useState({});

  const loadTickets = async () => {
    try {
      setError("");
      const response = await getMyTickets();
      setTickets(response);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to load tickets. Please sign in again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadTickets(); }, []);

  const toggleHistory = async (ticketId) => {
    const opening = !historyOpen[ticketId];
    setHistoryOpen((current) => ({ ...current, [ticketId]: opening }));
    if (opening && !history[ticketId]) {
      try {
        const events = await getTicketHistory(ticketId);
        setHistory((current) => ({ ...current, [ticketId]: events }));
      } catch (err) {
        setError(err.response?.data?.message || "Unable to load ticket activity.");
      }
    }
  };

  const handleStatusUpdate = async (ticketId, status) => {
    try {
      await updateTicketStatus(ticketId, status);
      await loadTickets();
    } catch (err) {
      setError(err.response?.data?.message || "Could not update ticket status.");
    }
  };

  return (
    <>
      <Navbar />
      <main className="page-container">
        <h1 className="page-title">{role === "SUPPORT_AGENT" ? "Assigned Support Tickets" : "My Support Requests"}</h1>
        {error && <p role="alert" className="ticket-card">{error}</p>}
        {loading && <p>Loading tickets…</p>}
        {!loading && tickets.length === 0 && <div className="form-card"><p>No tickets to display yet.</p></div>}
        {tickets.map((ticket) => (
          <article key={ticket.id} className="ticket-card">
            <h3>#{ticket.id} · {ticket.title}</h3>
            <p><strong>Description:</strong> {ticket.description}</p>
            <p><strong>Category:</strong> {ticket.category || "—"}</p>
            <p><strong>Priority:</strong> {ticket.priority}</p>
            <p><strong>Assigned to:</strong> {ticket.assignedTo || "Not assigned"}</p>
            <p><strong>Status:</strong> {ticket.status}</p>
            {ticket.dueAt && <p><strong>SLA due:</strong> {new Date(ticket.dueAt).toLocaleString()}</p>}
            <button type="button" className="secondary-btn" onClick={() => toggleHistory(ticket.id)}>
              {historyOpen[ticket.id] ? "Hide activity" : "View activity"}
            </button>
            {historyOpen[ticket.id] && (
              <div className="ticket-activity">
                {(history[ticket.id] || []).length === 0 ? <p>No activity recorded yet.</p> : history[ticket.id].map((event) => (
                  <p key={event.id}><strong>{event.action}</strong> — {event.details} · {event.changedBy} · {new Date(event.createdAt).toLocaleString()}</p>
                ))}
              </div>
            )}
            {role === "SUPPORT_AGENT" && ticket.status !== "CLOSED" && ticket.status !== "RESOLVED" && (
              <label>
                Update status
                <select value={ticket.status} onChange={(event) => handleStatusUpdate(ticket.id, event.target.value)}>
                  <option value={ticket.status}>{ticket.status.replaceAll("_", " ")}</option>
                  <option value="IN_PROGRESS">In progress</option>
                  <option value="RESOLVED">Resolved</option>
                </select>
              </label>
            )}
          </article>
        ))}
      </main>
    </>
  );
}

export default MyTickets;
