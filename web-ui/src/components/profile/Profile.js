import React from "react";
import "./Profile.css";

const Profile = (profile = {}) => {
  const { image_url, name, email, role, pdl } = profile;

  return (
    <div className="flex-row">
      <div className="image-div">
        <img
          alt="Profile"
          src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
        />
      </div>
      <div className="flex-row">
        <div style={{ marginTop: "30%", textAlign: "left" }}>
          <div className="tag">Software Engineer</div>
          {/* could put role up here as tag instead of below?? */}
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
