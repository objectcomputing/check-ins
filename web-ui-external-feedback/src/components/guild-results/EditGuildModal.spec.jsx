import React from 'react';
import { AppContextProvider } from '../../context/AppContext';
import EditGuildModal from './EditGuildModal';
import { http } from 'msw';
import { setupServer } from 'msw/node';
import { render, waitFor, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

window.snackDispatch = vi.fn();

const server = setupServer(
  http.get('http://localhost:8080/csrf/cookie', () => {
    return HttpResponse.text('O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi');
  }),
  http.get('http://localhost:8080/services/member-profiles/current', () => {
    return HttpResponse.json({ id: '12345', name: 'Test User' });
  }),
  http.get('http://localhost:8080/services/teams/members', () => {
    return HttpResponse.json([{ id: '12345', name: 'Test User' }]);
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

const testGuild = {
  name: 'Test Guild',
  description: 'A guild used for testing.',
  guildLeads: [
    { id: 123, name: 'Guild Leader' },
    { id: 124, name: 'Other Leader' }
  ],
  guildMembers: [
    { id: 125, name: 'Guild Member' },
    { id: 126, name: 'Other Member' }
  ],
  active: true,
};

const emptyGuild = {
  name: 'Test Guild',
  description: 'A guild used for testing.',
  active: true,
};

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: 'Current User',
  firstName: 'Current',
  lastName: 'User'
};

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    userProfile: {
      name: 'Current User',
      firstName: 'Current',
      lastName: 'User',
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg',
      memberProfile: currentUserProfile
    },
    checkins: [],
    guilds: [testGuild, emptyGuild],
    teams: [],
    skills: [],
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

it('Cannot save without lead', async () => {
  const mockOnSave = vi.fn();

  render(
    <AppContextProvider value={initialState}>
      <EditGuildModal
        guild={testGuild}
        open={true}
        onSave={mockOnSave}
        onClose={vi.fn()}
        headerText="Edit your guild"
      />
    </AppContextProvider>
  );

  await waitFor(() => screen.getByText(/Edit your guild/i));

  const guildNameInput = screen.getByLabelText(/Guild Name/i);
  const guildDescriptionInput = screen.getByLabelText(/Description/i);

  expect(guildNameInput).toHaveValue(testGuild.name);
  expect(guildDescriptionInput).toHaveValue(testGuild.description);

  const saveBtn = screen.getByText(/Save Guild/i);
  expect(saveBtn).toBeEnabled();
  await userEvent.click(saveBtn);
  await waitFor(() => {
    expect(mockOnSave).toHaveBeenCalled();
  });
});
