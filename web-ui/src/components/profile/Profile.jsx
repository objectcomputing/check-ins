import React from "react";
import "./Profile.css";

const Profile = (profile = {}) => {
const { image_url } = profile;

  return (
    <div className="flex-row">
      <div className="image-div">
        <img
          alt="Profile"
          src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
        />
      </div>
      <div className="flex-row">
        <div style={{ marginTop: "50%", textAlign: "left" }}>
          <h2 style={{ margin: 0 }}>Name</h2>
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
