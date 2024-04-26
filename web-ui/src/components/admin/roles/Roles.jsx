import React, { useContext, useEffect, useState } from 'react';

import { AppContext } from '../../../context/AppContext';
import {
  SET_ROLES,
  SET_USER_ROLES,
  UPDATE_TOAST
} from '../../../context/actions';
import {
  addUserToRole,
  addNewRole,
  removeUserFromRole,
  updateRole
} from '../../../api/roles';

import RoleUserCards from './RoleUserCards';

import {
  Button,
  Card,
  CardActions,
  CardContent,
  List,
  ListSubheader,
  Modal,
  TextField,
  Typography,
  Autocomplete,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  OutlinedInput,
  MenuItem,
  Checkbox,
  ListItemText,
  FormHelperText,
  Divider
} from '@mui/material';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';

import { isArrayPresent } from './../../../helpers/checks';

import './Roles.css';
import EditIcon from '@mui/icons-material/Edit';

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  // roles here is all possible roles, not the selected roles.
  const { csrf, memberProfiles, roles, userRoles } = state;

  const [showAddUser, setShowAddUser] = useState(false);
  const [showEditRole, setShowEditRole] = useState(false);
  const [editedRole, setEditedRole] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [selectedMember, setSelectedMember] = useState({});
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [roleToMemberMap, setRoleToMemberMap] = useState({});
  const [currentRole, setCurrentRole] = useState('');

  memberProfiles?.sort((a, b) => a.name.localeCompare(b.name));

  useEffect(() => {
    const url = new URL(location.href);
    setSearchText(url.searchParams.get('searchText') ?? '');
    const selectedRoles = url.searchParams.get('selectedRoles');
    if (selectedRoles?.length > 0) {
      // Select only the roles specified in the URL.
      setSelectedRoles(selectedRoles.split(','));
    } else {
      // Select all possible roles.
      setSelectedRoles(roles.map(r => r.role));
    }
  }, []);

  useEffect(() => {
    const url = new URL(location.href);
    const params = {
      searchText: searchText,
      selectedRoles: selectedRoles.join(',')
    };
    const q = new URLSearchParams(params).toString();
    const newUrl = url.origin + url.pathname + '?' + q;
    history.replaceState(params, '', newUrl);
  }, [searchText, selectedRoles]);

  useEffect(() => {
    const memberMap = {};
    if (memberProfiles && memberProfiles.length > 0) {
      for (const member of memberProfiles) {
        memberMap[member.id] = member;
      }
    }

    const newRoleToMemberMap = {};
    for (const userRole of userRoles || []) {
      const role = roles.find(
        role => role.id === userRole?.memberRoleId?.roleId
      );
      if (role) {
        let memberList = newRoleToMemberMap[role.role];
        if (!memberList) {
          memberList = newRoleToMemberMap[role.role] = [];
        }
        if (memberMap[userRole?.memberRoleId?.memberId] !== undefined) {
          memberList.push({
            ...memberMap[userRole?.memberRoleId?.memberId],
            roleId: role.id
          });
        }
      }
    }
    setRoleToMemberMap(newRoleToMemberMap);
  }, [userRoles, memberProfiles, roles]);

  const getRoleStats = role => {
    let members = roleToMemberMap[role];
    return isArrayPresent(members) ? members.length : 0;
  };

  const removeFromRole = async (member, role) => {
    const members = roleToMemberMap[role];
    const { roleId } = members.find(m => member.id === m.id);
    let res = await removeUserFromRole(roleId, member.id, csrf);
    let data =
      res.payload && res.payload.status === 200 && !res.error
        ? res.payload
        : null;
    if (data) {
      // TODO: Remove role from map....
      const filtered = userRoles.filter(
        userRole =>
          userRole?.memberRoleId?.roleId !== roleId ||
          userRole?.memberRoleId?.memberId !== member.id
      );
      dispatch({
        type: SET_USER_ROLES,
        payload: filtered
      });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `${member.name} removed from ${role}s`
        }
      });
    }
  };

  const addToRole = async member => {
    const role = roles.find(role => role.role === currentRole.role);
    let res = await addUserToRole(role.id, member.id, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      setShowAddUser(false);
      dispatch({
        type: SET_USER_ROLES,
        payload: [...userRoles, data]
      });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `${member.name} added to ${currentRole.role}s`
        }
      });
    }
    setSelectedMember({});
  };

  const saveRole = async role => {
    let res = role.id
      ? await updateRole(role, csrf)
      : await addNewRole(role, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      setShowEditRole(false);
      const updatedRoles = [
        ...roles.filter(role => role?.id !== data.id),
        data
      ];
      dispatch({
        type: SET_ROLES,
        payload: updatedRoles
      });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `Role Updated: ${role.role}`
        }
      });
    }
  };

  const closeAddUser = () => {
    setShowAddUser(false);
  };

  const closeEditRole = () => {
    setShowEditRole(false);
  };

  const setRoleName = event => {
    setEditedRole({ ...editedRole, role: event?.target?.value });
  };

  const setRoleDescription = event => {
    setEditedRole({ ...editedRole, description: event?.target?.value });
  };

  return (
    <div className="roles-content">
      <div className="roles">
        <div className="roles-top">
          <div className="roles-top-left">
            <div className="roles-top-search-fields">
              <FormControl className="role-select">
                <InputLabel id="roles-select-label">Roles</InputLabel>
                <Select
                  labelId="roles-select-label"
                  multiple
                  value={selectedRoles}
                  onChange={event => {
                    const value = event.target.value;
                    setSelectedRoles(
                      typeof value === 'string' ? value.split(',') : value
                    );
                  }}
                  input={<OutlinedInput label="Roles" />}
                  renderValue={selected => selected.join(', ')}
                >
                  {roles?.map(roleObj => (
                    <MenuItem key={roleObj.role} value={roleObj.role}>
                      <Checkbox
                        checked={selectedRoles.indexOf(roleObj.role) > -1}
                      />
                      <ListItemText primary={roleObj.role} />
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{`Showing ${selectedRoles.length}/${roles?.length} roles`}</FormHelperText>
              </FormControl>
              <TextField
                className="member-role-search"
                label="Search members"
                placeholder="Member Name"
                value={searchText}
                onChange={e => {
                  setSearchText(e.target.value);
                }}
                InputProps={{
                  endAdornment: (
                    <InputAdornment color="gray" position="end">
                      <SearchIcon />
                    </InputAdornment>
                  )
                }}
              />
            </div>
          </div>
          <div className="roles-top-right">
            <Button
              className="role-add"
              color="primary"
              endIcon={<AddIcon />}
              onClick={() => {
                setShowEditRole(true);
                setEditedRole({});
              }}
            >
              Add Role
            </Button>
          </div>
        </div>
        <Modal open={showAddUser} onClose={closeAddUser}>
          <div className="role-modal">
            <Autocomplete
              options={
                memberProfiles?.filter(member => {
                  return !roleToMemberMap[currentRole.role]?.find(
                    m => m.id === member.id
                  );
                }) || []
              }
              value={selectedMember}
              onChange={(event, newValue) => setSelectedMember(newValue)}
              getOptionLabel={option => option.name || ''}
              renderInput={params => (
                <TextField
                  {...params}
                  className="fullWidth"
                  label="User To Add"
                  placeholder={`Select User to add to ${currentRole.role}s`}
                />
              )}
            />
            <Button color="primary" onClick={() => addToRole(selectedMember)}>
              Save
            </Button>
          </div>
        </Modal>
        <div className="roles-bot">
          {roles?.map(roleObj =>
            selectedRoles.includes(roleObj.role) ? (
              <Card className="role" key={`${roleObj.role}-card`}>
                <CardContent className="role-card">
                  <List style={{ paddingTop: 0 }}>
                    <div>
                      <ListSubheader style={{ padding: 0 }}>
                        <div className="role-header">
                          <div className="role-header-title">
                            <Typography variant="h4" color="black">
                              {roleObj.role}
                            </Typography>
                            <Typography
                              variant="subtitle1"
                              style={{
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis'
                              }}
                            >
                              {roleObj.description || ''}
                            </Typography>
                            <Typography
                              variant="subtitle2"
                              style={{ fontSize: '0.75rem' }}
                            >
                              {getRoleStats(roleObj.role)} Users
                            </Typography>
                          </div>
                          <div className="role-header-buttons">
                            <Button
                              className="role-add"
                              color="primary"
                              onClick={() => {
                                setShowAddUser(true);
                                setCurrentRole(roleObj);
                              }}
                            >
                              <PersonAddIcon />
                            </Button>
                            <Button
                              className="role-edit"
                              color="secondary"
                              onClick={() => {
                                setShowEditRole(true);
                                setEditedRole(roleObj);
                              }}
                            >
                              <EditIcon />
                            </Button>
                          </div>
                        </div>
                        <Divider />
                      </ListSubheader>
                      <RoleUserCards
                        roleMembers={roleToMemberMap[roleObj.role]}
                        onRemove={member =>
                          removeFromRole(member, roleObj.role)
                        }
                        memberQuery={searchText}
                      />
                    </div>
                  </List>
                </CardContent>
                <CardActions>
                  <Modal open={showEditRole} onClose={closeEditRole}>
                    <div className="role-modal">
                      <TextField
                        className="fullWidth"
                        label="Role Name"
                        placeholder="Set new role name"
                        onChange={setRoleName}
                        value={editedRole?.role || ''}
                        variant="outlined"
                      />
                      <TextField
                        className="fullWidth"
                        label="Role Description"
                        placeholder="Set new role description"
                        onChange={setRoleDescription}
                        value={editedRole?.description || ''}
                        variant="outlined"
                      />
                      <Button
                        color="primary"
                        onClick={() => saveRole(editedRole)}
                      >
                        Save
                      </Button>
                    </div>
                  </Modal>
                </CardActions>
              </Card>
            ) : null
          )}
        </div>
      </div>
    </div>
  );
};

export default Roles;
