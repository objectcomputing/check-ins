import React, { useEffect, useRef } from "react";
import { Routes, Route, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "./../../auth/useAuth.js";
import RequireAuth from "./../../auth/requireAuth";

import Login from "./../login/Login";
import PageNotFound from "./PageNotFound";

import AccessCodePage from "./../../pages/AccessCodePage";
import WebPortal from "./../../pages/WebPortal";

// This is our base page routing for the app.
function BasePage() {
  const initialRender = useRef(true);
  let navigate = useNavigate();
  let location = useLocation();
  let auth = useAuth();

  let from = location?.state?.from?.pathname || "/";
  let onPageLoad = window.location.pathname || "/";

  useEffect(() => {
    console.log("isLoggedIn", auth.isLoggedIn);
    if (auth.isLoggedIn === false) {
      console.log("Pushing user to /login path");
      navigate("/login", { state: { from: location } });
    }
    if (auth.isLoggedIn && !initialRender.current) {
      console.log("Navigating to previous page if it exists, or to '/'.");
      navigate(from, { replace: true });
    }
    // Applies if user reloads page, or to a fresh load of page
    if (auth.isLoggedIn && initialRender.current) {
      initialRender.current = false;
      console.log(
        "Navigating on load to previous page if it exists, or to '/'."
      );
      navigate(onPageLoad, { replace: true });
    }
  }, [auth.isLoggedIn]);

  return (
    <Routes>
      {!auth.isLoggedIn && (
        <>
          <Route path="/login" index element={<Login />} />
          <Route path="*" element={<Login />} />
        </>
      )}
      {/* <Route path="/accesscode" element={<RequireAuth><AccessCodePage /></RequireAuth>} /> */}
      <Route
        path="/"
        element={
          <RequireAuth>
            <WebPortal />
          </RequireAuth>
        }
      >
        <Route
          path="*"
          element={
            <RequireAuth>
              <PageNotFound />
            </RequireAuth>
          }
        />
      </Route>
    </Routes>
  );
}
export default BasePage;
