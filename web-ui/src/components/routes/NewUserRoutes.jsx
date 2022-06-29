import React from "react";
import { Switch, Route } from "react-router-dom";
import Header from "../header/Header";
import HomePage from "../../pages/HomePage";
import AccessCodePage from "../../pages/AccessCodePage";
import WebPortal from "../../pages/WebPortal";

export default function NewUserRoutes() {
  return (
    <Switch>
      <Route exact path="/">
        <Header />
        <HomePage />
      </Route>


      <Route path="/accesscode">
        <AccessCodePage />
      </Route>

      <Route path="/onboarding">
        <WebPortal />
      </Route>

    </Switch>
  );
}
