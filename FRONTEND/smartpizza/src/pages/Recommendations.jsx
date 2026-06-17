import { useState } from "react";
import axiosInstance from "../api/axiosConfig";

function Recommendations() {
  const [preferredType, setPreferredType] = useState("VEG");
  const [cartAmount, setCartAmount] = useState("");
  const [season, setSeason] = useState("SUMMER");

  const [recommendation, setRecommendation] = useState(null);
  const [loading, setLoading] = useState(false);

  const [serverError, setServerError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchPersonalizedRecommendations = async () => {
    setServerError("");
    setSuccessMessage("");
    setRecommendation(null);
    setLoading(true);

    try {
      const response = await axiosInstance.get(
        `/api/recommendations/user?preferredType=${preferredType}`
      );

      setRecommendation(response.data);
      setSuccessMessage("Personalized recommendations generated.");
    } catch (error) {
      setServerError(
        error.response?.data?.message ||
          "Failed to fetch personalized recommendations."
      );
    } finally {
      setLoading(false);
    }
  };

  const fetchSmartCombos = async () => {
    setServerError("");
    setSuccessMessage("");
    setRecommendation(null);

    if (!cartAmount || Number(cartAmount) < 0) {
      setServerError("Please enter a valid cart amount.");
      return;
    }

    setLoading(true);

    try {
      const response = await axiosInstance.post("/api/recommendations/combos", {
        preferredType: preferredType,
        cartAmount: Number(cartAmount),
        season: season,
      });

      setRecommendation(response.data);
      setSuccessMessage("Smart combo recommendations generated.");
    } catch (error) {
      setServerError(
        error.response?.data?.message ||
          "Failed to fetch smart combo recommendations."
      );
    } finally {
      setLoading(false);
    }
  };

  const fetchSeasonalRecommendations = async () => {
    setServerError("");
    setSuccessMessage("");
    setRecommendation(null);
    setLoading(true);

    try {
      const response = await axiosInstance.get(
        `/api/recommendations/seasonal?season=${season}`
      );

      setRecommendation(response.data);
      setSuccessMessage("Seasonal recommendations generated.");
    } catch (error) {
      setServerError(
        error.response?.data?.message ||
          "Failed to fetch seasonal recommendations."
      );
    } finally {
      setLoading(false);
    }
  };

  const renderRecommendationResult = () => {
    if (loading) {
      return (
        <div className="text-center py-5">
          <div className="spinner-border text-danger"></div>
          <p className="mt-3">Generating AI recommendations...</p>
        </div>
      );
    }

    if (!recommendation) {
      return (
        <div className="ai-empty-state">
          <div>🤖</div>
          <h4>No recommendations yet</h4>
          <p>
            Choose a recommendation type and let SmartPizzaAI suggest something
            delicious.
          </p>
        </div>
      );
    }

    return (
      <div className="recommendation-result-card">
        <span className="recommendation-type-badge">
          {recommendation.recommendationType}
        </span>

        <h4>Recommended For You</h4>

        {recommendation.recommendedItems &&
          recommendation.recommendedItems.map((item, index) => (
            <div className="recommendation-item" key={index}>
              <div className="recommendation-item-icon">🍕</div>
              <div>{item}</div>
            </div>
          ))}

        <div className="recommendation-reason">
          <strong>Why this?</strong>
          <br />
          {recommendation.reason}
        </div>
      </div>
    );
  };

  return (
    <div className="page-container">
      <div className="recommendations-page">
        <div className="recommendations-hero">
          <h1>🤖 AI Recommendations</h1>
          <p>
            Get personalized pizza suggestions, smart combos, and seasonal
            recommendations.
          </p>
        </div>

        {serverError && <div className="error-message">{serverError}</div>}

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        <div className="row g-4">
          <div className="col-lg-4">
            <div className="recommendation-control-card">
              <h4>Recommendation Controls</h4>

              <div className="mb-3">
                <label className="form-label">Preferred Type</label>
                <select
                  className="form-select"
                  value={preferredType}
                  onChange={(event) => setPreferredType(event.target.value)}
                >
                  <option value="VEG">Veg</option>
                  <option value="NON_VEG">Non-Veg</option>
                  <option value="SPICY">Spicy</option>
                  <option value="CHEESE">Cheese</option>
                  <option value="POPULAR">Popular</option>
                </select>
              </div>

              <button
                className="btn btn-primary-theme w-100 mb-3"
                onClick={fetchPersonalizedRecommendations}
                disabled={loading}
              >
                Personalized Recommendations
              </button>

              <hr />

              <div className="mb-3">
                <label className="form-label">Cart Amount</label>
                <input
                  type="number"
                  className="form-control"
                  placeholder="Example: 850"
                  value={cartAmount}
                  onChange={(event) => setCartAmount(event.target.value)}
                />
              </div>

              <button
                className="btn btn-primary-theme w-100 mb-3"
                onClick={fetchSmartCombos}
                disabled={loading}
              >
                Smart Combo Suggestions
              </button>

              <hr />

              <div className="mb-3">
                <label className="form-label">Season</label>
                <select
                  className="form-select"
                  value={season}
                  onChange={(event) => setSeason(event.target.value)}
                >
                  <option value="SUMMER">Summer</option>
                  <option value="WINTER">Winter</option>
                  <option value="MONSOON">Monsoon</option>
                  <option value="DEFAULT">Default</option>
                </select>
              </div>

              <button
                className="btn btn-primary-theme w-100"
                onClick={fetchSeasonalRecommendations}
                disabled={loading}
              >
                Seasonal Recommendations
              </button>
            </div>
          </div>

          <div className="col-lg-8">{renderRecommendationResult()}</div>
        </div>
      </div>
    </div>
  );
}

export default Recommendations;