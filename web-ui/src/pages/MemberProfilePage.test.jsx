import React from 'react';
import MemberProfilePage from './MemberProfilePage';
import { AppContextProvider } from '../context/AppContext';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

const initialState = {
  state: {
    memberProfiles: [
      { name: 'homie test', firstName: 'homie', lastName: 'test', id: '123' },
      { name: 'mr. test', firstName: 'mr', lastName: 'test', id: '1234' }
    ],
    userProfile: {
      id: '1234',
    }
  }
};

it('renders correctly', () => {
  const history = createMemoryHistory(`/profile/1234`);
  snapshot(
    <Router history={history}>
      <AppContextProvider value={initialState}>
        <MemberProfilePage />
      </AppContextProvider>
    </Router>
  );
});
