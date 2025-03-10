import React from 'react';
import ProfilePage from './ProfilePage';
import { AppContextProvider } from '../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const userProfile = {
  id: 'member-id',
  name: 'Mitch Hedberg',
  role: ['MEMBER'],
};

const userStateWithPermission = {
  state: {
    memberProfiles: [{
      name: 'Mitch Hedberg',
      id: 'member-id',
      bioText: 'Died too young.',
      workEmail: 'hedbergm@objectcomputing.com',
      title: 'Strategic Placement Specialist',
      location: 'Roseville, Minnesota',
    }],
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
