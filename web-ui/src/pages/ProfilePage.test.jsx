import React from 'react';
import ProfilePage from './ProfilePage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const userProfile = {
  id: 'member-id',
  name: 'Mitch Hedberg',
  role: ['MEMBER'],
  workEmail: 'hedbergm@objectcomputing.com',
  title: 'Strategic Placement Specialist',
  location: 'Roseville, Minnesota',
  memberProfile: {
    id: 'member-id',
    bioText: 'Died too young.',
  },
};

const userStateWithPermission = {
  state: {
    memberProfiles: [ userProfile ],
    userProfile: userProfile,
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <ProfilePage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
