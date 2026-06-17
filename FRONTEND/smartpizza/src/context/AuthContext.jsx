import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    const fullName = localStorage.getItem("fullName");
    const email = localStorage.getItem("email");
    const role = localStorage.getItem("role");

    if (token && userId && role) {
      setUser({
        token,
        userId,
        fullName,
        email,
        role,
      });
    }

    setAuthLoading(false);
  }, []);

  const login = (authData) => {
    localStorage.setItem("token", authData.token);
    localStorage.setItem("userId", authData.userId);
    localStorage.setItem("fullName", authData.fullName);
    localStorage.setItem("email", authData.email);
    localStorage.setItem("role", authData.role);

    setUser({
      token: authData.token,
      userId: authData.userId,
      fullName: authData.fullName,
      email: authData.email,
      role: authData.role,
    });
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("fullName");
    localStorage.removeItem("email");
    localStorage.removeItem("role");

    setUser(null);
  };

  const isAuthenticated = () => {
    return !!localStorage.getItem("token");
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        authLoading,
        login,
        logout,
        isAuthenticated,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}