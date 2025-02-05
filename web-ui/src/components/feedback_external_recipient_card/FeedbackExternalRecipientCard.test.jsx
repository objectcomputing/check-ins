import React from 'react';
import FeedbackExternalRecipientCard from './FeedbackExternalRecipientCard.jsx';
import { AppContextProvider } from '../../context/AppContext.jsx';
import { BrowserRouter } from 'react-router-dom';

it('renders the recipient card', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <FeedbackExternalRecipientCard />
      </BrowserRouter>
    </AppContextProvider>
  );
});
