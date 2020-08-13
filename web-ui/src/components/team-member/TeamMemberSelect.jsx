import React from "react";
import Avatar from "@material-ui/core/Avatar";

import "./TeamMemberSelect.css";

const TeamMemberSelect = (props) => {
  const { teamMembers, onChange } = props;

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
      {teamMembers.map(renderTeamMember)}
    </div>
  );
};

export default TeamMemberSelect;
