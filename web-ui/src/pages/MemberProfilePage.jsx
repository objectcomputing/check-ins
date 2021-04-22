import React, { useContext, useEffect, useState } from "react";
import { AppContext } from "../context/AppContext";
import { getSelectedMemberSkills } from "../api/memberskill";
import { getTeamByMember } from "../api/team";
import { getGuildsForMember } from "../api/guild";

import "./MemberProfilePage.css";

import { Chip } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles({
  search: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  searchInput: {
    width: "20em",
  },
  members: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-evenly",
    width: "100%",
  },
});

const MemberProfilePage = () => {
  const { state } = useContext(AppContext);
  const { csrf, memberProfiles, skills, userProfile } = state;
  const pathname =
    (window &&
      window.location &&
      window.location.pathname &&
      window.location.pathname.split("/")) ||
    null;

  const locationId = pathname[2] || null;

  const selectedMember = state.selectedMember
    ? state.selectedMember
    : memberProfiles.length && locationId
    ? memberProfiles.find((member) => member.id === locationId)
    : null;

  const id = selectedMember
    ? selectedMember.id
    : locationId
    ? locationId
    : null;

  const [selectedMemberSkills, setSelectedMemberSkills] = useState([]);
  const [teams, setTeams] = useState([]);
  const [guilds, setGuilds] = useState([]);

  const levels = [
    {
      value: 1,
      label: "Interested",
    },
    {
      value: 2,
      label: "Novice",
    },
    {
      value: 3,
      label: "Intermediate",
    },
    {
      value: 4,
      label: "Advanced",
    },
    {
      value: 5,
      label: "Expert",
    },
  ];

  useEffect(() => {
    async function getTeamsAndGuilds() {
      if (id) {
        let teamRes = await getTeamByMember(id, csrf);
        let teamData =
          teamRes.payload && teamRes.payload.status === 200
            ? teamRes.payload.data
            : null;
        let memberTeams = teamData && !teamRes.error ? teamData : [];
        setTeams(memberTeams);

        let guildRes = await getGuildsForMember(id, csrf);
        let guildData =
          guildRes.payload && guildRes.payload.status === 200
            ? guildRes.payload.data
            : null;
        let memberGuilds = guildData && !guildRes.error ? guildData : [];
        setGuilds(memberGuilds);
      }
    }
    if (csrf) {
      getTeamsAndGuilds();
    }
  }, [csrf, id]);

  useEffect(() => {
    async function getMemberSkills() {
      if (id) {
        let res = await getSelectedMemberSkills(id, csrf);
        let data =
          res.payload && res.payload.data && !res.error ? res.payload.data : [];
        let memberSkills = skills.filter((skill) => {
          return data.some((mSkill) => {
            if (mSkill.skillid === skill.id) {
              let level = levels.filter(
                (level) => parseInt(mSkill.skilllevel) === level.value
              );
              level
                ? (skill.skilllevel = level[0].label)
                : (skill.skilllevel = mSkill.skilllevel);
              return skill;
            }
          });
        });
        setSelectedMemberSkills(memberSkills);
      }
    }
    if (csrf) {
      getMemberSkills();
    }
  }, [csrf, id, skills]);

  console.log({
    guilds,
    memberProfiles,
    selectedMember,
    selectedMemberSkills,
    teams,
  });

  const classes = useStyles();

  return (
    <div className="member-profile-page">
      <div className="left">
        {!selectedMember && (
          <div className="member-details">
            <h3>No member details found</h3>
          </div>
        )}
        {selectedMember && (
          <div className="member-details">
            <h3>Name: {selectedMember.name || ""}</h3>
            <h4>Bio: {selectedMember.bioText || ""}</h4>
          </div>
        )}
      </div>
      <div className="right">
        <div className="member-skills">
          <h2>Skills</h2>
          {!selectedMemberSkills.length > 0 && (
            <div className="member-skills">
              <h3>No member skills found</h3>
            </div>
          )}
          {selectedMemberSkills.length > 0 &&
            selectedMemberSkills.map((skill) => (
              <Chip
                key={skill.id}
                label={skill.name + " - " + skill.skilllevel}
              />
            ))}
        </div>
        <div className="member-teams">
          <h2>Teams</h2>
          {!teams.length > 0 && (
            <div className="member-teams">
              <h3>No member teams found</h3>
            </div>
          )}
          {teams.length > 0 &&
            teams.map((team) => <Chip key={team.id} label={team.name} />)}
        </div>
        <div className="member-guilds">
          <h2>Guilds</h2>
          {!guilds.length > 0 && (
            <div className="member-guilds">
              <h3>No member guilds found</h3>
            </div>
          )}
          {guilds.length > 0 &&
            guilds.map((guild) => <Chip key={guild.id} label={guild.name} />)}
        </div>
      </div>
    </div>
  );
};

export default MemberProfilePage;
