import React, { useCallback, useContext, useEffect, useState } from 'react';

import { debounce } from 'lodash/function';
import { AppContext } from '../context/AppContext';
import {
  selectCurrentUser,
  selectMyGuilds,
  selectUserProfile,
  selectMyTeams
} from '../context/selectors';
import { UPDATE_GUILD, UPDATE_USER_BIO } from '../context/actions';
import { addGuildMember, deleteGuildMember } from '../api/guild';
import { updateMember } from '../api/member';
import { getEmployeeHours } from '../api/hours';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';
import Profile from '../components/profile/Profile';
import SkillSection from '../components/skills/SkillSection';
import ProgressBar from '../components/contribution_hours/ProgressBar';

import { Info } from '@mui/icons-material';
import { Card, CardContent, CardHeader, Chip, TextField } from '@mui/material';
import GroupIcon from '@mui/icons-material/Group';
import Autocomplete from '@mui/material/Autocomplete';

import './ProfilePage.css';

const realStoreMember = (member, csrf) => updateMember(member, csrf);

const storeMember = debounce(realStoreMember, 1500);

const ProfilePage = () => {
  const { state, dispatch } = useContext(AppContext);
  const memberProfile = selectCurrentUser(state);
  const userProfile = selectUserProfile(state);

  const { csrf, guilds } = state;
  const { id, bioText, pdlId } = memberProfile;
  const { firstName, lastName, name } = userProfile;

  const [bio, setBio] = useState();
  const [myHours, setMyHours] = useState(null);

  const myTeams = selectMyTeams(state);

  const myGuilds = selectMyGuilds(state);

  useEffect(() => {
    async function getHours() {
      let res = await getEmployeeHours(csrf, memberProfile?.employeeId);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data && data.length > 0) setMyHours(data[0]);
    }
    if (csrf && memberProfile?.employeeId) {
      getHours();
    }
  }, [csrf, memberProfile]);

  useEffect(() => {
    async function updateBio() {
      setBio(bioText);
    }
    updateBio();
  }, [bioText]);

  const updateProfile = newBio => {
    dispatch({
      type: UPDATE_USER_BIO,
      payload: newBio
    });
  };

  const handleBioChange = e => {
    if (!csrf) {
      return;
    }
    const { value } = e.target;
    setBio(value);
    storeMember({ ...memberProfile, bioText: value }, csrf);
    updateProfile(value);
  };

  const addOrDeleteGuildMember = useCallback(
    async newVal => {
      if (!csrf) {
        return;
      }

      const myGuildsSet = new Set(myGuilds?.map(guild => guild.id));
      const newValSet = new Set(newVal?.map(val => val.id));

      const newInSet1 = new Set(
        [...myGuildsSet].filter(x => !newValSet.has(x))
      );
      const newInSet2 = new Set(
        [...newValSet].filter(x => !myGuildsSet.has(x))
      );

      for (const guildId of newInSet2.values()) {
        let res = await addGuildMember(id, false, guildId, csrf);
        const match = newVal.find(guild => guild.id === guildId);
        let data =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : null;
        if (data) {
          data.firstName = firstName;
          data.lastName = lastName;
          data.name = name;
          const guildMembers = match.guildMembers;
          const newGuildMembers = guildMembers
            ? [...guildMembers, data]
            : [data];
          const newGuild = { ...match };
          newGuild.guildMembers = newGuildMembers;
          dispatch({ type: UPDATE_GUILD, payload: newGuild });
        }
      }

      for (const guildId of newInSet1.values()) {
        const match = myGuilds.find(guild => guild.id === guildId);
        if (match) {
          const { guildMembers } = match;
          const memberToDelete = guildMembers.find(
            member => member.memberId === id
          );
          let res = await deleteGuildMember(memberToDelete.id, csrf);
          let success = res.payload && !res.error ? true : false;
          if (success) {
            const newGuildMembers = match.guildMembers.filter(
              member => member.memberId !== id
            );
            let newGuild = { ...match };
            newGuild.guildMembers = newGuildMembers;
            dispatch({ type: UPDATE_GUILD, payload: newGuild });
          }
        }
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [guilds]
  );

  return (
    <div className="Profile">
      <Profile memberId={id} pdlId={pdlId} />
      <div className="profile-grid">
        <div className="profile-page-bio">
          <Card>
            <CardHeader
              avatar={<Info />}
              title="Bio"
              titleTypographyProps={{ variant: 'h5', component: 'h2' }}
            />
            <CardContent>
              <TextField
                onChange={handleBioChange}
                value={bio}
                id="Bio"
                placeholder="Tell us about yourself..."
                multiline
                rows={3}
                fullWidth
              />
            </CardContent>
          </Card>
        </div>
        {myHours ? (
          <div className="profile-hours">
            {myHours && (
              <Card>
                <CardHeader
                  avatar={<Info />}
                  subheader={`As Of: ${new Date(
                    myHours?.asOfDate
                  ).toLocaleDateString()}`}
                  title="Contribution Hours"
                />
                <CardContent>
                  <ProgressBar {...myHours} />
                </CardContent>
              </Card>
            )}
          </div>
        ) : (
          ''
        )}
        <div className="profile-guilds">
          <Card style={{ minHeight: 150 }}>
            <CardHeader
              avatar={<GroupIcon />}
              title="Guilds"
              titleTypographyProps={{ variant: 'h5', component: 'h2' }}
            />
            <CardContent>
              <Autocomplete
                disableClearable
                id="guildsSelect"
                getOptionLabel={option => option.name}
                isOptionEqualToValue={(option, value) =>
                  value && value.id === option.id
                }
                multiple
                onChange={(event, newVal) => {
                  addOrDeleteGuildMember(newVal);
                }}
                options={guilds || []}
                required
                value={myGuilds}
                renderInput={params => (
                  <TextField {...params} placeholder="Join a guild..." />
                )}
              />
            </CardContent>
          </Card>
        </div>
        <div className="profile-teams">
          <Card>
            <CardHeader
              avatar={<GroupIcon />}
              title="Teams"
              titleTypographyProps={{ variant: 'h5', component: 'h1' }}
            />
            <CardContent>
              <div className="profile-teams">
                {myTeams.length > 0 ? (
                  myTeams.map(team => (
                    <Chip
                      className="chip"
                      // color="primary"
                      key={team.id}
                      label={team.name}
                    />
                  ))
                ) : (
                  <h3>No teams found</h3>
                )}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
      <div className="skills-section">
        <SkillSection userId={id} />
      </div>
      <EarnedCertificationsTable onlyMe />
    </div>
  );
};

export default ProfilePage;
