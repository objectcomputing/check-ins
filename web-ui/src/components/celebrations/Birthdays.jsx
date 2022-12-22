import React, { useContext, useState } from "react";
import { Link } from "react-router-dom";
import { AppContext } from "../../context/AppContext";

import { styled } from "@mui/material/styles";
import { getAvatarURL } from "../../api/api.js";
import {
  selectMemberProfilesLoading,
  selectProfile,
} from "../../context/selectors";
import SkeletonLoader from "../skeleton_loader/SkeletonLoader";

import { Card, CardHeader } from "@mui/material";
import Avatar from "@mui/material/Avatar";

import { Grid } from "@mui/material";

import "./Birthdays.css";

import { Typography } from "@mui/material";

const PREFIX = "MemberSummaryCard";
const classes = {
  header: `${PREFIX}-header`,
};
const Root = styled("div")({
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

const Birthdays = ({ birthdays }) => {
  console.log("BIRTHDAY PAGE", Array.isArray(birthdays), birthdays);
  const { state } = useContext(AppContext);

  const loading = selectMemberProfilesLoading(state);
  const { userProfile } = state;

  const createBirthdayCards = birthdays.map((bday, index) => {
    let user = selectProfile(state, bday.userId);
    console.warn({ bday, user });
    return (
      <Card className={"birthdays-card"}>
        <Link
          style={{ color: "black", textDecoration: "none" }}
          to={`/profile/${bday.userId}`}
        >
          <CardHeader
            className={"birthday-card"}
            title={
              <Typography variant="h5" component="h2">
                Happy Birthday{" "}
                <span>{user.firstName + " " + user.lastName}!</span>
              </Typography>
            }
            disableTypography
            avatar={
              <Avatar className={"large"} src={getAvatarURL(user?.workEmail)} />
            }
          />
        </Link>
      </Card>
    );
  });

  return (
    <div class="birthdays-container">
      <div class="balloon">
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
      </div>
      <Root>
        <h2>Birthdays</h2>
        <Grid container columns={6} spacing={3}>
          <Grid item className={classes.members}>
            {loading
              ? Array.from({ length: 20 }).map((_, index) => (
                  <SkeletonLoader key={index} type="people" />
                ))
              : !loading
              ? createBirthdayCards
              : null}
          </Grid>
        </Grid>
      </Root>
    </div>
  );
};

export default Birthdays;
