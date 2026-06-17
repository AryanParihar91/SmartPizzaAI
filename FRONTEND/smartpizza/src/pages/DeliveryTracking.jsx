import { useState } from "react";
import { useLocation } from "react-router-dom";

import axiosInstance from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";
import DeliveryMap from "../components/DeliveryMap";

function DeliveryTracking() {
  const { user } = useAuth();
  const location = useLocation();

  const [orderId, setOrderId] = useState(location.state?.orderId || "");
  const [tracking, setTracking] = useState(null);

  const [loading, setLoading] = useState(false);
  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const canUpdateStatus = user?.role === "DELIVERY" || user?.role === "ADMIN";

  const deliveryStatuses = [
    "NOT_ASSIGNED",
    "ASSIGNED",
    "PICKED_UP",
    "ON_THE_WAY",
    "NEAR_YOU",
    "DELIVERED",
  ];

  const getStatusClass = (status) => {
    if (status === "DELIVERED") {
      return "delivery-status-badge delivery-status-delivered";
    }

    if (
      status === "ASSIGNED" ||
      status === "PICKED_UP" ||
      status === "ON_THE_WAY" ||
      status === "NEAR_YOU"
    ) {
      return "delivery-status-badge delivery-status-progress";
    }

    return "delivery-status-badge delivery-status-pending";
  };

  const getTimelineDotClass = (status) => {
    if (!tracking) {
      return "timeline-dot";
    }

    const currentIndex = deliveryStatuses.indexOf(tracking.deliveryStatus);
    const itemIndex = deliveryStatuses.indexOf(status);

    if (itemIndex < currentIndex) {
      return "timeline-dot done";
    }

    if (itemIndex === currentIndex) {
      return "timeline-dot active";
    }

    return "timeline-dot";
  };

  const handleTrackOrder = async () => {
    setServerError("");
    setSuccessMessage("");
    setTracking(null);
    setLoading(true);

    try {
      let response;

      if (user?.role === "CUSTOMER") {
        response = await axiosInstance.get("/api/delivery/track/recent");
      } else if (user?.role === "DELIVERY") {
        response = await axiosInstance.get("/api/delivery/my-assigned");
      } else {
        if (!orderId) {
          setServerError("Please enter order ID.");
          setLoading(false);
          return;
        }

        response = await axiosInstance.get(`/api/delivery/track/${orderId}`);
      }

      setTracking(response.data);

      if (response.data?.orderId) {
        setOrderId(String(response.data.orderId));
      }
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to fetch tracking details.",
      );
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (nextStatus) => {
    setServerError("");
    setSuccessMessage("");

    if (!tracking?.orderId) {
      setServerError("Please load an assigned delivery first.");
      return;
    }

    setLoading(true);

    try {
      const response = await axiosInstance.put(
        `/api/delivery/status/${tracking.orderId}?status=${nextStatus}`,
      );

      setTracking(response.data);
      setSuccessMessage(`Delivery status updated to ${nextStatus}.`);
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to update delivery status.",
      );
    } finally {
      setLoading(false);
    }
  };

  const renderUpdateButtons = () => {
    if (!tracking) {
      return (
        <div className="payment-note">
          Load an assigned delivery first to update delivery status.
        </div>
      );
    }

    if (tracking.deliveryStatus === "NOT_ASSIGNED") {
      return (
        <div className="payment-note">
          This order is not assigned yet. New orders should auto-assign.
        </div>
      );
    }

    if (tracking.deliveryStatus === "ASSIGNED") {
      return (
        <button
          className="btn btn-primary-theme"
          onClick={() => handleUpdateStatus("PICKED_UP")}
          disabled={loading}
        >
          Picked Up
        </button>
      );
    }

    if (tracking.deliveryStatus === "PICKED_UP") {
      return (
        <button
          className="btn btn-primary-theme"
          onClick={() => handleUpdateStatus("ON_THE_WAY")}
          disabled={loading}
        >
          On The Way
        </button>
      );
    }

    if (tracking.deliveryStatus === "ON_THE_WAY") {
      return (
        <button
          className="btn btn-success"
          onClick={() => handleUpdateStatus("DELIVERED")}
          disabled={loading}
        >
          Delivered
        </button>
      );
    }

    if (tracking.deliveryStatus === "NEAR_YOU") {
      return (
        <button
          className="btn btn-success"
          onClick={() => handleUpdateStatus("DELIVERED")}
          disabled={loading}
        >
          Delivered
        </button>
      );
    }

    if (tracking.deliveryStatus === "DELIVERED") {
      return (
        <div className="success-message mb-0">
          This order has already been delivered.
        </div>
      );
    }

    return null;
  };

  const getTrackButtonText = () => {
    if (loading) {
      return "Loading...";
    }

    if (user?.role === "CUSTOMER") {
      return "Track My Latest Order";
    }

    if (user?.role === "DELIVERY") {
      return "Load My Assigned Delivery";
    }

    return "Track Order";
  };

  const getEmptyMessage = () => {
    if (user?.role === "CUSTOMER") {
      return "Click Track My Latest Order to view your active delivery.";
    }

    if (user?.role === "DELIVERY") {
      return "Click Load My Assigned Delivery to view your assigned order.";
    }

    return "Enter an order ID and click Track Order.";
  };

  return (
    <div className="page-container">
      <div className="delivery-page">
        <div className="delivery-hero role-hero">
          <div>

            <h1>Hello, {user?.fullName || "Delivery Partner"} 👋</h1>

            <p>
              {user?.role === "DELIVERY"
                ? "Load your assigned delivery and update the order progress step by step."
                : "Track delivery status, ETA, and assigned delivery partner details."}
            </p>
          </div>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        <div className="row g-4">
          <div className="col-lg-5">
            <div className="delivery-card">
              <h4>Track Order</h4>

              {user?.role === "ADMIN" && (
                <div className="mb-3">
                  <label className="form-label">Order ID</label>
                  <input
                    type="number"
                    className="form-control"
                    placeholder="Example: 1"
                    value={orderId}
                    onChange={(event) => setOrderId(event.target.value)}
                  />
                </div>
              )}

              {user?.role === "CUSTOMER" && (
                <div className="payment-note">
                  Customer tracking uses your latest active undelivered order.
                </div>
              )}

              {user?.role === "DELIVERY" && (
                <div className="payment-note">
                  Delivery partner tracking loads your currently assigned
                  delivery automatically.
                </div>
              )}

              <button
                className="btn btn-primary-theme w-100 mb-3"
                onClick={handleTrackOrder}
                disabled={loading}
              >
                {getTrackButtonText()}
              </button>

              {canUpdateStatus && (
                <div className="mt-4">
                  <h4>Update Delivery Status</h4>

                  <div className="d-grid gap-2">{renderUpdateButtons()}</div>

                  <small className="text-muted-custom d-block mt-2">
                    Only the next valid delivery action is shown.
                  </small>
                </div>
              )}

              {!canUpdateStatus && (
                <div className="payment-note mt-4">
                  Customers can only track delivery status. Updating delivery is
                  allowed for Delivery/Admin users.
                </div>
              )}
            </div>
          </div>

          <div className="col-lg-7">
            <div className="tracking-card">
              <h4>Tracking Details</h4>

              {!tracking ? (
                <div className="payment-empty-summary">{getEmptyMessage()}</div>
              ) : (
                <>
                  <div className="tracking-row">
                    <span>Tracking ID</span>
                    <span>{tracking.trackingId}</span>
                  </div>

                  <div className="tracking-row">
                    <span>Order ID</span>
                    <span>{tracking.orderId}</span>
                  </div>

                  <div className="tracking-row">
                    <span>Status</span>
                    <span className={getStatusClass(tracking.deliveryStatus)}>
                      {tracking.deliveryStatus}
                    </span>
                  </div>

                  <div className="tracking-row">
                    <span>Partner ID</span>
                    <span>{tracking.deliveryPartnerId || "Not Assigned"}</span>
                  </div>

                  <div className="tracking-row">
                    <span>Partner Name</span>
                    <span>
                      {tracking.deliveryPartnerName &&
                      tracking.deliveryPartnerName !== "Not Assigned"
                        ? tracking.deliveryPartnerName
                        : "Not Assigned"}
                    </span>
                  </div>

                  <div className="tracking-row">
                    <span>Partner Mobile</span>
                    <span>
                      {tracking.deliveryPartnerMobile &&
                      tracking.deliveryPartnerMobile !== "Not Assigned"
                        ? tracking.deliveryPartnerMobile
                        : "Not Assigned"}
                    </span>
                  </div>

                  <div className="eta-box">
                    <h2>{tracking.etaMinutes} min</h2>
                    <p className="mb-0">Estimated delivery time</p>
                  </div>
                  <DeliveryMap tracking={tracking} />

                  <ul className="delivery-timeline">
                    {deliveryStatuses.map((status) => (
                      <li className="delivery-timeline-item" key={status}>
                        <span className={getTimelineDotClass(status)}></span>
                        <span>{status}</span>
                      </li>
                    ))}
                  </ul>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DeliveryTracking;
