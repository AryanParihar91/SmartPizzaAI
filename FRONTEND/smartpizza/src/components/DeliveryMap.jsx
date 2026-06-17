import { MapContainer, TileLayer, Marker, Popup, Polyline } from "react-leaflet";
import L from "leaflet";

import "leaflet/dist/leaflet.css";

const restaurantIcon = new L.Icon({
  iconUrl: "https://cdn-icons-png.flaticon.com/512/3595/3595455.png",
  iconSize: [38, 38],
});

const customerIcon = new L.Icon({
  iconUrl: "https://cdn-icons-png.flaticon.com/512/684/684908.png",
  iconSize: [38, 38],
});

const deliveryIcon = new L.Icon({
  iconUrl: "https://cdn-icons-png.flaticon.com/512/1048/1048313.png",
  iconSize: [40, 40],
});

function DeliveryMap({ tracking }) {
    // need ai for the latitude and longitude.
  const restaurantLocation = [12.9716, 77.5946]; // Bengaluru center
  const customerLocation = [12.8452, 77.6602]; // Electronic City demo location

  const getDeliveryPartnerLocation = () => {
    if (!tracking) {
      return restaurantLocation;
    }

    if (tracking.deliveryStatus === "ASSIGNED") {
      return [12.965, 77.6];
    }

    if (tracking.deliveryStatus === "PICKED_UP") {
      return [12.9716, 77.5946];
    }

    if (tracking.deliveryStatus === "ON_THE_WAY") {
      return [12.9, 77.64];
    }

    if (tracking.deliveryStatus === "NEAR_YOU") {
      return [12.86, 77.655];
    }

    if (tracking.deliveryStatus === "DELIVERED") {
      return customerLocation;
    }

    return restaurantLocation;
  };

  const deliveryPartnerLocation = getDeliveryPartnerLocation();

  const routePoints = [
    restaurantLocation,
    deliveryPartnerLocation,
    customerLocation,
  ];

  return (
    <div className="delivery-map-wrapper">
      <MapContainer
        center={deliveryPartnerLocation}
        zoom={12}
        scrollWheelZoom={false}
        className="delivery-map"
      >
        <TileLayer
          attribution='&copy; OpenStreetMap contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        <Marker position={restaurantLocation} icon={restaurantIcon}>
          <Popup>
            <strong>SmartPizzaAI Kitchen</strong>
            <br />
            Bengaluru
          </Popup>
        </Marker>

        <Marker position={customerLocation} icon={customerIcon}>
          <Popup>
            <strong>Customer Location</strong>
            <br />
            Electronic City, Bengaluru
          </Popup>
        </Marker>

        <Marker position={deliveryPartnerLocation} icon={deliveryIcon}>
          <Popup>
            <strong>Delivery Partner</strong>
            <br />
            {tracking?.deliveryPartnerName || "Not Assigned"}
            <br />
            Status: {tracking?.deliveryStatus || "N/A"}
            <br />
            ETA: {tracking?.etaMinutes ?? 0} min
          </Popup>
        </Marker>

        <Polyline
          positions={routePoints}
          pathOptions={{
            color: "#b42318",
            weight: 5,
            opacity: 0.8,
          }}
        />
      </MapContainer>

      <div className="map-eta-card">
        <h5>Live ETA</h5>
        <h2>{tracking?.etaMinutes ?? 0} min</h2>
        <p>{tracking?.deliveryStatus || "Tracking not loaded"}</p>
      </div>
    </div>
  );
}

export default DeliveryMap;