import React from 'react';
import BirthdayReportPage from './BirthdayReportPage';
import { AppContextProvider } from '../context/AppContext';

it('renders correctly', () => {
  const mockDate = new Date(2022, 1, 1);
  const spy = vi.spyOn(global, 'Date').mockImplementation(() => mockDate);

  snapshot(
    <AppContextProvider>
      <BirthdayReportPage />
    </AppContextProvider>
  );

  spy.mockRestore();
});
