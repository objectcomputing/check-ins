import React from "react";
import Avatar from "@material-ui/core/Avatar";
import PersonIcon from "@material-ui/icons/Person";

const AvatarComponent = ({ loggedIn = false, profile = {} }) => {
  const { image_url } = profile;
  return (
    <Avatar
      style={{
        backgroundColor: "#72c7d5",
        position: "absolute",
        right: "5px",
        top: "10px",
      }}
    >
      {!image_url || loggedIn === false ? (
        <PersonIcon />
      ) : (
        <img alt="Profile" src={image_url} />
      )}
    </Avatar>
  );
};

export default AvatarComponent;
