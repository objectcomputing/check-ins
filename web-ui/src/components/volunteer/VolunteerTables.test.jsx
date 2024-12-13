import React from 'react';
import VolunteerTables from './VolunteerTables';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';


const initialState = {
  state: {
    userProfile: {
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
    },
  },
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <BrowserRouter>
        <VolunteerTables />
      </BrowserRouter>
    </AppContextProvider>
  );
});
