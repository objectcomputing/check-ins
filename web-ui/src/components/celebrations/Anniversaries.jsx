import React, { useContext } from "react";
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
  const { state } = useContext(AppContext);

  const loading = selectMemberProfilesLoading(state);

  const createAnniversaryCards = anniversaries.map((anniv, index) => {
    let user = selectProfile(state, anniv.userId);
    return (
      <Card className={"anniversaries-card"} key={index}>
        <Link
          style={{ color: "black", textDecoration: "none" }}
          to={`/profile/${anniv.userId}`}
        >
          <CardHeader
            className={classes.header}
            title={
              <Typography variant="h5" component="h2">
                {anniv.name} ({anniv.anniversary})
              </Typography>
            }
            subheader={
              <Typography color="textSecondary" component="h3">
                Thank you for{" "}
                <span>
                  {anniv.yearsOfService.toFixed(0)}
                  {anniv.yearsOfService > 1 ? " years" : " year"}{" "}
                </span>
                of service!
              </Typography>
            }
            disableTypography
            avatar={
              <Avatar
                className={"celebrations-avatar"}
                src={getAvatarURL(user?.workEmail)}
              />
            }
          />
        </Link>
      </Card>
    );
  });

  return (
    <div className="anniversaries">
      <div className={"anniversary-gift box"}>
        <div className={"anniversary-gift-top open"}></div>
        <div className={"anniversary-gift-text open"}></div>
        <div className={"anniversary-gift-box"}></div>
      </div>
      <Root>
        <div className="anniversary-title">
          <h2>Anniversaries</h2>
        </div>
        <Grid container columns={6} spacing={3}>
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
