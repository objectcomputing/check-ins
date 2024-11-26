import React from 'react';
import EmailPage from './EmailPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

window.scrollTo = vi.fn();

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_SEND_EMAIL' }],
    },
    terminatedMembers: [],
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <EmailPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <EmailPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
