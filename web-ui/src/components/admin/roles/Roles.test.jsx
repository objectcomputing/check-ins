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
    memberRoles: [
      { memberRoleId: { roleId: 1, memberId: 1 } },
      { memberRoleId: { roleId: 2, memberId: 2 } },
    ],
    roles: [
      { id: 1, role: 'ADMIN', description: 'Administrator' },
      { id: 2, role: 'PDL', description: 'Prof. Dev. Lead' },
      { id: 3, role: 'MEMBER', description: 'A member of the org' }
    ],
    userProfile: {
      name: 'Current User',
      role: ['MEMBER'],
      id: 1,
      permissions: [{ permission: 'CAN_EDIT_MEMBER_ROLES' }],
    },
  }
};

const noPermState = {
  state: {
    memberProfiles: [
      { id: 1, name: 'Señior Test' },
      { id: 2, name: 'Señora Test' },
      { id: 3, name: 'Herr Test' }
    ],
    memberRoles: [
      { memberRoleId: { roleId: 1, memberId: 1 } },
      { memberRoleId: { roleId: 2, memberId: 2 } },
    ],
    roles: [
      { id: 1, role: 'ADMIN', description: 'Administrator' },
      { id: 2, role: 'PDL', description: 'Prof. Dev. Lead' },
      { id: 3, role: 'MEMBER', description: 'A member of the org' }
    ],
    userProfile: {
      name: 'Current User',
      role: ['MEMBER'],
      id: 1,
      permissions: [],
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
    <AppContextProvider value={noPermState}>
      <Roles />
    </AppContextProvider>
  );
});
