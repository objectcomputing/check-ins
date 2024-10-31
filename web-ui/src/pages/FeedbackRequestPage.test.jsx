import React from 'react';
import FeedbackRequestPage from './FeedbackRequestPage';
import { AppContextProvider } from '../context/AppContext';
import { MemoryRouter } from 'react-router-dom';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_CREATE_FEEDBACK_REQUEST' }],
    }
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <MemoryRouter initialEntries={['/feedback/?for=1234']} initialIndex={0}>
        <FeedbackRequestPage />
      </MemoryRouter>
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <MemoryRouter initialEntries={['/feedback/?for=1234']} initialIndex={0}>
        <FeedbackRequestPage />
      </MemoryRouter>
    </AppContextProvider>
  );
});
