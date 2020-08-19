import React, { useContext, useState } from "react";
import MemberIcon from "./MemberIcon";
import { AppContext } from "../../context/AppContext";
import { getMembersByTeam, getTeamsByMember } from "../../api/team";
import { getMember } from "../../api/member";

import "./TeamMember.css";

const TeamMemberContainer = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { id } = userProfile;
  const [selectedProfile, setSelectedProfile] = useState({
    name: null,
    image_url: null,
  });
  const [teamMembers, setTeamMembers] = useState({});
  const [teams, setTeams] = useState([]);
  const [currentTeam, setCurrentTeam] = useState([]);

  // Get member teams
  React.useEffect(() => {
    async function updateTeams() {
      if (id) {
        let res = await getTeamsByMember(id);
        let data =
          res && res.payload && res.payload.status === 200
            ? res.payload.data
            : null;
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
              let res = await getMembersByTeam(team.id);
              let data =
                res && res.payload && res.payload.status === 200
                  ? res.payload.data
                  : null;
              if (data && !res.error) {
                return {
                  [team.id]: await Promise.all(
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
                return { [team.id]: [] };
              }
            })
          ))
        );
        setTeamMembers(teamMemberMap);
      }
    }
    updateTeamMembers();
  }, [teams]);

  const {
    bioText,
    image_url,
    location,
    name,
    pdl,
    role,
    startDate,
    workEmail,
  } = selectedProfile;

  const [PDL, setPDL] = useState(pdl);

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
  let team = teamProfile(currentTeam);

  const mapTeams = teams.map((team) => {
    return (
      <div
        key={team.name}
        onClick={async () => setCurrentTeam(teamMembers[team.id])}
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
              src={
                image_url
                  ? image_url
                  : require("../../images/default_profile.jpg")
              }
            />
          </div>
          <div className="team-member-info">
            <div style={{ textAlign: "left" }}>
              <h2 style={{ margin: 0 }}>{name}</h2>
              <div style={{ display: "flex" }}>
                <div style={{ marginRight: "50px", textAlign: "left" }}>
                  <p>Role: {role}</p>
                  <p>PDL: {PDL}</p>
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
        {team}
      </div>
    </div>
  );
};

export default TeamMemberContainer;
