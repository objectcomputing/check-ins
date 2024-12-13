import React from 'react';
import FeedbackSubmitPage from './FeedbackSubmitPage';
import { AppContextProvider } from '../context/AppContext';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { MemoryRouter } from 'react-router-dom';

window.snackDispatch = vi.fn();

const userProfile = {
  id: 'member-id',
  name: 'Mitch Hedberg',
  role: ['MEMBER'],
  workEmail: 'hedbergm@objectcomputing.com',
  title: 'Strategic Placement Specialist',
  location: 'Roseville, Minnesota',
  memberProfile: {
    id: 'member-id',
    bioText: 'Died too young.',
  },
};

const userStateWithPermission = {
  state: {
    csrf: 'csrf',
    userProfile: userProfile,
    teams: [],
    memberProfiles: [
      {
        id: 'some-id',
        name: 'James Johnson',
      },
      userProfile,
    ],
  },
  dispatch: vi.fn(),
};

const server = setupServer(
  http.get('http://localhost:8080/services/feedback/requests/request-id', ({ request }) => {
    return HttpResponse.json(
      {
        'id': 'request-id',
        'status': 'SUBMITTED',
        'requesteeId': userStateWithPermission.state.memberProfiles[0].id,
        'recipientId': userStateWithPermission.state.memberProfiles[1].id,
      },
    );
  }),
  http.get('http://localhost:8080/services/feedback/requests/canceled-request-id', ({ request }) => {
    return HttpResponse.json(
      {
        'id': 'canceled-request-id',
        'status': 'CANCELED',
        'requesteeId': userStateWithPermission.state.memberProfiles[0].id,
        'recipientId': userStateWithPermission.state.memberProfiles[1].id,
      },
    );
  }),
);

beforeAll(() => server.listen({ onUnhandledRequest(request, print) {} }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

it('renders correctly - submitted', async () => {
  await waitForSnapshot(
    'request-id',
    <AppContextProvider value={userStateWithPermission}>
      <MemoryRouter initialEntries={[{pathname: '/feedback/submit',
                                      search: '?request=request-id'}]}>
        <FeedbackSubmitPage />
      </MemoryRouter>
    </AppContextProvider>
  );
});

it('renders correctly - canceled', async () => {
  await waitForSnapshot(
    'canceled-request-id',
    <AppContextProvider value={userStateWithPermission}>
      <MemoryRouter initialEntries={[{pathname: '/feedback/submit',
                                      search: '?request=canceled-request-id'}]}>
        <FeedbackSubmitPage />
      </MemoryRouter>
    </AppContextProvider>
  );
});
