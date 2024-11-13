import React from 'react';
import Profile from './Profile';
import { AppContextProvider } from '../../context/AppContext';

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: '912834091823',
        pdlId: '0987654321',
        supervisorid: '9876543210'
      },
      role: ['MEMBER']
    },
    memberProfiles: [
      {
        id: '0987654321',
        name: 'TestName',
        lastName: 'Name'
      },
      {
        id: '9876543210',
        name: 'TestName2',
        lastName: 'Name2'
      }
    ],
    memberSkills: [
      {
        memberid: '912834091823',
        skillId: '99999'
      }
    ],
    skills: [
      {
        id: '99999'
      }
    ]
  }
};

const member = {
  name: 'testerson',
  id: '2o34i2j34',
  startDate: [2018, 1, 10],
  location: 'STL',
  imageURL: 'url.com',
  title: 'engineer',
  workEmail: 'testerson@oci.com'
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <Profile memberId="912834091823" />
    </AppContextProvider>
  );
});
