import React, { useContext, useState } from "react";

import { AppContext } from "../../context/AppContext";
import { selectNormalizedMembers } from "../../context/selectors";
import { getAvatarURL } from "../api/api.js";
import { removeUserFromRole } from "../../api/roles";

import {
  Card,
  CardContent,
  CardHeader,
  TextField,
  Typography,
} from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/Delete";

import "./Roles.css";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, roles } = state;

  const [addUser, setAddUser] = useState(false);
  const [editRole, setEditRole] = useState(false);
  const [searchText, setSearchText] = useState("");

  const removeFromRole = async (id) => {
    if (csrf && id) {
      let res = await removeUserFromRole(id, csrf);
      console.log({ res });
    }
  };

  const normalizedMembers = selectNormalizedMembers(state, searchText);
  const members = {};
  for (const member of normalizedMembers) {
    let temp = roles.find((role) => role.memberid === member.id);
    if (temp) {
      members[member.id] = { ...member, role: temp };
    }
  }

  const roleToMemberMap = {};
  for (const role of roles || []) {
    let memberList = roleToMemberMap[role.role];
    if (!memberList) {
      memberList = roleToMemberMap[role.role] = [];
    }
    if (members[role.memberid] !== undefined) {
      memberList.push(members[role.memberid]);
    }
  }

  let uniqueRoles = Object.keys(roleToMemberMap);

  console.log({ members, roles, uniqueRoles, roleToMemberMap });

  const createUserCards = (role) => {
    roleToMemberMap[role].map((member) => {
      console.log(member);
      return (
        member && (
          <Card className="member-card">
            <CardHeader
              title={
                <Typography variant="h5" component="h1">
                  {member.name}
                </Typography>
              }
              subheader={
                <Typography color="textSecondary" component="h2">
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
  };

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
                subheader="description"
                title={role}
                titleTypographyProps={{ variant: "h5", component: "h2" }}
              />
              <CardContent>
                {roleToMemberMap[role].length > 0 && createUserCards(role)}
              </CardContent>
            </Card>
          ) : null
        )}
      </div>
    </div>
  );
};

export default Roles;
