import React, { useContext, useState } from "react";

import Feedback_recipient_card from "../components/feedback_request/Feedback_recipient_card";
import { createMember } from "../api/member";
import { AppContext } from "../context/AppContext";
import { UPDATE_MEMBER_PROFILES } from "../context/actions";
import {
  selectNormalizedMembers,
  selectNormalizedMembersAdmin,
} from "../context/selectors";

import { Button, TextField, Grid } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import PersonIcon from "@material-ui/icons/Person";

import "./FeedbackRecipientSelector.css";
import MemberModal from "../components/member-directory/MemberModal";

const useStyles = makeStyles({
  search: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  searchInput: {
    width: "20em",
  },
  members: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-evenly",
    width: "100%",
  },
});

const FeedbackRecipientSelector = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, userProfile } = state;

  const classes = useStyles();

  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");

  const normalizedMembers = isAdmin
    ? selectNormalizedMembersAdmin(state, searchText)
    : selectNormalizedMembers(state, searchText);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const createMemberCards = normalizedMembers.map((member, index) => {
    return (
      <Feedback_recipient_card
        key={`${member.name}-${member.id}`}
        index={index}
        member={member}
      />
    );
  });

  return (
    <div className="FeedbackRecipientSelector">
      <Grid container spacing={3}>
        <Grid item xs={12} className={classes.search}>
          <TextField
            className={classes.searchInput}
            label="Select employees..."
            placeholder="Member Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
              <MemberModal
                open={open}
                onClose={handleClose}
                onSave={async (member) => {
                  if (
                    member.location &&
                    member.firstName &&
                    member.lastName &&
                    member.startDate &&
                    member.title &&
                    member.workEmail &&
                    csrf
                  ) {
                    let res = await createMember(member, csrf);

                    let data =
                      res.payload && res.payload.data && !res.error
                        ? res.payload.data
                        : null;
                    if (data) {
                      dispatch({
                        type: UPDATE_MEMBER_PROFILES,
                        payload: [...memberProfiles, data],
                      });
                    }
                    handleClose();
                  }
                }}
              />
            </div>
          )}
        </Grid>
        <Grid item className={classes.members}>
          {createMemberCards}
        </Grid>
      </Grid>
    </div>
  );
};

export default ;
