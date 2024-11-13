import React from 'react';
import FeedbackTemplateSelector from './FeedbackTemplateSelector';
import { AppContextProvider } from '../../context/AppContext';

it('renders the recipient card', () => {
  snapshot(
    <AppContextProvider>
      <FeedbackTemplateSelector />
    </AppContextProvider>
  );
});
