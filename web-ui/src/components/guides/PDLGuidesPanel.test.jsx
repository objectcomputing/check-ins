import React from 'react';
import {AppContextProvider} from '../../context/AppContext';
import PDLGuidesPanel from './PDLGuidesPanel';
import {Router} from 'react-router-dom';
import {createBrowserHistory} from 'history';

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
        name: 'Development Discussion Guide for PDLs',
        url: '/pdfs/Development_Discussion_Guide_for_PDLs.pdf',
      }
    ],
    guilds: [],
    teams: [],
    skills: [],
    userRoles: [],
    memberSkills: [],
    memberProfiles: []
  }
};

const pdlState = {
  state: {
    ...initialState.state,
    userProfile: {
      ...initialState.state.userProfile,
      role: ['MEMBER', 'PDL'],
    },
  }
};

it('renders correctly', () => {
  const customHistory = createBrowserHistory();
  snapshot(
    <Router history={customHistory}>
      <AppContextProvider value={pdlState}>
        <PDLGuidesPanel />
      </AppContextProvider>
    </Router>
  );
});

it("doesn't render for non-pdls", () => {
  const customHistory = createBrowserHistory();
  snapshot(
    <Router history={customHistory}>
      <AppContextProvider value={initialState}>
        <PDLGuidesPanel />
      </AppContextProvider>
    </Router>
  );
});
