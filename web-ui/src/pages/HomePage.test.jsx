import React from 'react';
import HomePage from './HomePage';
import { AppContextProvider } from '../context/AppContext';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

it('renders correctly', () => {
  const history = createMemoryHistory('/');
  snapshot(
    <Router history={history}>
      <AppContextProvider>
        <HomePage />
      </AppContextProvider>
    </Router>
  );
});
