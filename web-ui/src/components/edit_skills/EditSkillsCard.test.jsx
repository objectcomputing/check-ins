import React from 'react';
import EditSkillsCard from './EditSkillsCard';
import EditSkillsPage from '../../pages/EditSkillsPage';
import { AppContextProvider } from '../../context/AppContext';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { BrowserRouter } from 'react-router-dom';

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: 'Current User',
  firstName: 'Current',
  lastName: 'User'
};

const initialState = {
  state: {
    csrf: 'blah',
    userProfile: {
      name: 'Current User',
      firstName: 'Current',
      lastName: 'User',
      role: ['MEMBER'],
      permissions: [{ permission: 'CAN_VIEW_SKILLS_REPORT' }],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg',
      memberProfile: currentUserProfile
    },
    skills: [
      { id: '918275', name: 'skill1', description: 'first' },
      { id: '9183455', name: 'skill2', description: 'second' }
    ],
    checkins: [],
    guilds: [],
    teams: [],
    roles: [],
    userRoles: [],
    memberSkills: [],
    index: 0,
    memberProfiles: [
      currentUserProfile,
      { id: 123, name: 'Guild Leader' },
      { id: 124, name: 'Other Leader' },
      { id: 125, name: 'Guild Member' },
      { id: 126, name: 'Other Member' }
    ]
  }
};

const pendingSkill = {
  extraneous: false,
  id: '1134511f3e-7ab7-4edf-86f5-ab0b0a0d2ca9',
  name: 'Le test skill',
  pending: false,
  description: 'le description'
};

let open = true;
const handleClose = () => (open = false);

const server = setupServer(
  http.get('http://localhost:8080/services/member-skills', ({ request }) => {
    return HttpResponse.json([
      {
        id: '74422962-03ef-4957-b713-11def435db1d',
        memberid: currentUserProfile.id,
        skillid: pendingSkill.id,
        skilllevel: '3'
      }
    ]);
  })
);

beforeAll(() => server.listen({ onUnhandledRequest(request, print) {} }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('EditSkillsCard', () => {
  it('renders correctly with skill', async () => {
    await waitForSnapshot(
      'skill-submitted-by',
      <AppContextProvider value={initialState}>
        <BrowserRouter>
          <EditSkillsCard skill={pendingSkill} />
        </BrowserRouter>
      </AppContextProvider>
    );
  });

  it('renders correctly', () => {
    snapshot(
      <AppContextProvider value={initialState}>
        <BrowserRouter>
          <EditSkillsPage />
        </BrowserRouter>
      </AppContextProvider>
    );
  });

  it('renders an error if user does not have appropriate permission', () => {
    snapshot(
      <AppContextProvider>
        <BrowserRouter>
          <EditSkillsPage />
        </BrowserRouter>
      </AppContextProvider>
    );
  });
});
