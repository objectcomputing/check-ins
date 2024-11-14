import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import FeedbackSubmitConfirmation from '../feedback_submit_confirmation/FeedbackSubmitConfirmation';


export default function Routes() {
  const { state } = useContext(AppContext);

  return (
    <Switch>
      <Route exact path="/externalFeedback/">
        <FeedbackSubmitConfirmation />
      </Route>
    </Switch>
  );
}
