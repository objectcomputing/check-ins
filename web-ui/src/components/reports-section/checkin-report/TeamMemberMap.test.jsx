import { render, screen } from '@testing-library/react';
import TeamMemberMap from './TeamMemberMap';

import { AppContextProvider } from '../../../context/AppContext';

const members = [
  {
    id: 'fb5b1f62-f945-4b74-9f10-2247fcde3ddd',
    name: 'John Doe',
    description: 'Test data for John Doe'
  },
  {
    id: '09251b3b-d54f-4b3d-b3a1-d3cfb3b9a1e5',
    name: 'Jane Doe',
    description: 'Test data for Jane Doe'
  }
];

const initialState = {
  state: {
    userProfile: {
      name: 'holmes',
      memberProfile: {
        id: '3fa4-5717-4562-b3fc-2c963f66afa9',
        pdlId: '',
        title: 'Tester',
        workEmail: 'test@tester.com'
      },
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    }
  }
};

const adminState = { ...initialState };
adminState.state = { ...adminState.state };
adminState.state.userProfile = { ...adminState.state.userProfile };
adminState.state.userProfile.role = ['MEMBER', 'ADMIN'];

describe('TeamMemberMap', () => {
  it('should render the component without team members', () => {
    const withoutTeamMembers = {
      members: [],
      id: '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d',
      closed: true,
      planned: true,
      reportDate: new Date()
    };
    render(
      <AppContextProvider value={initialState}>
        <TeamMemberMap {...withoutTeamMembers} />
      </AppContextProvider>
    );
    const message = screen.getByText(
      'No team members associated with this PDL.'
    );
    expect(message).toBeInTheDocument();
  });

  it('should render the component with team members', () => {
    const withTeamMembers = {
      members,
      id: '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d',
      closed: true,
      planned: true,
      reportDate: new Date()
    };
    render(
      <AppContextProvider value={initialState}>
        <TeamMemberMap {...withTeamMembers} />
      </AppContextProvider>
    );
    const member1 = screen.getByText('John Doe');
    const member2 = screen.getByText('Jane Doe');
    expect(member1).toBeInTheDocument();
    expect(member2).toBeInTheDocument();
  });
});
