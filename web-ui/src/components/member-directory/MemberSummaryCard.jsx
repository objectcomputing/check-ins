import React, { useState } from "react";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";
import MemberModal from "./MemberModal";

import "./MemberSummaryCard.css";

const MemberSummaryCard = ({ member }) => {
  const { imageURL, name, workEmail, title, manager } = member;
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);
  // const [selectValue, setSelectValue] = useState("terminate");

  // const handleSelectChange = (e) => {
  //   setSelectValue(e.target.value);
  // };

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
          <Button onClick={handleOpen}>Edit Member</Button>
          <Button>Terminate Member</Button>
          <MemberModal
            member={member}
            open={open}
            onClose={handleClose}
            onSave={(member) => {
              handleClose();
            }}
          />
          {/* <div>
            <select value={selectValue} onChange={handleSelectChange}>
              <option value="terminate">Terminate</option>
              <option value="delete">Delete</option>
            </select> */}
          {/* <Button>{selectValue} Member</Button> */}
          {/* </div> */}
        </CardActions>
      </CardContent>
    </Card>
  );
};

export default MemberSummaryCard;
