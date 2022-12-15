import React, { useContext, useState } from "react";
import { Link } from "react-router-dom";
import { AppContext } from "../../context/AppContext";

import { styled } from "@mui/material/styles";
import { getAvatarURL } from "../../api/api.js";
import { selectMemberProfilesLoading } from "../../context/selectors";
import SkeletonLoader from "../skeleton_loader/SkeletonLoader";

import { Card, CardHeader } from "@mui/material";
import Avatar from "@mui/material/Avatar";

import { TextField, Grid } from "@mui/material";

import "./Anniversaries.css";

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

const Anniversaries = ({ anniversaries }) => {
  console.log("ANNIV PAGE", Array.isArray(anniversaries), anniversaries);
  const { state } = useContext(AppContext);

  const loading = selectMemberProfilesLoading(state);
  const { userProfile } = state;
  const email = userProfile?.memberProfile?.workEmail;

  console.log({ userProfile });

  const createAnniversaryCards = anniversaries.map((anniv, index) => {
    return (
      <Card className={"anniversarys-card"}>
        <Link
          style={{ color: "black", textDecoration: "none" }}
          to={`/profile/${anniv.userId}`}
        >
          <CardHeader
            className={classes.header}
            title={
              <Typography variant="h5" component="h2">
                {anniv.name}
              </Typography>
            }
            subheader={
              <Typography color="textSecondary" component="h3">
                Thank you for {anniv.yearsOfService}
                {anniv.yearsOfService > 1 ? " years" : " year"} of service!
              </Typography>
            }
            disableTypography
            avatar={<Avatar className={"large"} src={getAvatarURL(email)} />}
          />
        </Link>
      </Card>
    );
  });

  return (
    <div className="anniversaries">
      <h2>Anniversaries</h2>
      <div className={"gift dukdik"}>
        <div className={"gift-top open"}></div>
        <div className={"gift-text open"}></div>
        <div className={"gift-box"}></div>
      </div>
      <Root className="people-page">
        <Grid container spacing={3}>
          <Grid item className={classes.members}>
            {loading
              ? Array.from({ length: 20 }).map((_, index) => (
                  <SkeletonLoader key={index} type="people" />
                ))
              : !loading
              ? createAnniversaryCards
              : null}
          </Grid>
        </Grid>
      </Root>
    </div>
  );
};

export default Anniversaries;
