import React from 'react';
import { render } from '@testing-library/react';
import Menu from './Menu';
import { BrowserRouter } from 'react-router-dom';
import { MemoryRouter } from 'react-router-dom';
import { AppContextProvider } from '../../context/AppContext';

const testId = 'some-id';
const initialState = {
  state: {
    userProfile: {
      name: 'holmes',
      id: testId,
      role: ['MEMBER'],
      permissions: [],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    memberProfiles: [
      {
        id: testId,
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      }
    ]
  }
};

const adminState = {
  state: {
    userProfile: {
      name: 'holmes',
      id: testId,
      role: ['MEMBER', 'ADMIN'],
      permissions: [{ permission: 'CAN_VIEW_SKILLS_REPORT' }],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    memberProfiles: [
      {
        id: testId,
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      }
    ]
  }
};

const pdlState = {
  state: {
    userProfile: {
      name: 'holmes',
      id: testId,
      role: ['MEMBER', 'PDL'],
      permissions: [],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    memberProfiles: [
      {
        id: testId,
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      }
    ]
  }
};

describe('<Menu />', () => {
  let originalMatchMedia;
  beforeAll(() => {
    originalMatchMedia = window.matchMedia;
    window.matchMedia = query => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: () => {}, // Deprecated
      removeListener: () => {}, // Deprecated
      addEventListener: () => {},
      removeEventListener: () => {},
      dispatchEvent: () => {}
    });
  });

  afterAll(() => {
    window.matchMedia = originalMatchMedia;
  });
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

  it('adds link to avatar', () => {
    const { container } = render(
      <AppContextProvider value={initialState}>
        <BrowserRouter>
          <Menu />
        </BrowserRouter>
      </AppContextProvider>
    );
    const link = container.querySelector('header > a');
    const href = link.getAttribute('href');
    const lastIndex = href.lastIndexOf('/');
    const id = href.substring(lastIndex + 1);
    expect(id).toBe(testId);
  });
});
