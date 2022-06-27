import React, { useContext } from "react";
import { Switch, Route } from "react-router-dom";
import Header from "../header/Header";
import HomePage from "../../pages/HomePage";
import Test from "../../pages/Test";

export default function NewUserRoutes() {
  return (
    <Switch>
      <Route exact path="/">
        <Header />
        <HomePage />
      </Route>
      <Route path="/onboarding">
        <Test />
      </Route>
    </Switch>
  );
}
