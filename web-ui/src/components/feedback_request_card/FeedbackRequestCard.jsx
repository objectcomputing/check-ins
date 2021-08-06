import React, {useCallback, useContext} from 'react';
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
import {DateRange} from "../../pages/ViewFeedbackPage";

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
}, { name: "MuiCardContent" });

const useStylesCardActions = makeStyles({
  root: {
    padding: 0,
    maxHeight: "30px",
  },

}, { name: 'MuiCardActions' });

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
  dateRange: PropTypes.oneOf([DateRange.THREE_MONTHS, DateRange.SIX_MONTHS, DateRange.ONE_YEAR, DateRange.ALL_TIME]).isRequired
};

const FeedbackRequestCard = ({ requesteeId, templateName, responses, dateRange }) => {
  const classes = useStyles();
  const {state} = useContext(AppContext);
  const requesteeProfile = selectProfile(state, requesteeId);
  const avatarURL = getAvatarURL(requesteeProfile?.workEmail);
  useStylesCardActions();
  useStylesText();
  useStylesCardContent();
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  const withinDateRange = useCallback((requestDate) => {
    let oldestDate = new Date();
    switch (dateRange) {
      case DateRange.THREE_MONTHS:
        oldestDate.setMonth(oldestDate.getMonth() - 3);
        break;
      case DateRange.SIX_MONTHS:
        oldestDate.setMonth(oldestDate.getMonth() - 6);
        break;
      case DateRange.ONE_YEAR:
        oldestDate.setFullYear(oldestDate.getFullYear() - 1);
        break;
      case DateRange.ALL_TIME:
        return true;
      default:
        oldestDate.setMonth(oldestDate.getMonth() - 3);
    }

    return requestDate >= oldestDate;
  }, [dateRange]);

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
            {responses?.map((response) => (
              (response && withinDateRange(response.sendDate))
                ? <FeedbackRequestSubcard key={response.id} request={response}/>
                : null
            ))}
          </CardContent>
        </Collapse>
      </Card>
    </div>
  );
}

FeedbackRequestCard.propTypes = propTypes;

export default FeedbackRequestCard;
