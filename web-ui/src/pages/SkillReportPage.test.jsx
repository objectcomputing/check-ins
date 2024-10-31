import React from 'react';
import SkillReportPage from './SkillReportPage';
import { AppContextProvider } from '../context/AppContext';

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
      <SkillReportPage />
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <SkillReportPage />
    </AppContextProvider>
  );
});
