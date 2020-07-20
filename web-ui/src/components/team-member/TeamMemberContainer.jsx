import React, { useContext, useState } from "react";
import MemberIcon from "./MemberIcon";
import { SkillsContext } from "../../context/SkillsContext";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import { UPDATE_PDL } from "../../context/SkillsContext";

import "./TeamMember.css";

const TeamMemberContainer = ({ isAdmin = false }) => {
  const { state, dispatch } = useContext(SkillsContext);
  const { defaultTeamMembers } = state;
  const [selectedProfile, setSelectedProfile] = useState({
    name: null,
    image_url: null,
  });

  const {
    bioText,
    id,
    image_url,
    insperityId,
    location,
    name,
    pdl,
    role,
    startDate,
    workEmail,
  } = selectedProfile;

  const [PDL, setPDL] = useState(pdl);
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);

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
          onSelectPDL={setPDL}
        ></MemberIcon>
      );
    });

    return team;
  };
  let team = teamProfile(defaultTeamMembers);
  const updatePDL = () => {
    const updatedPDL = {
      name: name,
      id: id,
      role: role,
      location: location,
      workEmail: workEmail,
      insperityId: insperityId,
      startDate: startDate,
      bioText: bioText,
      pdl: PDL,
    };
    dispatch({
      type: UPDATE_PDL,
      payload: updatedPDL,
    });
  };

  return (
    <div>
      {name && (
        <div className="flex-row" style={{ minWidth: "800px" }}>
          <div className="image-div">
            <img
              alt="Profile"
              src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
            />
          </div>
          <div className="team-member-info">
            <div style={{ textAlign: "left" }}>
              <h2 style={{ margin: 0 }}>{name}</h2>
              {isAdmin && updating && (
                <Button
                  style={{
                    backgroundColor: "green",
                    color: "white",
                    marginLeft: "20px",
                  }}
                  onClick={() => {
                    setDisabled(!disabled);
                    setUpdating(!updating);
                    updatePDL();
                  }}
                >
                  Update
                </Button>
              )}
              {isAdmin && !updating && (
                <EditIcon
                  onClick={() => {
                    setDisabled(!disabled);
                    setUpdating(!updating);
                  }}
                  style={{
                    color: "black",
                    marginLeft: "20px",
                  }}
                />
              )}
              <div style={{ display: "flex" }}>
                <div style={{ marginRight: "50px", textAlign: "left" }}>
                  <p>Role: {role}</p>
                  <label htmlFor={pdl} style={{ fontWeight: "unset" }}>
                    PDL:
                  </label>
                  <input
                    disabled={disabled}
                    id={pdl}
                    onChange={(e) => setPDL(e.target.value)}
                    value={PDL}
                  ></input>
                  <p>Location: {location}</p>
                </div>
                <div>
                  <p>
                    Length of Service:
                    {`${time.years > 0 ? time.years + " year(s), " : ""} ${
                      time.months
                    } month(s)`}
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
        {team.length === 0 ? <p>No team members :/</p> : team}
      </div>
    </div>
  );
};

export default TeamMemberContainer;
