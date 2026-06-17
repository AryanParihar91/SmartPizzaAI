import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Home() {
  const { user } = useAuth();

  const getDashboardPath = () => {
    if (!user) {
      return "/login";
    }

    if (user.role === "ADMIN") {
      return "/admin/dashboard";
    }

    if (user.role === "DELIVERY") {
      return "/track";
    }

    return "/menu";
  };

  return (
    <div className="home-page">
      <section className="home-hero">
        <div className="home-hero-card">
          <h1>SmartPizzaAI</h1>

          <p>
            Order delicious pizzas, get AI-powered recommendations, track
            delivery in real time, and enjoy secure payments — all from one
            smart platform.
          </p>

          <div className="home-actions">
            <Link className="btn btn-primary-theme" to={getDashboardPath()}>
              {user ? "Go to Dashboard" : "Get Started"}
            </Link>

            {!user && (
              <Link className="home-secondary-btn" to="/register">
                Create Account
              </Link>
            )}

            {user?.role === "CUSTOMER" && (
              <Link className="home-secondary-btn" to="/recommendations">
                Try AI Combos
              </Link>
            )}
          </div>
        </div>
      </section>

      <section className="home-stats">
        <div className="row g-4">
          <div className="col-md-4">
            <div className="home-stat-card">
              <h3>AI</h3>
              <p>Smart pizza recommendations</p>
            </div>
          </div>

          <div className="col-md-4">
            <div className="home-stat-card">
              <h3>JWT</h3>
              <p>Secure role-based access</p>
            </div>
          </div>

          <div className="col-md-4">
            <div className="home-stat-card">
              <h3>Live</h3>
              <p>Delivery tracking with ETA</p>
            </div>
          </div>
        </div>
      </section>

      <section className="features-section">
        <div className="row g-4">
          <div className="col-md-6 col-lg-3">
            <div className="feature-card">
              <div className="feature-icon">🍕</div>
              <h4>Pizza Menu</h4>
              <p>
                Browse pizzas with category, price, availability, and
                preparation time.
              </p>
            </div>
          </div>

          <div className="col-md-6 col-lg-3">
            <div className="feature-card">
              <div className="feature-icon">🤖</div>
              <h4>AI Combos</h4>
              <p>
                Get smart personalized recommendations and combo suggestions.
              </p>
            </div>
          </div>

          <div className="col-md-6 col-lg-3">
            <div className="feature-card">
              <div className="feature-icon">💳</div>
              <h4>Payments</h4>
              <p>
                Pay using UPI, card, or COD with invoice generation and GST.
              </p>
            </div>
          </div>

          <div className="col-md-6 col-lg-3">
            <div className="feature-card">
              <div className="feature-icon">🚚</div>
              <h4>Delivery</h4>
              <p>
                City-based delivery assignment with status updates and ETA.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default Home;