import React from 'react';
import PermissionsPage from './PermissionsPage';
import { AppContextProvider } from '../context/AppContext';

const initialState = {
  state: {
    userProfile: {
      name: 'holmes',
      role: ['PDL'],
      permissions: [{ permission: 'CAN_ASSIGN_ROLE_PERMISSIONS' }],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    memberProfiles: [
      {
        id: '1234-5434-8765-3458',
        name: 'holmes'
      }
    ],
    index: 0
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <PermissionsPage />
    </AppContextProvider>
  );
});
