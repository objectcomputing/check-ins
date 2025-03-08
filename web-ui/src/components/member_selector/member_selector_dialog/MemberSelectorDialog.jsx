import { differenceInMonths } from 'date-fns';
import React, { useCallback, useContext, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import {
  AppBar,
  Autocomplete,
  Avatar,
  Button,
  Checkbox,
  Dialog,
  DialogContent,
  Divider,
  FormControl,
  FormControlLabel,
  FormGroup,
  IconButton,
  InputAdornment,
  InputLabel,
  List,
  ListItem,
  ListItemAvatar,
  ListItemButton,
  ListItemText,
  MenuItem,
  Select,
  Slide,
  TextField,
  Toolbar,
  Typography
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import SearchIcon from '@mui/icons-material/Search';
import { DatePicker } from '@mui/x-date-pickers';

import { getAvatarURL } from '../../../api/api';
import { AppContext } from '../../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentMembers,
  selectActiveGuilds,
  selectMappedMemberRoles,
  selectRoles,
  selectSkills,
  selectSubordinates,
  selectSupervisors,
  selectTeamMembersBySupervisorId,
  selectActiveTeams,
} from '../../../context/selectors';
import { UPDATE_TOAST } from '../../../context/actions';
import { getMembersByTeam } from '../../../api/team';
import { getMembersByGuild } from '../../../api/guild';
import { getSkillMembers } from '../../../api/memberskill';

import './MemberSelectorDialog.css';

const DialogTransition = React.forwardRef((props, ref) => (
  <Slide direction="up" ref={ref} {...props} />
));

export const FilterType = Object.freeze({
  GUILD: 'Guild',
  TEAM: 'Team',
  TITLE: 'Title',
  LOCATION: 'Location',
  ROLE: 'Role',
  SKILL: 'Skill',
  MANAGER: 'Manager'
});

export const Tenures = Object.freeze({
  All: 'All',
  Months6: '6 Months',
  Years1: '1 Year',
  Years5: '5 Years',
  Years10: '10 Years',
  Years20: '20 Years',
  Custom: 'Custom'
});

const tenureToMonths = {
  [Tenures.Months6]: 6,
  [Tenures.Years1]: 12,
  [Tenures.Years5]: 60,
  [Tenures.Years10]: 120,
  [Tenures.Years20]: 240,
  [Tenures.Custom]: 0
};

const propTypes = {
  initialFilters: PropTypes.arrayOf(
    PropTypes.shape({
      tenure: PropTypes.oneOf(Object.values(Tenures)),
      type: PropTypes.oneOf(Object.values(FilterType)),
      value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
        PropTypes.bool
      ])
    })
  ),
  memberDescriptor: PropTypes.string,
  open: PropTypes.bool.isRequired,
  selectedMembers: PropTypes.arrayOf(PropTypes.object).isRequired,
  onClose: PropTypes.func,
  onSubmit: PropTypes.func
};

const MemberSelectorDialog = ({
  open,
  memberDescriptor = 'Members',
  initialFilters = [],
  selectedMembers,
  onClose,
  onSubmit
}) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const members = selectCurrentMembers(state);

  // Use the first initial filter as the default filter type
  const [initialFilter] = initialFilters;

  // Contains set of ids of checked members for instant add/remove operations
  const [checked, setChecked] = useState(new Set());

  const [nameQuery, setNameQuery] = useState('');
  const [filterType, setFilterType] = useState(
    initialFilter?.type || FilterType.TEAM
  );
  const [filterOptions, setFilterOptions] = useState(null);
  const [filter, setFilter] = useState(null);
  const [filteredMembers, setFilteredMembers] = useState([]);
  const [directReportsOnly, setDirectReportsOnly] = useState(false);
  const [selectableMembers, setSelectableMembers] = useState([]);
  const [tenure, setTenure] = useState(initialFilter?.tenure || Tenures.All);
  const [customTenure, setCustomTenure] = useState(new Date());

  const handleSubmit = useCallback(() => {
    const membersToAdd = members.filter(member => checked.has(member.id));
    onSubmit(membersToAdd);
  }, [checked, members, onSubmit]);

  const initializeChecked = useCallback(() => {
    const initialChecked = new Set();
    selectedMembers.forEach(member => initialChecked.add(member.id));
    setChecked(initialChecked);
  });

  // Reset the dialog when it closes, or set the initial filter when it opens
  useEffect(() => {
    if (!open) {
      // Reset all state except for the chosen filter type and its corresponding options
      setNameQuery('');
      setFilter(null);
      setFilteredMembers([]);
      setDirectReportsOnly(false);
      setSelectableMembers([]);
    } else {
      initializeChecked();
      // If the dialog is opened with initial filters, set the initial filter
      if (initialFilter && initialFilter.type === FilterType.ROLE) {
        setFilterType(initialFilter.type);
        setFilter(
          selectRoles(state).find(role => role.role === initialFilter.value)
        );
      }
    }
  }, [open, selectedMembers]);

  // Change filter options when filter type is changed
  useEffect(() => {
    const getFilterOptions = () => {
      switch (filterType) {
        case FilterType.TEAM:
          const teams = selectActiveTeams(state);
          return {
            options: teams,
            label: team => team.name,
            equals: (team1, team2) => team1.id === team2.id
          };
        case FilterType.GUILD:
          const guilds = selectActiveGuilds(state);
          return {
            options: guilds,
            label: guild => guild.name,
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
            label: title => title,
            equals: (title1, title2) => title1 === title2
          };
        case FilterType.LOCATION:
          let locations = members
            .filter(member => !!member.location)
            .map(member => member.location);
          locations = [...new Set(locations)];
          return {
            options: locations,
            label: location => location,
            equals: (location1, location2) => location1 === location2
          };
        case FilterType.ROLE:
          const roles = selectRoles(state);
          return {
            options: roles,
            label: role => role.role,
            equals: (role1, role2) => role1.id === role2.id
          };
        case FilterType.SKILL:
          let skills = selectSkills(state);
          return {
            options: skills,
            label: skill => skill.name,
            equals: (skill1, skill2) => skill1.id === skill2.id
          };
        case FilterType.MANAGER:
          const supervisors = selectSupervisors(state);
          return {
            options: supervisors,
            label: supervisor => supervisor.name,
            equals: (supervisor1, supervisor2) =>
              supervisor1.id === supervisor2.id
          };
        default:
          console.warn(
            `Cannot get options for FilterType ${filterType}; no implementation provided`
          );
          return null;
      }
    };

    setFilterOptions(getFilterOptions());
  }, [filterType, members, state]);

  const showError = useCallback(
    message => {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: message
        }
      });
    },
    [dispatch]
  );

  // Filters the list of members based on the selected filter type and filter
  useEffect(() => {
    const getFilteredMembers = async () => {
      let filteredMemberList = [...members];

      // Exclude members that don't have the selected tenure.
      if (tenure === Tenures.Custom) {
        filteredMemberList = members.filter(member => {
          const start = new Date(member.startDate);
          return start <= customTenure;
        });
      } else if (tenure !== Tenures.All) {
        const now = new Date();
        const requiredMonths = tenureToMonths[tenure];
        filteredMemberList = members.filter(member => {
          const start = new Date(member.startDate);
          const diffMonths = differenceInMonths(now, start);
          return diffMonths >= requiredMonths;
        });
      }

      // If a filter is selected, use it to filter the list of selectable members
      if (filter) {
        switch (filterType) {
          case FilterType.TEAM:
            const teamId = filter.id;
            const teamRes = await getMembersByTeam(teamId, csrf);
            if (!teamRes.error) {
              const teamMembers = teamRes.payload.data;
              // Collect team member ids into a set for instant lookup when filtering
              const memberIdsForTeam = new Set(
                teamMembers.map(teamMember => teamMember.memberId)
              );
              filteredMemberList = filteredMemberList.filter(member =>
                memberIdsForTeam.has(member.id)
              );
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
              const memberIdsForGuild = new Set(
                guildMembers.map(guildMember => guildMember.memberId)
              );
              filteredMemberList = filteredMemberList.filter(member =>
                memberIdsForGuild.has(member.id)
              );
            } else {
              showError(`Could not retrieve members for guild ${filter.name}`);
            }
            break;
          case FilterType.TITLE:
            filteredMemberList = filteredMemberList.filter(
              member => member.title === filter
            );
            break;
          case FilterType.LOCATION:
            filteredMemberList = filteredMemberList.filter(
              member => member.location === filter
            );
            break;
          case FilterType.ROLE:
            const mappedMemberRoles = selectMappedMemberRoles(state);
            filteredMemberList = filteredMemberList.filter(
              member =>
                member.id in mappedMemberRoles &&
                mappedMemberRoles[member.id].has(filter.role)
            );
            break;
          case FilterType.SKILL:
            const skillId = filter.id;
            const skillRes = await getSkillMembers(skillId, csrf);
            if (!skillRes.error) {
              const memberSkills = skillRes.payload.data;
              // Collect member skill ids into a set for instant lookup when filtering
              const memberIdsForSkill = new Set(
                memberSkills.map(memberSkill => memberSkill.memberid)
              );
              filteredMemberList = filteredMemberList.filter(member =>
                memberIdsForSkill.has(member.id)
              );
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
            const subordinateIds = new Set(
              subordinates.map(member => member.id)
            );
            filteredMemberList = filteredMemberList.filter(member =>
              subordinateIds.has(member.id)
            );
            break;
          default:
            console.warn(
              `Cannot filter members based on FilterType ${filterType}; no implementation provided`
            );
        }
      }

      return filteredMemberList;
    };

    if (open) {
      getFilteredMembers().then(filtered => {
        setFilteredMembers(filtered);
      });
    }
  }, [
    csrf,
    customTenure,
    directReportsOnly,
    filter,
    filterType,
    members,
    open,
    selectedMembers,
    showError,
    state,
    tenure
  ]);

  useEffect(() => {
    let selectable = [...filteredMembers];

    // Search by member name
    if (nameQuery) {
      const query = nameQuery.trim().toLowerCase();
      selectable = selectable.filter(member =>
        member.name.toLowerCase().includes(query)
      );
    }

    setSelectableMembers(selectable);
  }, [nameQuery, filteredMembers]);

  const handleCheckboxToggle = useCallback(
    member => {
      const newChecked = new Set(checked);
      if (checked.has(member.id)) {
        newChecked.delete(member.id);
      } else {
        newChecked.add(member.id);
      }
      setChecked(newChecked);
    },
    [checked]
  );

  const handleToggleAll = useCallback(
    toggle => {
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
    },
    [checked, selectableMembers]
  );

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
            <CloseIcon />
          </IconButton>
          <div className="toolbar-title-container">
            <Typography className="toolbar-title" variant="h5">
              Select {memberDescriptor}
            </Typography>
            <Typography className="selected-count-label" variant="body1">
              {checked.size} selected
            </Typography>
          </div>
          <Button color="inherit" onClick={handleSubmit}>
            Save
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
              onChange={event => setNameQuery(event.target.value)}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end" color="gray">
                    <SearchIcon />
                  </InputAdornment>
                )
              }}
            />
            <FormControl className="filter-type-select">
              <InputLabel id="filter-type-label">Filter Type</InputLabel>
              <Select
                labelId="filter-type-label"
                label="Filter Type"
                value={filterType}
                onChange={event => {
                  setFilter(null);
                  setFilterType(event.target.value);
                }}
                disabled={!!initialFilter}
              >
                {Object.values(FilterType).map(name => (
                  <MenuItem key={name} value={name}>
                    {name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <Autocomplete
              className="filter-input"
              renderInput={params => (
                <TextField
                  {...params}
                  variant="outlined"
                  label="Filter Value"
                  placeholder={`Search for ${filterType.toLowerCase()}`}
                />
              )}
              disablePortal
              disabled={!filterOptions || !!initialFilter}
              options={filterOptions ? filterOptions.options : []}
              getOptionLabel={filterOptions ? filterOptions.label : () => ''}
              isOptionEqualToValue={
                filterOptions ? filterOptions.equals : () => false
              }
              value={filter}
              onChange={(_, value) => setFilter(value)}
            />
          </div>
          <Checkbox
            className="toggle-selectable-members-checkbox"
            onChange={event => handleToggleAll(event.target.checked)}
            checked={
              selectableMembers.length > 0 &&
              visibleChecked().length === selectableMembers.length
            }
            indeterminate={
              visibleChecked().length > 0 &&
              visibleChecked().length !== selectableMembers.length
            }
            disabled={selectableMembers.length === 0}
          />
        </FormGroup>
        <FormGroup className="dialog-form-group">
          <div className="filter-input-container">
            {filterType === FilterType.MANAGER && (
              <FormControlLabel
                className="direct-reports-only-checkbox"
                control={
                  <Checkbox
                    checked={directReportsOnly}
                    onChange={event =>
                      setDirectReportsOnly(event.target.checked)
                    }
                  />
                }
                label="Direct reports only"
              />
            )}
            <FormControl className="filter-type-select">
              <InputLabel id="member-filter-label">Required Tenure</InputLabel>
              <Select
                labelId="member-filter-label"
                label="Required Tenure"
                value={tenure}
                onChange={event => {
                  const tenure = event.target.value;
                  setTenure(tenure);
                }}
                disabled={!!initialFilter}
              >
                {Object.values(Tenures).map(name => (
                  <MenuItem key={name} value={name}>
                    {name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            {tenure === Tenures.Custom && (
              <DatePicker
                className="custom-tenure-picker"
                slotProps={{ textField: { className: 'halfWidth' } }}
                label="Custom Tenure Start"
                format="MM/dd/yyyy"
                value={customTenure}
                openTo="year"
                onChange={setCustomTenure}
                KeyboardButtonProps={{
                  'aria-label': 'Change Date'
                }}
              />
            )}
          </div>
        </FormGroup>
        <Divider />
        <List dense role="list" sx={{ height: '85%', overflowY: 'scroll' }}>
          {selectableMembers.map(member => (
            <ListItem
              key={member.id}
              role="listitem"
              disablePadding
              onClick={() => handleCheckboxToggle(member)}
              secondaryAction={
                <Checkbox checked={checked.has(member.id)} disableRipple />
              }
            >
              <ListItemButton>
                <ListItemAvatar>
                  <Avatar src={getAvatarURL(member.workEmail)} />
                </ListItemAvatar>
                <ListItemText
                  primary={
                    <Typography fontWeight="bold">{member.name}</Typography>
                  }
                  secondary={
                    <Typography color="textSecondary" component="h6">
                      {member.title}
                    </Typography>
                  }
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider />
      </DialogContent>
    </Dialog>
  );
};

MemberSelectorDialog.propTypes = propTypes;

export default MemberSelectorDialog;
