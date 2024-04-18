import React, {useCallback, useEffect, useState} from "react";
import PropTypes from "prop-types";
import {
  Avatar,
  Card,
  CardHeader, Collapse,
  Divider,
  IconButton,
  List,
  ListItem,
  ListItemAvatar, ListItemIcon,
  ListItemText, Menu, MenuItem,
  Tooltip,
  Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {getAvatarURL} from "../../api/api";

import MemberSelectorDialog from "./member_selector_dialog/MemberSelectorDialog";

import "./MemberSelector.css";

const propTypes = {
  onChange: PropTypes.func,
  listHeight: PropTypes.number,
  className: PropTypes.string,
  style: PropTypes.object
};

const MemberSelector = ({ onChange, listHeight, className, style }) => {
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [expanded, setExpanded] = useState(true);
  const [menuAnchor, setMenuAnchor] = useState(null);

  useEffect(() => {
    if (onChange) {
      onChange(selectedMembers);
    }
  }, [selectedMembers, onChange]);

  const addMembers = useCallback((membersToAdd) => {
    const selected = [...selectedMembers, ...membersToAdd];
    setSelectedMembers(selected);
    setDialogOpen(false);
  }, [selectedMembers]);

  const removeMember = useCallback((member) => {
    const selected = [...selectedMembers]
    const indexToRemove = selected.findIndex(selectedMember => selectedMember.id === member.id);
    selected.splice(indexToRemove, 1);
    setSelectedMembers(selected);
  }, [selectedMembers]);

  const clearMembers = useCallback(() => {
    setSelectedMembers([]);
  }, []);

  return (
    <>
      <Card
        className={"member-selector-card" + (className ? ` ${className}` : "")}
        style={style}>
        <CardHeader
          avatar={
            <IconButton onClick={() => setExpanded(!expanded)}>
              {expanded ? <ExpandLessIcon/> : <ExpandMoreIcon/>}
            </IconButton>
          }
          title={
            <div className="member-selector-card-title-container">
              <Typography className="member-selector-card-title" variant="h5" noWrap>Selected Members</Typography>
              <Typography className="member-selector-card-count" variant="h6" color="gray">({selectedMembers.length})</Typography>
            </div>
          }
          action={
            <>
              <Tooltip title="Add members" arrow>
                <IconButton style={{ margin: "4px 8px 0 0" }} onClick={() => setDialogOpen(true)}>
                  <AddIcon/>
                </IconButton>
              </Tooltip>
              <IconButton style={{ margin: "4px 8px 0 0" }} onClick={(event) => setMenuAnchor(event.currentTarget)}>
                <MoreVertIcon/>
              </IconButton>
              <Menu
                anchorEl={menuAnchor}
                open={!!menuAnchor}
                onClose={() => setMenuAnchor(null)}
              >
                <MenuItem
                  onClick={() => {
                    setMenuAnchor(null);
                    clearMembers();
                  }}
                  disabled={!selectedMembers.length}
                >
                  <ListItemIcon>
                    <HighlightOffIcon fontSize="small"/>
                  </ListItemIcon>
                  <ListItemText>Remove all</ListItemText>
                </MenuItem>
              </Menu>
            </>
          }
        />
        <Collapse in={expanded}>
          <Divider/>
          <List dense role="list" sx={{ maxHeight: listHeight || 400, overflow: "auto" }}>
            {selectedMembers.length
              ? (selectedMembers.map(member =>
                <ListItem
                  key={member.id}
                  role="listitem"
                  secondaryAction={
                    <Tooltip title="Deselect member" arrow>
                      <IconButton onClick={() => removeMember(member)}><RemoveIcon/></IconButton>
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
        </Collapse>
      </Card>
      <MemberSelectorDialog
        open={dialogOpen}
        selectedMembers={selectedMembers}
        onClose={() => setDialogOpen(false)}
        onSubmit={(membersToAdd) => addMembers(membersToAdd)}
      />
    </>
  );
};

MemberSelector.propTypes = propTypes;

export default MemberSelector;