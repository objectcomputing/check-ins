import React from 'react';
import EditSkillsPage from './EditSkillsPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_EDIT_SKILLS' }],
    }
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <EditSkillsPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <EditSkillsPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
