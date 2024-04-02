import React, {useCallback, useEffect, useState} from "react";
import PropTypes from "prop-types";
import {
  Avatar,
  Card,
  CardHeader,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Tooltip,
  Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import {getAvatarURL} from "../../api/api";

import "./MemberSelector.css";
import MemberSelectorDialog from "./member_selector_dialog/MemberSelectorDialog";
import Divider from "@mui/material/Divider";

const propTypes = {
  onChange: PropTypes.func,
  listHeight: PropTypes.number,
  className: PropTypes.string,
  style: PropTypes.object
};

const MemberSelector = ({ onChange, listHeight, className, style }) => {
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);

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

  return (
    <>
      <Card
        className={"member-selector-card" + (className ? ` ${className}` : "")}
        style={style}>
        <CardHeader
          title={
            <Typography variant="h5">Selected Members
              <span style={{ color: "gray", fontSize: "0.75em" }}> ({selectedMembers.length})</span>
            </Typography>
          }
          action={
            <Tooltip title="Add members" arrow>
              <IconButton onClick={() => setDialogOpen(true)}><AddIcon/></IconButton>
            </Tooltip>
          }
        />
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
      </Card>
      {dialogOpen &&
        <MemberSelectorDialog
          open={dialogOpen}
          selectedMembers={selectedMembers}
          onClose={() => setDialogOpen(false)}
          onSubmit={(membersToAdd) => addMembers(membersToAdd)}
        />
      }
    </>
  );
};

MemberSelector.propTypes = propTypes;

export default MemberSelector;