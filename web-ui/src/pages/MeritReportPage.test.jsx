import React from 'react';
import MeritReportPage from './MeritReportPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_CREATE_MERIT_REPORT' }],
    }
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <MeritReportPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <MeritReportPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
