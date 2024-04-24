import React from 'react';
import FeedbackRecipientSelector from './FeedbackRecipientSelector';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

describe('FeedbackRecipientSelector', () => {
  it('renders the component', () => {
    snapshot(
      <BrowserRouter>
        <AppContextProvider>
          <FeedbackRecipientSelector
            changeQuery={vi.fn()}
            fromQuery={[]}
            forQuery=""
          />
        </AppContextProvider>
      </BrowserRouter>
    );
  });
});
