import React from 'react';
import SelfReviewsPage from './SelfReviewsPage';
import { AppContextProvider } from '../context/AppContext';
import { MemoryRouter } from 'react-router-dom';

window.snackDispatch = vi.fn();

it('SelfReviewsPage renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <MemoryRouter
        initialEntries={['/feedback/self-reviews']}
        initialIndex={0}
      >
        <SelfReviewsPage />
      </MemoryRouter>
    </AppContextProvider>
  );
});
