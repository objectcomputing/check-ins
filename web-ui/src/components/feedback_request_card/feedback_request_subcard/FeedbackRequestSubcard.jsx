import React, {useContext} from "react";
import PropTypes from "prop-types";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import {Link} from "react-router-dom";
import Divider from "@material-ui/core/Divider";
import {sendReminderNotification} from "../../../api/notifications";
import IconButton from "@material-ui/core/IconButton";
import NotificationsActiveIcon from "@material-ui/icons/NotificationsActive";
import {AppContext} from "../../../context/AppContext";
import {selectCsrfToken, selectProfile} from "../../../context/selectors";
import {Avatar, Tooltip} from "@material-ui/core";
import { UPDATE_TOAST } from "../../../context/actions";
import DateFnsAdapter from "@date-io/date-fns";
import {getAvatarURL} from "../../../api/api";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles({
  redTypography: {
    color: "#FF0000"
    },

  yellowTypography: {
      color: "#EE8C00"
    },

  greenTypography: {
      color: "#006400"
    },

  darkGrayTypography: {
    color: "#333333"
  }
});

const dateFns = new DateFnsAdapter();

const propTypes = {
  request: PropTypes.object.isRequired,
}

const FeedbackRequestSubcard = (props) => {
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStyles();
  const recipient = selectProfile(state, props.request?.recipientId);
  const submitDate = props.request?.submitDate ? dateFns.format(new Date(props.request.submitDate.join("-")), "LLLL dd, yyyy") : null;
  const dueDate = props.request?.dueDate ? dateFns.format(new Date(props.request.dueDate.join("-")), "LLLL dd, yyyy"): null;
  const sendDate = dateFns.format(new Date(props.request.sendDate.join("-")), "LLLL dd, yyyy");

  const handleReminderNotification = async() => {
    if (csrf) {
      let res = await sendReminderNotification(props.request.id, [recipient.email], csrf);
      let reminderResponse =
        res &&
        res.payload &&
        res.payload.status === 200 &&
        !res.error
      if (reminderResponse) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "success",
            toast: "Notification sent!"
          },
        });
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Notification could not be sent :("
          },
        });
      }
    }
  }

  const handleReminderClick = () => {
    handleReminderNotification();
  }

  const Submitted = (props) => {
      if (props.dueDate) {
        let today = new Date();
        let due = new Date(props.dueDate);
        if ((!props.submitDate) && today > due) {
          return (
            <Typography className={classes.redTypography}>Overdue</Typography>
          )
        }
      }
      if (props.submitDate) {
        return <Typography className={classes.greenTypography}>Submitted {submitDate}</Typography>
      } else
        return <Typography className={classes.yellowTypography}>Not Submitted</Typography>
    }

    Submitted.propTypes = {submitDate: PropTypes.arrayOf(PropTypes.string)};

  return (
    <React.Fragment>
      <Divider className="person-divider"/>
      <Grid container spacing={6} className="person-row">
        <Grid item xs={12}>
          <Grid
            container
            direction="row"
            alignItems="center"
            className="no-wrap">
            <Grid item>
              <Avatar style={{marginRight: "1em"}} src={getAvatarURL(recipient?.workEmail)}/>
            </Grid>
            <Grid item xs className="small-margin">
              <Typography className="person-name">{recipient?.name}</Typography>
              <Typography className="position-text">{recipient?.title}</Typography>
            </Grid>
            <Grid item xs={3}>
              <Typography className={classes.darkGrayTypography} variant= "body1">Sent on {sendDate}</Typography>
              <Typography variant="body2">{props.request?.dueDate ? `Due on ${dueDate}` : "No due date"}</Typography>
            </Grid>
            <Grid item xs={3}>
              <Submitted dueDate={props.request?.dueDate} submitDate={props.request?.submitDate}/>
            </Grid>
            <Grid item xs={2} className="align-end">
              {props.request && !props.request.submitDate &&
                <Tooltip title={"Send Reminder"} aria-label={"Send Reminder"}>
                  <IconButton
                    onClick={handleReminderClick}
                    aria-label="Send Reminder"
                  label = "Send Reminder">
                    <NotificationsActiveIcon/>
                  </IconButton>
                </Tooltip>
              }
              {props.request.submitDate ? <Link to="" className="response-link"> View response </Link> : null}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
}

FeedbackRequestSubcard.propTypes = propTypes;

export default FeedbackRequestSubcard;