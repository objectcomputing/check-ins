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
  CardHeader,
  List,
  Modal,
  TextField,
  Typography,
} from "@mui/material";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import Autocomplete from '@mui/material/Autocomplete';

import { isArrayPresent } from './../../../helpers/checks';

import "./Roles.css";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, roles, userRoles } = state;

  const [showAddUser, setShowAddUser] = useState(false);
  const [showAddRole, setShowAddRole] = useState(false);
  const [newRole, setNewRole] = useState("");
  // const [editRole, setEditRole] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [selectedMember, setSelectedMember] = useState({});
  const [roleToMemberMap, setRoleToMemberMap] = useState({});
  const [selectedRole, setSelectedRole] = useState("");

  memberProfiles?.sort((a, b) => a.name.localeCompare(b.name));

  useEffect(() => {
      const memberMap = {};
      if(memberProfiles && memberProfiles.length > 0) {
        for (const member of memberProfiles) {
          memberMap[member.id] = member;
        }
      }

      const newRoleToMemberMap = {};
      for (const userRole of userRoles || []) {
        const role = roles.find((role) => role.id === userRole?.memberRoleId?.roleId);
        if(role) {
          let memberList = newRoleToMemberMap[role.role];
          if (!memberList) {
            memberList = newRoleToMemberMap[role.role] = [];
          }
          if (memberMap[userRole?.memberRoleId?.memberId] !== undefined) {
            memberList.push({ ...memberMap[userRole?.memberRoleId?.memberId], roleId: role.id });
          }
        }
      }
      setRoleToMemberMap(newRoleToMemberMap);
    }, [userRoles, memberProfiles, roles]);

  const uniqueRoles = Object.keys(roleToMemberMap);

  const getRoleStats = (role) => {
    let members = roleToMemberMap[role];
    return isArrayPresent(members) ? members.length : 0;
  }

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
      const filtered = userRoles.filter((userRole) => userRole?.memberRoleId?.roleId !== roleId || userRole?.memberRoleId?.memberId !== member.id);
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

  const addRole = async (member, role) => {
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

  // const closeEditRole = () => {
  //   setEditRole(false);
  // };

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
          {/* <Button color="primary" onClick={() => setShowAddRole(true)}>
            Add New Role
          </Button> */}
        </div>
        <div className="roles-bot">
          {uniqueRoles.map((role) =>
            role.toLowerCase().includes(searchText.toLowerCase()) ? (
              <Card className="role" key={role}>
                <CardHeader
                  title={
                    <div className="role-header">
                      <Typography variant="h4" component="h3">
                        {role}
                      </Typography>
                      <Typography variant="h5" component="h5">
                        {role.description || ""}
                      </Typography>
                      <Typography variant="h5" component="h5">
                        {getRoleStats(role)} Users
                      </Typography>
                    </div>
                  }
                  subheader={
                    <div>
                      <div className="role-buttons">
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
                        {/* <Button className="role-edit" color="primary">
                        <span>Edit Role</span> <EditIcon />
                      </Button> */}
                      </div>
                    </div>
                  }
                />
                <CardContent className="role-card">
                  {
                    <List>
                      <RoleUserCards
                        role={role}
                        roleToMemberMap={roleToMemberMap}
                        removeFromRole={removeFromRole}
                      />
                    </List>
                  }
                </CardContent>
                <CardActions>
                  <Modal open={showAddUser} onClose={closeAddUser}>
                    <div className="role-modal">
                      <Autocomplete
                        options={memberProfiles?.filter(
                          (member) => !roleToMemberMap[role].includes(member.id)
                        ) || []}
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
                  {/* <Modal open={} onClose={closeEditRole}>
                  <div className="edit-role-modal">
                    <TextField
                      id="role-description"
                      label="description"
                      required
                      className="halfWidth"
                      placeholder="Pdl Description"
                      value={
                        editedMember.firstName ? editedMember.firstName : ""
                      }
                      onChange={(e) =>
                        setMember({
                          ...editedMember,
                          firstName: e.target.value,
                        })
                      }
                    />
                    <Button onClick={() => addToRole(selectedMember)}>
                      Save
                    </Button>
                  </div>
                </Modal> */}
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
