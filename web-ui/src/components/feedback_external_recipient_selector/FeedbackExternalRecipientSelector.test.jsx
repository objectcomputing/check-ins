import React from 'react';
import FeedbackExternalRecipientSelector from './FeedbackExternalRecipientSelector.jsx';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

describe('FeedbackExternalRecipientSelector', () => {
  it('renders the component', () => {
    snapshot(
      <BrowserRouter>
        <AppContextProvider>
          <FeedbackExternalRecipientSelector
            changeQuery={vi.fn()}
            fromQuery={[]}
            forQuery=""
          />
        </AppContextProvider>
      </BrowserRouter>
    );
  });
});
