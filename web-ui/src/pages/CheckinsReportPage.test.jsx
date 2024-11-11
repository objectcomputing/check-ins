import React from 'react';
import CheckinsReportPage from './CheckinsReportPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_CHECKINS_REPORT' }],
    },
    terminatedMembers: [],
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <CheckinsReportPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <CheckinsReportPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
