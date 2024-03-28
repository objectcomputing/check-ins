import React, {useContext, useState} from "react";
import PropTypes from "prop-types";
import {
  AppBar,
  Avatar, Button,
  Card,
  CardHeader, DialogContent,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText, Toolbar,
  Tooltip, Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import {getAvatarURL} from "../../api/api";
import {AppContext} from "../../context/AppContext";
import {selectCurrentMembers} from "../../context/selectors";
import Dialog from "@mui/material/Dialog";
import Slide from "@mui/material/Slide";
import CloseIcon from "@mui/icons-material/Close";

const DialogTransition = React.forwardRef((props, ref) => (
  <Slide direction="up" ref={ref} {...props}/>
));

const FilterOption = Object.freeze({
  GUILD: "GUILD",
  TEAM: "TEAM",
  TITLE: "TITLE",
  LOCATION: "LOCATION",
  SKILLS: "SKILLS",
});

const propTypes = {
  onChange: PropTypes.func
};

const MemberSelector = (onChange) => {
  const { state } = useContext(AppContext);
  const members = selectCurrentMembers(state);
  const [selectedMembers, setSelectedMembers] = useState([members[0], members[1]]);
  const [dialogOpen, setDialogOpen] = useState(false);

  return (
    <>
      <Card>
        <CardHeader
          title="Selected Members"
          action={
            <Tooltip title="Add members" arrow>
              <IconButton onClick={() => setDialogOpen(true)}><AddIcon/></IconButton>
            </Tooltip>
          }
        />
        <List dense role="list">
          {selectedMembers.length
            ? (selectedMembers.map(member =>
              <ListItem
                role="listitem"
                secondaryAction={
                  <Tooltip title="Deselect member" arrow>
                    <IconButton><RemoveIcon/></IconButton>
                  </Tooltip>
                }
              >
                <ListItemAvatar>
                  <Avatar src={getAvatarURL(member.workEmail)}/>
                </ListItemAvatar>
                <ListItemText
                  primary={<Typography fontWeight="bold">{member.name}</Typography>}
                  secondary={<Typography color="textSecondary" component="h6">{member.title}</Typography>}
                />
              </ListItem>
            ))
            : (
              <ListItem><ListItemText style={{ color: "gray" }}>No members selected</ListItemText></ListItem>
            )
          }
        </List>
      </Card>
      <Dialog
        open={dialogOpen}
        fullScreen
        onClose={() => setDialogOpen(false)}
        TransitionComponent={DialogTransition}
      >
        <AppBar>
          <Toolbar>
            <IconButton edge="start" color="inherit" onClick={() => setDialogOpen(false)}>
              <CloseIcon/>
            </IconButton>
            <Typography variant="h6" flexGrow={1}>Select Members</Typography>
            <Button color="inherit">
              Save
            </Button>
          </Toolbar>
        </AppBar>
        <DialogContent>

        </DialogContent>
      </Dialog>
    </>
  );
};

MemberSelector.propTypes = propTypes;

export default MemberSelector;