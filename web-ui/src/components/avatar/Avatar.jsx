import React from "react";
import Avatar from "@material-ui/core/Avatar";

const AvatarComponent = ({ loggedIn = false, profile = {} }) => {
  const { image_url } = profile;
  const src = !image_url || loggedIn === false ? "" : image_url;
  return (
    <Avatar
      style={{
        backgroundColor: "#72c7d5",
        cursor: "pointer",
        position: "absolute",
        right: "5px",
        top: "10px",
      }}
      src={src}
    ></Avatar>
  );
};

export default AvatarComponent;
