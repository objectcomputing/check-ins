import React, { useContext, useState, useRef } from "react";
import { styled } from '@mui/material/styles';
import MemberSummaryCard from "../components/member-directory/MemberSummaryCard";
import { AppContext } from "../context/AppContext";
import {
  selectNormalizedMembers,
  selectNormalizedMembersAdmin,
} from "../context/selectors";
import { TextField, Grid } from "@mui/material";
import "./PeoplePage.css";
import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader"

const PREFIX = 'PeoplePage';
const classes = {
  search: `${PREFIX}-search`,
  searchInput: `${PREFIX}-searchInput`,
  members: `${PREFIX}-members`
};

const Root = styled('div')({
  [`& .${classes.search}`]: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  [`& .${classes.searchInput}`]: {
    width: "20em",
  },
  [`& .${classes.members}`]: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-evenly",
    width: "100%",
  },
});

const PeoplePage = () => {
  const { state } = useContext(AppContext);
  //set ref initially to false to tell renderer to load skeleton keys
  const doneLoading = useRef(false);
  const { userProfile } = state;

  const [searchText, setSearchText] = useState("");

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");

  const normalizedMembers = isAdmin
    ? selectNormalizedMembersAdmin(state, searchText)
    : selectNormalizedMembers(state, searchText);

//checks to see if the selector has returned but there are no results
  if (userProfile?.role && normalizedMembers.length === 0) {
      doneLoading.current=true;
  }

  const createMemberCards = normalizedMembers.map((member, index) => {
    //if there are more than 0 cards, render skeleton keys until card mapping is done
    doneLoading.current=false;
    if (normalizedMembers.length-1===index) {
      doneLoading.current=true;
    }
    return (
      <MemberSummaryCard
        key={`${member.name}-${member.id}`}
        index={index}
        member={member}
      />
    );

  })

  return (
    <Root className="people-page">
      <Grid container spacing={3}>
        <Grid item xs={12} className={classes.search}>
          <TextField
            className={classes.searchInput}
            label="Select employees..."
            placeholder="Member Name"
            value={searchText}
            onChange={(e) => {
              doneLoading.current=false;
              setSearchText(e.target.value);
            }}
          />
        </Grid>
        <Grid item className={classes.members}>
          {!doneLoading?.current ? Array.from({length: 20}).map((_, index) => <SkeletonLoader key={index} type="people" />):
           normalizedMembers.length && doneLoading?.current ? createMemberCards : null}
        </Grid>
      </Grid>
    </Root>
  );
};

export default PeoplePage;
