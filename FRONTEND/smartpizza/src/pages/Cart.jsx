import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosConfig";

function Cart() {
  const navigate = useNavigate();

  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);

  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchCart = async () => {
    setLoading(true);
    setServerError("");

    try {
      const response = await axiosInstance.get("/api/cart/my-cart");
      setCartItems(response.data || []);
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to load cart items."
      );
    } finally {
      setLoading(false);
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

  const handleIncrement = async (item) => {
    setServerError("");
    setSuccessMessage("");

    try {
      await axiosInstance.put(
        `/api/cart/update/${item.cartItemId}?quantity=${item.quantity + 1}`
      );

      setSuccessMessage("Cart updated.");
      fetchCart();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to update cart item."
      );
    }
  };

  const handleDecrement = async (item) => {
    setServerError("");
    setSuccessMessage("");

    try {
      if (item.quantity <= 1) {
        await axiosInstance.delete(`/api/cart/remove/${item.cartItemId}`);
      } else {
        await axiosInstance.put(
          `/api/cart/update/${item.cartItemId}?quantity=${item.quantity - 1}`
        );
      }

      setSuccessMessage("Cart updated.");
      fetchCart();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to update cart item."
      );
    }
  };

  const handleRemoveItem = async (cartItemId) => {
    setServerError("");
    setSuccessMessage("");

    try {
      await axiosInstance.delete(`/api/cart/remove/${cartItemId}`);

      setSuccessMessage("Item removed from cart.");
      fetchCart();
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Failed to remove cart item."
      );
    }
  };

  const handleClearCart = async () => {
    setServerError("");
    setSuccessMessage("");

    try {
      await axiosInstance.delete("/api/cart/clear");

      setSuccessMessage("Cart cleared successfully.");
      fetchCart();
    } catch (error) {
      setServerError(error.response?.data?.message || "Failed to clear cart.");
    }
  };

  const handleCheckout = () => {
    if (cartItems.length === 0) {
      setServerError("Cart is empty. Please add pizzas before checkout.");
      return;
    }

    navigate("/checkout");
  };

  const subtotal = calculateSubtotal();

  return (
    <div className="page-container">
      <div className="cart-page">
        <div className="cart-hero">
          <h1>🛒 Your Cart</h1>
          <p>Review your selected pizzas before placing the order.</p>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-danger"></div>
            <p className="mt-3">Loading cart...</p>
          </div>
        ) : cartItems.length === 0 ? (
          <div className="empty-cart">
            <div style={{ fontSize: "54px" }}>🍕</div>
            <h4>Your cart is empty</h4>
            <p>Add some delicious pizzas from the menu.</p>

            <button
              className="btn btn-primary-theme mt-2"
              onClick={() => navigate("/menu")}
            >
              Go to Menu
            </button>
          </div>
        ) : (
          <div className="row g-4">
            <div className="col-lg-8">
              {cartItems.map((item) => (
                <div className="cart-item-card" key={item.cartItemId}>
                  <div className="d-flex align-items-center gap-3">
                    <div className="cart-pizza-icon">🍕</div>

                    <div className="flex-grow-1">
                      <h5 className="mb-1 fw-bold">{item.pizzaName}</h5>

                      <p className="text-muted-custom mb-1">
                        Price: ₹{item.price} × {item.quantity}
                      </p>

                      <span className="price-badge">
                        Total: ₹{item.totalPrice}
                      </span>
                    </div>

                    <div className="cart-actions">
                      <div className="cart-qty-control">
                        <button
                          type="button"
                          className="cart-qty-btn"
                          onClick={() => handleDecrement(item)}
                        >
                          -
                        </button>

                        <span className="cart-qty-count">
                          {item.quantity}
                        </span>

                        <button
                          type="button"
                          className="cart-qty-btn"
                          onClick={() => handleIncrement(item)}
                        >
                          +
                        </button>
                      </div>

                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => handleRemoveItem(item.cartItemId)}
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                </div>
              ))}

              <button
                className="btn btn-outline-danger mt-2"
                onClick={handleClearCart}
              >
                Clear Cart
              </button>
            </div>

            <div className="col-lg-4">
              <div className="cart-summary">
                <h4>Order Summary</h4>

                <div className="summary-row">
                  <span>Items</span>
                  <span>{cartItems.length}</span>
                </div>

                <div className="summary-row">
                  <span>Subtotal</span>
                  <span>₹{subtotal}</span>
                </div>

                <div className="summary-row">
                  <span>Coupon</span>
                  <span>Apply at checkout</span>
                </div>

                <div className="summary-row summary-total">
                  <span>Total</span>
                  <span>₹{subtotal}</span>
                </div>

                <button
                  className="btn btn-primary-theme w-100 mt-3"
                  onClick={handleCheckout}
                >
                  Proceed to Checkout
                </button>

                <button
                  className="btn btn-outline-secondary w-100 mt-2"
                  onClick={() => navigate("/menu")}
                >
                  Add More Pizzas
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Cart;