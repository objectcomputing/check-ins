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
          alt="Profile"
          src={image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg"}
          style={{ borderRadius: "50%", maxWidth: "256px", maxHeight: "256px" }}
        />
      </div>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
        }}
      >
        <div style={{ marginTop: "30%", textAlign: "left" }}>
          <div
            style={{
              backgroundColor: "green",
              borderRadius: "15px",
              color: "white",
              padding: "5px",
              marginBottom: "10px",
              textAlign: "center",
            }}
          >
            Software Engineer
          </div>
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
