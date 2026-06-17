import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";

import Unauthorized from "./pages/Unauthorized";

import Menu from "./pages/Menu";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Payment from "./pages/Payment";
import Recommendations from "./pages/Recommendations";
import DeliveryTracking from "./pages/DeliveryTracking";
import AdminDashboard from "./pages/AdminDashboard";
import Orders from "./pages/Orders";
import AuthLanding from "./pages/AuthLanding";

function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <Routes>
        <Route path="/" element={<AuthLanding defaultMode="login" />} />
        <Route path="/login" element={<AuthLanding defaultMode="login" />} />
        <Route
          path="/register"
          element={<AuthLanding defaultMode="register" />}
        />

        <Route path="/unauthorized" element={<Unauthorized />} />

        <Route
          path="/menu"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER", "ADMIN", "DELIVERY"]}>
              <Menu />
            </ProtectedRoute>
          }
        />

        <Route
          path="/cart"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Cart />
            </ProtectedRoute>
          }
        />

        <Route
          path="/checkout"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Checkout />
            </ProtectedRoute>
          }
        />

        <Route
          path="/payment/:orderId"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Payment />
            </ProtectedRoute>
          }
        />

        <Route
          path="/recommendations"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Recommendations />
            </ProtectedRoute>
          }
        />

        <Route
          path="/track"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER", "ADMIN", "DELIVERY"]}>
              <DeliveryTracking />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute allowedRoles={["ADMIN"]}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/orders"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Orders />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
