import React from 'react';
import FeedbackRecipientCard from './FeedbackRecipientCard';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

it('renders the recipient card', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <FeedbackRecipientCard />
      </BrowserRouter>
    </AppContextProvider>
  );
});
