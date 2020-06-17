import React, { useState } from "react";
import "./Profile.css";

const Profile = (props) => {
  const { image_url, name, team } = props;
  const [hidden, setHidden] = useState(team ? true : false);

  return (
    <div className="flex-row">
      <div className="image-div">
        <img
          alt="Profile"
          onClick={() => {
            if (!team) {
              return;
            }
            setHidden(!hidden);
          }}
          src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
        />
      </div>
      <div className="flex-row" style={hidden ? { display: "none" } : null}>
        <div style={{ marginTop: "50%", textAlign: "left" }}>
          <h2 style={{ margin: 0 }}>{name}</h2>
          <div>
            <p>Role</p>
            <p>Email</p>
            <p>Current PDL</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
