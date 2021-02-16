import React, { useContext, useEffect, useState } from "react";
import { debounce } from "lodash/function";
import {
  AppContext,
  selectCurrentUser,
  UPDATE_USER_BIO,
} from "../context/AppContext";
import { updateMember } from "../api/member";
import { Info } from "@material-ui/icons";
import { Card, CardContent, CardHeader, TextField } from "@material-ui/core";

import "./ProfilePage.css";
import Profile from '../components/profile/Profile';
import SkillSection from '../components/skills/SkillSection';

const realStoreMember = (member, csrf) => updateMember(member, csrf);

const storeMember = debounce(realStoreMember, 1500);

const ProfilePage = () => {
  const { state, dispatch } = useContext(AppContext);
  const userProfile = selectCurrentUser(state);
  const {csrf} = state;

  const { id, bioText } = userProfile;

  const [bio, setBio] = useState();

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
    storeMember({...userProfile, bioText: value}, csrf);
    updateProfile(value);
  };

  return (
    <div className="Profile">
      <Profile memberId={id} />
      <Card>
        <CardHeader
          avatar={<Info />}
          title="Bio"
          titleTypographyProps={{variant: "h5", component: "h2"}} />
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
      <div className="skills-section">
        <SkillSection userId={id} />
      </div>
    </div>
  );
};

export default ProfilePage;
