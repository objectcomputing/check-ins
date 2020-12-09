import React from "react";
import "./Avatar.css";

import Avatar from "@material-ui/core/Avatar";

const AvatarComponent = ({ imageUrl, ...props }) => {
  return <Avatar className="avatar" src={imageUrl} {...props}></Avatar>;
};

export default AvatarComponent;
