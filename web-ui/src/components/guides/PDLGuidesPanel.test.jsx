import React from 'react';
import { AppContextProvider } from '../../context/AppContext';
import PDLGuidesPanel from './PDLGuidesPanel';
import { render } from '@testing-library/react';
import { Router } from 'react-router-dom';
import { createBrowserHistory } from 'history';

const initialState = {
  state: {
    userProfile: {
      name: 'holmes',
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    }
  }
};

const pdlState = {
  state: {
    userProfile: {
      ...initialState.state.userProfile,
      role: ['MEMBER', 'PDL']
    }
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
