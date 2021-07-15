import React, { useContext, useEffect, useState } from "react";

import { AppContext } from "../../context/AppContext";
import { DELETE_ROLE, UPDATE_ROLES, UPDATE_TOAST } from "../../context/actions";
import { getAvatarURL } from "../../api/api.js";
import { addUserToRole, removeUserFromRole } from "../../api/roles";

import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Modal,
  TextField,
  Typography,
} from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/Delete";
import PersonAddIcon from "@material-ui/icons/PersonAdd";
import EditIcon from "@material-ui/icons/Edit";
import Autocomplete from "@material-ui/lab/Autocomplete";
import Avatar from "@material-ui/core/Avatar";

import "./Roles.css";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, roles } = state;

  const [addUser, setAddUser] = useState(false);
  const [editRole, setEditRole] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [selectedMember, setSelectedMember] = useState({});
  const [roleToMemberMap, setRoleToMemberMap] = useState({});
  const [selectedRole, setSelectedRole] = useState("");

  memberProfiles.sort((a, b) => a.name.localeCompare(b.name));

  useEffect(() => {
    const memberMap = {};
    for (const member of memberProfiles) {
      // let temp = roles.find((role) => role.memberid === member.id);
      // if (temp) {
      //   memberMap[member.id] = { ...member, role: temp };
      // }
      memberMap[member.id] = member;
    }

    const newRoleToMemberMap = {};
    for (const role of roles || []) {
      let memberList = newRoleToMemberMap[role.role];
      if (!memberList) {
        memberList = newRoleToMemberMap[role.role] = [];
      }
      if (memberMap[role.memberid] !== undefined) {
        memberList.push({ ...memberMap[role.memberid], roleId: role.id });
      }
    }
    console.log({ newRoleToMemberMap });
    setRoleToMemberMap(newRoleToMemberMap);
  }, [memberProfiles, roles]);

  const uniqueRoles = Object.keys(roleToMemberMap);

  const removeFromRole = async (member, role) => {
    console.log({ member, role });
    const members = roleToMemberMap[role];
    const { roleId } = members.find((m) => member.id === m.id);
    let res = await removeUserFromRole(roleId, csrf);
    let data =
      res.payload && res.payload.status === 200 && !res.error
        ? res.payload
        : null;
    if (data) {
      dispatch({
        type: DELETE_ROLE,
        payload: roleId,
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
    let res = await addUserToRole(selectedRole, member.id, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      setAddUser(false);
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
    setSelectedMember({});
  };

  const closeAddUser = () => {
    setAddUser(false);
  };

  const closeEditRole = () => {
    setEditRole(false);
  };

  const createUserCards = (role) =>
    roleToMemberMap[role].map(
      (member) =>
        member && (
          <div>
            <Card className="member-card">
              <CardHeader
                title={
                  <Typography variant="h5" component="h2">
                    {member.name}
                  </Typography>
                }
                subheader={
                  <Typography color="textSecondary" component="h3">
                    {member.title}
                  </Typography>
                }
                disableTypography
                avatar={
                  <Avatar
                    className="large"
                    src={getAvatarURL(member.workEmail)}
                  />
                }
              />
              <div
                className="icon"
                onClick={() => {
                  removeFromRole(member, role);
                }}
              >
                <DeleteIcon />
              </div>
            </Card>
          </div>
        )
    );
  console.log({ roleToMemberMap, uniqueRoles });

  return (
    <div className="role-content">
      <div className="roles">
        <div className="role-top">
          <h2>Roles</h2>
          <TextField
            className="role-search"
            label="Search Roles..."
            placeholder="Role Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
        </div>
        {uniqueRoles.map((role) =>
          role.toLowerCase().includes(searchText.toLowerCase()) ? (
            <Card className="role">
              <CardHeader
                title={
                  <div className="role-header">
                    <Typography variant="h4" component="h3">
                      {role}
                    </Typography>
                    <Typography variant="h5" component="h4">
                      {role.description || ""}
                    </Typography>
                    <div
                      className="role-buttons"
                      onClick={() => {
                        setAddUser(true);
                        setSelectedRole(role);
                      }}
                    >
                      <div className="role-add">
                        <span>Add User</span>
                        <PersonAddIcon />
                      </div>
                      <div className="role-edit">
                        <span>Edit Role</span> <EditIcon />
                      </div>
                    </div>
                  </div>
                }
                subheader={
                  <Typography color="textSecondary" component="h5">
                    {role?.description || ""}
                  </Typography>
                }
              />
              <CardContent className="role-card">
                {createUserCards(role)}
              </CardContent>
              <CardActions>
                <Modal open={addUser} onClose={closeAddUser}>
                  <div className="member-modal">
                    <Autocomplete
                      options={memberProfiles}
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
                          placeholder="Select User to add to role"
                        />
                      )}
                    />
                    <Button onClick={() => addToRole(selectedMember)}>
                      Save
                    </Button>
                  </div>
                </Modal>
                {/* <Modal open={editRole} onClose={closeEditRole}>
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
  );
};

export default Roles;
