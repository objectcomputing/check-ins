import React from 'react';
import AnniversaryReportPage from './AnniversaryReportPage';
import { AppContextProvider } from '../context/AppContext';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_ANNIVERSARY_REPORT' }],
    }
  }
};

it('renders correctly', () => {
  const mockDate = new Date(2022, 1, 1);
  vi.useFakeTimers();
  vi.setSystemTime(mockDate);

  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <AnniversaryReportPage />
    </AppContextProvider>
  );

  vi.useRealTimers();
});

it('renders an error if user does not have appropriate permission', () => {
  const mockDate = new Date(2022, 1, 1);
  vi.useFakeTimers();
  vi.setSystemTime(mockDate);

  snapshot(
    <AppContextProvider>
      <AnniversaryReportPage />
    </AppContextProvider>
  );

  vi.useRealTimers();
});
