import React from 'react';
import SkillCategoryCard from './SkillCategoryCard';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <SkillCategoryCard name="Languages" id="languages-id" />
      </BrowserRouter>
    </AppContextProvider>
  );
});
