import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

import { AuthProvider } from "./context/AuthContext";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "leaflet/dist/leaflet.css";

import "./index.css";
import "./styles/theme.css";
import "./styles/navbar.css";
import "./styles/auth.css";
import "./styles/common.css";
import "./styles/menu.css";
import "./styles/cart.css";
import "./styles/checkout.css";
import "./styles/payment.css";
import "./styles/delivery.css";
import "./styles/recommendations.css";
import "./styles/admin.css";
import "./styles/orders.css";
import "./styles/home.css";
import "./styles/authLanding.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </React.StrictMode>,
);
