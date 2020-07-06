import React, { useContext, useState } from "react";
import ProfileContext from "../../context/ProfileContext";
import EditIcon from "@material-ui/icons/Edit";
import Button from "@material-ui/core/Button";
import Search from "./Search";

import "./Profile.css";

const InputComponent = ({ disabled, label, rows = 1, value, setValue }) => {
  if (rows > 1) {
    return (
      <div>
        <label htmlFor={label}>{label}</label>
        <textarea
          disabled={disabled}
          id={label}
          onChange={(e) => setValue(e.target.value)}
          value={value}
        ></textarea>
      </div>
    );
  } else
    return (
      <div>
        <label htmlFor={label}>{label}</label>
        <input
          disabled={disabled}
          id={label}
          onChange={(e) => setValue(e.target.value)}
          value={value}
        ></input>
      </div>
    );
};

const Profile = () => {
  const context = useContext(ProfileContext);
  const { bio, email, image_url, name, pdl, role } = context.defaultProfile;

  const [Role, setRole] = useState(role);
  const [Email, setEmail] = useState(email);
  const [PDL, setPDL] = useState(pdl);
  const [Bio, setBio] = useState(bio);
  const [updating, setUpdating] = useState(false);
  const [disabled, setDisabled] = useState(true);

  let defaultSkills = ["Blockchain", "Mobile", "Node"];
  const [currentSkills, setCurrentSkills] = useState(defaultSkills);

  const onClick = (skill) => {
    if (currentSkills.includes(skill)) {
      return;
    }
    setCurrentSkills(currentSkills.concat(skill));
  };

  return (
    <div>
      <div className="flex-row" style={{ marginTop: "20px" }}>
        <div className="profile-image">
          <img
            alt="Profile"
            src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
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
            <InputComponent
              disabled={disabled}
              label="Role: "
              value={Role}
              setValue={setRole}
            />
            <InputComponent
              disabled={disabled}
              label="Email: "
              value={Email}
              setValue={setEmail}
            />
            <InputComponent
              disabled
              label="PDL: "
              value={PDL}
              setValue={setPDL}
            />
            <InputComponent
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
        {currentSkills.map((e) => {
          return (
            <div className="current-skills" key={e}>
              {e}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Profile;
