import { Link } from "react-router-dom";

function Unauthorized() {
  return (
    <div className="page-container">
      <div className="auth-card text-center">
        <h2>403 - Unauthorized</h2>
        <p>You do not have permission to access this page.</p>
        <Link className="btn btn-italy-green" to="/login">
          Go to Login
        </Link>
      </div>
    </div>
  );
}

export default Unauthorized;