import React from 'react';
import ReceivedRequestsPage from './ReceivedRequestsPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';


const initialState = {
  state: {
    userProfile: {
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
    },
  },
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <BrowserRouter>
        <ReceivedRequestsPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
