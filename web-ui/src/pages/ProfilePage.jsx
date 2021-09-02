import React, { useCallback, useContext, useEffect, useState } from "react";

import { debounce } from "lodash/function";
import { AppContext } from "../context/AppContext";
import {
  selectCurrentUser,
  selectMyGuilds,
  selectUserProfile,
} from "../context/selectors";
import {
  UPDATE_GUILD,
  UPDATE_USER_BIO,
  UPDATE_TOAST,
} from "../context/actions";
import { addGuildMember, deleteGuildMember } from "../api/guild";
import { updateMember } from "../api/member";
import { getEmployeeHours } from "../api/hours";
import Profile from "../components/profile/Profile";
import SkillSection from "../components/skills/SkillSection";
import ProgressBar from "../components/contribution_hours/ProgressBar";

import { Info } from "@material-ui/icons";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  TextField,
} from "@material-ui/core";
import GroupIcon from "@material-ui/icons/Group";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./ProfilePage.css";

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

  const updateProfile = (newBio) => {
    dispatch({
      type: UPDATE_USER_BIO,
      payload: newBio,
    });
  };

  const handleBioChange = (e) => {
    if (!csrf) {
      return;
    }
    const { value } = e.target;
    setBio(value);
    storeMember({ ...memberProfile, bioText: value }, csrf);
    updateProfile(value);
  };

  const addOrDeleteGuildMember = useCallback(
    async (newVal) => {
      if (!csrf) {
        return;
      }

      const myGuildsSet = new Set(myGuilds?.map((guild) => guild.id));
      const newValSet = new Set(newVal?.map((val) => val.id));

      const newInSet1 = new Set(
        [...myGuildsSet].filter((x) => !newValSet.has(x))
      );
      const newInSet2 = new Set(
        [...newValSet].filter((x) => !myGuildsSet.has(x))
      );

      for (const guildId of newInSet2.values()) {
        if (newVal.length > 3) {
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast:
                "You must contact the guild leader in order to be added to more guilds",
            },
          });
          return;
        }
        let res = await addGuildMember(id, false, guildId, csrf);
        const match = newVal.find((guild) => guild.id === guildId);
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
        const match = myGuilds.find((guild) => guild.id === guildId);
        if (match) {
          const { guildMembers } = match;
          const memberToDelete = guildMembers.find(
            (member) => member.memberId === id
          );
          let res = await deleteGuildMember(memberToDelete.id, csrf);
          let success = res.payload && !res.error ? true : false;
          if (success) {
            const newGuildMembers = match.guildMembers.filter(
              (member) => member.memberId !== id
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
      <Card>
        <CardHeader
          avatar={<Info />}
          title="Bio"
          titleTypographyProps={{ variant: "h5", component: "h2" }}
        />
        <CardContent>
          <TextField
            onChange={handleBioChange}
            value={bio}
            id="Bio"
            style={{ margin: 8 }}
            placeholder="Tell us about yourself..."
            multiline
            fullWidth
          />
        </CardContent>
      </Card>
      <Grid container spacing={3}>
        {myHours ? (
          <Grid item xs>
            {myHours && (
              <Card style={{ minHeight: 150 }}>
                <CardHeader
                  avatar={<Info />}
                  subheader={`Updated On: ${new Date(
                    myHours.updatedDate
                  ).toLocaleDateString()}`}
                  title="Contribution Hours"
                />
                <CardContent>
                  <ProgressBar {...myHours} />
                </CardContent>
              </Card>
            )}
          </Grid>
        ) : (
          ""
        )}
        <Grid item xs>
          <Card style={{ minHeight: 150 }}>
            <CardHeader
              avatar={<GroupIcon />}
              title="Guilds"
              titleTypographyProps={{ variant: "h5", component: "h2" }}
            />
            <CardContent>
              <Autocomplete
                disableClearable
                id="guildsSelect"
                getOptionLabel={(option) => option.name}
                getOptionSelected={(option, value) =>
                  value ? value.id === option.id : false
                }
                multiple
                onChange={(event, newVal) => {
                  addOrDeleteGuildMember(newVal);
                }}
                options={guilds}
                required
                value={myGuilds}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    className="halfWidth"
                    placeholder="Join a guild..."
                  />
                )}
              />
            </CardContent>
          </Card>
        </Grid>
      </Grid>
      <div className="skills-section">
        <SkillSection userId={id} />
      </div>
    </div>
  );
};

export default ProfilePage;
