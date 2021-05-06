import React, { useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { AppContext } from "../context/AppContext";
import { getSelectedMemberSkills } from "../api/memberskill";
import { getTeamByMember } from "../api/team";
import { getGuildsForMember } from "../api/guild";
import { getAvatarURL } from "../api/api.js";
import ProfilePage from "./ProfilePage";

import "./MemberProfilePage.css";

import { Avatar, Chip } from "@material-ui/core";

const MemberProfilePage = () => {
  const { state } = useContext(AppContext);
  const { csrf, memberProfiles, skills, userProfile } = state;
  const { memberId } = useParams();

  const selectedMember = state.selectedMember
    ? state.selectedMember
    : memberProfiles.length && memberId
    ? memberProfiles.find((member) => member.id === memberId)
    : null;

  const id = selectedMember ? selectedMember.id : memberId ? memberId : null;

  const [selectedMemberSkills, setSelectedMemberSkills] = useState([]);
  const [teams, setTeams] = useState([]);
  const [guilds, setGuilds] = useState([]);

  const isCurrentUser =
    userProfile &&
    userProfile.memberProfile &&
    selectedMember &&
    userProfile.memberProfile.id === selectedMember.id;

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
        memberTeams.sort((a, b) => a.name.localeCompare(b.name));
        setTeams(memberTeams);

        let guildRes = await getGuildsForMember(id, csrf);
        let guildData =
          guildRes.payload && guildRes.payload.status === 200
            ? guildRes.payload.data
            : null;
        let memberGuilds = guildData && !guildRes.error ? guildData : [];
        memberGuilds.sort((a, b) => a.name.localeCompare(b.name));
        setGuilds(memberGuilds);
      }
    }
    if (csrf) {
      getTeamsAndGuilds();
    }
  }, [csrf, id]);

  useEffect(() => {
    async function getMemberSkills() {
      if (!id) return;
      let res = await getSelectedMemberSkills(id, csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : [];
      let memberSkills = skills.filter((skill) => {
        //filter out memberSkills and set level
        return data.some((mSkill) => {
          if (mSkill.skillid === skill.id) {
            let level = levels.filter(
              (level) => parseInt(mSkill.skilllevel) === level.value
            );
            level && level[0] && level[0].label
              ? (skill.skilllevel = level[0].label)
              : skill.skilllevel === mSkill.skilllevel &&
                mSkill.skilllevel !== undefined
              ? (skill.skilllevel = mSkill.skilllevel)
              : (skill.skilllevel = "Intermediate");
            return skill;
          }
          return null;
        });
      });
      memberSkills.sort((a, b) => a.name.localeCompare(b.name));
      setSelectedMemberSkills(memberSkills);
    }
    if (csrf) {
      getMemberSkills();
    }
    // complains about needing 'levels' but levels is a const
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf, id, skills, selectedMember]);

  return (
    <div>
      {isCurrentUser ? (
        <ProfilePage />
      ) : (
        <div className="profile-page">
          <div className="left">
            {!selectedMember && (
              <div className="profile-details">
                <h3>No member details found</h3>
              </div>
            )}
            {selectedMember && (
              <div className="profile-details">
                <Avatar
                  className="avatar"
                  src={getAvatarURL(selectedMember.workEmail)}
                />
                <h3>Name: {selectedMember.name || ""}</h3>
                <h3>Bio: {selectedMember.bioText || ""}</h3>
                <h3>Email: {selectedMember.workEmail || ""}</h3>
                <h3>Location: {selectedMember.location || ""}</h3>
              </div>
            )}
          </div>
          <div className="right">
            <div className="profile-skills">
              <h2>Skills</h2>
              {!selectedMemberSkills.length > 0 && (
                <div className="profile-skills">
                  <h3>No member skills found</h3>
                </div>
              )}
              {selectedMemberSkills.length > 0 &&
                selectedMemberSkills.map((skill) => (
                  <Chip
                    className="chip"
                    color="primary"
                    key={skill.id}
                    label={skill.name + " - " + skill.skilllevel}
                  />
                ))}
            </div>
            <div className="profile-teams">
              <h2>Teams</h2>
              {!teams.length > 0 && (
                <div className="profile-teams">
                  <h3>No member teams found</h3>
                </div>
              )}
              {teams.length > 0 &&
                teams.map((team) => (
                  <Chip
                    className="chip"
                    color="primary"
                    key={team.id}
                    label={team.name}
                  />
                ))}
            </div>
            <div className="profile-guilds">
              <h2>Guilds</h2>
              {!guilds.length > 0 && (
                <div className="profile-guilds">
                  <h3>No member guilds found</h3>
                </div>
              )}
              {guilds.length > 0 &&
                guilds.map((guild) => (
                  <Chip
                    className="chip"
                    color="primary"
                    key={guild.id}
                    label={guild.name}
                  />
                ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MemberProfilePage;
