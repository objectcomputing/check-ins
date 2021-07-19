import React, { useContext } from "react";
import Typography from "@material-ui/core/Typography";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import { makeStyles } from '@material-ui/core/styles';
import { selectProfile } from "../../context/selectors";
import { AppContext } from "../../context/AppContext";
import { useLocation } from 'react-router-dom';
import queryString from 'query-string';
import "./FeedbackSubmitConfirmation.css";
import { green } from "@material-ui/core/colors";

const useStyles = makeStyles({
  announcement: {
    textAlign: "center",
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "x-large",
    },
  },

  checkmark: {
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      width: "65%",
    },
  },

});

const FeedbackSubmitConfirmation = (props) => {
  const classes = useStyles();
  const { state } = useContext(AppContext);
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const forQuery = query.for?.toString();
  const requestee = selectProfile(state, forQuery);

  return (
    <div className="submit-confirmation">
      <CheckCircleIcon style={{ color: green[500], fontSize: '40vh' }}>checkmark-image</CheckCircleIcon>
      <Typography className={classes.announcement} variant="h3"><b>Thank you for your feedback on {requestee?.name} </b></Typography>
    </div>
  );
}

export default FeedbackSubmitConfirmation;