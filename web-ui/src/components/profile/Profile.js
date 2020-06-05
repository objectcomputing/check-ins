import React from "react";

const Profile = (profile = {}) => {
  const { image_url, name, email, role, pdl } = profile;

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "row",
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          marginLeft: "50px",
          marginRight: "50px",
        }}
      >
        <img
          src={image_url}
          style={{ maxWidth: "256px", maxHeight: "256px" }}
        />
      </div>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
        }}
      >
        <div style={{ textAlign: "left" }}>
          <h2>Name</h2>
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
