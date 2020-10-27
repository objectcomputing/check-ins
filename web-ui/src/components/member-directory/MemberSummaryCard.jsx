import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
import { AppContext } from "../../context/AppContext";

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
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
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
        {isAdmin && (
          <div className="member-card-actions">
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
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default MemberSummaryCard;
