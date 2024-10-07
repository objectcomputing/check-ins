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
  const spy = vi.spyOn(global, 'Date').mockImplementation(() => mockDate);

  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <AnniversaryReportPage />
    </AppContextProvider>
  );

  spy.mockRestore();
});

it('renders an error if user does not have appropriate permission', () => {
  const mockDate = new Date(2022, 1, 1);
  const spy = vi.spyOn(global, 'Date').mockImplementation(() => mockDate);

  snapshot(
    <AppContextProvider>
      <AnniversaryReportPage />
    </AppContextProvider>
  );

  spy.mockRestore();
});
