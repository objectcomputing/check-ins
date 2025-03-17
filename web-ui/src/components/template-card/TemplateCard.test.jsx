import React from 'react';
import TemplateCard from './TemplateCard';
import { AppContextProvider } from '../../context/AppContext';

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <TemplateCard
        title="Template"
        description="Sample feedback template"
        creatorId="84682de9-85a7-4bf7-b74b-e9054311a61a"
      />
    </AppContextProvider>
  );
});
