import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import FeedbackVerifyPage from "../../pages/FeedbackVerifyPage.jsx";
import FeedbackSubmitPage from "../../pages/FeedbackSubmitPage.jsx";
import FeedbackSubmitConfirmation from "../feedback_submit_confirmation/FeedbackSubmitConfirmation.jsx";

export default function Routes() {
  const { state } = useContext(AppContext);

  return (
    <Switch>
      <Route path="/externalFeedback/verify">
        <FeedbackVerifyPage />
      </Route>
      <Route path="/externalFeedback/submit">
        <FeedbackSubmitPage />
      </Route>
      <Route path="/externalFeedback/confirmation">
        <FeedbackSubmitConfirmation />
      </Route>
    </Switch>
  );
}
