import React from 'react';
import { AppContextProvider } from '../../context/AppContext';
import EditTeamModal from './EditTeamModal';
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

const testTeam = {
  id: '54345',
  name: 'Test Team',
  description: 'A team used for testing.',
  teamLeads: [
    { id: 123, name: 'Team Leader', lastName: 'Leader' },
    { id: 124, name: 'Other Leader', lastName: 'OLeader' }
  ],
  teamMembers: [
    { id: 125, name: 'Team Member', lastName: 'Member' },
    { id: 126, name: 'Other Member', lastName: 'OMember' }
  ]
};

const emptyTeam = {
  id: '64346',
  name: 'Empty Team',
  description: 'A empty team used for testing.'
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
    guilds: [],
    teams: [testTeam, emptyTeam],
    skills: [],
    roles: [],
    userRoles: [],
    memberSkills: [],
    index: 0,
    memberProfiles: [
      currentUserProfile,
      { id: 123, name: 'Team Leader', lastName: 'Leader' },
      { id: 124, name: 'Other Leader', lastName: 'OLeader' },
      { id: 125, name: 'Team Member', lastName: 'Member' },
      { id: 126, name: 'Other Member', lastName: 'OMember' }
    ]
  }
};

describe('EditTeamModal', () => {
  it('User added as lead when none exists', async () => {
    const mockOnSave = vi.fn();

    render(
      <AppContextProvider value={initialState}>
        <EditTeamModal
          team={emptyTeam}
          open={true}
          onSave={mockOnSave}
          onClose={vi.fn()}
          headerText="Edit your team"
        />
      </AppContextProvider>
    );

    await waitFor(() => screen.getByText(/Edit your team/i));

    const teamNameInput = screen.getByLabelText(/Team Name/i);
    const teamDescriptionInput = screen.getByLabelText(/Description/i);

    const expectedTeam = {
      ...emptyTeam,
      teamMembers: [
        {
          id: undefined,
          memberId: currentUserProfile.id,
          name: currentUserProfile.name,
          teamId: emptyTeam.id,
          lead: true
        }
      ]
    };
    expect(teamNameInput).toHaveValue(emptyTeam.name);
    expect(teamDescriptionInput).toHaveValue(emptyTeam.description);

    const saveBtn = screen.getByText(/Save Team/i);
    // expect(saveBtn).toBeDisabled();
    await userEvent.click(saveBtn);
    await waitFor(() => {
      expect(mockOnSave).toHaveBeenCalledWith(expectedTeam);
    });
  });

  it('Can save with lead', async () => {
    const mockOnSave = vi.fn();

    render(
      <AppContextProvider value={initialState}>
        <EditTeamModal
          team={testTeam}
          open={true}
          onSave={mockOnSave}
          onClose={vi.fn()}
          headerText="Edit your team"
        />
      </AppContextProvider>
    );

    await waitFor(() => screen.getByText(/Edit your team/i));

    const teamNameInput = screen.getByLabelText(/Team Name/i);
    const teamDescriptionInput = screen.getByLabelText(/Description/i);

    expect(teamNameInput).toHaveValue(testTeam.name);
    expect(teamDescriptionInput).toHaveValue(testTeam.description);

    const saveBtn = screen.getByText(/Save Team/i);
    expect(saveBtn).toBeEnabled();
    await userEvent.click(saveBtn);
    await waitFor(() => {
      expect(mockOnSave).toHaveBeenCalled();
    });
  });
});
