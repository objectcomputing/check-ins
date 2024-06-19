import React, { useContext, useEffect, useState } from 'react';
import { useHistory, useParams } from 'react-router-dom';

import {
  selectCurrentUserId,
  selectIsAdmin,
  selectOrderedMemberFirstName,
  selectOrderedPdls,
  selectProfile,
  selectTerminatedMembers,
  selectSupervisorHierarchyIds
} from '../context/selectors';
import { AppContext } from '../context/AppContext';
import { getSelectedMemberSkills } from '../api/memberskill';
import { getTeamByMember } from '../api/team';
import { getGuildsForMember } from '../api/guild';
import { getAvatarURL } from '../api/api.js';
import ProfilePage from './ProfilePage';
import CertificationBadges from '../components/certifications/CertificationBadges';
import VolunteerBadges from '../components/volunteer/VolunteerBadges';
import { levelList } from '../context/util';

import {
  Avatar,
  Button,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Container,
  Grid,
  Tooltip,
  Typography
} from '@mui/material';

import './MemberProfilePage.css';

const MemberProfilePage = () => {
  const { state } = useContext(AppContext);
  const history = useHistory();
  const { csrf, skills, userProfile } = state;
  const { memberId } = useParams();
  const [selectedMember, setSelectedMember] = useState(null);
  const [lastSeen, setLastSeen] = useState('');
  const sortedPdls = selectOrderedPdls(state);
  const sortedMembers = selectOrderedMemberFirstName(state);
  const isAdmin = selectIsAdmin(state);
  const currentUserId = selectCurrentUserId(state);
  const pdlInfo =
    sortedPdls && sortedPdls.find(pdl => pdl?.id === selectedMember?.pdlId);
  const supervisorInfo =
    sortedMembers &&
    sortedMembers.find(
      memberProfile => memberProfile?.id === selectedMember?.supervisorid
    );
  const supervisorChain = selectSupervisorHierarchyIds(selectedMember)(state);
  const currentUserIsPdl = pdlInfo?.id === currentUserId;
  const currentUserIsSupervisor = supervisorChain.includes(currentUserId);
  const canRequestFeedback =
    isAdmin || currentUserIsPdl || currentUserIsSupervisor;

  useEffect(() => {
    // in the case of a terminated member, member details will still display
    const member = selectProfile(state, memberId);
    const terminatedMember = selectTerminatedMembers(state)?.filter(
      terminatedMember => terminatedMember.id === memberId
    );
    if (member) {
      setSelectedMember(member);
      const { lastSeen } = member;
      setLastSeen(`${lastSeen[1]}/${lastSeen[2]}/${lastSeen[0]}`);
    } else if (terminatedMember) {
      setSelectedMember(terminatedMember[0]);
    }
  }, [memberId, state]);

  const [selectedMemberSkills, setSelectedMemberSkills] = useState([]);
  const [teams, setTeams] = useState([]);
  const [guilds, setGuilds] = useState([]);
  const isCurrentUser = userProfile?.memberProfile?.id === memberId;

  useEffect(() => {
    async function getTeamsAndGuilds() {
      if (memberId) {
        let teamRes = await getTeamByMember(memberId, csrf);
        let teamData =
          teamRes.payload && teamRes.payload.status === 200
            ? teamRes.payload.data
            : null;
        let memberTeams = teamData && !teamRes.error ? teamData : [];
        memberTeams.sort((a, b) => a.name.localeCompare(b.name));
        setTeams(memberTeams);

        let guildRes = await getGuildsForMember(memberId, csrf);
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
  }, [csrf, memberId]);

  useEffect(() => {
    async function getMemberSkills() {
      if (!memberId) return;
      const res = await getSelectedMemberSkills(memberId, csrf);
      const data =
        res.payload && res.payload.data && !res.error ? res.payload.data : [];
      const memberSkills = (skills || []).filter(skill => {
        // Filter out memberSkills and set level.
        return data.some(mSkill => {
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
  }, [csrf, memberId, skills, selectedMember]);

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
                      component="div"
                    >
                      <h4>Last Seen: {lastSeen}</h4>
                      <h4>Email: {selectedMember.workEmail || ''}</h4>
                      <h4>Location: {selectedMember.location || ''}</h4>
                      <h4>Bio: {selectedMember.bioText || ''}</h4>
                      <h4>
                        {(supervisorInfo &&
                          'Supervisor: ' +
                            supervisorInfo.firstName +
                            ' ' +
                            supervisorInfo.lastName) ||
                          ''}
                      </h4>
                      <h4>
                        {(pdlInfo &&
                          'PDL: ' +
                            pdlInfo.firstName +
                            ' ' +
                            pdlInfo.lastName) ||
                          ''}
                      </h4>
                    </Typography>
                  </Container>
                  {canRequestFeedback && (
                    <Container
                      fixed
                      sx={{ display: 'flex', justifyContent: 'center' }}
                    >
                      <Button
                        variant="outlined"
                        color="primary"
                        onClick={e => {
                          e.stopPropagation();
                          history.push(`/feedback/request?for=${memberId}`);
                        }}
                      >
                        Request Feedback
                      </Button>
                    </Container>
                  )}
                </CardContent>
              </Card>
            )}
          </Grid>
          <Grid item md={7} className="right">
            <Card>
              <CardHeader
                title="Skills"
                titleTypographyProps={{ variant: 'h5', component: 'h1' }}
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
                              ' - ' +
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
                            skill.name + ' - ' + skill.skilllevel.toLowerCase()
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
                titleTypographyProps={{ variant: 'h5', component: 'h1' }}
              />
              <CardContent>
                <div className="profile-teams">
                  {!teams.length > 0 && (
                    <div className="profile-teams">
                      <h3>No teams found</h3>
                    </div>
                  )}
                  {teams.length > 0 &&
                    teams.map(team => (
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
                titleTypographyProps={{ variant: 'h5', component: 'h1' }}
              />
              <CardContent>
                <div className="profile-guilds">
                  {!guilds.length > 0 && (
                    <div className="profile-guilds">
                      <h3>No guilds found</h3>
                    </div>
                  )}
                  {guilds.length > 0 &&
                    guilds.map(guild => (
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
            <CertificationBadges memberId={memberId} />
            <VolunteerBadges memberId={memberId} />
          </Grid>
        </Grid>
      )}
    </>
  );
};

export default MemberProfilePage;
