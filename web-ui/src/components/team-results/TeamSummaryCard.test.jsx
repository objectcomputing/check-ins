import React from 'react';
import TeamSummaryCard from './TeamSummaryCard';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const teams = [
  {
    id: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    name: 'string',
    description: 'string'
  },
  {
    id: '3fa4-5717-4562-b3fc-2c963f66afa6',
    name: 'stuff',
    description: '',
    teamMembers: [
      {
        memberId: '3fa4-5717-4562-b3fc-2c963f66afa9',
        name: 'testname',
        lead: true
      }
    ]
  }
];

const initialState = {
  state: {
    userProfile: {
      name: 'holmes',
      id: '3fa4-5717-4562-b3fc-2c963f66afa9',
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    teams,
    memberProfiles: [
      {
        id: '3fa4-5717-4562-b3fc-2c963f66afa9',
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      }
    ]
  }
};

const adminState = { ...initialState };
adminState.state = { ...adminState.state };
adminState.state.userProfile = { ...adminState.state.userProfile };
adminState.state.userProfile.role = ['MEMBER', 'ADMIN'];

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <BrowserRouter>
        <TeamSummaryCard team={teams[0]} />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders correctly for ADMIN', () => {
  snapshot(
    <AppContextProvider value={adminState}>
      <BrowserRouter>
        <TeamSummaryCard team={teams[0]} />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders correctly for team lead', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <BrowserRouter>
        <TeamSummaryCard team={teams[1]} />
      </BrowserRouter>
    </AppContextProvider>
  );
});
