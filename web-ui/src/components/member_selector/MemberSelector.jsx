import React, {useCallback, useEffect, useState} from "react";
import PropTypes from "prop-types";
import {
  Avatar,
  Card,
  CardHeader, Collapse,
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
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {getAvatarURL} from "../../api/api";

import "./MemberSelector.css";
import MemberSelectorDialog from "./member_selector_dialog/MemberSelectorDialog";
import Divider from "@mui/material/Divider";

const propTypes = {
  /** The members that are currently selected. Use to make this a controlled component. */
  selected: PropTypes.arrayOf(PropTypes.object),
  /** Listener for whenever the list of selected members changes. Passes the updated list as an argument. */
  onChange: PropTypes.func,
  /** Optional title for the card. Default is "Selected Members". */
  title: PropTypes.string,
  /** Set to true to use the outlined variant of the card. Default is the elevated variant. */
  outlined: PropTypes.bool,
  /** Adjusts the height of the scrollable list of selected members (in pixels) */
  listHeight: PropTypes.number,
  /** If true, members cannot be added to or removed from the current selection. False by default. */
  disabled: PropTypes.bool,
  /** A custom class name to additionally apply to the top-level card */
  className: PropTypes.string,
  /** Custom style properties to apply to the top-level card */
  style: PropTypes.object
};

const MemberSelector = ({selected, onChange, title = "Selected Members", outlined = false, listHeight = 400, disabled = false, className, style }) => {
  const isControlled = !!selected && Array.isArray(selected);

  const [selectedMembers, setSelectedMembers] = useState(isControlled ? selected : []);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [expanded, setExpanded] = useState(true);

  // When the selected members change, fire the onChange event
  useEffect(() => {
    if (onChange) {
      onChange(selectedMembers);
    }
  }, [selectedMembers, onChange]);

  // If the selector is disabled, make sure the selector dialog is closed
  useEffect(() => {
    if (disabled) {
      setDialogOpen(false);
    }
  }, [disabled]);

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
        variant={outlined ? "outlined" : "elevation"}
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
              <Typography className="member-selector-card-title" variant="h5" noWrap>{title}</Typography>
              <Typography className="member-selector-card-count" variant="h6" color="gray">({selectedMembers.length})</Typography>
            </div>
          }
          action={
            <Tooltip title="Add members" arrow>
              <IconButton
                style={{ margin: "4px 8px 0 0" }}
                onClick={() => setDialogOpen(true)}
                disabled={disabled}
              >
                <AddIcon/>
              </IconButton>
            </Tooltip>
          }
        />
        <Collapse in={expanded}>
          <Divider/>
          <List dense role="list" sx={{ maxHeight: listHeight, overflow: "auto" }}>
            {selectedMembers.length
              ? (selectedMembers.map(member =>
                <ListItem
                  key={member.id}
                  role="listitem"
                  secondaryAction={
                    <Tooltip title="Deselect member" arrow>
                      <IconButton
                        onClick={() => removeMember(member)}
                        disabled={disabled}
                      >
                        <RemoveIcon/>
                      </IconButton>
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