import React, { useContext } from "react";
import "./Avatar.css";

import Avatar from "@material-ui/core/Avatar";

const AvatarComponent = ({imageUrl, ...props}) => {
  return (
    <Avatar className="avatar"
      src={imageUrl ? imageUrl : "/default_profile.jpg"}
      {...props}
    ></Avatar>
  );
};

export default AvatarComponent;
