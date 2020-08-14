import React, { useState } from "react";
import Avatar from "@material-ui/core/Avatar";

import "./TeamMemberSelect.css";

const TeamMemberSelect = (props) => {
  const { teamMembers, onChange, singleSelect = false } = props;
  const [filteredTeamMembers, setFilteredTeamMembers] = useState(teamMembers);

  const filterTeamMembers = (e) => {
    let searchInput = e.target.value;
    let filtered = teamMembers.filter((member) => {
      return member.name.includes(searchInput);
    });
    setFilteredTeamMembers(filtered);
  };

  const selectTeamMember = (member) => {
    member.selected = !member.selected;
    onChange(filteredTeamMembers.filter((m) => m.selected));
    setFilteredTeamMembers([...filteredTeamMembers]);
  };

  const renderTeamMember = (member) => {
    const className = "team-member" + (member.selected ? " selected" : "");
    return (
      <div className={className} onClick={() => selectTeamMember(member)}>
        <Avatar
          alt="Team Member"
          src={
            member.image_url
              ? member.image_url
              : require("../../images/default_profile.jpg")
          }
          style={{ marginLeft: "10px" }}
        />
        <div className="name">{member.name}</div>
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
