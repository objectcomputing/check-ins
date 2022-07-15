import React from "react";
import {Link} from "react-router-dom";
import {
  ListItem,
  ListItemButton,
  ListItemText
} from "@mui/material";
import PropTypes from "prop-types";
import "./GuideLink.css";

const propTypes = {
  document: PropTypes.object.isRequired,
  draggable: PropTypes.bool.isRequired
};

const GuideLink = ({ document, draggable }) => {

  return (
    <>
      <ListItem
        disablePadding
      >
        <ListItemButton
          component={Link}
          to={document?.url || ""}
          target="_blank">
          <ListItemText
            style={{ paddingLeft: draggable ? 0 : "1rem"}}
            primary={document?.name || "Undefined"}
          />
        </ListItemButton>
      </ListItem>
    </>
  );
};

GuideLink.propTypes = propTypes;

export default GuideLink;
