import React from 'react';
import ViewFeedbackPage from './ViewFeedbackPage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_FEEDBACK_REQUEST' }],
    }
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <ViewFeedbackPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <ViewFeedbackPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
