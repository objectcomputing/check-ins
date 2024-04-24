import React from 'react';
import TeamSkillReportPage from './TeamSkillReportPage';
import { AppContextProvider } from '../context/AppContext';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

it('renders correctly', () => {
  const history = createMemoryHistory(`/profile/12345`);
  snapshot(
    <Router history={history}>
      <AppContextProvider>
        <TeamSkillReportPage />
      </AppContextProvider>
    </Router>
  );
});
