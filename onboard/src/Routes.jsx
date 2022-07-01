import React from "react";
import { Switch, Route } from "react-router-dom";
import AccessCodePage from "./pages/AccessCodePage";
import WebPortal from "./pages/WebPortal";

export default function Routes() {
  return (
    <Switch>

      <Route path="/accesscode">
        <AccessCodePage />
      </Route>

      <Route>
        <WebPortal />
      </Route>

    </Switch>
  );
}
