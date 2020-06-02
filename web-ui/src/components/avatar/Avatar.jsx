import React from "react";
import Avatar from "@material-ui/core/Avatar";
import PersonIcon from "@material-ui/icons/Person";

const AvatarComponent = (profile = {}) => {
  const { image_url } = profile;
  return (
    <Avatar
      style={{
        backgroundColor: "#72c7d5",
        position: "absolute",
        right: "5px",
      }}
    >
      {Object.keys(profile).length === 0 ? (
        <PersonIcon />
      ) : (
        <img alt="Profile Picture" src={{ image_url }} />
      )}
    </Avatar>
  );
};

export default AvatarComponent;
