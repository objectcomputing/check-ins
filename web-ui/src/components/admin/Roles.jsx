import React, { useContext, useEffect, useState } from "react";

import { AppContext } from "../../context/AppContext";
import { selectNormalizedMembers } from "../../context/selectors";
import { getAvatarURL } from "../../api/api.js";
import { addUserToRole, removeUserFromRole } from "../../api/roles";

import {
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
  const { csrf, roles } = state;

  const [addUser, setAddUser] = useState(false);
  const [editRole, setEditRole] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState({});
  const [roleToMemberMap, setRoleToMemberMap] = useState({});
  const [members, setMembers] = useState({});

  const normalizedMembers = selectNormalizedMembers(state, searchText);

  useEffect(() => {
    for (const member of normalizedMembers) {
      let temp = roles.find((role) => role.memberid === member.id);
      if (temp) {
        members[member.id] = { ...member, role: temp };
      }
    }
    setMembers(members);

    for (const role of roles || []) {
      let memberList = roleToMemberMap[role.role];
      if (!memberList) {
        memberList = roleToMemberMap[role.role] = [];
      }
      if (members[role.memberid] !== undefined) {
        memberList.push(members[role.memberid]);
      }
    }
    setRoleToMemberMap(roleToMemberMap);
  }, [normalizedMembers]);

  const removeFromRole = async (id) => {
    if (csrf && id) {
      let res = await removeUserFromRole(id, csrf);
    }
  };

  const addToRole = async (role, memberid) => {
    if (csrf && memberid && role) {
      let res = await addUserToRole(role, memberid, csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
    }
  };

  const onClose = () => {
    setModalOpen(false);
  };

  let uniqueRoles = Object.keys(roleToMemberMap);

  console.log({ members, roles, uniqueRoles, roleToMemberMap, selectedMember });

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
            <div onClick={() => removeFromRole(role.id)}>
              <DeleteIcon />
            </div>
          </Card>
        )
      );
    });

  return (
    <div className="role-content">
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
                      onClick={() => setModalOpen(true)}
                    >
                      Add User
                      <PersonAddIcon />
                    </div>
                  </div>
                }
                subheader={
                  <Typography color="textSecondary" component="h5">
                    {role?.description || ""}
                  </Typography>
                }
              />
              <CardContent>
                {roleToMemberMap[role].length > 0 && createUserCards(role)}
              </CardContent>
              <CardActions>
                <Modal open={modalOpen} onClose={onClose}>
                  <div className="member-modal">
                    <Autocomplete
                      options={Object.values(members)}
                      value={selectedMember}
                      onChange={(event, newValue) =>
                        setSelectedMember(newValue)
                      }
                      getOptionLabel={(option) => option.name || ""}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          className="fullWidth"
                          label="Member"
                          placeholder="Select Member to add to role"
                        />
                      )}
                    />
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
