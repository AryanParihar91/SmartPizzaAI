import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";

import axiosInstance from "../api/axiosConfig";

function Checkout() {
  const navigate = useNavigate();

  const [cartItems, setCartItems] = useState([]);
  const [cartLoading, setCartLoading] = useState(false);

  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchCart = async () => {
    setCartLoading(true);
    setServerError("");

    try {
      const response = await axiosInstance.get("/api/cart/my-cart");
      setCartItems(response.data || []);
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to load cart details."
      );
    } finally {
      setCartLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  const calculateSubtotal = () => {
    let subtotal = 0;

    for (let item of cartItems) {
      subtotal = subtotal + item.totalPrice;
    }

    return subtotal;
  };

  const initialValues = {
    couponCode: "",
    deliveryAddress: "",
    customerMobile: "",
  };

  const validationSchema = Yup.object({
    couponCode: Yup.string(),

    deliveryAddress: Yup.string()
      .min(5, "Delivery address must be at least 5 characters")
      .required("Delivery address is required"),

    customerMobile: Yup.string()
      .matches(/^[0-9]{10}$/, "Mobile number must be 10 digits")
      .required("Customer mobile number is required"),
  });

  const handlePlaceOrder = async (values, { setSubmitting }) => {
    setServerError("");
    setSuccessMessage("");

    if (cartItems.length === 0) {
      setServerError("Your cart is empty. Please add pizzas before checkout.");
      setSubmitting(false);
      return;
    }

    try {
      const requestBody = {
        couponCode: values.couponCode,
        deliveryAddress: values.deliveryAddress,
        customerMobile: values.customerMobile,
      };

      const response = await axiosInstance.post(
        "/api/orders/place",
        requestBody
      );

      setSuccessMessage("Order placed successfully.");

      const orderId = response.data.orderId;

      setTimeout(() => {
        navigate(`/payment/${orderId}`, {
          state: {
            order: response.data,
          },
        });
      }, 700);
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to place order."
      );
    } finally {
      setSubmitting(false);
    }
  };

  const subtotal = calculateSubtotal();

  return (
    <div className="page-container">
      <div className="checkout-page">
        <div className="checkout-hero">
          <h1>📦 Checkout</h1>
          <p>Confirm your delivery details and place your pizza order.</p>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        {cartLoading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-danger"></div>
            <p className="mt-3">Loading checkout...</p>
          </div>
        ) : (
          <div className="row g-4">
            <div className="col-lg-8">
              <div className="checkout-card">
                <h4>Delivery Details</h4>

                <Formik
                  initialValues={initialValues}
                  validationSchema={validationSchema}
                  onSubmit={handlePlaceOrder}
                >
                  {({ isSubmitting }) => (
                    <Form>
                      <div className="mb-3">
                        <label className="form-label">Delivery Address</label>
                        <Field
                          as="textarea"
                          name="deliveryAddress"
                          className="form-control"
                          placeholder="Example: Electronic City Phase 1"
                          rows="3"
                        />
                        <ErrorMessage
                          name="deliveryAddress"
                          component="div"
                          className="field-error"
                        />
                      </div>

                      <div className="mb-3">
                        <label className="form-label">
                          Customer Mobile Number
                        </label>
                        <Field
                          type="text"
                          name="customerMobile"
                          className="form-control"
                          placeholder="9876543210"
                        />
                        <ErrorMessage
                          name="customerMobile"
                          component="div"
                          className="field-error"
                        />
                      </div>

                      <div className="mb-3">
                        <label className="form-label">Coupon Code</label>
                        <Field
                          type="text"
                          name="couponCode"
                          className="form-control"
                          placeholder="Optional: SAVE10"
                        />
                        <ErrorMessage
                          name="couponCode"
                          component="div"
                          className="field-error"
                        />
                        <small className="text-muted-custom">
                          If you have a coupon, enter it here.
                        </small>
                      </div>

                      <button
                        type="submit"
                        className="btn btn-primary-theme w-100"
                        disabled={isSubmitting || cartItems.length === 0}
                      >
                        {isSubmitting ? "Placing Order..." : "Place Order"}
                      </button>
                    </Form>
                  )}
                </Formik>
              </div>
            </div>

            <div className="col-lg-4">
              <div className="checkout-summary">
                <h4>Cart Summary</h4>

                {cartItems.length === 0 ? (
                  <div>
                    <p className="text-muted-custom">Your cart is empty.</p>
                    <button
                      className="btn btn-primary-theme w-100"
                      onClick={() => navigate("/menu")}
                    >
                      Go to Menu
                    </button>
                  </div>
                ) : (
                  <>
                    <div className="checkout-row">
                      <span>Items</span>
                      <span>{cartItems.length}</span>
                    </div>

                    {cartItems.map((item) => (
                      <div
                        className="checkout-row small"
                        key={item.cartItemId}
                      >
                        <span>
                          {item.pizzaName} × {item.quantity}
                        </span>
                        <span>₹{item.totalPrice}</span>
                      </div>
                    ))}

                    <div className="checkout-row checkout-total">
                      <span>Subtotal</span>
                      <span>₹{subtotal}</span>
                    </div>

                    <button
                      className="btn btn-outline-secondary w-100 mt-3"
                      onClick={() => navigate("/cart")}
                    >
                      Back to Cart
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Checkout;