import React from 'react';
import { AppContextProvider } from '../context/AppContext';
import SkillCategoriesPage from './SkillCategoriesPage';

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <SkillCategoriesPage />
    </AppContextProvider>
  );
});
