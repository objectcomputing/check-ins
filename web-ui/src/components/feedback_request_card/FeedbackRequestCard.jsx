import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import FeedbackRequestSubcard from "./feedback_request_subcard/FeedbackRequestSubcard";
import Card from '@material-ui/core/Card';
import { Typography } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import AvatarComponent from '../avatar/Avatar';
import Collapse from '@material-ui/core/Collapse';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import IconButton from '@material-ui/core/IconButton';
import Grid from '@material-ui/core/Grid';
import { Link } from "react-router-dom";
import PropTypes from "prop-types";
import "./FeedbackRequestCard.css";

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
  requesteeName: PropTypes.string.isRequired,
  requesteeTitle: PropTypes.string.isRequired,
  templateName: PropTypes.string.isRequired,
  sendDate: PropTypes.any.isRequired,
  dueDate: PropTypes.any,
  submitted: PropTypes.string.isRequired,
  submitDate: PropTypes.any,
}

const FeedbackRequestCard = (props) => {
  const classes = useStyles();
  useStylesCardActions();
  useStylesText();
  useStylesCardContent();
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  const DueDate = (props) => {
    if(props.dueDate) {
      return (
        <Typography className="dark-gray-text">{props.dueDate.toString()}</Typography>
      )
    }else {
      return (
        <Typography className="dark-gray-text">No due date</Typography>
      )
    }
  }

  const Submitted = (props) => {
    if(props.submitted === "submitted") {
      return (
        <Typography>Submitted {props.submitDate.toString()}</Typography>
      )
    }else{
      return (
        <Typography>{props.submitted}</Typography>
      )
    }
  }

  const Overdue = (props) => {
    let today = new Date();
    if(today )
  }

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
                    <AvatarComponent imageUrl="../../../public/default_profile.jpg"/>
                  </Grid>
                  <Grid item xs className="small-margin">
                    <Typography className="person-name" >{props.requesteeName}</Typography>
                    <Typography className="position-text">{props.requesteeTitle}</Typography>
                  </Grid>
                  <Grid item xs={4} className="align-end">
                    <Typography className="dark-gray-text">{props.templateName}</Typography>
                    <Typography className="dark-gray-text">{props.sendDate.toString()}</Typography>
                    <DueDate/>
                    <Submitted/>
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
            <FeedbackRequestSubcard
              recipientName={"Jane Doe"}
              recipientTitle={"Senior Engineer"}
              />
            <FeedbackRequestSubcard
              recipientName={"Joe PDL"}
              recipientTitle={"PDL"}/>
          </CardContent>
        </Collapse>
      </Card>
    </div>
  );
}

FeedbackRequestCard.propTypes = propTypes;

export default FeedbackRequestCard;
