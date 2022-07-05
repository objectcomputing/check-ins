import React, {useState} from "react";
import { Link } from "react-router-dom";
import {
  CardHeader,
  IconButton,
  ListItem,
  ListItemButton,
  ListItemText,
  Modal,
  Card,
  CardContent,
  CardActions, Button, TextField
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import PropTypes from "prop-types";
import "./GuideLink.css";

const propTypes = {
  name: PropTypes.string.isRequired,
  url: PropTypes.string.isRequired
};

const GuideLink = ({ name, url }) => {

  const [editGuideDialogOpen, setEditGuideDialogOpen] = useState(false);

  return (
    <>
      <ListItem
        secondaryAction={
          <IconButton edge="end" onClick={() => setEditGuideDialogOpen(true)}>
            <EditIcon/>
          </IconButton>
        }
      >
        <ListItemButton
          component={Link}
          to={url}
          target="_blank">
          <ListItemText primary={name}/>
        </ListItemButton>
      </ListItem>
      <Modal open={editGuideDialogOpen} onClose={() => setEditGuideDialogOpen(false)}>
        <Card>
          <CardHeader title="Edit Document" subheader={name}/>
          <CardContent>
            <div>
              <TextField label="Name"/>
              <TextField label="Description" multiline/>
              <TextField label="URL"/>
            </div>
          </CardContent>
          <CardActions>
            <Button>Cancel</Button>
            <Button>Save</Button>
          </CardActions>
        </Card>
      </Modal>
    </>
  );
};

GuideLink.propTypes = propTypes;

export default GuideLink;
