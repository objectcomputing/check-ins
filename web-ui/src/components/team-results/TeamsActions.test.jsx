import React from 'react';
import TeamsActions from './TeamsActions';
import { AppContextProvider } from '../../context/AppContext';

const initialState = {
  state: {
    userProfile: {
      name: 'holmes',
      memberProfile: {
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      },
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    teams: [
      {
        id: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
        name: 'string',
        description: 'string'
      },
      {
        id: '3fa4-5717-4562-b3fc-2c963f66afa6',
        name: 'stuff',
        description: ''
      }
    ]
  }
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <TeamsActions />
    </AppContextProvider>
  );
});
