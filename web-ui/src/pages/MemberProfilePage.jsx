import React, { useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { AppContext } from "../context/AppContext";
import { getSelectedMemberSkills } from "../api/memberskill";
import { getTeamByMember } from "../api/team";
import { getGuildsForMember } from "../api/guild";
import { getAvatarURL } from "../api/api.js";
import ProfilePage from "./ProfilePage";
import { levelList } from "../context/util";

import "./MemberProfilePage.css";

import {
  Avatar,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Container,
  Grid,
  Tooltip,
  Typography,
} from "@material-ui/core";

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
            skill.skilllevel = levelList[mSkill.skilllevel || 3];
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
    <>
      {isCurrentUser ? (
        <ProfilePage />
      ) : (
        <Grid container className="profile-page">
          <Grid item md={4} className="left">
            {!selectedMember && (
              <div className="profile-details">
                <h3>No member details found</h3>
              </div>
            )}
            {selectedMember && (
              <Card className="member-profile-card">
                <CardHeader
                  title={
                    <Typography variant="h5" component="h1">
                      {selectedMember.name}
                    </Typography>
                  }
                  subheader={
                    <Typography color="textSecondary" component="h2">
                      {selectedMember.title}
                    </Typography>
                  }
                  disableTypography
                  avatar={
                    <Avatar
                      className="large"
                      src={getAvatarURL(selectedMember.workEmail)}
                    />
                  }
                />
                <CardContent>
                  <Container fixed className="info-container">
                    <Typography
                      variant="body2"
                      color="textSecondary"
                      component="p"
                    >
                      <h4>Email: {selectedMember.workEmail || ""}</h4>
                      <h4>Location: {selectedMember.location || ""}</h4>
                      <h4>Bio: {selectedMember.bioText || ""}</h4>
                    </Typography>
                  </Container>
                </CardContent>
              </Card>
            )}
          </Grid>
          <Grid item md={7} className="right">
            <Card>
              <CardHeader
                title="Skills"
                titleTypographyProps={{ variant: "h5", component: "h1" }}
              />
              <CardContent>
                <div className="profile-skills">
                  {!selectedMemberSkills.length > 0 && (
                    <div className="profile-skills">
                      <h3>No skills found</h3>
                    </div>
                  )}
                  {selectedMemberSkills.length > 0 &&
                    selectedMemberSkills.map((skill, index) =>
                      skill.description ? (
                        <Tooltip
                          enterTouchDelay={0}
                          placement="top-start"
                          title={skill.description}
                        >
                          <Chip
                            className="chip"
                            color="primary"
                            key={skill.id}
                            label={
                              skill.name +
                              " - " +
                              skill.skilllevel.toLowerCase()
                            }
                          />
                        </Tooltip>
                      ) : (
                        <Chip
                          className="chip"
                          color="primary"
                          key={skill.id}
                          label={
                            skill.name + " - " + skill.skilllevel.toLowerCase()
                          }
                        />
                      )
                    )}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader
                title="Teams"
                titleTypographyProps={{ variant: "h5", component: "h1" }}
              />
              <CardContent>
                <div className="profile-teams">
                  {!teams.length > 0 && (
                    <div className="profile-teams">
                      <h3>No teams found</h3>
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
              </CardContent>
            </Card>
            <Card>
              <CardHeader
                title="Guilds"
                titleTypographyProps={{ variant: "h5", component: "h1" }}
              />
              <CardContent>
                <div className="profile-guilds">
                  {!guilds.length > 0 && (
                    <div className="profile-guilds">
                      <h3>No guilds found</h3>
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
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </>
  );
};

export default MemberProfilePage;
