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
  removeUserFromRole, getAllMembersGroupedByRole,
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

import "./Roles.css";
import {selectProfile} from "../../../context/selectors";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, roles, userRoles } = state;

  const [showAddUser, setShowAddUser] = useState(false);
  const [showAddRole, setShowAddRole] = useState(false);
  const [newRole, setNewRole] = useState("");
  // const [editRole, setEditRole] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [selectedMember, setSelectedMember] = useState({});
  const [selectedRole, setSelectedRole] = useState("");
  const [memberRoles, setMemberRoles] = useState([]);

  memberProfiles.sort((a, b) => a.name.localeCompare(b.name));

  useEffect(() => {
    const getMemberRoles = async () => {
      return await getAllMembersGroupedByRole(csrf);
    }

    getMemberRoles().then(res => {
      if (res && res.payload && res.payload.data && !res.error) {
        console.log(res.payload.data);
        setMemberRoles(res.payload.data);
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to retrieve member roles",
          },
        });
      }
    });
  }, [csrf]);

  const removeFromRole = async (member, role) => {
    const roleId = role.id;
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
          {memberRoles.map((roleObj) =>
            roleObj.role.toLowerCase().includes(searchText.toLowerCase()) ? (
              <Card className="role" key={roleObj.roleId}>
                <CardHeader
                  title={
                    <div className="role-header">
                      <Typography variant="h4" component="h3">
                        {roleObj.role}
                      </Typography>
                      <Typography variant="h5" component="h5">
                        {roleObj.description || ""}
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
                            setSelectedRole(roleObj.role);
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
                        role={roleObj.role}
                        roleMembers={roleObj.memberIds.map((memberId) => selectProfile(state, memberId))}
                        removeFromRole={removeFromRole}
                      />
                    </List>
                  }
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
