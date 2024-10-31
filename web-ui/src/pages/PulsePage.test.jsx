import React from 'react';
import { AppContextProvider } from '../context/AppContext';
import PulsePage from './PulsePage';

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <PulsePage />
    </AppContextProvider>
  );
});
