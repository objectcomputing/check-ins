import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AppContextProvider } from '../../context/AppContext';
import FeedbackSubmitConfirmation from './FeedbackSubmitConfirmation';

it('renders the confirmation message', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider>
        <FeedbackSubmitConfirmation />
      </AppContextProvider>
    </BrowserRouter>
  );
});
