import React from 'react';
import { AppContextProvider } from '../context/AppContext';
import SkillCategoriesPage from './SkillCategoriesPage';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_SKILLS_REPORT' }],
    }
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <SkillCategoriesPage />
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <SkillCategoriesPage />
    </AppContextProvider>
  );
});
