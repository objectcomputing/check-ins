import React from "react";
import { getAvatarURL } from "../../api/api.js";

import DeleteIcon from "@material-ui/icons/Delete";
import Avatar from "@material-ui/core/Avatar";

import { Card, CardHeader, Typography } from "@material-ui/core";

const RoleUserCards = ({ role, roleToMemberMap, removeFromRole }) => {
  return roleToMemberMap[role].map(
    (member) =>
      member && (
        <div key={member.id}>
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
};

export default RoleUserCards;
