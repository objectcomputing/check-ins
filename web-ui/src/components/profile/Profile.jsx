import React, { useContext, useState } from "react";
import ProfileContext from "../../context/ProfileContext";
import EditIcon from "@material-ui/icons/Edit";
import "./Profile.css";

const Profile = () => {
  const context = useContext(ProfileContext);
  const { bio, email, image_url, name, pdl, role } = context.defaultProfile;
  const [roles, setRole] = useState(role);
  const [disabled, setDisabled] = useState(true);

  return (
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
            <EditIcon
              onClick={() => {
                setDisabled(!disabled);
              }}
              style={{
                color: !disabled ? "green" : "black",
                marginLeft: "20px",
              }}
            />
          </h2>
          <div>
            <label htmlFor="role">Role:</label>
            <input
              disabled={disabled}
              onChange={(e) => setRole(e.target.value)}
              id="role"
              style={{
                border: disabled ? "none" : "1px solid black",
              }}
              value={roles}
            ></input>
          </div>
          <div>
            <label htmlFor="email">Email:</label>
            <input
              disabled={disabled}
              id="email"
              style={{ border: disabled ? "none" : "1px solid black" }}
              value={email}
            ></input>
          </div>
          <div style={{ display: "flex", textAlign: "left" }}>
            <label htmlFor="pdl">PDL:</label>
            <input
              disabled
              id="pdl"
              style={{ border: "none" }}
              value={PDL}
            ></input>
          </div>
          <div>
            <label htmlFor="bio">Bio:</label>
            <textarea
              disabled={disabled}
              id="bio"
              style={{
                border: disabled ? "none" : "1px solid black",
              }}
              value={bio}
            ></textarea>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
