import React, { useContext } from "react";
import { AppContext } from "../../context/AppContext";

import Avatar from "@material-ui/core/Avatar";

const AvatarComponent = () => {
  const { state } = useContext(AppContext);
  const { selectedProfile, userProfile } = state;
  const { imageUrl } = selectedProfile
    ? selectedProfile
    : userProfile
    ? userProfile
    : {};
  return (
    <Avatar
      style={{
        backgroundColor: "#72c7d5",
        cursor: "pointer",
        position: "absolute",
        right: "5px",
        top: "10px",
      }}
      src={imageUrl ? imageUrl : "/default_profile.jpg"}
    ></Avatar>
  );
};

export default AvatarComponent;
