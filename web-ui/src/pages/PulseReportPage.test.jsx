import React from 'react';
import PulseReportPage from './PulseReportPage';
import { AppContextProvider } from '../context/AppContext';

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <PulseReportPage />
    </AppContextProvider>
  );
});
