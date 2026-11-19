import { useEffect, useState } from "react";
import { getDashboard } from "../../services/dashboardService";
import Navbar from "../../components/Navbar";

function Dashboard() {

    const [dashboard,
        setDashboard] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {

        loadDashboard();

    }, []);

    const loadDashboard = async () => {

        try {

            const response =
                await getDashboard();

            setDashboard(response);
            setError("");

        } catch (error) {

            setError(error.response?.data?.message || "Unable to load dashboard. Check your account permissions and API connection.");
        }
    };

    if (!dashboard) {

        return (
            <>
                <Navbar />
                <main className="page-container">
                    <h2>{error ? "Dashboard unavailable" : "Loading dashboard…"}</h2>
                    {error && <p role="alert">{error}</p>}
                </main>
            </>
        );
    }

    return (

    <>
        <Navbar />

        <div className="page-container">

            <h1 className="page-title">
                TechSupport 360 Dashboard
            </h1>

            <div className="dashboard-grid">

                <div className="stat-card">
                    <h2>
                        {dashboard.totalTickets}
                    </h2>

                    <p>
                        Total Tickets
                    </p>
                </div>

                <div className="stat-card">
                    <h2>
                        {dashboard.openTickets}
                    </h2>

                    <p>
                        Open Tickets
                    </p>
                </div>

                <div className="stat-card">
                    <h2>
                        {dashboard.closedTickets}
                    </h2>

                    <p>
                        Closed Tickets
                    </p>
                </div>

                <div className="stat-card">
                    <h2>{dashboard.resolvedTickets ?? 0}</h2>
                    <p>Resolved Tickets</p>
                </div>

                <div className="stat-card stat-card-alert">
                    <h2>{dashboard.slaBreachedTickets ?? 0}</h2>
                    <p>SLA Breached</p>
                </div>

            </div>

        </div>

    </>

);
}

export default Dashboard;