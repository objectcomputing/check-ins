import React, { useContext } from "react";
import { Link } from "react-router-dom";

import { AppContext } from "../../context/AppContext";
import { selectIsAdmin, selectProfileMap } from "../../context/selectors";
import { getAvatarURL } from "../../api/api.js";

import { Card, CardHeader } from "@material-ui/core";
import Avatar from "@material-ui/core/Avatar";
import PriorityHighIcon from "@material-ui/icons/PriorityHigh";

import "./MemberSummaryCard.css";

import {
  Box,
  CardContent,
  Container,
  makeStyles,
  Typography,
} from "@material-ui/core";

import { isPast } from "date-fns/esm";

const useStyles = makeStyles(() => ({
  header: {
    cursor: "pointer",
  },
}));

const MemberSummaryCard = ({ member }) => {
  const { state } = useContext(AppContext);
  const isAdmin = selectIsAdmin(state);
  const {
    location,
    name,
    workEmail,
    title,
    supervisorid,
    pdlId,
    terminationDate,
  } = member;
  const supervisorProfile = selectProfileMap(state)[supervisorid];
  const pdlProfile = selectProfileMap(state)[pdlId];

  const classes = useStyles();

  // if the current user is an admin, show the alert icon if the user has a termination date
  // if the current user is not an admin, only show the alert icon if they have a termination date and it is past the present day
  const decideAvatar = () => {
    if (isAdmin){
      return terminationDate?
        (
          <Avatar className={"large"}>
            <PriorityHighIcon />
          </Avatar>
        )
        :
        (<Avatar className={"large"} src={getAvatarURL(workEmail)} />)
    }
    else {
      return terminationDate && isPast(new Date(terminationDate)) ? 
      (
        <Avatar className={"large"}>
          <PriorityHighIcon />
        </Avatar>
      ) : 
      (<Avatar className={"large"} src={getAvatarURL(workEmail)} />)
    }
  }
  
  return (
    <Box display="flex" flexWrap="wrap">
      <Card className={"member-card"}>
        <Link
          style={{ color: "black", textDecoration: "none" }}
          to={`/profile/${member.id}`}
        >
          <CardHeader
            className={classes.header}
            title={
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
            }
            subheader={
              <Typography color="textSecondary" component="h3">
                {title}
              </Typography>
            }
            disableTypography
            avatar={decideAvatar()}
          />
        </Link>
        <CardContent>
          <Container fixed className={"info-container"}>
            <Typography variant="body2" color="textSecondary" component="p">
              <a
                href={`mailto:${workEmail}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {workEmail}
              </a>
              <br />
              Location: {location}
              <br />
              Supervisor: {supervisorProfile?.name}
              <br />
              PDL: {pdlProfile?.name}
              <br />
            </Typography>
          </Container>
        </CardContent>
      </Card>
    </Box>
  );
};

export default MemberSummaryCard;
