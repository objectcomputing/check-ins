import React, { useContext, useState } from "react";

import MemberModal from "./MemberModal";
import { AppContext, UPDATE_MEMBER_PROFILES } from "../../context/AppContext";

import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";

import "./MemberSummaryCard.css";

const MemberSummaryCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const { imageURL, name, workEmail, title } = member;
  const [currentMember, setCurrentMember] = useState(member);
  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);
  // const [selectValue, setSelectValue] = useState("terminate");

  // const handleSelectChange = (e) => {
  //   setSelectValue(e.target.value);
  // };

  return (
    <Card className="member-card">
      <CardHeader
        avatar={
          <Avatar
            alt={name}
            className="member-summary-avatar"
            src={imageURL}
            style={{ margin: "0px" }}
          />
        }
        subheader={
          <div>
            {title}
            <br />
            {workEmail}
          </div>
        }
        title={name}
      />
      <CardContent>
        {isAdmin && (
          <div className="member-card-actions">
            <CardActions>
              <Button onClick={handleOpen}>Edit Member</Button>
              <Button>Terminate Member</Button>
              <MemberModal
                member={currentMember}
                open={open}
                onClose={handleClose}
                onSave={(member) => {
                  setCurrentMember(member);
                  const copy = [...memberProfiles];
                  copy[index] = member;
                  dispatch({
                    type: UPDATE_MEMBER_PROFILES,
                    payload: copy,
                  });
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
