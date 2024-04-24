import React from 'react';
import Menu from './Menu';
import { MemoryRouter } from 'react-router-dom';
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
      permissions: [],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    }
  }
};

const adminState = {
  state: {
    userProfile: {
      name: 'holmes',
      memberProfile: {
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      },
      role: ['MEMBER', 'ADMIN'],
      permissions: [{ permission: 'CAN_VIEW_SKILLS_REPORT' }],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    }
  }
};

const pdlState = {
  state: {
    userProfile: {
      name: 'holmes',
      memberProfile: {
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      },
      role: ['MEMBER', 'PDL'],
      permissions: [],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    }
  }
};

describe('<Menu />', () => {
  it('renders correctly', () => {
    snapshot(
      <AppContextProvider value={initialState}>
        <MemoryRouter initialEntries={['/guilds']} keyLength={0}>
          <Menu />
        </MemoryRouter>
      </AppContextProvider>
    );
  });

  it('renders correctly for admin', () => {
    snapshot(
      <AppContextProvider value={adminState}>
        <MemoryRouter initialEntries={['/guilds']} keyLength={0}>
          <Menu />
        </MemoryRouter>
      </AppContextProvider>
    );
  });

  it('renders correctly for pdl', () => {
    snapshot(
      <AppContextProvider value={pdlState}>
        <MemoryRouter initialEntries={['/guilds']} keyLength={0}>
          <Menu />
        </MemoryRouter>
      </AppContextProvider>
    );
  });
});
