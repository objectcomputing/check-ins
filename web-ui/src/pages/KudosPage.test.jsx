import React from 'react';
import KudosPage from './KudosPage';
import { AppContextProvider } from '../context/AppContext';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { BrowserRouter } from 'react-router-dom';

window.snackDispatch = vi.fn();

const userStateWithPermission = {
  state: {
    csrf: 'csrf',
    userProfile: {
      id: 'current-id',
      name: 'Mitch Hedberg',
      role: ['MEMBER'],
      permissions: [{ permission: '' }],
    },
    teams: [],
    memberProfiles: [
      {
        id: 'some-id',
        firstName: 'First',
        lastName: 'Last',
      },
      {
        id: 'other-id',
        firstName: 'First',
        lastName: 'Last',
      },
    ],
  }
};

const server = setupServer(
  http.get('http://localhost:8080/services/kudos', ({ request }) => {
    return HttpResponse.json([
      {
        id: 'kudos-id',
        message: 'You are great!',
        senderId: 'some-id',
        recipientTeam: null,
        dateCreated: [2024, 11, 11],
        dateApproved: [2024, 11, 11],
        recipientMembers: [
          {id: 'current-id'},
        ],
      },
    ]);
  }),
);

beforeAll(() => server.listen({ onUnhandledRequest(request, print) {} }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

it('renders correctly', () => {
  snapshot(
    <AppContextProvider value={userStateWithPermission}>
      <BrowserRouter>
        <KudosPage />
      </BrowserRouter>
    </AppContextProvider>
  );
});
