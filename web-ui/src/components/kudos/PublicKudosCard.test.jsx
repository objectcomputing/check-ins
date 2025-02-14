import React from 'react';
import PublicKudosCard from './PublicKudosCard';
import { AppContextProvider } from '../../context/AppContext';

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    teams: [],
    userProfile: {
      id: "1",
      firstName: 'Jimmy',
      lastName: 'Johnson',
      role: ['MEMBER'],
    },
    memberProfiles: [
      {
        id: "1",
        firstName: 'Jimmy',
        lastName: 'Johnson',
        role: ['MEMBER'],
      },
      {
        id: "2",
        firstName: 'Jimmy',
        lastName: 'Olsen',
        role: ['MEMBER'],
      },
      {
        id: "3",
        firstName: 'Clark',
        lastName: 'Kent',
        role: ['MEMBER'],
      },
      {
        id: "4",
        firstName: 'Kent',
        lastName: 'Brockman',
        role: ['MEMBER'],
      },
      {
        id: "5",
        firstName: 'Jerry',
        lastName: 'Garcia',
        role: ['MEMBER'],
      },
      {
        id: "6",
        firstName: 'Brock',
        lastName: 'Smith',
        role: ['MEMBER'],
      },
      {
        id: "7",
        firstName: 'Jimmy',
        middleName: 'T.',
        lastName: 'Olsen',
        role: ['MEMBER'],
      },
    ],
  }
};

const kudos = {
  id: 'test-kudos',
  message: "Brock and Brockman did a great job helping Clark, Jimmy Olsen, Jimmy T. Olsen, and Johnson",
  senderId: "5",
  dateCreated: [ 2025, 2, 14 ],
  recipientMembers: [
      {
        id: "1",
        firstName: 'Jimmy',
        lastName: 'Johnson',
        role: ['MEMBER'],
      },
      {
        id: "2",
        firstName: 'Jimmy',
        lastName: 'Olsen',
        role: ['MEMBER'],
      },
      {
        id: "3",
        firstName: 'Clark',
        lastName: 'Kent',
        role: ['MEMBER'],
      },
      {
        id: "6",
        firstName: 'Brock',
        lastName: 'Smith',
        role: ['MEMBER'],
      },
      {
        id: "4",
        firstName: 'Kent',
        lastName: 'Brockman',
        role: ['MEMBER'],
      },
      {
        id: "7",
        firstName: 'Jimmy',
        middleName: 'T.',
        lastName: 'Olsen',
        role: ['MEMBER'],
      },
  ],
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <PublicKudosCard
        kudos={kudos}
      />
    </AppContextProvider>
  );
});
