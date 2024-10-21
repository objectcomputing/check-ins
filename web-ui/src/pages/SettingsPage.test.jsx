import React from 'react';
import SettingsPage from './SettingsPage';
import { AppContextProvider } from '../context/AppContext';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { BrowserRouter } from 'react-router-dom';

const initialState = {
  state: {
    csrf: 'csrf',
    userProfile: {
      name: 'Current User',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_ADMINISTER_SETTINGS' }],
    },
    loading: {
      teams: [],
    },
  }
};

const server = setupServer(
  http.get('http://localhost:8080/services/settings/options', ({ request }) => {
    return HttpResponse.json([
      {
        'name': 'STRING_SETTING',
        'description': 'The description',
        'category': 'THEME',
        'type': 'STRING',
        'value': 'The value',
      },
      {
        'name': 'OTHER_SETTING',
        'description': 'The description',
        'category': 'THEME',
        'type': 'NUMBER',
        'value': '42',
      },
      {
        'name': 'ANOTHER_SETTING',
        'description': 'The description',
        'category': 'INTEGRATIONS',
        'type': 'BOOLEAN',
        'value': 'false',
      },
    ]);
  }),
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('SettingsPage', () => {
  it('renders correctly', async () => {
    await waitForSnapshot(
      // There are two settings with the THEME category.  If the page is
      // rendered correctly, there will only be one THEME category heading.
      'THEME',
      <AppContextProvider value={initialState}>
        <BrowserRouter>
          <SettingsPage />
        </BrowserRouter>
      </AppContextProvider>
    );
  });

  it('renders an error if user does not have appropriate permission', () => {
    snapshot(
      <AppContextProvider>
        <BrowserRouter>
          <SettingsPage />
        </BrowserRouter>
      </AppContextProvider>
    );
  });
});
