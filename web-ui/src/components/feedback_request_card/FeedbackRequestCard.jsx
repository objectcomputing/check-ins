import React, {useContext, useEffect, useState} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import FeedbackRequestSubcard from "./feedback_request_subcard/FeedbackRequestSubcard";
import Card from '@material-ui/core/Card';
import {Avatar, Typography} from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import Collapse from '@material-ui/core/Collapse';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import IconButton from '@material-ui/core/IconButton';
import Grid from '@material-ui/core/Grid';
import { Link } from "react-router-dom";
import PropTypes from "prop-types";
import "./FeedbackRequestCard.css";
import {selectProfile} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import { getAvatarURL } from "../../api/api.js";

const useStyles = makeStyles({
  root: {
    color: "gray",
    width: "100%",
    maxHeight: "10%",
    ['@media (max-width:769px)']: { // eslint-disable-line no-useless-computed-key
      width: '100%',
      maxWidth: '100%',
    },
  },
  expandClose: {
    transform: 'rotate(0deg)',
    marginLeft: 'auto',
    transition: "transform 0.1s linear",
  },
  expandOpen: {
    transform: 'rotate(180deg)',
    transition: "transform 0.1s linear",
    marginLeft: 'auto',
  },
});

const useStylesCardContent = makeStyles({
  root: {
    paddingBottom: 0,
    paddingTop: 0,
    '&:last-child': {
      paddingBottom: 0,
    }
  }
}, { name: "MuiCardContent" })

const useStylesCardActions = makeStyles({
  root: {
    padding: 0,
    maxHeight: "30px",
  },

}, { name: 'MuiCardActions' })

const useStylesText = makeStyles({
  body1: {
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "0.7rem",
    },
  }
}, { name: "MuiTypography" })

const propTypes = {
  requesteeId: PropTypes.string.isRequired,
  templateName: PropTypes.string.isRequired,
  responses: PropTypes.arrayOf(PropTypes.object).isRequired,
  sortType: PropTypes.oneOf(["sent_date", "submission_date", "recipient_name_alphabetical", "recipient_name_reverse_alphabetical"])
};

const FeedbackRequestCard = ({ requesteeId, templateName, responses, sortType }) => {
  const classes = useStyles();
  const {state} = useContext(AppContext);
  const requesteeProfile = selectProfile(state, requesteeId);
  const avatarURL = getAvatarURL(requesteeProfile?.workEmail);
  useStylesCardActions();
  useStylesText();
  useStylesCardContent();
  const [expanded, setExpanded] = React.useState(false);
  const [sortedResponses, setSortedResponses] = useState(responses);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  // Sort the responses by either the send date or the submit date
  useEffect(() => {
    const responsesCopy = [...sortedResponses];
    let sortMethod = null;
    if (sortType === "sent_date") {
      sortMethod = ((a, b) => (new Date(a.sendDate) > new Date(b.sendDate)) ? -1: 1);
    } else if (sortType === "submission_date") {
      sortMethod = ((a, b) => (new Date(a.submitDate) > new Date(b.submitDate)) ? -1: 1);
    } else if (sortType === "recipient_name_alphabetical") {
      sortMethod = ((a, b) => (selectProfile(state, a.recipientId).name > selectProfile(state, b.recipientId).name) ? 1 : -1);
    } else if (sortType === "recipient_name_reverse_alphabetical") {
      sortMethod = ((a, b) => (selectProfile(state, a.recipientId).name > selectProfile(state, b.recipientId).name) ? -1 : 1);
    }
    responsesCopy.sort(sortMethod);
    setSortedResponses(responsesCopy); // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sortType, responses]);

  return (
    <div className="feedback-request-card">
      <Card className={classes.root}>
        <div className="has-padding-top">
          <CardContent className={classes.noBottomPadding}>
            <Grid container spacing={0}>
              <Grid item xs={12}>
                <Grid container
                  direction="row"
                  alignItems="center"
                  className="no-wrap"
                >
                  <Grid item>
                    <Avatar style={{marginRight: "1em"}} src={avatarURL}/>
                  </Grid>
                  <Grid item xs className="small-margin">
                    <Typography className="person-name" >{requesteeProfile?.name}</Typography>
                    <Typography className="position-text">{requesteeProfile?.title}</Typography>
                  </Grid>
                  <Grid item xs={4} className="align-end">
                    <Typography className="dark-gray-text">{templateName}</Typography>
                    <Link to="" className="response-link red-text">View all responses</Link>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </CardContent>
        </div>
        <CardActions disableSpacing>
          <IconButton
            onClick={handleExpandClick}
            aria-expanded={expanded}
            aria-label="show more"
            className={expanded ? classes.expandOpen : classes.expandClose}
          >
            <ExpandMoreIcon />
          </IconButton>
        </CardActions>
        <Collapse in={expanded} timeout="auto" unmountOnExit>
          <CardContent>
            {sortedResponses?.map((response) => (
              <FeedbackRequestSubcard
                key={response.id}
                request={response}
              />
            ))}
          </CardContent>
        </Collapse>
      </Card>
    </div>
  );
}

FeedbackRequestCard.propTypes = propTypes;

export default FeedbackRequestCard;
