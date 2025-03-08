import React from 'react';
import Users from './Users';
import { AppContextProvider } from '../../../context/AppContext';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

const history = createMemoryHistory(`/people`);

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: 'Current User',
  firstName: 'Current',
  lastName: 'User'
};

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    userProfile: {
      name: 'Current User',
      firstName: 'Current',
      lastName: 'User',
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg',
      memberProfile: currentUserProfile
    },
    checkins: [],
    guilds: [],
    teams: [],
    skills: [],
    roles: [],
    memberRoles: [],
    memberSkills: [],
    index: 0,
    memberProfiles: [currentUserProfile]
  }
};

it('renders correctly', () => {
  snapshot(
    <Router history={history}>
      <AppContextProvider value={initialState}>
        <Users />
      </AppContextProvider>
    </Router>
  );
});
