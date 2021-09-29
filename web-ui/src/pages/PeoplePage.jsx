import React, { useContext, useState } from "react";

import MemberSummaryCard from "../components/member-directory/MemberSummaryCard";
import { AppContext } from "../context/AppContext";
import {
  selectNormalizedMembers,
  selectNormalizedMembersAdmin,
} from "../context/selectors";

import { TextField, Grid } from "@mui/material";
import makeStyles from '@mui/styles/makeStyles';

import "./PeoplePage.css";

import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader"

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

const PeoplePage = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;

  const classes = useStyles();

  const [searchText, setSearchText] = useState("");

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");

  const normalizedMembers = isAdmin
    ? selectNormalizedMembersAdmin(state, searchText)
    : selectNormalizedMembers(state, searchText);

  const createMemberCards = normalizedMembers.map((member, index) => {
    return (
      <MemberSummaryCard
        key={`${member.name}-${member.id}`}
        index={index}
        member={member}
      />
    );
  });

  return (
    <div className="people-page">
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
        </Grid>
        <Grid item className={classes.members}>
          {createMemberCards.length? 
            createMemberCards : 
            Array.from({length: 20}).map((_, index) => <SkeletonLoader key={index} type="people" />)
          }
        </Grid>
      </Grid>
    </div>
  );
};

export default PeoplePage;
