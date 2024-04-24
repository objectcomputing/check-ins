import React from 'react';
import SkillReportPage from './SkillReportPage';
import { AppContextProvider } from '../context/AppContext';

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <SkillReportPage />
    </AppContextProvider>
  );
});
