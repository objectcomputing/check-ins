import React, { useContext, useState } from "react";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import CancelIcon from "@material-ui/icons/Cancel";
import Avatar from "@material-ui/core/Avatar";
import {
  AppContext,
  MY_SKILL_REMOVE,
  MY_SKILL_TOGGLE,
  MY_PROFILE_UPDATE,
} from "../../context/AppContext";
import Search from "./Search";
import Input from "./Input";

import "./Profile.css";

const Profile = () => {
  const { state, dispatch } = useContext(AppContext);
  const { mySkills, defaultProfile } = state;

  const { bio, email, image_url, name, pdl, role } = defaultProfile;

  const [Role, setRole] = useState(role);
  const [Email, setEmail] = useState(email);
  const [Bio, setBio] = useState(bio);
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);

  const updateProfile = () => {
    const updatedProfile = {
      role: Role,
      email: Email,
      name: name,
      pdl: pdl,
      bio: Bio,
    };
    dispatch({
      type: MY_PROFILE_UPDATE,
      payload: updatedProfile,
    });
  };

  const onClick = (item) => {
    const inMySkills = mySkills.find(({ name }) => {
      return name.toUpperCase() === item.toUpperCase();
    });
    if (inMySkills) {
      return;
    }
    dispatch({
      type: MY_SKILL_TOGGLE,
      payload: { name: item },
    });
  };

  const removeSkill = (name) => {
    dispatch({
      type: MY_SKILL_REMOVE,
      payload: { name: name },
    });
  };

  return (
    <div>
      <div className="flex-row" style={{ marginTop: "20px" }}>
        <div className="profile-image">
          <Avatar
            alt="Profile"
            src={
              image_url
                ? image_url
                : require("../../images/default_profile.jpg")
            }
            style={{ width: "200px", height: "220px" }}
          />
        </div>
        <div className="flex-row">
          <div style={{ textAlign: "left" }}>
            <h2 style={{ margin: 0 }}>
              {name}
              {updating && (
                <Button
                  style={{
                    backgroundColor: "green",
                    color: "white",
                    marginLeft: "20px",
                  }}
                  onClick={() => {
                    setDisabled(!disabled);
                    setUpdating(!updating);
                    updateProfile();
                  }}
                >
                  Update
                </Button>
              )}
              {!updating && (
                <EditIcon
                  onClick={() => {
                    setDisabled(!disabled);
                    setUpdating(!updating);
                  }}
                  style={{
                    color: "black",
                    marginLeft: "20px",
                  }}
                />
              )}
            </h2>
            <Input
              disabled={disabled}
              label="Role: "
              value={Role}
              setValue={setRole}
            />
            <Input
              disabled={disabled}
              label="Email: "
              value={Email}
              setValue={setEmail}
            />
            <div>
              <span>PDL: </span>
              {pdl}
            </div>
            <Input
              disabled={disabled}
              label="Bio: "
              rows={2}
              value={Bio}
              setValue={setBio}
            />
          </div>
        </div>
      </div>
      <div>
        <Search onClick={onClick} />
        <h2>Skills</h2>
        {mySkills.map(({ name }) => {
          return (
            <div className="current-skills" key={name}>
              {name}
              <CancelIcon
                onClick={() => {
                  removeSkill(name);
                }}
                style={{
                  cursor: "pointer",
                  fontSize: "1rem",
                  marginLeft: "5px",
                }}
              />
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Profile;
