import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();

    navigate("/login", {
      replace: true,
      state: {
        resetAuthForm: Date.now(),
      },
    });
  };

  const getLogoPath = () => {
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
    <nav className="navbar navbar-expand-lg smart-navbar px-3">
      <div className="container-fluid">
        <Link
          className="navbar-brand d-flex align-items-center gap-2"
          to={getLogoPath()}
        >
          <span>🍕</span>
          <span>SmartPizzaAI</span>
        </Link>

        <button
          className="navbar-toggler bg-light"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#smartNavbar"
          aria-controls="smartNavbar"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="smartNavbar">
          <ul className="navbar-nav ms-auto align-items-lg-center gap-lg-2">
            {!user && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/login">
                    Login
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/register">
                    Register
                  </Link>
                </li>
              </>
            )}

            {user?.role === "CUSTOMER" && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/menu">
                    Menu
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/cart">
                    Cart
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/orders">
                    My Orders
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/track">
                    Track
                  </Link>
                </li>
              </>
            )}

            {user?.role === "ADMIN" && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/admin/dashboard">
                    Dashboard
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/menu">
                    Manage Menu
                  </Link>
                </li>
              </>
            )}

            {user?.role === "DELIVERY" && (
              <li className="nav-item">
                <Link className="nav-link" to="/track">
                  Deliveries
                </Link>
              </li>
            )}

            {user && (
              <>
                <li className="nav-item">
                  <span className="user-badge">
                    {user.fullName} · {user.role}
                  </span>
                </li>

                <li className="nav-item">
                  <button
                    className="btn btn-sm btn-italy-red px-3 py-2"
                    type="button"
                    onClick={handleLogout}
                  >
                    Logout
                  </button>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
