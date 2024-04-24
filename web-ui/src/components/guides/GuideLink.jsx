import React from 'react';
import { Link } from 'react-router-dom';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import './GuideLink.css';

const GuideLink = props => {
  const path = '/pdfs/';
  const fileName = props.name;
  let fullPath = path + fileName.split(' ').join('_') + '.pdf';
  return (
    <ListItem
      key={`guides-{fileName.split(" ").join("_")}`}
      button
      component={Link}
      to={fullPath}
      target="_blank"
    >
      <ListItem>
        <ListItemText primary={fileName} />
      </ListItem>
    </ListItem>
  );
};

export default GuideLink;
