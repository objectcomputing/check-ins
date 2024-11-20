import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AppContextProvider } from '../../context/AppContext';
import FeedbackSubmitConfirmation from './FeedbackSubmitConfirmation';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_CREATE_FEEDBACK_REQUEST' }],
    }
  }
};

it('renders the confirmation message', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider value={userStateWithPermission}>
        <FeedbackSubmitConfirmation />
      </AppContextProvider>
    </BrowserRouter>
  );
});

it('renders an error if user does not have appropriate permission', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider>
        <FeedbackSubmitConfirmation />
      </AppContextProvider>
    </BrowserRouter>
  );
});
