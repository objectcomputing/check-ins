import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from 'auth/useAuth.js';

function RequireAuth({ children }) {
  let auth = useAuth();
  let location = useLocation();

  if (!auth.isLoggedIn) {
    console.log("Navigate to /login from requireAuth");
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return children;
}
export default RequireAuth;
