import React, {useContext, useEffect, useState} from "react";
import {
  Autocomplete,
  Avatar,
  Button,
  Card,
  CardHeader,
  Checkbox,
  Divider, FormControl,
  Grid, IconButton, InputAdornment, InputLabel,
  List,
  ListItem, ListItemAvatar, ListItemButton,
  ListItemText, MenuItem, Select, TextField, Tooltip, Typography, Collapse,
} from "@mui/material";
import LeftArrowIcon from "@mui/icons-material/KeyboardArrowLeft";
import RightArrowIcon from "@mui/icons-material/KeyboardArrowRight";
import SearchIcon from "@mui/icons-material/Search";
import FilterIcon from "@mui/icons-material/FilterList";
import DownloadIcon from "@mui/icons-material/FileDownload";
import PropTypes from "prop-types";
import {getAvatarURL} from "../../api/api";

import "./TransferList.css";
import {AppContext} from "../../context/AppContext";
import {getMembersByTeam} from "../../api/team";
import {getMembersByGuild} from "../../api/guild";
import {UPDATE_TOAST} from "../../context/actions";
import {selectMappedUserRoles} from "../../context/selectors";
import {reportSelectedMembersCsv} from "../../api/member";
import fileDownload from "js-file-download";

const not = (a, b) => a.filter((value) => b.indexOf(value) === -1);
const intersection = (a, b) => a.filter((value) => b.indexOf(value) !== -1);
const union = (a, b) => [...a, ...not(b, a)];

const FilterOption = {
  NAME: "NAME",
  GUILD: "GUILD",
  TEAM: "TEAM",
  TITLE: "TITLE",
  LOCATION: "LOCATION",
  ROLE: "ROLE"
};

const propTypes = {
  leftList: PropTypes.arrayOf(PropTypes.object).isRequired,
  rightList: PropTypes.arrayOf(PropTypes.object).isRequired,
  leftLabel: PropTypes.string,
  rightLabel: PropTypes.string,
  onListsChanged: PropTypes.func,
  disabled: PropTypes.bool
};

const TransferList = ({ leftList, rightList, leftLabel, rightLabel, onListsChanged, disabled }) => {

  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, guilds, teams, roles, csrf } = state;
  const mappedUserRoles = selectMappedUserRoles(state);
  const [checked, setChecked] = useState([]);
  const [recipientFilterVisible, setRecipientFilterVisible] = useState(false);
  const [recipientFilter, setRecipientFilter] = useState(FilterOption.NAME);
  const [recipientQuery, setRecipientQuery] = useState(null);
  const [filteredLeftList, setFilteredLeftList] = useState([]);

  const leftChecked = intersection(checked, leftList);
  const rightChecked = intersection(checked, rightList);

  // Get all unique, defined titles
  let memberTitles = memberProfiles.filter(member => !!member.title).map(member => member.title);
  memberTitles = [...new Set(memberTitles)];
  memberTitles = memberTitles.map(title => {return {name: title}});

  // Get all unique, defined locations
  let memberLocations = memberProfiles.filter(member => !!member.location).map(member => member.location);
  memberLocations = [...new Set(memberLocations)];
  memberLocations = memberLocations.map(location => {return {name: location}});

  // Get all roles
  let memberRoles = roles.map(role => {return {name: role.role}});

  const filterOptions = {
    [FilterOption.GUILD]: guilds,
    [FilterOption.TEAM]: teams,
    [FilterOption.TITLE]: memberTitles,
    [FilterOption.LOCATION]: memberLocations,
    [FilterOption.ROLE]: memberRoles
  };

  const handleToggle = (value) => {
    if (disabled) return;

    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setChecked(newChecked);
  }

  const numberOfChecked = (items) => intersection(checked, items).length;

  const handleToggleAll = (items) => {
    if (numberOfChecked(items) === items.length) {
      setChecked(not(checked, items));
    } else {
      setChecked(union(checked, items));
    }
  }

  const handleCheckedRight = () => {
    onListsChanged({
      left: not(leftList, leftChecked),
      right: rightList.concat(leftChecked)
    });
    setChecked(not(checked, leftChecked));
  }

  const handleCheckedLeft = () => {
    onListsChanged({
      left: leftList.concat(rightChecked),
      right: not(rightList, rightChecked)
    });
    setChecked(not(checked, rightChecked));
  }

  const downloadMemberCsv = () => {
    const res = reportSelectedMembersCsv(rightList, csrf);
    if (!res.error) {
      fileDownload(res?.payload?.data, "members.csv");
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `Member export has been saved!`,
        },
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to export to CSV",
        },
      });
    }
  }

  useEffect(() => {
    const getFilteredOptions = async (members) => {
      let filterMembers;
      switch (recipientFilter) {
        case FilterOption.NAME:
          filterMembers = (member) => member.name.toLowerCase().includes(recipientQuery.trim().toLowerCase());
          break;
        case FilterOption.GUILD:
          if (csrf) {
            const guildId = recipientQuery.id;
            const res = await getMembersByGuild(guildId, csrf);
            const guildMembers = res && res.payload && res.payload.data && !res.error ? res.payload.data : null;
            if (guildMembers) {
              // Create set of member ids in the guild, then filter out ids not in the set (using set for quick access)
              const guildMemberIds = new Set();
              guildMembers.forEach(guildMember => guildMemberIds.add(guildMember.memberId));
              filterMembers = (member) => guildMemberIds.has(member.id);
            } else {
              dispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: "error",
                  toast: `Could not retrieve members for guild ${recipientQuery.name}`
                }
              });
            }
          }
          break;
        case FilterOption.TEAM:
          if (csrf) {
            const teamId = recipientQuery.id;
            const res = await getMembersByTeam(teamId, csrf);
            const teamMembers = res && res.payload && res.payload.data && !res.error ? res.payload.data : null;
            if (teamMembers) {
              // Create set of member ids in the guild, then filter out ids not in the set (using set for quick access)
              const teamMemberIds = new Set();
              teamMembers.forEach(teamMember => teamMemberIds.add(teamMember.memberId));
              filterMembers = (member) => teamMemberIds.has(member.id);
            } else {
              dispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: "error",
                  toast: `Could not retrieve members for team ${recipientQuery.name}`
                }
              });
            }
          }
          break;
        case FilterOption.TITLE:
          filterMembers = (member) => member.title === recipientQuery.name;
          break;
        case FilterOption.LOCATION:
          filterMembers = (member) => member.location === recipientQuery.name;
          break;
        case FilterOption.ROLE:
          filterMembers = (member) => (member.id in mappedUserRoles) && mappedUserRoles[member.id].has(recipientQuery.name);
          break;
        default:
          console.warn(`Invalid recipient filter ${recipientFilter}`);
          return members;
      }

      // Display members that are checked, even if not included by the filter
      const checkedMembers = new Set();
      checked.forEach(member => checkedMembers.add(member.id));
      if (filterMembers) {
        return members.filter(member => filterMembers(member) || checkedMembers.has(member.id));
      }
      return members;
    }

    // Only filter items if the filter is open and the user has entered a query
    if (recipientFilterVisible && recipientQuery) {
      getFilteredOptions(leftList).then(filtered => {
        setFilteredLeftList(filtered);
      });
    } else {
      setFilteredLeftList(leftList);
    }
  },[leftList, recipientFilter, recipientQuery, recipientFilterVisible, csrf, dispatch, checked, mappedUserRoles]);

  const customList = (title, items, emptyMessage, includeFilter) => {
    items = items.sort((a, b) => a.name.localeCompare(b.name));
    return (
      <Card className="transfer-list" variant="outlined">
        <CardHeader
          action={
            <div style={{ display: "flex", alignItems: "center" }}>
              {includeFilter ?
                <Tooltip arrow title="Filter">
                  <IconButton
                    style={{ marginTop: "-8px" }}
                    onClick={() => {
                      if (!recipientFilterVisible) {
                        setRecipientQuery(null);  // Reset the query when opening the filter
                      }
                      setRecipientFilterVisible(!recipientFilterVisible);
                    }}
                  >
                    <FilterIcon/>
                  </IconButton>
                </Tooltip>
                :
                <Tooltip arrow title="Download CSV">
                  <div>
                    <IconButton onClick={downloadMemberCsv} disabled={!rightList?.length} style={{ marginTop: "-8px" }}>
                      <DownloadIcon/>
                    </IconButton>
                  </div>
                </Tooltip>
              }
              <Checkbox
                onClick={() => handleToggleAll(items)}
                checked={numberOfChecked(items) === items.length && items.length !== 0}
                indeterminate={numberOfChecked(items) !== items.length && numberOfChecked(items) !== 0}
                disabled={items.length === 0 || disabled}
                style={{marginRight: "8px", marginTop: "-8px"}}
              />
            </div>
          }
          title={title}
          subheader={`${numberOfChecked(items)}/${items.length} selected`}
          titleTypographyProps={{
            fontWeight: "bold",
            fontSize: "18px"
          }}
        />
        {includeFilter &&
          <Collapse in={recipientFilterVisible}>
            <div className="transfer-list-filter-container">
              {recipientFilter === FilterOption.NAME ?
                <TextField
                  className="transfer-list-filter-field"
                  label="Filter Recipients"
                  placeholder="Search for a member name..."
                  variant="outlined"
                  value={recipientQuery ? recipientQuery : ""}
                  onChange={(event) => setRecipientQuery(event.target.value)}
                  InputProps={{
                    endAdornment: <InputAdornment color="gray" position="end"><SearchIcon/></InputAdornment>
                  }}
                />
                :
                <Autocomplete
                  className="transfer-list-filter-field"
                  disablePortal
                  options={filterOptions[recipientFilter]}
                  getOptionLabel={(option) => option.name}
                  isOptionEqualToValue={(option, value) => option.name === value.name}
                  value={recipientQuery}
                  onChange={(event, value) => setRecipientQuery(value)}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="outlined"
                      placeholder={`Search for a ${recipientFilter.toLowerCase()}...`}
                      label="Filter Recipients"
                    />
                  )}
                />
              }
              <FormControl className="transfer-list-filter">
                <InputLabel id="recipient-filter-label">Filter by</InputLabel>
                <Select
                  labelId="recipient-filter-label"
                  label="Filter by"
                  value={recipientFilter}
                  onChange={(event) => {
                    setRecipientQuery(null);
                    setRecipientFilter(event.target.value);
                  }}
                >
                  <MenuItem value={FilterOption.NAME}>Name</MenuItem>
                  <MenuItem value={FilterOption.GUILD}>Guild</MenuItem>
                  <MenuItem value={FilterOption.TEAM}>Team</MenuItem>
                  <MenuItem value={FilterOption.TITLE}>Title</MenuItem>
                  <MenuItem value={FilterOption.LOCATION}>Location</MenuItem>
                  <MenuItem value={FilterOption.ROLE}>Role</MenuItem>
                </Select>
              </FormControl>
            </div>
          </Collapse>
        }
        <Divider/>
        <List
          dense
          role="list"
          sx={{
            height: 400,
            overflow: "auto"
          }}
        >
          {items.length === 0 &&
            <div className="empty-list-message-container">
              <Typography className="empty-list-message">{emptyMessage}</Typography>
            </div>
          }
          {items.map((member) => (
            <ListItem
              key={member.id}
              role="listitem"
              onClick={() => handleToggle(member)}
              disablePadding
              secondaryAction={
                <Checkbox
                  checked={checked.indexOf(member) !== -1}
                  tabIndex={-1}
                  disableRipple
                  disabled={disabled}
                />
              }
            >
              <ListItemButton selected={checked.indexOf(member) !== -1} disableTouchRipple={disabled}>
                <ListItemAvatar>
                  <Avatar src={getAvatarURL(member?.workEmail)}/>
                </ListItemAvatar>
                <ListItemText
                  primary={<Typography fontWeight="bold">{member?.name}</Typography>}
                  secondary={<Typography color="textSecondary" component="h6">{member?.title}</Typography>}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Card>
    )
  };

  return (
    <div className="transfer-list-container">
      <Grid item>{customList(leftLabel || "Choices", filteredLeftList, "No members to select", true)}</Grid>
      <Grid item>
        <Grid container direction="column" alignContent="center" justifyContent="center" sx={{height: 480}}>
          <Button
            sx={{ my: 1 }}
            variant="outlined"
            size="small"
            onClick={handleCheckedRight}
            disabled={leftChecked.length === 0 || disabled}
            aria-label="move selected right"
          >
            <RightArrowIcon/>
          </Button>
          <Button
            sx={{ my: 1 }}
            variant="outlined"
            size="small"
            onClick={handleCheckedLeft}
            disabled={rightChecked.length === 0 || disabled}
            aria-label="move selected left"
          >
            <LeftArrowIcon/>
          </Button>
        </Grid>
      </Grid>
      <Grid item>{customList(rightLabel || "Chosen", rightList, "No recipients", false)}</Grid>
    </div>
  );
}

TransferList.propTypes = propTypes;

export default TransferList;