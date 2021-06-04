import React, { useContext, useEffect, useState } from "react";

import { debounce } from "lodash/function";
import { AppContext } from "../context/AppContext";
import { selectCurrentUser } from "../context/selectors";
import {
  UPDATE_GUILD,
  UPDATE_USER_BIO,
  UPDATE_TOAST,
} from "../context/actions";
import { getGuildsForMember, updateGuild } from "../api/guild";
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
  const userProfile = selectCurrentUser(state);

  const { csrf, guilds } = state;
  const { id, bioText, pdlId } = userProfile;

  const [bio, setBio] = useState();
  const [myGuilds, setMyGuilds] = useState([]);
  const [myHours, setMyHours] = useState(null);

  useEffect(() => {
    async function getMyGuilds() {
      let res = await getGuildsForMember(id, csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) setMyGuilds(data);
    }
    if (csrf && id) {
      getMyGuilds();
    }
  }, [csrf, id, guilds]);

  useEffect(() => {
    async function getHours() {
      let res = await getEmployeeHours(csrf, userProfile?.employeeId);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data && data.length > 0) setMyHours(data[0]);
    }
    if (csrf && userProfile?.employeeId) {
      getHours();
    }
  }, [csrf, userProfile]);

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
    storeMember({ ...userProfile, bioText: value }, csrf);
    updateProfile(value);
  };

  const addOrDeleteGuildMember = async (newVal) => {
    if (!csrf) {
      return;
    }
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
    const index = newVal.length - 1;
    const newId = newVal[index].id;
    if (newVal.length > myGuilds.length) {
      if (myGuilds.some((guild) => guild.id === newId)) return;
      newVal.filter((guild) => !myGuilds.includes(guild.id));
      newVal[index].guildMembers = [...newVal[index].guildMembers, userProfile];
      let res = await updateGuild(newVal[index], csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      dispatch({ type: UPDATE_GUILD, payload: data });
      setMyGuilds(newVal);
    } else {
      const guildToEdit = myGuilds.find((guild) =>
        newVal.every((newGuild) => newGuild.id !== guild.id)
      );
      const guildMembers = guildToEdit.guildMembers.filter(
        (member) => member.memberId !== userProfile.id
      );
      guildToEdit.guildMembers = guildMembers;
      let res = await updateGuild(guildToEdit, csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      dispatch({ type: UPDATE_GUILD, payload: data });
      setMyGuilds(newVal);
    }
  };

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
      <div>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6}>
            {myHours && (
              <Card style={{ minHeight: 150 }}>
                <CardHeader avatar={<Info />} title="Contribution Hours" />
                <CardContent>
                  <ProgressBar {...myHours} />
                </CardContent>
              </Card>
            )}
          </Grid>
          <Grid item xs={12} sm={6}>
            <Card style={{ minHeight: 150 }}>
              <CardHeader
                avatar={<GroupIcon />}
                title="Guilds"
                titleTypographyProps={{ variant: "h5", component: "h2" }}
              />
              <CardContent>
                <Autocomplete
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
      </div>
      <div className="skills-section">
        <SkillSection userId={id} />
      </div>
    </div>
  );
};

export default ProfilePage;
