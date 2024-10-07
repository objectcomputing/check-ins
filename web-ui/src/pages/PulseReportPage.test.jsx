import React from 'react';
import PulseReportPage from './PulseReportPage';
import { AppContextProvider } from '../context/AppContext';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_ALL_PULSE_RESPONSES' }],
    }
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <PulseReportPage />
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <PulseReportPage />
    </AppContextProvider>
  );
});
