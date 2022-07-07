import React, {useState} from "react";
import { Link } from "react-router-dom";
import {
  IconButton,
  ListItem,
  ListItemButton,
  ListItemText
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import PropTypes from "prop-types";
import "./GuideLink.css";
import DocumentModal from "../document_modal/DocumentModal";

const propTypes = {
  document: PropTypes.object.isRequired
};

const GuideLink = ({ document }) => {

  const [guideDialogOpen, setGuideDialogOpen] = useState(false);

  return (
    <>
      <DocumentModal
        open={guideDialogOpen}
        onClose={() => setGuideDialogOpen(false)}
        onSave={() => {
          setGuideDialogOpen(false);
          console.log("Saving...");
        }}
        document={document}
      />
      <ListItem
        disablePadding
        secondaryAction={
          <IconButton edge="end" onClick={() => setGuideDialogOpen(true)}>
            <EditIcon/>
          </IconButton>
        }
      >
        <ListItemButton
          component={Link}
          to={document?.url || ""}
          target="_blank">
          <ListItemText style={{paddingLeft: "1rem"}} primary={document?.name || "Undefined"}/>
        </ListItemButton>
      </ListItem>
    </>
  );
};

GuideLink.propTypes = propTypes;

export default GuideLink;
