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
  const [membersWithRoles, setMembersWithRoles] = useState({});
  const [selectedRole, setSelectedRole] = useState("");

  memberProfiles.sort((a, b) => a.name.localeCompare(b.name));

  useEffect(() => {
    const newMembersWithRoles = {};
    for (const member of memberProfiles) {
      let temp = roles.find((role) => role.memberid === member.id);
      if (temp) {
        newMembersWithRoles[member.id] = { ...member, role: temp };
      }
    }
    setMembersWithRoles(newMembersWithRoles);

    const newRoleToMemberMap = {};
    for (const role of roles || []) {
      let memberList = newRoleToMemberMap[role.role];
      if (!memberList) {
        memberList = newRoleToMemberMap[role.role] = [];
      }
      if (newMembersWithRoles[role.memberid] !== undefined) {
        memberList.push(newMembersWithRoles[role.memberid]);
      }
    }
    setRoleToMemberMap(newRoleToMemberMap);
  }, [memberProfiles, roles]);

  const removeFromRole = async (member) => {
    if (member.role) {
      let res = await removeUserFromRole(member.role.id, csrf);
      let data =
        res.payload && res.payload.status === 200 && !res.error
          ? res.payload
          : null;
      if (data) {
        // roleToMemberMap[member.role.role].filter(
        //   (m) => m.role.id !== member.role.id
        // );
        // setRoleToMemberMap(roleToMemberMap);
        dispatch({
          type: DELETE_ROLE,
          payload: member.role.id,
        });
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "success",
            toast: `${member.name} removed from ${member.role.role}s`,
          },
        });
      }
    }
  };

  const addToRole = async (member) => {
    let res = await addUserToRole(selectedRole, member.id, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      setAddUser(false);
      roleToMemberMap[selectedRole].push(member);
      setRoleToMemberMap(roleToMemberMap);
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

  let uniqueRoles = Object.keys(roleToMemberMap);

  console.log({ roleToMemberMap, roles });

  const createUserCards = (role) =>
    roleToMemberMap[role].map((member) => {
      return (
        member && (
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
                removeFromRole(member);
              }}
            >
              <DeleteIcon />
            </div>
          </Card>
        )
      );
    });

  return (
    <div className="role-content">
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
      <div className="roles">
        {uniqueRoles.map((role) =>
          role.toLowerCase().includes(searchText.toLowerCase()) ? (
            <Card className="role">
              <CardHeader
                title={
                  <div className="role-header">
                    <Typography variant="h4" component="h3">
                      {role}
                    </Typography>
                    <div
                      className="add-user-to-role"
                      onClick={() => {
                        setAddUser(true);
                        setSelectedRole(role);
                      }}
                    >
                      Add User
                      <div className="person-icon">
                        <PersonAddIcon />
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
                {roleToMemberMap[role].length > 0 && createUserCards(role)}
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
              </CardActions>
            </Card>
          ) : null
        )}
      </div>
    </div>
  );
};

export default Roles;
