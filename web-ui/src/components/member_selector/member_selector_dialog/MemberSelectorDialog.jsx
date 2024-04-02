import React, {useCallback, useContext, useEffect, useState} from "react";
import PropTypes from "prop-types";
import {
  AppBar, Avatar,
  Button,
  Checkbox,
  Dialog, DialogContent,
  FormGroup,
  IconButton,
  InputLabel, List,
  ListItem,
  ListItemAvatar,
  ListItemButton, ListItemText,
  MenuItem,
  Select,
  TextField,
  Toolbar,
  Typography
} from "@mui/material";
import Slide from "@mui/material/Slide";
import CloseIcon from "@mui/icons-material/Close";
import InputAdornment from "@mui/material/InputAdornment";
import SearchIcon from "@mui/icons-material/Search";
import Autocomplete from "@mui/material/Autocomplete";
import FormControl from "@mui/material/FormControl";
import {getAvatarURL} from "../../../api/api";
import {AppContext} from "../../../context/AppContext";
import {
  selectCsrfToken,
  selectCurrentMembers,
  selectGuilds,
  selectSkills, selectSubordinates, selectSupervisors, selectTeamMembersBySupervisorId,
  selectTeams
} from "../../../context/selectors";
import {UPDATE_TOAST} from "../../../context/actions";
import {getMembersByTeam} from "../../../api/team";
import {getMembersByGuild} from "../../../api/guild";
import {getSkillMembers} from "../../../api/memberskill";

import "./MemberSelectorDialog.css";
import FormControlLabel from "@mui/material/FormControlLabel";
import Divider from "@mui/material/Divider";

const DialogTransition = React.forwardRef((props, ref) => (
  <Slide direction="up" ref={ref} {...props}/>
));

const FilterType = Object.freeze({
  GUILD: "Guild",
  TEAM: "Team",
  TITLE: "Title",
  LOCATION: "Location",
  SKILL: "Skill",
  MANAGER: "Manager",
});

const propTypes = {
  open: PropTypes.bool.isRequired,
  selectedMembers: PropTypes.arrayOf(PropTypes.object).isRequired,
  onClose: PropTypes.func,
  onSubmit: PropTypes.func
};

const MemberSelectorDialog = ({ open, selectedMembers, onClose, onSubmit }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const members = selectCurrentMembers(state);

  // Contains set of ids of checked members for instant add/remove operations
  const [checked, setChecked] = useState(new Set());

  const [nameQuery, setNameQuery] = useState("");
  const [filterType, setFilterType] = useState(FilterType.TEAM)
  const [filter, setFilter] = useState(null);
  const [filterOptions, setFilterOptions] = useState(null);
  const [filteredMembers, setFilteredMembers] = useState([]);
  const [directReportsOnly, setDirectReportsOnly] = useState(false);

  const [selectableMembers, setSelectableMembers] = useState([]);

  const handleSubmit = useCallback(() => {
    const membersToAdd = members.filter(member => checked.has(member.id));
    onSubmit(membersToAdd);
  }, [checked, members, onSubmit]);

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
        case FilterType.SKILL:
          let skills = selectSkills(state);
          return {
            options: skills,
            label: (skill) => skill.name,
            equals: (skill1, skill2) => skill1.id === skill2.id
          };
        case FilterType.MANAGER:
          const supervisors = selectSupervisors(state);
          return {
            options: supervisors,
            label: (supervisor) => supervisor.name,
            equals: (supervisor1, supervisor2) => supervisor1.id === supervisor2.id
          };
        default:
          console.warn(`Cannot get options for FilterType ${filterType}; no implementation provided`);
          return null;
      }
    }

    setFilterOptions(getFilterOptions());

  }, [filterType, members, state]);

  const showError = useCallback((message) => {
    dispatch({
      type: UPDATE_TOAST,
      payload: {
        severity: "error",
        toast: message
      }
    });
  }, [dispatch]);

  // Filters the list of members based on the selected filter type and filter
  useEffect(() => {
    const getFilteredMembers = async () => {
      // Exclude members that are already selected
      let filteredMemberList = members.filter(member =>
        !selectedMembers.includes(member)
      );

      // If a filter is selected, use it to filter the list of selectable members
      if (filter) {
        switch (filterType) {
          case FilterType.TEAM:
            const teamId = filter.id;
            const teamRes = await getMembersByTeam(teamId, csrf);
            if (!teamRes.error) {
              const teamMembers = teamRes.payload.data;
              // Collect team member ids into a set for instant lookup when filtering
              const memberIdsForTeam = new Set(teamMembers.map(teamMember => teamMember.memberId));
              filteredMemberList = filteredMemberList.filter(member => memberIdsForTeam.has(member.id));
              break;
            } else {
              showError(`Could not retrieve members for team ${filter.name}`);
            }
            break;
          case FilterType.GUILD:
            const guildId = filter.id;
            const guildRes = await getMembersByGuild(guildId, csrf);
            if (!guildRes.error) {
              const guildMembers = guildRes.payload.data;
              // Collect guild member ids into a set for instant lookup when filtering
              const memberIdsForGuild = new Set(guildMembers.map(guildMember => guildMember.memberId));
              filteredMemberList = filteredMemberList.filter(member => memberIdsForGuild.has(member.id));
            } else {
              showError(`Could not retrieve members for guild ${filter.name}`);
            }
            break;
          case FilterType.TITLE:
            filteredMemberList = filteredMemberList.filter(member => member.title === filter);
            break;
          case FilterType.LOCATION:
            filteredMemberList = filteredMemberList.filter(member => member.location === filter);
            break;
          case FilterType.SKILL:
            const skillId = filter.id;
            const skillRes = await getSkillMembers(skillId, csrf);
            if (!skillRes.error) {
              const memberSkills = skillRes.payload.data;
              // Collect member skill ids into a set for instant lookup when filtering
              const memberIdsForSkill = new Set(memberSkills.map(memberSkill => memberSkill.memberid));
              filteredMemberList = filteredMemberList.filter(member => memberIdsForSkill.has(member.id));
            } else {
              showError(`Could not retrieve members with skill ${filter.name}`);
            }
            break;
          case FilterType.MANAGER:
            const managerId = filter.id;
            // Determine whether to include all subordinates or only direct subordinates
            const subordinates = directReportsOnly
              ? selectTeamMembersBySupervisorId(state, managerId)
              : selectSubordinates(state, managerId);
            // Collect subordinate ids into a set for instant lookup when filtering
            const subordinateIds = new Set(subordinates.map(member => member.id));
            filteredMemberList = filteredMemberList.filter(member => subordinateIds.has(member.id));
            break;
          default:
            console.warn(`Cannot filter members based on FilterType ${filterType}; no implementation provided`);
        }
      }

      return filteredMemberList;
    }

    getFilteredMembers().then(filtered => {
      setFilteredMembers(filtered);
    });
  }, [state, csrf, members, filterType, filter, selectedMembers, showError, directReportsOnly]);

  useEffect(() => {
    let selectable = [...filteredMembers];

    // Search by member name
    if (nameQuery) {
      selectable = selectable.filter(member => {
        const sanitizedQuery = nameQuery.trim().toLowerCase();
        return member.name.toLowerCase().includes(sanitizedQuery);
      });
    }

    setSelectableMembers(selectable);
  }, [nameQuery, filteredMembers]);

  const handleCheckboxToggle = useCallback((member) => {
    const newChecked = new Set(checked);
    if (checked.has(member.id)) {
      newChecked.delete(member.id);
    } else {
      newChecked.add(member.id);
    }
    setChecked(newChecked);
  }, [checked]);

  const handleToggleAll = useCallback((toggle) => {
    const newChecked = new Set(checked);
    if (toggle) {
      selectableMembers.forEach(member => {
        newChecked.add(member.id);
      });
    } else {
      selectableMembers.forEach(member => {
        newChecked.delete(member.id);
      });
    }
    setChecked(newChecked);
  }, [checked, selectableMembers]);

  const visibleChecked = useCallback(() => {
    // Find only the checked members that are currently visible
    return selectableMembers.filter(member => checked.has(member.id));
  }, [selectableMembers, checked]);

  return (
    <Dialog
      className="member-selector-dialog"
      open={open}
      fullScreen
      onClose={onClose}
      TransitionComponent={DialogTransition}
    >
      <AppBar>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={onClose}>
            <CloseIcon/>
          </IconButton>
          <div className="toolbar-title-container">
            <Typography className="toolbar-title" variant="h5">Select Members</Typography>
            <Typography className="selected-count-label" variant="body1">{checked.size} selected</Typography>
          </div>
          <Button
            color="inherit"
            disabled={checked.size === 0}
            onClick={handleSubmit}>
            Add
          </Button>
        </Toolbar>
      </AppBar>
      <DialogContent className="member-selector-dialog-content">
        <FormGroup className="dialog-form-group">
          <div className="filter-input-container">
            <TextField
              className="name-search-field"
              label="Name"
              placeholder="Search by member name"
              variant="outlined"
              value={nameQuery}
              onChange={(event) => setNameQuery(event.target.value)}
              InputProps={{
                endAdornment: <InputAdornment position="end" color="gray"><SearchIcon/></InputAdornment>
              }}
            />
            {filterType === FilterType.MANAGER &&
              <FormControlLabel
                control={<Checkbox
                  checked={directReportsOnly}
                  onChange={(event) => setDirectReportsOnly(event.target.checked)}
                />}
                label="Direct reports only"
              />
            }
            <Autocomplete
              className="filter-input"
              renderInput={(params) => (
                <TextField
                  {...params}
                  variant="outlined"
                  label="Filter Members"
                  placeholder={`Search for ${filterType.toLowerCase()}`}
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
            <FormControl className="filter-type-select">
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
          </div>
          <Checkbox
            className="toggle-selectable-members-checkbox"
            onChange={(event) => handleToggleAll(event.target.checked)}
            checked={selectableMembers.length > 0 && visibleChecked().length === selectableMembers.length}
            indeterminate={visibleChecked().length > 0 && visibleChecked().length !== selectableMembers.length}
            disabled={selectableMembers.length === 0}
          />
        </FormGroup>
        <Divider/>
        <List dense role="list" sx={{ maxHeight: "80vh", overflow: "auto" }}>
          {selectableMembers.map(member => (
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
  );
}

MemberSelectorDialog.propTypes = propTypes;

export default MemberSelectorDialog;