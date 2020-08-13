import React, { useState } from "react";
import Avatar from "@material-ui/core/Avatar";

import "./TeamMemberSelect.css";

const TeamMemberSelect = (props) => {
  const { teamMembers, onChange } = props;
  const [filteredTeamMembers, setFilteredTeamMembers] = useState(teamMembers);

  const filterTeamMembers = (e) => {
    let searchInput = e.target.value;
    let filtered = teamMembers.filter((member) => {
      return member.name.includes(searchInput);
    });
    setFilteredTeamMembers(filtered);
  };

  const selectTeamMember = (e, member) => {
    const { checked } = e.target;

    member.selected = checked;
    onChange(member);
  };

  const renderTeamMember = (member) => {
    return (
      <div className="team-member">
        <Avatar
          alt="Team Member"
          src={
            member.image_url
              ? member.image_url
              : require("../../images/default_profile.jpg")
          }
        />
        <div className="name">{member.name}</div>
        <input
          type="checkbox"
          checked={member.selected}
          onChange={(e) => selectTeamMember(e, member)}
        ></input>
      </div>
    );
  };

  return (
    <div className="team-member-select">
      <input
        placeholder="Search team members"
        onChange={(e) => filterTeamMembers(e)}
      ></input>
      {filteredTeamMembers.map(renderTeamMember)}
    </div>
  );
};

export default TeamMemberSelect;
