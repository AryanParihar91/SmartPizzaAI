import { useEffect, useState } from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import { useAuth } from "../context/AuthContext";
import { toast } from "react-toastify";

import axiosInstance from "../api/axiosConfig";

function AdminDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [analytics, setAnalytics] = useState(null);

  const [orders, setOrders] = useState([]);
  const [partners, setPartners] = useState([]);
  const [pizzas, setPizzas] = useState([]);
  const [categories, setCategories] = useState([]);

  const [selectedPizza, setSelectedPizza] = useState(null);

  const [loading, setLoading] = useState(false);
  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const { user } = useAuth();

  const fetchAdminData = async () => {
    setLoading(true);
    setServerError("");
    setSuccessMessage("");

    try {
      const dashboardResponse = await axiosInstance.get("/api/admin/dashboard");
      const analyticsResponse = await axiosInstance.get("/api/admin/analytics");
      const ordersResponse = await axiosInstance.get("/api/admin/orders");
      const partnersResponse = await axiosInstance.get(
        "/api/delivery/partners",
      );
      const pizzasResponse = await axiosInstance.get("/api/menu/pizzas");
      const categoriesResponse = await axiosInstance.get(
        "/api/menu/categories",
      );

      setDashboard(dashboardResponse.data);
      setAnalytics(analyticsResponse.data);
      setOrders(ordersResponse.data || []);
      setPartners(partnersResponse.data || []);
      setPizzas(pizzasResponse.data || []);
      setCategories(categoriesResponse.data || []);

      setSuccessMessage("Admin dashboard loaded successfully.");
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to load admin dashboard.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAdminData();
  }, []);

  const formatAmount = (amount) => {
    if (amount === null || amount === undefined) {
      return "₹0.00";
    }

    return `₹${Number(amount).toFixed(2)}`;
  };

  const getOrderStatusClass = (status) => {
    if (status === "DELIVERED") {
      return "order-status-badge status-delivered";
    }

    if (status === "CANCELLED") {
      return "order-status-badge status-cancelled";
    }

    return "order-status-badge status-active";
  };

  const getMaxCategoryRevenue = () => {
    if (!analytics?.categoryRevenue || analytics.categoryRevenue.length === 0) {
      return 0;
    }

    let max = 0;

    for (let item of analytics.categoryRevenue) {
      if (item.revenue > max) {
        max = item.revenue;
      }
    }

    return max;
  };

  const handleEditClick = (pizza) => {
    setSelectedPizza(pizza);

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleCancelEdit = () => {
    setSelectedPizza(null);
  };

  const editPizzaValidationSchema = Yup.object({
    pizzaName: Yup.string()
      .min(3, "Pizza name must be at least 3 characters")
      .required("Pizza name is required"),

    description: Yup.string().required("Description is required"),

    price: Yup.number()
      .typeError("Price must be a number")
      .positive("Price must be greater than 0")
      .required("Price is required"),

    size: Yup.string().required("Size is required"),

    imageUrl: Yup.string(),

    available: Yup.string().required("Availability is required"),

    veg: Yup.string().required("Pizza type is required"),

    preparationTimeMinutes: Yup.number()
      .typeError("Preparation time must be a number")
      .positive("Preparation time must be greater than 0")
      .required("Preparation time is required"),

    categoryId: Yup.string().required("Category is required"),
  });

  const handleUpdatePizza = async (values, { setSubmitting }) => {
    setServerError("");
    setSuccessMessage("");

    if (!selectedPizza) {
      setServerError("No pizza selected for update.");
      setSubmitting(false);
      return;
    }

    try {
      const requestBody = {
        pizzaName: values.pizzaName,
        description: values.description,
        price: Number(values.price),
        size: values.size,
        imageUrl: values.imageUrl,
        available: values.available === "true",
        veg: values.veg === "true",
        preparationTimeMinutes: Number(values.preparationTimeMinutes),
        categoryId: Number(values.categoryId),
      };

      await axiosInstance.put(
        `/api/menu/pizzas/${selectedPizza.pizzaId}`,
        requestBody,
      );

      setSuccessMessage("Pizza updated successfully.");
      setSelectedPizza(null);
      fetchAdminData();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to update pizza.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeletePizza = async (pizzaId) => {
    setServerError("");
    setSuccessMessage("");

    const confirmDelete = window.confirm(
      "Are you sure you want to delete this pizza?",
    );

    if (!confirmDelete) {
      return;
    }

    try {
      await axiosInstance.delete(`/api/menu/pizzas/${pizzaId}`);

      setSuccessMessage("Pizza deleted successfully.");
      fetchAdminData();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to delete pizza.",
      );
    }
  };

  const maxCategoryRevenue = getMaxCategoryRevenue();

  return (
    <div className="page-container">
      <div className="admin-page">
        <div className="admin-hero role-hero">
          <div>
            <h1>Hello, {user?.fullName || "Admin"} 👋</h1>

            <p>
              Here is your restaurant performance overview, including orders,
              revenue, delivery activity, menu analytics, and pizza management.
            </p>
          </div>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        <div className="d-flex justify-content-end mb-3">
          <button
            className="btn btn-primary-theme"
            onClick={fetchAdminData}
            disabled={loading}
          >
            {loading ? "Refreshing..." : "Refresh Dashboard"}
          </button>
        </div>

        {selectedPizza && (
          <div className="admin-edit-panel">
            <h4>Edit Pizza</h4>

            <Formik
              enableReinitialize
              initialValues={{
                pizzaName: selectedPizza.pizzaName || "",
                description: selectedPizza.description || "",
                price: selectedPizza.price || "",
                size: selectedPizza.size || "MEDIUM",
                imageUrl: selectedPizza.imageUrl || "",
                available: String(selectedPizza.available),
                veg: String(selectedPizza.veg),
                preparationTimeMinutes:
                  selectedPizza.preparationTimeMinutes || "",
                categoryId: selectedPizza.categoryId || "",
              }}
              validationSchema={editPizzaValidationSchema}
              onSubmit={handleUpdatePizza}
            >
              {({ isSubmitting }) => (
                <Form>
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Pizza Name</label>
                      <Field
                        name="pizzaName"
                        className="form-control"
                        placeholder="Pizza name"
                      />
                      <ErrorMessage
                        name="pizzaName"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label className="form-label">Price</label>
                      <Field
                        name="price"
                        className="form-control"
                        placeholder="Price"
                      />
                      <ErrorMessage
                        name="price"
                        component="div"
                        className="field-error"
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Description</label>
                    <Field
                      as="textarea"
                      name="description"
                      className="form-control"
                      placeholder="Pizza description"
                    />
                    <ErrorMessage
                      name="description"
                      component="div"
                      className="field-error"
                    />
                  </div>

                  <div className="row">
                    <div className="col-md-4 mb-3">
                      <label className="form-label">Size</label>
                      <Field as="select" name="size" className="form-select">
                        <option value="SMALL">SMALL</option>
                        <option value="MEDIUM">MEDIUM</option>
                        <option value="LARGE">LARGE</option>
                      </Field>
                    </div>

                    <div className="col-md-4 mb-3">
                      <label className="form-label">Type</label>
                      <Field as="select" name="veg" className="form-select">
                        <option value="true">Veg</option>
                        <option value="false">Non-Veg</option>
                      </Field>
                    </div>

                    <div className="col-md-4 mb-3">
                      <label className="form-label">Available</label>
                      <Field
                        as="select"
                        name="available"
                        className="form-select"
                      >
                        <option value="true">Yes</option>
                        <option value="false">No</option>
                      </Field>
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Preparation Time</label>
                      <Field
                        name="preparationTimeMinutes"
                        className="form-control"
                        placeholder="20"
                      />
                      <ErrorMessage
                        name="preparationTimeMinutes"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label className="form-label">Category</label>
                      <Field
                        as="select"
                        name="categoryId"
                        className="form-select"
                      >
                        <option value="">Select category</option>
                        {categories.map((category) => (
                          <option
                            key={category.categoryId}
                            value={category.categoryId}
                          >
                            {category.categoryName}
                          </option>
                        ))}
                      </Field>
                      <ErrorMessage
                        name="categoryId"
                        component="div"
                        className="field-error"
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Image URL</label>
                    <Field
                      name="imageUrl"
                      className="form-control"
                      placeholder="https://example.com/pizza.jpg"
                    />
                  </div>

                  <div className="d-flex gap-2">
                    <button
                      type="submit"
                      className="btn btn-primary-theme"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? "Updating..." : "Update Pizza"}
                    </button>

                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={handleCancelEdit}
                    >
                      Cancel
                    </button>
                  </div>
                </Form>
              )}
            </Formik>
          </div>
        )}

        {loading && !dashboard ? (
          <div className="text-center py-5">
            <div className="spinner-border text-danger"></div>
            <p className="mt-3">Loading admin dashboard...</p>
          </div>
        ) : (
          <>
            <div className="row g-4 mb-4">
              <div className="col-md-4 col-lg-2">
                <div className="admin-stat-card">
                  <h5>Total Orders</h5>
                  <h2>{dashboard?.totalOrders || 0}</h2>
                </div>
              </div>

              <div className="col-md-4 col-lg-3">
                <div className="admin-stat-card">
                  <h5>Total Revenue</h5>
                  <h2>{formatAmount(dashboard?.totalRevenue)}</h2>
                </div>
              </div>

              <div className="col-md-4 col-lg-2">
                <div className="admin-stat-card">
                  <h5>Delivered</h5>
                  <h2>{dashboard?.deliveredOrders || 0}</h2>
                </div>
              </div>

              <div className="col-md-4 col-lg-2">
                <div className="admin-stat-card">
                  <h5>Cancelled</h5>
                  <h2>{dashboard?.cancelledOrders || 0}</h2>
                </div>
              </div>

              <div className="col-md-4 col-lg-3">
                <div className="admin-stat-card">
                  <h5>Active Orders</h5>
                  <h2>{dashboard?.activeOrders || 0}</h2>
                </div>
              </div>
            </div>

            <h4 className="analytics-section-title">Business Analytics</h4>

            <div className="analytics-grid">
              <div className="analytics-card">
                <h5>Total Items Sold</h5>
                <h2>{analytics?.totalItemsSold || 0}</h2>
              </div>

              <div className="analytics-card">
                <h5>Base Revenue</h5>
                <h2>{formatAmount(analytics?.baseRevenue)}</h2>
              </div>

              <div className="analytics-card">
                <h5>GST Collected</h5>
                <h2>{formatAmount(analytics?.gstCollected)}</h2>
              </div>

              <div className="analytics-card">
                <h5>Final Revenue</h5>
                <h2>{formatAmount(analytics?.finalRevenueWithGst)}</h2>
              </div>

              <div className="analytics-card">
                <h5>Average Order Value</h5>
                <h2>{formatAmount(analytics?.averageOrderValue)}</h2>
              </div>

              <div className="analytics-card">
                <h5>Active Deliveries</h5>
                <h2>{analytics?.activeDeliveryCount || 0}</h2>
              </div>
            </div>

            <div className="row g-4 mb-4">
              <div className="col-lg-7">
                <div className="admin-section-card">
                  <h4>Top 3 Most Sold Pizzas</h4>

                  {!analytics?.topSellingPizzas ||
                  analytics.topSellingPizzas.length === 0 ? (
                    <div className="admin-empty">
                      No sales data available yet.
                    </div>
                  ) : (
                    <div className="row g-3">
                      {analytics.topSellingPizzas.map((pizza, index) => (
                        <div className="col-md-4" key={pizza.pizzaId}>
                          <div className="top-pizza-card">
                            <div className="top-pizza-rank">#{index + 1}</div>

                            <h5>{pizza.pizzaName}</h5>

                            <div className="analytics-mini-row">
                              <span>Sold</span>
                              <strong>{pizza.quantitySold}</strong>
                            </div>

                            <div className="analytics-mini-row">
                              <span>Revenue</span>
                              <strong>{formatAmount(pizza.revenue)}</strong>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              <div className="col-lg-5">
                <div className="admin-section-card">
                  <h4>Category-wise Revenue</h4>

                  {!analytics?.categoryRevenue ||
                  analytics.categoryRevenue.length === 0 ? (
                    <div className="admin-empty">
                      No category revenue available.
                    </div>
                  ) : (
                    <>
                      {analytics.categoryRevenue.map((category) => {
                        const percentage =
                          maxCategoryRevenue > 0
                            ? (category.revenue / maxCategoryRevenue) * 100
                            : 0;

                        return (
                          <div
                            className="category-revenue-card"
                            key={category.categoryName}
                          >
                            <h6>{category.categoryName}</h6>

                            <div className="analytics-mini-row mb-0">
                              <span>Revenue</span>
                              <strong>{formatAmount(category.revenue)}</strong>
                            </div>

                            <div className="category-revenue-bar">
                              <div
                                className="category-revenue-fill"
                                style={{ width: `${percentage}%` }}
                              ></div>
                            </div>
                          </div>
                        );
                      })}
                    </>
                  )}
                </div>
              </div>
            </div>

            <div className="admin-section-card mb-4">
              <h4>Order Status Breakdown</h4>

              {!analytics?.orderStatusCounts ? (
                <div className="admin-empty">No status data available.</div>
              ) : (
                <div className="status-breakdown-grid">
                  {Object.entries(analytics.orderStatusCounts).map(
                    ([status, count]) => (
                      <div className="status-breakdown-card" key={status}>
                        <h6>{status}</h6>
                        <h3>{count}</h3>
                      </div>
                    ),
                  )}
                </div>
              )}
            </div>

            <div className="row g-4 mb-4">
              <div className="col-lg-8">
                <div className="admin-section-card">
                  <h4>All Orders</h4>

                  {orders.length === 0 ? (
                    <div className="admin-empty">No orders available yet.</div>
                  ) : (
                    <div className="table-responsive">
                      <table className="admin-table">
                        <thead>
                          <tr>
                            <th>Order ID</th>
                            <th>User ID</th>
                            <th>City</th>
                            <th>Amount</th>
                            <th>Status</th>
                          </tr>
                        </thead>

                        <tbody>
                          {orders.map((order) => (
                            <tr key={order.orderId}>
                              <td>#{order.orderId}</td>
                              <td>{order.userId}</td>
                              <td>{order.deliveryCity || "N/A"}</td>
                              <td>{formatAmount(order.totalAmount)}</td>
                              <td>
                                <span
                                  className={getOrderStatusClass(
                                    order.orderStatus,
                                  )}
                                >
                                  {order.orderStatus}
                                </span>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </div>

              <div className="col-lg-4">
                <div className="admin-section-card">
                  <h4>Delivery Partners</h4>

                  {partners.length === 0 ? (
                    <div className="admin-empty">
                      No delivery partners available.
                    </div>
                  ) : (
                    <div className="table-responsive">
                      <table className="admin-table">
                        <thead>
                          <tr>
                            <th>Name</th>
                            <th>City</th>
                            <th>Status</th>
                          </tr>
                        </thead>

                        <tbody>
                          {partners.map((partner) => (
                            <tr key={partner.partnerId}>
                              <td>{partner.partnerName}</td>
                              <td>{partner.city}</td>
                              <td>
                                <span
                                  className={
                                    partner.available
                                      ? "partner-available"
                                      : "partner-unavailable"
                                  }
                                >
                                  {partner.available ? "Available" : "Busy"}
                                </span>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="admin-section-card">
              <h4>Pizza Management</h4>

              {pizzas.length === 0 ? (
                <div className="admin-empty">No pizzas available.</div>
              ) : (
                <div className="row">
                  {pizzas.map((pizza) => (
                    <div className="col-md-6 col-lg-4" key={pizza.pizzaId}>
                      <div className="admin-pizza-card">
                        <h5>{pizza.pizzaName}</h5>

                        <p className="text-muted-custom small mb-2">
                          {pizza.description}
                        </p>

                        <div className="admin-pizza-meta">
                          <span className="admin-pizza-badge">
                            ₹{pizza.price}
                          </span>

                          <span className="admin-pizza-badge">
                            {pizza.veg ? "Veg" : "Non-Veg"}
                          </span>

                          <span className="admin-pizza-badge">
                            {pizza.available ? "Available" : "Unavailable"}
                          </span>

                          <span className="admin-pizza-badge">
                            {pizza.size}
                          </span>
                        </div>

                        <p className="small text-muted-custom">
                          Category: {pizza.categoryName || "N/A"} | Prep:{" "}
                          {pizza.preparationTimeMinutes} min
                        </p>

                        <div className="d-flex gap-2">
                          <button
                            className="btn btn-sm btn-primary-theme"
                            onClick={() => handleEditClick(pizza)}
                          >
                            Edit
                          </button>

                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleDeletePizza(pizza.pizzaId)}
                          >
                            Delete
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;
