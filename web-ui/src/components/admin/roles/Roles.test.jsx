import React from 'react';
import Roles from './Roles';
import { AppContextProvider } from '../../../context/AppContext';

const initialState = {
  state: {
    memberProfiles: [
      { id: 1, name: 'Señior Test' },
      { id: 2, name: 'Señora Test' },
      { id: 3, name: 'Herr Test' }
    ],
    roles: [
      { id: 1, role: 'ADMIN', memberid: 1 },
      { id: 2, role: 'PDL', memberid: 2 }
    ],
    userProfile: {
      name: 'Current User',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_ASSIGN_ROLE_PERMISSIONS' }],
    },
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <Roles />
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <Roles />
    </AppContextProvider>
  );
});
