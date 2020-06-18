import React from "react";
import "./Profile.css";

const Profile = (props) => {
  const { image_url, name } = props;

  return (
    <div className="flex-row">
      <div className="profile-image">
        <img
          alt="Profile"
          src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
        />
      </div>
      <div className="flex-row">
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
