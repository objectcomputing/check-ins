import React, { useContext } from "react";
import Typography from "@material-ui/core/Typography";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import { makeStyles } from '@material-ui/core/styles';
import { selectProfile } from "../../context/selectors";
import { AppContext } from "../../context/AppContext";
import { useLocation } from 'react-router-dom';
import queryString from 'query-string';
import DateFnsUtils from "@date-io/date-fns";
import "./FeedbackRequestConfirmation.css";
import { green } from "@material-ui/core/colors";

const dateUtils = new DateFnsUtils();

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

let today = new Date();
today = dateUtils.format(today, "yyyy-MM-dd");

const FeedbackRequestConfirmation = (props) => {
  const classes = useStyles();
  const { state } = useContext(AppContext);
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const forQuery = query.for?.toString();
  const fromQuery = query.from?.toString();
  const sendQuery = query.send?.toString();
  const requestee = selectProfile(state, forQuery);
  let recipientInfo = getRecipientNames();

  function getRecipientNames() {
    if (fromQuery !== undefined) {
      let fromArray = fromQuery.split(',')
      let recipientProfiles = []
      if (fromArray.length !== 0) {
        for (let i = 0; i < fromArray.length; ++i) {
          let element = fromArray[i]
          recipientProfiles.push(element)
        }

      } else {
        recipientProfiles.push(fromQuery)

      }
      return recipientProfiles;

    }

  }
  return (
    <div className="request-confirmation">
      <CheckCircleIcon style={{ color: green[500], fontSize: '40vh' }}>checkmark-image</CheckCircleIcon>
      <Typography className={classes.announcement} variant="h3"><b>Feedback request {sendQuery > today ? " scheduled on: " + sendQuery : " sent"} for {requestee?.name} </b></Typography>
      <Typography className="recipients-list" variant="h6"><b>Sent to: </b>
        {recipientInfo?.map((member, index) =>
          `${selectProfile(state, member)?.name}${index === recipientInfo.length - 1 ? "" : ', '}`
        )}
      </Typography>
    </div>
  );
}

export default FeedbackRequestConfirmation;