import React, {useCallback, useContext, useEffect, useState} from "react";
import PropTypes from "prop-types";
import {
  AppBar,
  Avatar, Button,
  Card,
  CardHeader, Checkbox, DialogContent, FormGroup,
  IconButton, InputLabel,
  List,
  ListItem,
  ListItemAvatar, ListItemButton,
  ListItemText, MenuItem, Select, TextField, Toolbar,
  Tooltip, Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import {getAvatarURL} from "../../api/api";
import {AppContext} from "../../context/AppContext";
import {selectCurrentMembers, selectGuilds, selectSkills, selectTeams} from "../../context/selectors";
import Dialog from "@mui/material/Dialog";
import Slide from "@mui/material/Slide";
import CloseIcon from "@mui/icons-material/Close";
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";

import "./MemberSelector.css";
import FormControl from "@mui/material/FormControl";
import Autocomplete from "@mui/material/Autocomplete";

const DialogTransition = React.forwardRef((props, ref) => (
  <Slide direction="up" ref={ref} {...props}/>
));

const FilterType = Object.freeze({
  GUILD: "Guild",
  TEAM: "Team",
  TITLE: "Title",
  LOCATION: "Location",
  SKILLS: "Skills",
});

const propTypes = {
  onChange: PropTypes.func
};

const MemberSelector = (onChange) => {
  const { state } = useContext(AppContext);
  const members = selectCurrentMembers(state);

  const [selectedMembers, setSelectedMembers] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);

  // Contains set of ids of checked members for instant add/remove operations
  const [checked, setChecked] = useState(new Set());

  const [nameQuery, setNameQuery] = useState("");
  const [filterType, setFilterType] = useState(FilterType.TEAM)
  const [filter, setFilter] = useState(null);
  const [filterOptions, setFilterOptions] = useState(null);

  // Reset dialog when it is closed
  useEffect(() => {
    if (!dialogOpen) {
      setChecked(new Set());
      setNameQuery("");
    }
  }, [dialogOpen]);

  // Change filter options when filter type is changed
  useEffect(() => {
    const getFilterOptions = () => {
      switch (filterType) {
        case FilterType.TEAM:
          const teams = selectTeams(state);
          return {
            options: teams,
            label: (team) => team.name,
            equals: (team1, team2) => team1.id === team2.id
          };
        case FilterType.GUILD:
          const guilds = selectGuilds(state);
          return {
            options: guilds,
            label: (guild) => guild.name,
            equals: (guild1, guild2) => guild1.id === guild2.id
          };
        case FilterType.TITLE:
          // Create a list of unique titles from current members
          let titles = members
            .filter(member => !!member.title)
            .map(member => member.title);
          titles = [...new Set(titles)];
          return {
            options: titles,
            label: (title) => title,
            equals: (title1, title2) => title1 === title2
          };
        case FilterType.LOCATION:
          let locations = members
            .filter(member => !!member.location)
            .map(member => member.location);
          locations = [...new Set(locations)];
          return {
            options: locations,
            label: (location) => location,
            equals: (location1, location2) => location1 === location2
          };
        case FilterType.SKILLS:
          let skills = selectSkills(state);
          return {
            options: skills,
            label: (skill) => skill.name,
            equals: (skill1, skill2) => skill1.id === skill2.id
          };
        default:
          console.warn(`No filter implementation for FilterType ${filterType}`);
          return null;
      }
    }

    setFilterOptions(getFilterOptions());

  }, [filterType, members, state]);

  const getSelectableMembers = useCallback(() => {
    // TODO: Make it so this filter doesn't need to happen when changing queries
    // Only include members that are not already selected
    let selectableMembers = members.filter(member =>
      !selectedMembers.includes(member)
    );

    // Filter by member name
    selectableMembers = selectableMembers.filter(member => {
      const sanitizedQuery = nameQuery.trim().toLowerCase();
      return member.name.toLowerCase().includes(sanitizedQuery);
    });

    return selectableMembers;
  }, [members, selectedMembers, nameQuery]);

  const handleCheckboxToggle = useCallback((member) => {
    const newChecked = new Set(checked);
    if (checked.has(member.id)) {
      newChecked.delete(member.id);
    } else {
      newChecked.add(member.id);
    }
    setChecked(newChecked);
  }, [checked]);

  const addMembers = useCallback(() => {
    const membersToAdd = members.filter(member => checked.has(member.id));
    const selected = [...selectedMembers, ...membersToAdd];
    setSelectedMembers(selected);
    setDialogOpen(false);
  }, [checked, members, selectedMembers]);

  const removeMember = useCallback((member) => {
    const selected = [...selectedMembers]
    const indexToRemove = selected.findIndex(selectedMember => selectedMember.id === member.id);
    selected.splice(indexToRemove, 1);
    setSelectedMembers(selected);
  }, [selectedMembers]);

  return (
    <>
      <Card className="member-selector-card">
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
        <List dense role="list">
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
      <Dialog
        className="member-selector-dialog"
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
            <Button color="inherit" disabled={checked.size === 0} onClick={addMembers}>
              Add
            </Button>
          </Toolbar>
        </AppBar>
        <DialogContent className="member-selector-dialog-content">
          <FormGroup row>
            <TextField
              label="Name"
              placeholder="Search by member name"
              variant="outlined"
              value={nameQuery}
              onChange={(event) => setNameQuery(event.target.value)}
              InputProps={{
                endAdornment: <InputAdornment position="end" color="gray"><SearchIcon/></InputAdornment>
              }}
            />
            <Autocomplete
              renderInput={(params) => (
                <TextField
                  {...params}
                  variant="outlined"
                  label="Filter Members"
                  placeholder="Search"
                />
              )}
              disablePortal
              disabled={!filterOptions}
              options={filterOptions ? filterOptions.options : []}
              getOptionLabel={filterOptions ? filterOptions.label : () => ""}
              isOptionEqualToValue={filterOptions ? filterOptions.equals : () => false}
              value={filter}
              onChange={(_, value) => setFilter(value)}
            />
            <FormControl>
              <InputLabel id="member-filter-label">Filter by</InputLabel>
              <Select
                labelId="member-filter-label"
                label="Filter by"
                value={filterType}
                onChange={(event) => {
                  setFilter(null);
                  setFilterType(event.target.value);
                }}
              >
                {Object.values(FilterType).map((name) =>
                  <MenuItem key={name} value={name}>{name}</MenuItem>
                )}
              </Select>
            </FormControl>
          </FormGroup>
          <List dense role="list">
            {getSelectableMembers().map(member => (
              <ListItem
                key={member.id}
                role="listitem"
                disablePadding
                onClick={() => handleCheckboxToggle(member)}
                secondaryAction={
                  <Checkbox checked={checked.has(member.id)} disableRipple/>
                }
              >
                <ListItemButton>
                  <ListItemAvatar>
                    <Avatar src={getAvatarURL(member.workEmail)}/>
                  </ListItemAvatar>
                  <ListItemText
                    primary={<Typography fontWeight="bold">{member.name}</Typography>}
                    secondary={<Typography color="textSecondary" component="h6">{member.title}</Typography>}
                  />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </DialogContent>
      </Dialog>
    </>
  );
};

MemberSelector.propTypes = propTypes;

export default MemberSelector;