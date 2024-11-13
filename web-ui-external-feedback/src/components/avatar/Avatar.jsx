import React from 'react';
import './Avatar.css';

import Avatar from '@mui/material/Avatar';

const AvatarComponent = ({ imageUrl, ...props }) => {
  return <Avatar className="avatar" src={imageUrl} {...props}></Avatar>;
};

export default AvatarComponent;
