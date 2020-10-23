import React from "react";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";

import "./MemberSummaryCard.css";

const MemberSummaryCard = ({ member }) => {
  const { imageURL, name, workEmail, title, manager } = member;
  console.log({ member });
  return (
    <Card className="member-card">
      <CardHeader title={name} />
      <CardContent>
        <div className="member-summary-parent">
          <Avatar alt={name} className="member-summary-avatar" src={imageURL} />
          <div className="member-summary-info">
            <div>
              <strong>Name: </strong>
              {name}
            </div>
            <div>
              <strong>Email: </strong>
              {workEmail}
            </div>
            <div>
              <strong>Title: </strong>
              {title}
            </div>
            <div>
              <strong>Manager: </strong>
              {manager}
            </div>
          </div>
        </div>
        <CardActions>
          <Button>Edit Member</Button>
          <Button>Delete Member</Button>
        </CardActions>
      </CardContent>
    </Card>
  );
};

export default MemberSummaryCard;
