import React, { useContext, useEffect, useState } from "react";

import MemberSummaryCard from "../components/member-directory/MemberSummaryCard";
import { createMember } from "../api/member";
import { AppContext } from "../context/AppContext";
import { UPDATE_MEMBER_PROFILES } from "../context/actions";

import { Button, TextField, Grid } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import PersonIcon from "@material-ui/icons/Person";

import "./DirectoryPage.css";
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

const DirectoryPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, userProfile } = state;

  const classes = useStyles();

  const [members, setMembers] = useState([]);

  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");

  useEffect(() => {
    memberProfiles &&
      setMembers(
        memberProfiles
          .filter((profile) => {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return (
              profile.terminationDate === null ||
              profile.terminationDate === undefined ||
              today <= new Date(profile.terminationDate)
            );
          })
          .sort((a, b) => {
            const aPieces = a.name.split(" ").slice(-1);
            const bPieces = b.name.split(" ").slice(-1);
            return aPieces.toString().localeCompare(bPieces);
          })
      );
  }, [memberProfiles]);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const createMemberCards = members.map((member, index) => {
    if (member.name.toLowerCase().includes(searchText.toLowerCase())) {
      return (
        <MemberSummaryCard
          key={`${member.name}-${member.id}`}
          index={index}
          member={member}
        />
      );
    } else return null;
  });

  return (
    <div className="directory-page">
      <Grid container spacing={3}>
        <Grid item xs={12} className={classes.search}>
          <TextField
            className={classes.searchInput}
            label="Search Members"
            placeholder="Member Name"
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
          {isAdmin && (
            <div className="add-member">
              <Button startIcon={<PersonIcon />} onClick={handleOpen}>
                Add Member
              </Button>

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

export default DirectoryPage;
