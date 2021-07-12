import React, {useContext} from "react";
import Typography from "@material-ui/core/Typography";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import {makeStyles} from '@material-ui/core/styles';
import {selectProfile} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {useLocation, useHistory } from 'react-router-dom';
import queryString from 'query-string';
import DateFnsUtils from "@date-io/date-fns";
import "./FeedbackRequestConfirmation.css";
import {green} from "@material-ui/core/colors";

const dateUtils = new DateFnsUtils();

const formatNamesAsList = (names) => {
  return names.join(", ")
}
const useStyles = makeStyles({
announcement: {
  textAlign: "center"
},
});

    let today = new Date();
    today = dateUtils.format(today, "yyyy-MM-dd");

const FeedbackRequestConfirmation = (props) => {
  const classes = useStyles();
    const {state} = useContext(AppContext);
    const location = useLocation();
    const history = useHistory();
     const query = queryString.parse(location?.search);
    console.log(JSON.stringify(query))
    const forQuery = query.for?.toString();
     const fromQuery = query.from?.toString();
     const sendQuery = query.send?.toString();
    const requestee = selectProfile(state, forQuery);

  return (
    <div className="request-confirmation">
      <CheckCircleIcon style={{ color: green[500] , fontSize: '40vh' }}>checkmark-image</CheckCircleIcon>
      <Typography className={classes.announcement} variant="h3"><b>Feedback request {sendQuery > today ? " scheduled on: " + sendQuery : " sent"} for {requestee?.name} </b></Typography>
      <Typography className="recipients-list" variant="h6"><b></b> {formatNamesAsList(["Alice", "Bob", "Eve"])}</Typography>
    </div>
  );
}

export default FeedbackRequestConfirmation;