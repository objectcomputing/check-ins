import React from "react";
import { getAvatarURL } from "../../../api/api.js";

import DeleteIcon from "@mui/icons-material/Delete";
import {
  Avatar,
  Divider,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Typography,
} from "@mui/material";

const RoleUserCards = ({ role, roleToMemberMap, removeFromRole }) => {
  roleToMemberMap[role].sort((a, b) => a.name.localeCompare(b.name));
  return roleToMemberMap[role].map(
    (member) =>
      member && (
        <div key={member.id}>
          <ListItem className="roles-list-item">
            <ListItemAvatar>
              <Avatar
                alt={`${member.name}'s avatar`}
                className="large"
                src={getAvatarURL(member.workEmail)}
              />
            </ListItemAvatar>
            <ListItemText
              primary={
                <Typography variant="h5" component="h2">
                  {member.name}
                </Typography>
              }
              secondary={
                <Typography color="textSecondary" component="h3">
                  {member.title}
                </Typography>
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
          </ListItem>
          <Divider variant="inset" component="li" />
        </div>
      )
  );
};

export default RoleUserCards;
