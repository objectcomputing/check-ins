import React, { useContext, useState } from "react";
import MemberIcon from "./MemberIcon";
import ProfileContext from "../../context/ProfileContext";
import "./TeamMember.css";

const TeamMemberContainer = () => {
  const context = useContext(ProfileContext);
  // const profiles = context.teamMembers;
  const defaultProfiles = context.defaultTeamMembers;
  const [selectedProfile, setSelectedProfile] = useState({
    name: null,
    image_url: null,
  });
  const {
    bioText,
    image_url,
    location,
    name,
    pdlId,
    role,
    startDate,
    workEmail,
  } = selectedProfile;

  let now = Date.now();

  const monthsandYears = (date, now) => {
    if (!date) {
      return;
    }
    let diff = Math.floor(now - date);
    let day = 1000 * 60 * 60 * 24;

    let days = Math.floor(diff / day);
    let months = Math.floor(days / 31);
    let years = Math.floor(months / 12);
    months %= 12;
    const time = { months: months, years: years };

    return time;
  };

  const time = monthsandYears(startDate, now);

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
  let team = teamProfile(defaultProfiles);
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
          <div className="team-member-info">
            <div style={{ textAlign: "left" }}>
              <h2 style={{ margin: 0 }}>{name}</h2>
              <div style={{ display: "flex" }}>
                <div style={{ marginRight: "50px", textAlign: "left" }}>
                  <p>Role: {role}</p>
                  <p>PDL: {pdlId}</p>
                  <p>Location: {location}</p>
                </div>
                <div>
                  <p>
                    Length of Service:
                    {`${time.years} year(s), ${time.months} month(s)`}
                  </p>
                  <p>Email: {workEmail}</p>
                  <p>Bio: {bioText}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
      <div className="flex-row" style={{ flexWrap: "wrap" }}>
        {team.length === 0 ? <p>"No team members :/"</p> : team}
      </div>
    </div>
  );
};

export default TeamMemberContainer;
