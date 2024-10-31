import React from 'react';
import TeamSkillReportPage from './TeamSkillReportPage';
import { AppContextProvider } from '../context/AppContext';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

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
  const history = createMemoryHistory(`/profile/12345`);
  snapshot(
    <Router history={history}>
      <AppContextProvider value={userStateWithPermission}>
        <TeamSkillReportPage />
      </AppContextProvider>
    </Router>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  const history = createMemoryHistory(`/profile/12345`);
  snapshot(
    <Router history={history}>
      <AppContextProvider>
        <TeamSkillReportPage />
      </AppContextProvider>
    </Router>
  );
});
