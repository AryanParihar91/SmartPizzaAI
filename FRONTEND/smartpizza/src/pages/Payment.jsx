import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useState } from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";

import axiosInstance from "../api/axiosConfig";

function Payment() {
  const { orderId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const order = location.state?.order;

  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [paymentResponse, setPaymentResponse] = useState(null);
  const [invoice, setInvoice] = useState(null);

  const calculateGst = (amount) => {
    if (!amount) {
      return 0;
    }

    return Number(amount) * 0.05;
  };

  const calculatePayableAmount = (amount) => {
    if (!amount) {
      return 0;
    }

    return Number(amount) + calculateGst(amount);
  };

  const initialValues = {
    amount: order?.totalAmount || "",
    paymentMode: "UPI",
    upiId: "",
    cardNumber: "",
    cardHolderName: "",
    simulateFailure: false,
  };

  const validationSchema = Yup.object({
    amount: Yup.number()
      .typeError("Amount must be a number")
      .positive("Amount must be greater than 0")
      .required("Amount is required"),

    paymentMode: Yup.string()
      .oneOf(["UPI", "CARD", "COD"], "Invalid payment mode")
      .required("Payment mode is required"),

    upiId: Yup.string().when("paymentMode", {
      is: "UPI",
      then: (schema) => schema.required("UPI ID is required"),
      otherwise: (schema) => schema.notRequired(),
    }),

    cardNumber: Yup.string().when("paymentMode", {
      is: "CARD",
      then: (schema) =>
        schema
          .matches(/^[0-9]{12,16}$/, "Card number must be 12 to 16 digits")
          .required("Card number is required"),
      otherwise: (schema) => schema.notRequired(),
    }),

    cardHolderName: Yup.string().when("paymentMode", {
      is: "CARD",
      then: (schema) => schema.required("Card holder name is required"),
      otherwise: (schema) => schema.notRequired(),
    }),
  });

  const handlePayment = async (values, { setSubmitting }) => {
    setServerError("");
    setSuccessMessage("");
    setPaymentResponse(null);
    setInvoice(null);

    try {
      /*
        IMPORTANT:
        We send only order amount to backend.
        Backend will calculate GST and total amount.
        Do not send GST-inclusive amount, otherwise GST will be added twice.
      */
      const requestBody = {
        orderId: Number(orderId),
        amount: Number(values.amount),
        paymentMode: values.paymentMode,
        simulateFailure: values.simulateFailure,
      };

      if (values.paymentMode === "UPI") {
        requestBody.upiId = values.upiId;
      }

      if (values.paymentMode === "CARD") {
        requestBody.cardNumber = values.cardNumber;
        requestBody.cardHolderName = values.cardHolderName;
      }

      const response = await axiosInstance.post(
        "/api/payments/pay",
        requestBody
      );

      setPaymentResponse(response.data);

      if (response.data.paymentStatus === "FAILED") {
        setServerError("Payment failed. Please retry with another attempt.");
        return;
      }

      setSuccessMessage("Payment processed successfully.");

      try {
        const invoiceResponse = await axiosInstance.get(
          `/api/invoices/order/${orderId}`
        );

        setInvoice(invoiceResponse.data);
      } catch (invoiceError) {
        setServerError(
          invoiceError.response?.data?.message ||
            "Payment done, but invoice could not be loaded."
        );
      }
    } catch (error) {
      setServerError(
        error.response?.data?.message || "Payment failed. Please try again."
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page-container">
      <div className="payment-page">
        <div className="payment-hero">
          <h1>💳 Payment</h1>
          <p>Complete your payment using UPI, Card, or Cash on Delivery.</p>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        <div className="row g-4">
          <div className="col-lg-7">
            <div className="payment-card">
              <h4>Payment Details</h4>

              <Formik
                initialValues={initialValues}
                validationSchema={validationSchema}
                onSubmit={handlePayment}
                enableReinitialize
              >
                {({ values, isSubmitting }) => (
                  <Form>
                    <div className="mb-3">
                      <label className="form-label">Order ID</label>
                      <input
                        type="text"
                        className="form-control"
                        value={orderId}
                        disabled
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Order Amount</label>
                      <Field
                        type="text"
                        name="amount"
                        className="form-control"
                        placeholder="Enter amount"
                        readOnly={!!order}
                      />
                      <ErrorMessage
                        name="amount"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    <div className="payment-note mt-3 mb-3">
                      <div className="d-flex justify-content-between mb-2">
                        <span>Order Amount</span>
                        <strong>₹{Number(values.amount || 0).toFixed(2)}</strong>
                      </div>

                      <div className="d-flex justify-content-between mb-2">
                        <span>GST 5%</span>
                        <strong>
                          ₹{calculateGst(values.amount).toFixed(2)}
                        </strong>
                      </div>

                      <hr />

                      <div className="d-flex justify-content-between">
                        <span>Amount to Pay</span>
                        <strong>
                          ₹{calculatePayableAmount(values.amount).toFixed(2)}
                        </strong>
                      </div>
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Payment Mode</label>
                      <Field
                        as="select"
                        name="paymentMode"
                        className="form-select"
                      >
                        <option value="UPI">UPI</option>
                        <option value="CARD">Card</option>
                        <option value="COD">Cash on Delivery</option>
                      </Field>
                      <ErrorMessage
                        name="paymentMode"
                        component="div"
                        className="field-error"
                      />
                    </div>

                    {values.paymentMode === "UPI" && (
                      <div className="payment-mode-box">
                        <label className="form-label">UPI ID</label>
                        <Field
                          type="text"
                          name="upiId"
                          className="form-control"
                          placeholder="aryan@upi"
                        />
                        <ErrorMessage
                          name="upiId"
                          component="div"
                          className="field-error"
                        />
                      </div>
                    )}

                    {values.paymentMode === "CARD" && (
                      <div className="payment-mode-box">
                        <div className="mb-3">
                          <label className="form-label">Card Number</label>
                          <Field
                            type="text"
                            name="cardNumber"
                            className="form-control"
                            placeholder="4111111111111111"
                          />
                          <ErrorMessage
                            name="cardNumber"
                            component="div"
                            className="field-error"
                          />
                        </div>

                        <div className="mb-3">
                          <label className="form-label">Card Holder Name</label>
                          <Field
                            type="text"
                            name="cardHolderName"
                            className="form-control"
                            placeholder="Aryan Parihar"
                          />
                          <ErrorMessage
                            name="cardHolderName"
                            component="div"
                            className="field-error"
                          />
                        </div>
                      </div>
                    )}

                    {values.paymentMode !== "COD" && (
                      <div className="form-check mb-3">
                        <Field
                          type="checkbox"
                          name="simulateFailure"
                          className="form-check-input"
                          id="simulateFailure"
                        />
                        <label
                          className="form-check-label"
                          htmlFor="simulateFailure"
                        >
                          Simulate failed payment
                        </label>
                      </div>
                    )}

                    {values.paymentMode === "COD" && (
                      <div className="alert alert-warning">
                        COD payment will be marked as{" "}
                        <strong>PENDING</strong>.
                      </div>
                    )}

                    <button
                      type="submit"
                      className="btn btn-primary-theme w-100"
                      disabled={isSubmitting}
                    >
                      {isSubmitting
                        ? "Processing..."
                        : `Pay ₹${calculatePayableAmount(
                            values.amount
                          ).toFixed(2)}`}
                    </button>
                  </Form>
                )}
              </Formik>
            </div>
          </div>

          <div className="col-lg-5">
            <div className="invoice-card">
              <h4>Payment Summary</h4>

              {paymentResponse ? (
                <>
                  <div className="invoice-row">
                    <span>Status</span>
                    <span>{paymentResponse.paymentStatus}</span>
                  </div>

                  <div className="invoice-row">
                    <span>Mode</span>
                    <span>{paymentResponse.paymentMode}</span>
                  </div>

                  <div className="invoice-row">
                    <span>Order Amount</span>
                    <span>
                      ₹{Number(paymentResponse.subtotal || 0).toFixed(2)}
                    </span>
                  </div>

                  <div className="invoice-row">
                    <span>GST 5%</span>
                    <span>
                      ₹{Number(paymentResponse.gstAmount || 0).toFixed(2)}
                    </span>
                  </div>

                  <div className="invoice-row invoice-total">
                    <span>Total Paid</span>
                    <span>
                      ₹{Number(paymentResponse.totalAmount || 0).toFixed(2)}
                    </span>
                  </div>

                  <div className="invoice-row">
                    <span>Transaction ID</span>
                    <span>{paymentResponse.transactionId}</span>
                  </div>
                </>
              ) : (
                <div className="payment-empty-summary">
                  Payment summary will appear after payment.
                </div>
              )}

              {invoice && (
                <div className="mt-4">
                  <h4>Invoice</h4>

                  <div className="invoice-row">
                    <span>Invoice No.</span>
                    <span>{invoice.invoiceNumber}</span>
                  </div>

                  <div className="invoice-row">
                    <span>Invoice ID</span>
                    <span>{invoice.invoiceId}</span>
                  </div>

                  <div className="invoice-row">
                    <span>Invoice Total</span>
                    <span>₹{Number(invoice.totalAmount || 0).toFixed(2)}</span>
                  </div>

                  <button
                    className="btn btn-outline-secondary w-100 mt-3"
                    onClick={() => navigate("/track")}
                  >
                    Track Delivery
                  </button>

                  <button
                    className="btn btn-primary-theme w-100 mt-2"
                    onClick={() => navigate("/menu")}
                  >
                    Order More
                  </button>
                </div>
              )}

              {!invoice && (
                <button
                  className="btn btn-outline-secondary w-100 mt-3"
                  onClick={() => navigate("/menu")}
                >
                  Back to Menu
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Payment;