import React from "react";
import { Link } from 'react-router-dom';
import Avatar from "@material-ui/core/Avatar";
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import "./GuideLink.css";

const GuideLink = (props) => {
  const path = "/pdfs/";
  const fileName = props.name;
  let fullPath = path + fileName.split(" ").join("_") + ".pdf";
  return (
    <ListItem key={`guides-{fileName.split(" ").join("_")}`} button component={Link} to={fullPath} target="_blank" >
        <ListItem>
          <ListItemText primary={fileName}/>
        </ListItem>
    </ListItem>
  );
};

export default GuideLink;
