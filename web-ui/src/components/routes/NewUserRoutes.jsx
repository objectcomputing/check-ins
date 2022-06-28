import React, { useContext } from "react";
import { Switch, Route } from "react-router-dom";
import Header from "../header/Header";
import HomePage from "../../pages/HomePage";
import CultureVideoPage from "../../pages/CultureVideoPage";
import AccessCodePage from "../../pages/AccessCodePage";
import BackgroundInformationPage from "../../pages/BackgroundInformationPage";
import IntroductionSurveyPage from "../../pages/IntroductionSurveyPage";
import WorkingLocationPage from "../../pages/WorkingLocationPage";
import EquipmentPage from "../../pages/EquipmentPage";
import DocumentSigningPage from "../../pages/DocumentSigningPage";
import Congratulations from "../../pages/CongratulationsPage";

export default function NewUserRoutes() {
  return (
    <Switch>
      <Route exact path="/">
        <Header />
        <HomePage />
      </Route>
      <Route path="/culturevideo">
        <CultureVideoPage />
      </Route>

      <Route path="/accesscode">
        <AccessCodePage />
      </Route>

      <Route path="/backgroundinformation">
        <BackgroundInformationPage />
      </Route>

      <Route path="/survey">
        <IntroductionSurveyPage />
      </Route>

      <Route path="/worklocation">
        <WorkingLocationPage />
      </Route>

      <Route path="/equipment">
        <EquipmentPage />
      </Route>

      <Route path="/documents">
        <DocumentSigningPage />
      </Route>

      <Route path="/congratulations">
        <Congratulations />
      </Route>
    </Switch>
  );
}
