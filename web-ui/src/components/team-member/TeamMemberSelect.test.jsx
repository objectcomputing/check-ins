import React from 'react';
import TeamMemberSelect from './TeamMemberSelect';
import { render, fireEvent, screen, act } from '@testing-library/react';

let teamMembers;

const handleMemberSelect = members => {
  console.log('Parent Comp', members);
};

beforeEach(() => {
  teamMembers = [
    { name: 'jesse', image_url: null },
    { name: 'mark', image_url: null },
    { name: 'michael', image_url: null },
    { name: 'mj', image_url: null },
    { name: 'kobe', image_url: null },
    { name: 'lebron', image_url: null }
  ];
});

it('renders correctly', () => {
  snapshot(
    <TeamMemberSelect teamMembers={teamMembers} onChange={handleMemberSelect} />
  );
});

it('clicks single item', done => {
  const name = 'jesse';
  const handleChange = teamMembers => {
    expect(teamMembers).toHaveLength(1);
    expect(teamMembers[0].name === name);
  };
  render(
    <TeamMemberSelect
      teamMembers={teamMembers}
      onChange={handleChange}
      singleSelect
    />
  );
  act(() => {
    fireEvent.click(screen.getByText(name));
  });
});

it('clicks multiple items', done => {
  const name1 = 'jesse';
  const name2 = 'michael';
  const handleChange = teamMembers => {
    if (teamMembers.length === 1) {
      expect(teamMembers[0].name === name1);
    } else if (teamMembers.length === 2) {
      expect(teamMembers[0].name === name1);
      expect(teamMembers[1].name === name2);
    }
  };
  render(
    <TeamMemberSelect teamMembers={teamMembers} onChange={handleChange} />
  );
  act(() => {
    fireEvent.click(screen.getByText(name1));
    fireEvent.click(screen.getByText(name2));
  });
});
