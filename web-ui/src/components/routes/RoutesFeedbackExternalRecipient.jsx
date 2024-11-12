import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';
import FeedbackRequestForExternalRecipientPage from "../../pages/FeedbackRequestForExternalRecipientPage.jsx";
import {AppFeedbackExternalRecipientContext} from "../../context/AppFeedbackExternalRecipientContext.jsx";

export default function RoutesFeedbackExternalRecipient() {
  const { state } = useContext(AppFeedbackExternalRecipientContext);

  return (
    <Switch>
        <Route path="/feedbackExternalRecipient">
            <FeedbackRequestForExternalRecipientPage />
        </Route>
    </Switch>
  );
}
