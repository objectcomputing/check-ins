import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AppContextProvider } from '../../context/AppContext';
import FeedbackRequestConfirmation from './FeedbackRequestConfirmation';

const userStateWithPermission = {
  state: {
    userProfile: {
      name: 'sherlock holmes',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_CREATE_FEEDBACK_REQUEST' }],
    }
  }
};

const userStateWithoutPermission = {
  state: {
    userProfile: {
      name: 'john watson',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_SKILLS_REPORT' }],
    }
  }
};

it('renders the confirmation message', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider value={userStateWithPermission}>
        <FeedbackRequestConfirmation />
      </AppContextProvider>
    </BrowserRouter>
  );
});

it('renders an error if the user does not have appropriate permission', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider value={userStateWithoutPermission}>
        <FeedbackRequestConfirmation />
      </AppContextProvider>
    </BrowserRouter>
  );
});
