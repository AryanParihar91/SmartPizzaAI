import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";

import axiosInstance from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

function AuthLanding({ defaultMode = "login" }) {
  const navigate = useNavigate();
  const location = useLocation();

  const { user, login } = useAuth();

  const [formKey, setFormKey] = useState(Date.now());
  const [mode, setMode] = useState(defaultMode);

  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    setMode(defaultMode);
    setServerError("");
    setSuccessMessage("");
  }, [defaultMode]);

  useEffect(() => {
    if (location.state?.resetAuthForm) {
      setFormKey(Date.now());
      setServerError("");
      setSuccessMessage("");
    }
  }, [location.state]);

  const redirectByRole = (role) => {
    if (role === "ADMIN") {
      navigate("/admin/dashboard");
    } else if (role === "DELIVERY") {
      navigate("/track");
    } else {
      navigate("/menu");
    }
  };

  const loginInitialValues = {
    email: "",
    password: "",
  };

  const loginValidationSchema = Yup.object({
    email: Yup.string()
      .email("Invalid email format")
      .required("Email is required"),

    password: Yup.string().required("Password is required"),
  });

  const handleLogin = async (values, { setSubmitting }) => {
    setServerError("");
    setSuccessMessage("");

    try {
      const response = await axiosInstance.post("/api/auth/login", values);

      login(response.data);
      redirectByRole(response.data.role);
    } catch (error) {
      let message = "Invalid credentials. Please check email and password.";

      if (error.response?.data?.message) {
        message = error.response.data.message;
      } else if (error.response?.data?.error) {
        message = error.response.data.error;
      }

      setServerError(message);
    } finally {
      setSubmitting(false);
    }
  };

  const registerInitialValues = {
    fullName: "",
    email: "",
    mobileNumber: "",
    password: "",
    role: "CUSTOMER",
  };

  const registerValidationSchema = Yup.object({
    fullName: Yup.string()
      .min(3, "Full name must be at least 3 characters")
      .required("Full name is required"),

    email: Yup.string()
      .email("Invalid email format")
      .required("Email is required"),

    mobileNumber: Yup.string()
      .matches(/^[0-9]{10}$/, "Mobile number must be 10 digits")
      .required("Mobile number is required"),

    password: Yup.string()
      .min(6, "Password must be at least 6 characters")
      .required("Password is required"),

    role: Yup.string()
      .oneOf(["CUSTOMER", "DELIVERY"], "Invalid role")
      .required("Role is required"),
  });

  const handleRegister = async (values, { setSubmitting, resetForm }) => {
    setServerError("");
    setSuccessMessage("");

    try {
      const requestBody = {
        fullName: values.fullName,
        email: values.email,
        mobileNumber: values.mobileNumber,
        password: values.password,
        role: values.role,
      };

      await axiosInstance.post("/api/auth/register", requestBody);

      setSuccessMessage("Registration successful. Please login.");
      resetForm();
      setFormKey(Date.now());
      setMode("login");
      navigate("/login");
    } catch (error) {
      let message = "Registration failed. Please try again.";

      if (error.response?.data?.message) {
        message = error.response.data.message;
      } else if (error.response?.data?.error) {
        message = error.response.data.error;
      }

      setServerError(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-landing-page">
      <div className="auth-landing-wrapper">
        <div className="auth-landing-shell">
          <section className="auth-hero-panel">
            <div className="auth-hero-content">
              <div className="auth-brand-pill">
                <span>🍕</span>
                <span>SmartPizzaAI</span>
              </div>

              <h1>Pizza ordering made smarter.</h1>

              <p>
                Browse pizzas, get AI-powered recommendations, make secure
                payments, and track delivery with smart partner assignment.
              </p>

              {user ? (
                <button
                  className="btn btn-light rounded-pill fw-bold px-4 py-2"
                  onClick={() => redirectByRole(user.role)}
                >
                  Go to Dashboard
                </button>
              ) : (
                <button
                  className="btn btn-light rounded-pill fw-bold px-4 py-2"
                  onClick={() => {
                    setMode("register");
                    setServerError("");
                    setSuccessMessage("");
                    navigate("/register");
                  }}
                >
                  Start Ordering
                </button>
              )}

              <div className="auth-feature-grid">
                <div className="auth-feature-card">
                  <div className="auth-feature-icon">🤖</div>
                  <h5>AI Picks</h5>
                  <span>Personalized pizza recommendations.</span>
                </div>

                <div className="auth-feature-card">
                  <div className="auth-feature-icon">🚚</div>
                  <h5>Smart Delivery</h5>
                  <span>Automatic delivery assignment with ETA.</span>
                </div>

                <div className="auth-feature-card">
                  <div className="auth-feature-icon">💳</div>
                  <h5>Payments</h5>
                  <span>UPI, Card, COD and invoice generation.</span>
                </div>

                <div className="auth-feature-card">
                  <div className="auth-feature-icon">🔐</div>
                  <h5>Secure Access</h5>
                  <span>JWT with customer/admin/delivery roles.</span>
                </div>
              </div>
            </div>
          </section>

          <section className="auth-panel">
            <div className="auth-panel-header">
              <div className="auth-panel-logo">🍕</div>

              <h2>{mode === "login" ? "Welcome Back" : "Create Account"}</h2>

              <p>
                {mode === "login"
                  ? "Login and continue your SmartPizzaAI journey."
                  : "Register as a customer or delivery partner."}
              </p>
            </div>

            <div className="auth-toggle">
              <button
                type="button"
                className={mode === "login" ? "active" : ""}
                onClick={() => {
                  setMode("login");
                  setServerError("");
                  setSuccessMessage("");
                  setFormKey(Date.now());
                  navigate("/login");
                }}
              >
                Login
              </button>

              <button
                type="button"
                className={mode === "register" ? "active" : ""}
                onClick={() => {
                  setMode("register");
                  setServerError("");
                  setSuccessMessage("");
                  setFormKey(Date.now());
                  navigate("/register");
                }}
              >
                Register
              </button>
            </div>

            {serverError && <div className="error-message">{serverError}</div>}

            {successMessage && (
              <div className="success-message">{successMessage}</div>
            )}

            {mode === "login" ? (
              <Formik
                key={`login-${formKey}`}
                initialValues={loginInitialValues}
                validationSchema={loginValidationSchema}
                onSubmit={handleLogin}
              >
                {({ isSubmitting }) => (
                  <Form>
                    <div className="mb-3">
                      <label className="form-label">Email</label>

                      <Field
                        type="email"
                        name="email"
                        className="form-control"
                        placeholder="Enter email"
                        autoComplete="off"
                      />

                      <ErrorMessage
                        name="email"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Password</label>

                      <Field
                        type="password"
                        name="password"
                        className="form-control"
                        placeholder="Enter password"
                        autoComplete="new-password"
                      />

                      <ErrorMessage
                        name="password"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <button
                      type="submit"
                      className="btn btn-primary-theme auth-submit-btn"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? "Logging in..." : "Login"}
                    </button>
                  </Form>
                )}
              </Formik>
            ) : (
              <Formik
                key={`register-${formKey}`}
                initialValues={registerInitialValues}
                validationSchema={registerValidationSchema}
                onSubmit={handleRegister}
              >
                {({ isSubmitting }) => (
                  <Form>
                    <div className="mb-3">
                      <label className="form-label">Full Name</label>

                      <Field
                        type="text"
                        name="fullName"
                        className="form-control"
                        placeholder="Enter full name"
                      />

                      <ErrorMessage
                        name="fullName"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Email</label>

                      <Field
                        type="email"
                        name="email"
                        className="form-control"
                        placeholder="Enter email"
                      />

                      <ErrorMessage
                        name="email"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Mobile Number</label>

                      <Field
                        type="text"
                        name="mobileNumber"
                        className="form-control"
                        placeholder="Enter phone number"
                      />

                      <ErrorMessage
                        name="mobileNumber"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Password</label>

                      <Field
                        type="password"
                        name="password"
                        className="form-control"
                        placeholder="Enter password"
                        autoComplete="new-password"
                      />

                      <ErrorMessage
                        name="password"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Role</label>

                      <Field as="select" name="role" className="form-select">
                        <option value="CUSTOMER">Customer</option>
                        <option value="DELIVERY">Delivery Partner</option>
                      </Field>

                      <ErrorMessage
                        name="role"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <button
                      type="submit"
                      className="btn btn-primary-theme auth-submit-btn"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? "Registering..." : "Register"}
                    </button>
                  </Form>
                )}
              </Formik>
            )}

            <div className="auth-small-link">
              {mode === "login" ? (
                <>
                  New here?{" "}
                  <button
                    type="button"
                    onClick={() => {
                      setMode("register");
                      setServerError("");
                      setSuccessMessage("");
                      setFormKey(Date.now());
                      navigate("/register");
                    }}
                  >
                    Create account
                  </button>
                </>
              ) : (
                <>
                  Already registered?{" "}
                  <button
                    type="button"
                    onClick={() => {
                      setMode("login");
                      setServerError("");
                      setSuccessMessage("");
                      setFormKey(Date.now());
                      navigate("/login");
                    }}
                  >
                    Login now
                  </button>
                </>
              )}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}

export default AuthLanding;