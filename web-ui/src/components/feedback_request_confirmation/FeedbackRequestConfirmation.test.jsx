import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AppContextProvider } from '../../context/AppContext';
import FeedbackRequestConfirmation from './FeedbackRequestConfirmation';

it('renders the confirmation message', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider>
        <FeedbackRequestConfirmation />
      </AppContextProvider>
    </BrowserRouter>
  );
});
