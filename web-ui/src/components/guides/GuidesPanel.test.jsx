import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import GuidesPanel from './GuidesPanel';
import { AppContextProvider } from '../../context/AppContext.jsx';

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    userProfile: {
      name: 'holmes',
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    roles: [
      { id: 1, role: 'MEMBER' },
      { id: 2, role: 'PDL' }
    ],
    mockuments: [
      {
        id: 'mockument-1',
        name: 'Expectations Discussion Guide for Team Members',
        url: '/pdfs/Expectations_Discussion_Guide_for_Team_Members.pdf',
        description: 'My description'
      },
      {
        id: 'mockument-2',
        name: 'Expectations Worksheet',
        url: '/pdfs/Expectations_Worksheet.pdf'
      }
    ],
    teams: []
  }
};


it('renders correctly', () => {
  snapshot(
    <MemoryRouter>
      <AppContextProvider value={initialState}>
        <GuidesPanel />
      </AppContextProvider>
    </MemoryRouter>
  );
});
