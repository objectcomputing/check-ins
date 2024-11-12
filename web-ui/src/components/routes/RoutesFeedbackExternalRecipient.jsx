import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';
import {AppFeedbackExternalRecipientContext} from "../../context/AppFeedbackExternalRecipientContext.jsx";
import FeedbackSubmitForExternalRecipientPage from "../../pages/FeedbackSubmitPageForExternalRecipient.jsx";

export default function RoutesFeedbackExternalRecipient() {
  const { state } = useContext(AppFeedbackExternalRecipientContext);

  return (
    <Switch>
        <Route path="/feedbackExternalRecipient">
            <FeedbackSubmitForExternalRecipientPage />
        </Route>
    </Switch>
  );
}
