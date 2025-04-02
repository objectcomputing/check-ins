import React from 'react';
import Notes from './Note';
import { AppContextProvider } from '../../context/AppContext';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';
import { setupServer } from 'msw/node';
import { http, HttpResponse } from 'msw';

const mockMemberId = '912834091823';
const mockCheckinId = '837465917381';

const history = createMemoryHistory(
  `/checkins/${mockMemberId}/${mockCheckinId}`
);

vi.mock('react-router-dom', async () => ({
  ...(await vi.importActual('react-router-dom')), // use actual for all non-hook parts
  useParams: () => ({
    memberId: mockMemberId,
    checkinId: mockCheckinId
  }),
  useRouteMatch: () => ({ url: `/checkins/${mockMemberId}/${mockCheckinId}` })
}));

const checkin = {
  id: mockCheckinId,
  teamMemberId: mockMemberId,
  createdbyid: '5425d835-dcd1-4d91-9540-200c06f18f28',
  description: 'updated string'
};

const initialState = {
  state: {
    csrf: 'fake-csrf',
    userProfile: {
      name: 'holmes',
      memberProfile: {
        id: mockMemberId,
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      },
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    memberProfiles: [
      {
        name: 'holmes',
        id: mockMemberId,
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      }
    ],
    checkins: [checkin]
  }
};

const notes = [
  {
    id: 'note-1',
    description: 'Some kind of note...'
  }
];

const server = setupServer(
  http.get(
    `http://localhost:8080/services/checkin-notes?checkinid=${checkin.id}`,
    () => {
      return HttpResponse.json(notes);
    }
  )
);

beforeAll(() => server.listen({ onUnhandledRequest(request, print) {} }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

it('renders correctly', async () => {
  await waitForSnapshot(
    'tiny-mce-checkin-notes',
    <Router history={history}>
      <AppContextProvider value={initialState}>
        <Notes
          checkin={checkin}
          memberName={initialState.state.userProfile.name}
        />
      </AppContextProvider>
    </Router>
  );
});
