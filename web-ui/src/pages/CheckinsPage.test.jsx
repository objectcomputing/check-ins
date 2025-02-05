import React from 'react';
import CheckinsPage from './CheckinsPage';
import { AppContextProvider } from '../context/AppContext';
import { MemoryRouter } from 'react-router-dom';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

const mockMemberId = 'bf9975f8-a5b2-4551-b729-afd56b49e2cc';
const mockCheckinId = '3a1906df-d45c-4ff5-a6f8-7dacba97ff1a';

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    checkins: [
      {
        id: mockCheckinId,
        memberId: mockMemberId,
        createdbyid: '5425d835-dcd1-4d91-9540-200c06f18f28',
        description: 'updated string',
        checkInDate: [2020, 9, 8]
      },
      {
        id: '3a1906df-d45c-4ff5-a6f8-7dacba97ff1b',
        memberId: 'bf9975f8-a5b2-4551-b729-afd56b49e2cc',
        createdbyid: '5425d835-dcd1-4d91-9540-200c06f18f29',
        description: 'second updated string',
        checkInDate: [2020, 10, 18]
      }
    ],
    userProfile: {
      name: 'holmes',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_CREATE_CHECKINS' }],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    memberProfiles: [
      {
        id: mockMemberId,
        name: 'holmes'
      }
    ],
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
    teams: [],
    index: 0
  }
};

it('renders correctly', () => {
  snapshot(
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <MemoryRouter>
        <AppContextProvider value={initialState}>
          <CheckinsPage />
        </AppContextProvider>
      </MemoryRouter>
    </LocalizationProvider>
  );
});
