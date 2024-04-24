import React from 'react';
import Personnel from './Personnel';
import { AppContextProvider } from '../../context/AppContext';

it('renders correctly with null', () => {
  snapshot(
    <AppContextProvider state={null}>
      <Personnel />
    </AppContextProvider>
  );
});
