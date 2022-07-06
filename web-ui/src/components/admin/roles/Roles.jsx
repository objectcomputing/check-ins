import React, { useContext, useState } from "react";

import { AppContext } from "../../../context/AppContext";
import {
  UPDATE_ROLES,
  SET_USER_ROLES,
  UPDATE_TOAST,
} from "../../../context/actions";
import {
  addUserToRole,
  addNewRole,
  removeUserFromRole,
} from "../../../api/roles";

import RoleUserCards from "./RoleUserCards";

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
} from "@mui/material";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import SearchIcon from "@mui/icons-material/Search";

import "./Roles.css";
import {selectProfile} from "../../../context/selectors";
import {Search} from "@mui/icons-material";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, roles, userRoles } = state;

  const [showAddUser, setShowAddUser] = useState(false);
  const [showAddRole, setShowAddRole] = useState(false);
  const [newRole, setNewRole] = useState("");
  const [searchText, setSearchText] = useState("");
  // const [editRole, setEditRole] = useState(false);
  const [memberSearchText, setMemberSearchText] = useState("");
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [selectedMember, setSelectedMember] = useState({});
  const [selectedRole, setSelectedRole] = useState("");

  memberProfiles.sort((a, b) => a.name.localeCompare(b.name));

  const removeFromRole = async (member, roleId, roleName) => {
    let res = await removeUserFromRole(roleId, member.id, csrf);
    let data =
      res.payload && res.payload.status === 200 && !res.error
        ? res.payload
        : null;
    if (data) {
      const updatedMemberRoles = [...userRoles];
      const roleIndex = updatedMemberRoles.findIndex(memberRole => memberRole.roleId === roleId);
      updatedMemberRoles[roleIndex].memberIds.splice(updatedMemberRoles[roleIndex].memberIds.indexOf(member.id), 1);

      // Update the global state
      dispatch({
        type: SET_USER_ROLES,
        payload: updatedMemberRoles,
      });

      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `${member.name} removed from ${roleName}s`,
        },
      });
    }
  };

  const addToRole = async (member) => {
    const role = roles.find((role) => role.role === selectedRole);
    let res = await addUserToRole(role.id, member.id, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      setShowAddUser(false);

      const updatedMemberRoles = [...userRoles];
      const roleIndex = updatedMemberRoles.findIndex(memberRole => memberRole.roleId === data.memberRoleId.roleId);
      updatedMemberRoles[roleIndex].memberIds.push(data.memberRoleId.memberId);

      // Update the global state
      dispatch({
        type: SET_USER_ROLES,
        payload: updatedMemberRoles,
      });

      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `${member.name} added to ${selectedRole}s`,
        },
      });
    }
    setSelectedMember({});
  };

  const addRole = async (member) => {
    let res = await addNewRole(selectedRole, member.id, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      setShowAddRole(false);
      dispatch({
        type: UPDATE_ROLES,
        payload: data,
      });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `${member.name} added to ${selectedRole}s`,
        },
      });
    }
  };

  const closeAddUser = () => {
    setShowAddUser(false);
  };

  const closeAddRole = () => {
    setShowAddRole(false);
  };

  return (
    <div className="roles-content">
      <div className="roles">
        <div className="roles-top">
          <div className="roles-top-left">
            <div className="roles-top-search-fields">
              <FormControl size="small" className="role-select">
                <InputLabel id="roles-select-label">Roles</InputLabel>
                <Select
                  labelId="roles-select-label"
                  multiple
                  value={selectedRoles}
                  onChange={(event) => {
                    const value = event.target.value;
                    setSelectedRoles(typeof value === "string" ? value.split(",") : value)
                  }}
                  input={<OutlinedInput label="Roles"/>}
                  renderValue={(selected) => selected.join(", ")}
                >
                  {userRoles?.map((roleObj) => (
                    <MenuItem value={roleObj.role}>
                      <Checkbox checked={selectedRoles.indexOf(roleObj.role) > -1}/>
                      <ListItemText primary={roleObj.role}/>
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{`Showing ${selectedRoles.length}/${userRoles.length} roles`}</FormHelperText>
              </FormControl>
              <TextField
                className="member-role-search"
                label="Search members"
                placeholder="Member Name"
                value={memberSearchText}
                onChange={(e) => {
                  setMemberSearchText(e.target.value);
                }}
                InputProps={{
                  endAdornment: <InputAdornment color="gray" position="end"><SearchIcon/></InputAdornment>
                }}
              />
            </div>
          </div>
        </div>
        <div className="roles-bot">
          {userRoles?.map((roleObj) =>
            roleObj.role.toLowerCase().includes(searchText.toLowerCase()) ? (
              <Card className="role" key={roleObj.roleId}>
                <CardContent className="role-card">
                  <List style={{ paddingTop: 0 }}>
                    <div>
                      <ListSubheader style={{ padding: 0 }}>
                        <div className="role-header">
                          <div className="role-header-title">
                            <Typography variant="h4" component="h3" color="black">
                              {roleObj.role}
                            </Typography>
                            <Typography variant="h6" component="h6">
                              {roleObj.description || ""}
                            </Typography>
                          </div>
                          <div className="role-header-buttons">
                            <Button
                              className="role-add"
                              color="primary"
                              endIcon={<PersonAddIcon/>}
                              onClick={() => {
                                setShowAddUser(true);
                                setSelectedRole(roleObj.role);
                              }}
                            >
                              Add User
                            </Button>
                          </div>
                        </div>
                        <Divider component="li"/>
                      </ListSubheader>
                      <RoleUserCards
                        roleId={roleObj.roleId}
                        roleName={roleObj.role}
                        roleMembers={roleObj.memberIds.map((memberId) => selectProfile(state, memberId))}
                        onRemove={(member) => removeFromRole(member, roleObj.roleId, roleObj.role)}
                        memberQuery={memberSearchText}
                      />
                    </div>
                  </List>
                </CardContent>
                <CardActions>
                  <Modal open={showAddUser} onClose={closeAddUser}>
                    <div className="role-modal">
                      <Autocomplete
                        options={memberProfiles.filter(
                          (member) => !roleObj.memberIds.includes(member.id)
                        )}
                        value={selectedMember}
                        onChange={(event, newValue) =>
                          setSelectedMember(newValue)
                        }
                        getOptionLabel={(option) => option.name || ""}
                        renderInput={(params) => (
                          <TextField
                            {...params}
                            className="fullWidth"
                            label="User To Add"
                            placeholder={`Select User to add to ${selectedRole}s`}
                          />
                        )}
                      />
                      <Button
                        color="primary"
                        onClick={() => addToRole(selectedMember)}
                      >
                        Save
                      </Button>
                    </div>
                  </Modal>
                  <Modal open={showAddRole} onClose={closeAddRole}>
                    <div className="role-modal">
                      <TextField
                        className="fullWidth"
                        label="Role Description"
                        placeholder="Set new role description"
                        onChange={setNewRole}
                        value={newRole ? newRole : ""}
                        variant="outlined"
                      />
                      <Button color="primary" onClick={() => addRole(newRole)}>
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
