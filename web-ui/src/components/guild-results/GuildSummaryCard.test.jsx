import React from 'react';
import GuildSummaryCard from './GuildSummaryCard';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const guilds = [
  {
    id: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    name: 'string',
    description: 'string'
  },
  {
    id: '3fa4-5717-4562-b3fc-2c963f66afa6',
    name: 'stuff',
    description: '',
    guildMembers: [
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
      memberProfile: {
        id: '3fa4-5717-4562-b3fc-2c963f66afa9',
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      },
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    guilds
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
        <GuildSummaryCard guild={guilds[0]} />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders correctly for ADMIN', () => {
  snapshot(
    <AppContextProvider value={adminState}>
      <BrowserRouter>
        <GuildSummaryCard guild={guilds[0]} />
      </BrowserRouter>
    </AppContextProvider>
  );
});

it('renders correctly for guild lead', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <BrowserRouter>
        <GuildSummaryCard guild={guilds[1]} />
      </BrowserRouter>
    </AppContextProvider>
  );
});
