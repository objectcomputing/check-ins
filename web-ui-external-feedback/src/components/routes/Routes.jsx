import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import FeedbackSubmitExternalRecipientPage from "../../pages/FeedbackSubmitExternalRecipientPage.jsx";


export default function Routes() {
  const { state } = useContext(AppContext);

  return (
    <Switch>
      <Route path="/externalFeedback/">
        <FeedbackSubmitExternalRecipientPage />
      </Route>
    </Switch>
  );
}
