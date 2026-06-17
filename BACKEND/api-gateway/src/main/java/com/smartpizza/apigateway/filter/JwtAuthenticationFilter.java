package com.smartpizza.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.smartpizza.apigateway.security.JwtUtil;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	@Autowired
	private JwtUtil jwtUtil;

	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> {

			// Allow CORS 
			if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
				return chain.filter(exchange);
			}

			// Check Authorization header
			if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			String token = authHeader.substring(7);

			// Validate JWT
			if (!jwtUtil.isTokenValid(token)) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			// Extract user details
			String userId = String.valueOf(jwtUtil.extractUserId(token));
			String email = jwtUtil.extractEmail(token);
			String role = jwtUtil.extractRole(token);
			String fullName = jwtUtil.extractFullName(token);

			String path = exchange.getRequest().getURI().getPath();
			HttpMethod method = exchange.getRequest().getMethod();

			// Authorization check
			if (!isAuthorized(path, method, role)) {
				exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
				return exchange.getResponse().setComplete();
			}

			// Attach headers downstream
			exchange = exchange.mutate().request(exchange.getRequest().mutate().header("X-User-Id", userId)
					.header("X-User-Email", email).header("X-User-Role", role).header("X-User-Name", fullName).build())
					.build();

			return chain.filter(exchange);
		};
	}

	// Authorization Logic

	private boolean isAuthorized(String path, HttpMethod method, String role) {

		// Admin APIs
		if (path.startsWith("/api/admin")) {
			return isAdmin(role);
		}

		// Menu APIs
		if (path.startsWith("/api/menu")) {
			if (method == HttpMethod.GET) {
				return isCustomer(role) || isAdmin(role) || isDelivery(role);
			}
			return isAdmin(role); // POST, PUT, DELETE
		}

		// Coupons APIs
		if (path.startsWith("/api/coupons/create")) {
			return isAdmin(role);
		}

		if (path.startsWith("/api/coupons/apply")) {
			return isCustomer(role) || isAdmin(role);
		}

		if (path.startsWith("/api/coupons")) {
			return isCustomer(role) || isAdmin(role);
		}

		// Cart APIs
		if (path.startsWith("/api/cart")) {
			return isCustomer(role);
		}

		// Order APIs
		if (path.startsWith("/api/orders/place")) {
			return isCustomer(role);
		}

		if (path.startsWith("/api/orders/my-orders")) {
			return isCustomer(role);
		}

		if (path.startsWith("/api/orders/status")) {
			return isAdmin(role);
		}

		if (path.startsWith("/api/orders")) {
			return isCustomer(role) || isAdmin(role);
		}

		// Delivery APIs
		if (path.startsWith("/api/delivery/partners")) {
			return isAdmin(role);
		}

		if (path.startsWith("/api/delivery/status")) {
			return isAdmin(role) || isDelivery(role);
		}

		if (path.startsWith("/api/delivery/track")) {
			return isCustomer(role) || isAdmin(role) || isDelivery(role);
		}

		if (path.startsWith("/api/delivery")) {
			return isAdmin(role) || isDelivery(role);
		}

		// Recommendation APIs
		if (path.startsWith("/api/recommendations")) {
			return isCustomer(role);
		}

		// Payments APIs
		if (path.startsWith("/api/payments")) {
			return isCustomer(role);
		}

		// Invoices APIs
		if (path.startsWith("/api/invoices")) {
			return isCustomer(role) || isAdmin(role);
		}

		// Auth APIs
		if (path.startsWith("/api/auth/users")) {
			return isCustomer(role) || isAdmin(role) || isDelivery(role);
		}

		return true;
	}

	// Roles

	private boolean isCustomer(String role) {
		return "CUSTOMER".equalsIgnoreCase(role);
	}

	private boolean isAdmin(String role) {
		return "ADMIN".equalsIgnoreCase(role);
	}

	private boolean isDelivery(String role) {
		return "DELIVERY".equalsIgnoreCase(role);
	}

	public static class Config {
	}
}