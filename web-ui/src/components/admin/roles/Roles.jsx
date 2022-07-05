import React, { useContext, useEffect, useState } from "react";

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
  Divider
} from "@mui/material";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import Autocomplete from "@mui/material/Autocomplete";

import "./Roles.css";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, roles, userRoles } = state;

  const [showAddUser, setShowAddUser] = useState(false);
  const [showAddRole, setShowAddRole] = useState(false);
  const [newRole, setNewRole] = useState("");
  const [searchText, setSearchText] = useState("");
  const [selectedMember, setSelectedMember] = useState({});
  const [roleToMemberMap, setRoleToMemberMap] = useState({});
  const [selectedRole, setSelectedRole] = useState("");

  memberProfiles.sort((a, b) => a.name.localeCompare(b.name));

  useEffect(() => {
    const memberMap = {};
    for (const member of memberProfiles) {
      memberMap[member.id] = member;
    }

    const newRoleToMemberMap = {};
    for (const userRole of userRoles || []) {
      const role = roles.find(
        (role) => role.id === userRole?.memberRoleId?.roleId
      );
      if (role) {
        let memberList = newRoleToMemberMap[role.role];
        if (!memberList) {
          memberList = newRoleToMemberMap[role.role] = [];
        }
        if (memberMap[userRole?.memberRoleId?.memberId] !== undefined) {
          memberList.push({
            ...memberMap[userRole?.memberRoleId?.memberId],
            roleId: role.id,
          });
        }
      }
    }
    setRoleToMemberMap(newRoleToMemberMap);
  }, [userRoles, memberProfiles, roles]);

  const uniqueRoles = Object.keys(roleToMemberMap);

  const removeFromRole = async (member, role) => {
    const members = roleToMemberMap[role];
    const { roleId } = members.find((m) => member.id === m.id);
    let res = await removeUserFromRole(roleId, member.id, csrf);
    let data =
      res.payload && res.payload.status === 200 && !res.error
        ? res.payload
        : null;
    if (data) {
      // TODO: Remove role from map....
      const filtered = userRoles.filter(
        (userRole) =>
          userRole?.memberRoleId?.roleId !== roleId ||
          userRole?.memberRoleId?.memberId !== member.id
      );
      dispatch({
        type: SET_USER_ROLES,
        payload: filtered,
      });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `${member.name} removed from ${role}s`,
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
      dispatch({
        type: SET_USER_ROLES,
        payload: [...userRoles, data],
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

  const roleContainsMember = (member) => {
    return selectedRole && roleToMemberMap[selectedRole].find((currentMember) => {
      return currentMember.id === member.id;
    });
  };

  return (
    <div className="roles-content">
      <div className="roles">
        <div className="roles-top">
          <div className="roles-top-left">
            <h2>Roles</h2>
            <TextField
              className="role-search"
              label="Search Roles"
              placeholder="Role"
              fullWidth={true}
              value={searchText}
              onChange={(e) => {
                setSearchText(e.target.value);
              }}
            />
          </div>
        </div>
        <div className="roles-bot">
          {uniqueRoles.map((role) =>
            role.toLowerCase().includes(searchText.toLowerCase()) ? (
              <Card className="role" key={role}>
                <CardContent className="role-card" style={{ padding: 0 }}>
                  {
                    <List style={{ paddingTop: 0 }}>
                      <div>
                        <ListSubheader style={{ padding: 0 }}>
                          <div className="role-header">
                            <div className="role-header-title">
                              <Typography variant="h4" component="h3" color="black">
                                {role}
                              </Typography>
                              <Typography variant="h5" component="h5">
                                {role.description || ""}
                              </Typography>
                            </div>
                            <div className="role-header-buttons">
                              <Button
                                className="role-add"
                                color="primary"
                                onClick={() => {
                                  setShowAddUser(true);
                                  setSelectedRole(role);
                                }}
                              >
                                <span>Add User</span>
                                <PersonAddIcon />
                              </Button>
                              {/*<Button className="role-edit" color="primary"><span>Edit Role</span> <EditIcon /></Button> */}
                            </div>
                          </div>
                          <Divider component="li"/>
                        </ListSubheader>
                        <RoleUserCards
                          role={role}
                          roleToMemberMap={roleToMemberMap}
                          removeFromRole={removeFromRole}
                        />
                      </div>
                    </List>
                  }
                </CardContent>
                <CardActions>
                  <Modal open={showAddUser} onClose={closeAddUser}>
                    <div className="role-modal">
                      <Autocomplete
                        options={memberProfiles.filter((member) =>
                          !roleContainsMember(member)
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
