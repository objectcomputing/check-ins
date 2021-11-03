import React, { useContext, useCallback } from "react";
import PropTypes from "prop-types";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { Link } from "react-router-dom";
import Divider from "@material-ui/core/Divider";
import { sendReminderNotification } from "../../../api/notifications";
import { deleteFeedbackRequestById } from "../../../api/feedback";
import IconButton from "@material-ui/core/IconButton";
import NotificationsActiveIcon from "@material-ui/icons/NotificationsActive";
import TrashIcon from "@material-ui/icons/Delete";
import { AppContext } from "../../../context/AppContext";
import { selectCsrfToken, selectProfile } from "../../../context/selectors";
import { Avatar, Tooltip } from "@material-ui/core";
import { UPDATE_TOAST } from "../../../context/actions";
import DateFnsAdapter from "@date-io/date-fns";
import { getAvatarURL } from "../../../api/api";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles({
  redTypography: {
    color: "#FF0000",
  },
  yellowTypography: {
    color: "#EE8C00",
  },
  greenTypography: {
    color: "#006400",
  },
  darkGrayTypography: {
    color: "#333333",
  },
});

const dateFns = new DateFnsAdapter();

const propTypes = {
  request: PropTypes.object.isRequired,
};

const FeedbackRequestSubcard = ({ request }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStyles();
  let { submitDate, dueDate, sendDate } = request;
  const recipient = selectProfile(state, request?.recipientId);
  submitDate = submitDate
    ? dateFns.format(new Date(submitDate.join("/")), "LLLL dd, yyyy")
    : null;
  dueDate = dueDate
    ? dateFns.format(new Date(dueDate.join("/")), "LLLL dd, yyyy")
    : null;
  sendDate = dateFns.format(new Date(sendDate.join("/")), "LLLL dd, yyyy");

  const recipientId = request?.id;
  const recipientEmail = recipient?.workEmail;
  const handleReminderClick = useCallback(() => {
    const handleReminderNotification = async () => {
      let res = await sendReminderNotification(
        recipientId,
        [recipientEmail],
        csrf
      );
      let reminderResponse =
        res && res.payload && res.payload.status === 200 && !res.error;
      if (reminderResponse) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "success",
            toast: "Notification sent!",
          },
        });
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Notification could not be sent :(",
          },
        });
      }
    };
    if (csrf) {
      handleReminderNotification();
    }
  }, [recipientId, recipientEmail, csrf]);

  const handleDeleteClick = useCallback(() => {
    const handleDeleteFeedback = async () => {
      let res = await deleteFeedbackRequestById(recipientId, csrf);
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
            toast: "Feedback request deleted."
          },
        });
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "There was an error deleting the feedback request. Please contact your administrator."
          },
        });
      }
    };
    if (csrf) {
      handleDeleteFeedback();
    }
  }, [recipientId, csrf]);

  const Submitted = () => {
    if (request.dueDate) {
      let today = new Date();
      let due = new Date(request.dueDate);
      if (!request.submitDate && today > due) {
        return (
          <Typography className={classes.redTypography}>Overdue</Typography>
        );
      }
    }
    if (request.submitDate) {
      return (
        <Typography className={classes.greenTypography}>
          Submitted {submitDate}
        </Typography>
      );
    } else
      return (
        <Typography className={classes.yellowTypography}>
          Not Submitted
        </Typography>
      );
  };

  return (
    <React.Fragment>
      <Divider className="person-divider" />
      <Grid
        container
        spacing={6}
        style={{ paddingLeft: "16px", paddingRight: "16px" }}
        className="person-row"
      >
        <Grid item xs={12}>
          <Grid
            container
            direction="row"
            alignItems="center"
            className="no-wrap"
          >
            <Grid item>
              <Avatar
                style={{ marginRight: "1em" }}
                src={getAvatarURL(recipientEmail)}
              />
            </Grid>
            <Grid item xs className="small-margin">
              <Typography className="person-name">{recipient?.name}</Typography>
              <Typography className="position-text">
                {recipient?.title}
              </Typography>
            </Grid>
            <Grid item xs={3}>
              <Typography
                className={classes.darkGrayTypography}
                variant="body1"
              >
                Sent on {sendDate}
              </Typography>
              <Typography variant="body2">
                {request?.dueDate ? `Due on ${dueDate}` : "No due date"}
              </Typography>
            </Grid>
            <Grid item xs={3}>
              <Submitted />
            </Grid>
            <Grid item xs={2} className="align-end">
              {request && !request.submitDate && (
                <>
                <Tooltip title={"Delete Request"} aria-label={"Delete Request"}>
                  <IconButton
                    onClick={handleDeleteClick}
                    aria-label="Delete Request"
                  label = "Delete Request">
                    <TrashIcon/>
                  </IconButton>
                </Tooltip>
                <Tooltip title={"Send Reminder"} aria-label={"Send Reminder"}>
                  <IconButton
                    onClick={handleReminderClick}
                    aria-label="Send Reminder"
                    label="Send Reminder"
                  >
                    <NotificationsActiveIcon />
                  </IconButton>
                </Tooltip>
                </>
              )}
              {request && request.submitDate && request.id
                ? <Link to={`/feedback/view/responses/?request=${request.id}`} className="response-link">View response</Link>
                : null}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

FeedbackRequestSubcard.propTypes = propTypes;

export default FeedbackRequestSubcard;
