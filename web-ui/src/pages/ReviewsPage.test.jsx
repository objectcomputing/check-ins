import React from 'react';
import ReviewsPage from './ReviewsPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

window.snackDispatch = vi.fn();

const initialState = {
  state: {
    userProfile: {
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
    },
    // Review Period 2 should be listed before Review Period 1 because it's OPEN
    reviewPeriods: [
      {
        id: 'a44fc66a-86b0-4f15-8459-e7d4b4ecc330',
        name: 'Review Period 1',
        reviewStatus: 'CLOSED',
      },
      {
        id: 'a44fc66a-86b0-4f15-8459-e7d4b4ecc331',
        name: 'Review Period 2',
        reviewStatus: 'OPEN',
      },
    ],
  },
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <BrowserRouter>
        <ReviewsPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
