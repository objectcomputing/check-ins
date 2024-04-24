import React, { useState } from 'react';
import Avatar from '@mui/material/Avatar';

import './TeamMemberSelect.css';

const TeamMemberSelect = props => {
  const { teamMembers, onChange, singleSelect = false } = props;
  const [filteredTeamMembers, setFilteredTeamMembers] = useState(teamMembers);

  const filterTeamMembers = e => {
    let searchInput = e.target.value.toLowerCase();
    let filtered = teamMembers.filter(member => {
      return member.name.toLowerCase().includes(searchInput);
    });
    setFilteredTeamMembers(filtered);
  };

  const selectMultipleTeamMembers = member => {
    member.selected = !member.selected;
    onChange(filteredTeamMembers.filter(m => m.selected));
    setFilteredTeamMembers([...filteredTeamMembers]);
  };

  const selectSingleTeamMember = member => {
    filteredTeamMembers.map(m =>
      m.name !== member.name ? (m.selected = false) : null
    );
    member.selected = !member.selected;
    onChange([member]);
    setFilteredTeamMembers([...filteredTeamMembers]);
  };

  const renderTeamMember = member => {
    const className = 'team-member' + (member.selected ? ' selected' : '');
    return (
      <div
        className={className}
        key={member.name}
        onClick={() =>
          singleSelect
            ? selectSingleTeamMember(member)
            : selectMultipleTeamMembers(member)
        }
      >
        <Avatar
          alt="Team Member"
          src={
            member.image_url
              ? member.image_url
              : '../../images/default_profile.jpg'
          }
          style={{ marginLeft: '10px' }}
        />
        <div className="name">{member.name}</div>
      </div>
    );
  };

  return (
    <div className="team-member-select">
      <input
        placeholder="Search team members"
        onChange={e => filterTeamMembers(e)}
      ></input>
      {filteredTeamMembers.map(renderTeamMember)}
    </div>
  );
};

export default TeamMemberSelect;
