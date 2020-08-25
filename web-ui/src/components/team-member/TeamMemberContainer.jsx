import React, { useContext, useState } from "react";
import MemberIcon from "./MemberIcon";
import { AppContext } from "../../context/AppContext";
import { getMembersByTeam, getTeamsByMember } from "../../api/team";
import { getMember } from "../../api/member";

import "./TeamMember.css";

const TeamMemberContainer = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const id =
    userProfile && userProfile.memberProfile
      ? userProfile.memberProfile.uuid
      : undefined;

  const [selectedProfile, setSelectedProfile] = useState({
    name: null,
    image_url: null,
  });
  const [teamMembers, setTeamMembers] = useState({});
  const [teams, setTeams] = useState([]);
  const [currentTeam, setCurrentTeam] = useState([]);
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

  const [pdl, setPDL] = useState();

  // Get PDL's name
  React.useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId);
        let pdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : "");
      }
    }
    getPDLName();
  }, [pdlId]);

  // Get member teams
  React.useEffect(() => {
    async function updateTeams() {
      if (id) {
        let res = await getTeamsByMember(id);
        let data =
          res.payload && res.payload.status === 200 ? res.payload.data : null;
        let memberTeams = data && !res.error ? data : [];
        setTeams(memberTeams);
      }
    }
    updateTeams();
  }, [id]);

  React.useEffect(() => {
    async function updateTeamMembers() {
      if (teams) {
        const teamMemberMap = Object.assign(
          {},
          ...(await Promise.all(
            teams.map(async (team) => {
              let res = await getMembersByTeam(team.uuid);
              let data =
                res && res.payload && res.payload.status === 200
                  ? res.payload.data
                  : null;
              if (data && !res.error) {
                return {
                  [team.uuid]: await Promise.all(
                    data.map(async (member) => {
                      let res = await getMember(member.memberid);
                      let data =
                        res &&
                        res.payload &&
                        res.payload.status === 200 &&
                        !res.error
                          ? res.payload.data
                          : null;
                      return data;
                    })
                  ),
                };
              } else {
                return { [team.uuid]: [] };
              }
            })
          ))
        );
        setTeamMembers(teamMemberMap);
      }
    }
    updateTeamMembers();
  }, [teams]);

  let teamProfile = (profiles) => {
    let team = profiles.map((profile) => {
      return (
        <MemberIcon
          key={`profile-${profile.workEmail}`}
          profile={profile}
          onSelect={setSelectedProfile}
          onSelectPDL={setPDL}
        ></MemberIcon>
      );
    });

    return team;
  };
  let team = teamProfile(currentTeam);

  const mapTeams = teams.map((team) => {
    return (
      <div
        key={`team-${team.uuid}`}
        onClick={async () => setCurrentTeam(teamMembers[team.uuid])}
      >
        {team.name.toUpperCase()}
      </div>
    );
  });

  return (
    <div>
      <div className="team-names">{mapTeams}</div>
      {name && (
        <div className="flex-row" style={{ minWidth: "800px" }}>
          <div className="image-div">
            <img
              alt="Profile"
              src={image_url ? image_url : "/default_profile.jpg"}
            />
          </div>
          <div className="team-member-info">
            <div style={{ textAlign: "left" }}>
              <h2 style={{ margin: 0 }}>{name}</h2>
              <div style={{ display: "flex" }}>
                <div style={{ marginRight: "50px", textAlign: "left" }}>
                  <p>Role: {role}</p>
                  <p>PDL: {pdl}</p>
                  <p>Location: {location}</p>
                </div>
                <div>
                  <p>
                    Start Date:{" "}
                    {new Date(
                      startDate[0],
                      startDate[1] - 1,
                      startDate[2]
                    ).toLocaleDateString()}
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
        {team}
      </div>
    </div>
  );
};

export default TeamMemberContainer;
