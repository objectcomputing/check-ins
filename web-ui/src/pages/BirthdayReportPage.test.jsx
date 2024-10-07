import React from 'react';
import BirthdayReportPage from './BirthdayReportPage';
import { AppContextProvider } from '../context/AppContext';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_BIRTHDAY_REPORT' }],
    }
  }
};

it('renders correctly', () => {
  const mockDate = new Date(2022, 1, 1);
  const spy = vi.spyOn(global, 'Date').mockImplementation(() => mockDate);

  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BirthdayReportPage />
    </AppContextProvider>
  );

  spy.mockRestore();
});

it('renders an error if user does not have appropriate permission', () => {
  const mockDate = new Date(2022, 1, 1);
  const spy = vi.spyOn(global, 'Date').mockImplementation(() => mockDate);

  snapshot(
    <AppContextProvider>
      <BirthdayReportPage />
    </AppContextProvider>
  );

  spy.mockRestore();
});
