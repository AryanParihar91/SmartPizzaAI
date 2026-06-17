import { useEffect, useState } from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";

import axiosInstance from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

function Menu() {
  const { user } = useAuth();

  const [pizzas, setPizzas] = useState([]);
  const [categories, setCategories] = useState([]);
  const [cartItems, setCartItems] = useState([]);
  const [recommendedPizzas, setRecommendedPizzas] = useState([]);

  const [loading, setLoading] = useState(false);
  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const isAdmin = user?.role === "ADMIN";
  const isCustomer = user?.role === "CUSTOMER";

  const fetchMenuData = async () => {
    setLoading(true);
    setServerError("");

    try {
      const pizzaResponse = await axiosInstance.get("/api/menu/pizzas");
      const categoryResponse = await axiosInstance.get("/api/menu/categories");

      setPizzas(pizzaResponse.data || []);
      setCategories(categoryResponse.data || []);
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to load menu data.",
      );
    } finally {
      setLoading(false);
    }
  };

  const fetchCart = async () => {
    if (user?.role !== "CUSTOMER") {
      return;
    }

    try {
      const response = await axiosInstance.get("/api/cart/my-cart");
      setCartItems(response.data || []);
    } catch (error) {
      console.log("Cart not loaded", error);
    }
  };

  const fetchRecommendations = async () => {
    if (user?.role !== "CUSTOMER") {
      return;
    }

    try {
      const response = await axiosInstance.get(
        "/api/recommendations/user/top-pizzas",
      );

      setRecommendedPizzas(response.data || []);
    } catch (error) {
      console.log("Recommendations not loaded", error);
    }
  };

  useEffect(() => {
    fetchMenuData();

    if (user?.role === "CUSTOMER") {
      fetchCart();
      fetchRecommendations();
    }
  }, [user?.role]);

  const categoryInitialValues = {
    categoryName: "",
    description: "",
  };

  const categoryValidationSchema = Yup.object({
    categoryName: Yup.string()
      .min(3, "Category name must be at least 3 characters")
      .required("Category name is required"),

    description: Yup.string(),
  });

  const handleCreateCategory = async (values, { setSubmitting, resetForm }) => {
    setServerError("");
    setSuccessMessage("");

    try {
      await axiosInstance.post("/api/menu/categories", values);

      setSuccessMessage("Category created successfully.");
      resetForm();
      fetchMenuData();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to create category.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  const pizzaInitialValues = {
    pizzaName: "",
    description: "",
    price: "",
    size: "MEDIUM",
    imageUrl: "",
    available: "true",
    veg: "true",
    preparationTimeMinutes: "",
    categoryId: "",
  };

  const pizzaValidationSchema = Yup.object({
    pizzaName: Yup.string()
      .min(3, "Pizza name must be at least 3 characters")
      .required("Pizza name is required"),

    description: Yup.string().required("Description is required"),

    price: Yup.number()
      .typeError("Price must be a number")
      .positive("Price must be greater than 0")
      .required("Price is required"),

    size: Yup.string().required("Size is required"),

    available: Yup.string().required("Availability is required"),

    veg: Yup.string().required("Veg type is required"),

    preparationTimeMinutes: Yup.number()
      .typeError("Preparation time must be a number")
      .positive("Preparation time must be greater than 0")
      .required("Preparation time is required"),

    categoryId: Yup.string().required("Category is required"),
  });

  const handleCreatePizza = async (values, { setSubmitting, resetForm }) => {
    setServerError("");
    setSuccessMessage("");

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

      await axiosInstance.post("/api/menu/pizzas", requestBody);

      setSuccessMessage("Pizza created successfully.");
      resetForm();
      fetchMenuData();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to create pizza.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  const getCartItemByPizzaId = (pizzaId) => {
    return cartItems.find((item) => item.pizzaId === pizzaId);
  };

  const getPizzaQuantity = (pizzaId) => {
    const cartItem = getCartItemByPizzaId(pizzaId);
    return cartItem ? cartItem.quantity : 0;
  };

  const handleIncrementPizza = async (pizzaId) => {
    setServerError("");
    setSuccessMessage("");

    try {
      const cartItem = getCartItemByPizzaId(pizzaId);

      if (!cartItem) {
        await axiosInstance.post("/api/cart/add", {
          pizzaId: pizzaId,
          quantity: 1,
        });
      } else {
        await axiosInstance.put(
          `/api/cart/update/${cartItem.cartItemId}?quantity=${
            cartItem.quantity + 1
          }`,
        );
      }

      setSuccessMessage("Cart updated.");
      fetchCart();
    } catch (error) {
      setServerError(error.response?.data?.message || "Failed to update cart.");
    }
  };

  const handleDecrementPizza = async (pizzaId) => {
    setServerError("");
    setSuccessMessage("");

    try {
      const cartItem = getCartItemByPizzaId(pizzaId);

      if (!cartItem) {
        return;
      }

      if (cartItem.quantity <= 1) {
        await axiosInstance.delete(`/api/cart/remove/${cartItem.cartItemId}`);
      } else {
        await axiosInstance.put(
          `/api/cart/update/${cartItem.cartItemId}?quantity=${
            cartItem.quantity - 1
          }`,
        );
      }

      setSuccessMessage("Cart updated.");
      fetchCart();
    } catch (error) {
      setServerError(error.response?.data?.message || "Failed to update cart.");
    }
  };

  const renderPizzaCard = (pizza) => {
    return (
      <div className="pizza-card" key={pizza.pizzaId}>
        <div className="pizza-card-img">
          {pizza.imageUrl ? (
            <img
              src={pizza.imageUrl}
              alt={pizza.pizzaName}
              className="pizza-image"
            />
          ) : (
            <span>🍕</span>
          )}
        </div>

        <div className="card-body">
          <h5 className="card-title">{pizza.pizzaName}</h5>

          <p className="pizza-description">{pizza.description}</p>

          <div className="pizza-meta">
            <span className="price-badge">₹{pizza.price}</span>

            <span className={pizza.veg ? "veg-badge" : "nonveg-badge"}>
              {pizza.veg ? "Veg" : "Non-Veg"}
            </span>
          </div>

          <div className="pizza-meta">
            <span
              className={
                pizza.available ? "available-badge" : "unavailable-badge"
              }
            >
              {pizza.available ? "Available" : "Unavailable"}
            </span>

            <span className="text-muted-custom small">
              {pizza.preparationTimeMinutes} min
            </span>
          </div>

          <p className="pizza-category-line">
            Category: {pizza.categoryName || "N/A"} | Size:{" "}
            {pizza.size || "N/A"}
          </p>

          <div className="pizza-card-actions">
            {isCustomer && (
              <>
                {getPizzaQuantity(pizza.pizzaId) === 0 ? (
                  <button
                    className="btn btn-primary-theme w-100 add-cart-btn"
                    disabled={!pizza.available}
                    onClick={() => handleIncrementPizza(pizza.pizzaId)}
                  >
                    Add to Cart
                  </button>
                ) : (
                  <div className="qty-control">
                    <button
                      type="button"
                      className="qty-btn"
                      onClick={() => handleDecrementPizza(pizza.pizzaId)}
                    >
                      -
                    </button>

                    <span className="qty-count">
                      {getPizzaQuantity(pizza.pizzaId)}
                    </span>

                    <button
                      type="button"
                      className="qty-btn"
                      onClick={() => handleIncrementPizza(pizza.pizzaId)}
                    >
                      +
                    </button>
                  </div>
                )}
              </>
            )}

            {isAdmin && (
              <button className="btn btn-outline-secondary w-100" disabled>
                Admin View
              </button>
            )}

            {!isAdmin && !isCustomer && (
              <button className="btn btn-outline-secondary w-100" disabled>
                View Only
              </button>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="page-container">
      <div className="menu-page">
        <div className="menu-hero role-hero">
          <div>
            <h1>Hello, {user?.fullName || "Customer"} 👋</h1>

            <p>
              Ready to order something delicious? Explore pizzas and get AI
              recommendations based on your previous orders.
            </p>
          </div>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        {isAdmin && (
          <div className="admin-panel">
            <h4>Admin Menu Management</h4>

            <div className="row g-4">
              <div className="col-md-5">
                <div className="smart-card p-3 h-100">
                  <h5 className="mb-3">Add Category</h5>

                  <Formik
                    initialValues={categoryInitialValues}
                    validationSchema={categoryValidationSchema}
                    onSubmit={handleCreateCategory}
                  >
                    {({ isSubmitting }) => (
                      <Form>
                        <div className="mb-3">
                          <label className="form-label">Category Name</label>
                          <Field
                            name="categoryName"
                            className="form-control"
                            placeholder="Example: Veg Pizza"
                          />
                          <ErrorMessage
                            name="categoryName"
                            component="div"
                            className="field-error"
                          />
                        </div>

                        <div className="mb-3">
                          <label className="form-label">Description</label>
                          <Field
                            as="textarea"
                            name="description"
                            className="form-control"
                            placeholder="Category description"
                          />
                          <ErrorMessage
                            name="description"
                            component="div"
                            className="field-error"
                          />
                        </div>

                        <button
                          type="submit"
                          className="btn btn-primary-theme w-100"
                          disabled={isSubmitting}
                        >
                          {isSubmitting ? "Saving..." : "Add Category"}
                        </button>
                      </Form>
                    )}
                  </Formik>
                </div>
              </div>

              <div className="col-md-7">
                <div className="smart-card p-3 h-100">
                  <h5 className="mb-3">Add Pizza</h5>

                  <Formik
                    initialValues={pizzaInitialValues}
                    validationSchema={pizzaValidationSchema}
                    onSubmit={handleCreatePizza}
                  >
                    {({ isSubmitting }) => (
                      <Form>
                        <div className="row">
                          <div className="col-md-6 mb-3">
                            <label className="form-label">Pizza Name</label>
                            <Field
                              name="pizzaName"
                              className="form-control"
                              placeholder="Farmhouse Pizza"
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
                              placeholder="349"
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
                            <Field
                              as="select"
                              name="size"
                              className="form-select"
                            >
                              <option value="SMALL">SMALL</option>
                              <option value="MEDIUM">MEDIUM</option>
                              <option value="LARGE">LARGE</option>
                            </Field>
                            <ErrorMessage
                              name="size"
                              component="div"
                              className="field-error"
                            />
                          </div>

                          <div className="col-md-4 mb-3">
                            <label className="form-label">Type</label>
                            <Field
                              as="select"
                              name="veg"
                              className="form-select"
                            >
                              <option value="true">Veg</option>
                              <option value="false">Non-Veg</option>
                            </Field>
                            <ErrorMessage
                              name="veg"
                              component="div"
                              className="field-error"
                            />
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
                            <ErrorMessage
                              name="available"
                              component="div"
                              className="field-error"
                            />
                          </div>
                        </div>

                        <div className="row">
                          <div className="col-md-6 mb-3">
                            <label className="form-label">
                              Preparation Time
                            </label>
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
                          <ErrorMessage
                            name="imageUrl"
                            component="div"
                            className="field-error"
                          />
                        </div>

                        <button
                          type="submit"
                          className="btn btn-primary-theme w-100"
                          disabled={isSubmitting}
                        >
                          {isSubmitting ? "Saving..." : "Add Pizza"}
                        </button>
                      </Form>
                    )}
                  </Formik>
                </div>
              </div>
            </div>
          </div>
        )}

        {isCustomer && recommendedPizzas.length > 0 && (
          <div className="ai-recommendation-section">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <div>
                <h2 className="section-title mb-1">AI Recommendations</h2>
                <p className="ai-recommendation-subtitle">
                  Based on your previous orders
                </p>
              </div>

              <button
                className="btn btn-outline-secondary"
                onClick={fetchRecommendations}
              >
                Refresh
              </button>
            </div>

            <div className="pizza-grid">
              {recommendedPizzas.map((pizza) => renderPizzaCard(pizza))}
            </div>
          </div>
        )}

        <div className="d-flex justify-content-between align-items-center mb-3">
          <h2 className="section-title mb-0">Available Pizzas</h2>

          <button className="btn btn-outline-secondary" onClick={fetchMenuData}>
            Refresh
          </button>
        </div>

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-danger"></div>
            <p className="mt-3">Loading pizzas...</p>
          </div>
        ) : pizzas.length === 0 ? (
          <div className="menu-empty">
            <h4>No pizzas found</h4>
            <p>Admin can add pizzas from the menu management panel.</p>
          </div>
        ) : (
          <div className="pizza-grid">
            {pizzas.map((pizza) => renderPizzaCard(pizza))}
          </div>
        )}
      </div>
    </div>
  );
}

export default Menu;
