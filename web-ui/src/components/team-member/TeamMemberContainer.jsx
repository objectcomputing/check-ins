import React, { useState } from "react";
import MemberIcon from "./MemberIcon";
import "./TeamMember.css";

const TeamMemberContainer = (props) => {
  const { profiles } = props;
  const [selectedProfile, setSelectedProfile] = useState({
    name: null,
    image_url: null,
  });
  const { name, image_url } = selectedProfile;

  let teamProfile = (profiles) => {
    let team = profiles.map((profile) => {
      return (
        <MemberIcon
          key={profile.name}
          profile={profile}
          onSelect={setSelectedProfile}
        ></MemberIcon>
      );
    });
    return team;
  };
  let team = teamProfile(profiles);
  return (
    <div>
      {name && (
        <div className="flex-row">
          <div className="image-div">
            <img
              alt="Profile"
              src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
            />
          </div>
          <div className="flex-row">
            <div style={{ marginTop: "50%", textAlign: "left" }}>
              <h2 style={{ margin: 0 }}>{name}</h2>
              <div>
                <p>Role</p>
                <p>Email</p>
                <p>Current PDL</p>
              </div>
            </div>
          </div>
        </div>
      )}
      <div className="flex-row" style={{ flexWrap: "wrap" }}>
        {team}
      </div>
    </div>
  );
};

export default TeamMemberContainer;
