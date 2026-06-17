import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig";

function Orders() {
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);

  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchOrders = async () => {
    setLoading(true);
    setServerError("");
    setSuccessMessage("");

    try {
      const response = await axiosInstance.get("/api/orders/my-orders");

      setOrders(response.data || []);
      setSuccessMessage("Orders loaded successfully.");
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to load your orders."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const getOrderStatusClass = (status) => {
    if (status === "DELIVERED") {
      return "order-status-badge status-delivered";
    }

    if (status === "CANCELLED") {
      return "order-status-badge status-cancelled";
    }

    return "order-status-badge status-active";
  };

  const formatAmount = (amount) => {
    if (amount === null || amount === undefined) {
      return "₹0.00";
    }

    return `₹${Number(amount).toFixed(2)}`;
  };

  const calculateGst = (amount) => {
    if (amount === null || amount === undefined) {
      return 0;
    }

    return Number(amount) * 0.05;
  };

  const calculatePaidAmount = (amount) => {
    if (amount === null || amount === undefined) {
      return 0;
    }

    return Number(amount) + calculateGst(amount);
  };

  return (
    <div className="page-container">
      <div className="orders-page">
        <div className="orders-hero">
          <h1>📦 My Orders</h1>
          <p>
            View your pizza orders, order status, items, and delivery details.
          </p>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        <div className="d-flex justify-content-end mb-3">
          <button
            className="btn btn-primary-theme"
            onClick={fetchOrders}
            disabled={loading}
          >
            {loading ? "Refreshing..." : "Refresh Orders"}
          </button>
        </div>

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-danger"></div>
            <p className="mt-3">Loading your orders...</p>
          </div>
        ) : orders.length === 0 ? (
          <div className="orders-empty">
            <div>🍕</div>
            <h4>No orders yet</h4>
            <p>Place your first pizza order from the menu.</p>

            <button
              className="btn btn-primary-theme mt-2"
              onClick={() => navigate("/menu")}
            >
              Go to Menu
            </button>
          </div>
        ) : (
          <>
            {orders.map((order) => (
              <div className="order-card" key={order.orderId}>
                <div className="order-card-header">
                  <div>
                    <h4>Order #{order.orderId}</h4>

                    <p className="text-muted-custom mb-0">
                      {order.orderDate
                        ? new Date(order.orderDate).toLocaleString()
                        : "Order date not available"}
                    </p>
                  </div>

                  <span className={getOrderStatusClass(order.orderStatus)}>
                    {order.orderStatus}
                  </span>
                </div>

                <div className="row">
                  <div className="col-md-6">
                    <div className="order-info-row">
                      <span>Subtotal</span>
                      <span>{formatAmount(order.subtotal)}</span>
                    </div>

                    <div className="order-info-row">
                      <span>Discount</span>
                      <span>{formatAmount(order.discountAmount)}</span>
                    </div>

                    <div className="order-info-row">
                      <span>Order Amount</span>
                      <span>{formatAmount(order.totalAmount)}</span>
                    </div>

                    <div className="order-info-row">
                      <span>GST 5%</span>
                      <span>{formatAmount(calculateGst(order.totalAmount))}</span>
                    </div>

                    <div className="order-info-row order-paid-row">
                      <span>Amount Paid</span>
                      <span>
                        {formatAmount(calculatePaidAmount(order.totalAmount))}
                      </span>
                    </div>

                    <div className="order-info-row">
                      <span>Coupon</span>
                      <span>{order.couponCode || "Not Applied"}</span>
                    </div>
                  </div>

                  <div className="col-md-6">
                    <div className="order-info-row">
                      <span>Delivery City</span>
                      <span>{order.deliveryCity || "N/A"}</span>
                    </div>

                    <div className="order-info-row">
                      <span>Mobile</span>
                      <span>{order.customerMobile || "N/A"}</span>
                    </div>

                    <div className="order-info-row">
                      <span>Address</span>
                      <span>{order.deliveryAddress || "N/A"}</span>
                    </div>
                  </div>
                </div>

                <div className="order-items-box">
                  <h6 className="fw-bold mb-3">Order Items</h6>

                  {order.orderItems && order.orderItems.length > 0 ? (
                    order.orderItems.map((item) => (
                      <div className="order-item-line" key={item.orderItemId}>
                        <span>
                          {item.pizzaName} × {item.quantity}
                        </span>
                        <span>{formatAmount(item.totalPrice)}</span>
                      </div>
                    ))
                  ) : (
                    <p className="text-muted-custom mb-0">No items found.</p>
                  )}
                </div>

                <div className="d-flex gap-2 mt-3 flex-wrap">
                  <button
                    className="btn btn-primary-theme"
                    onClick={() =>
                      navigate("/track", {
                        state: {
                          orderId: order.orderId,
                        },
                      })
                    }
                  >
                    Track Delivery
                  </button>

                  <button
                    className="btn btn-outline-secondary"
                    onClick={() =>
                      navigate(`/payment/${order.orderId}`, {
                        state: {
                          order: order,
                        },
                      })
                    }
                  >
                    Go to Payment
                  </button>
                </div>
              </div>
            ))}
          </>
        )}
      </div>
    </div>
  );
}

export default Orders;