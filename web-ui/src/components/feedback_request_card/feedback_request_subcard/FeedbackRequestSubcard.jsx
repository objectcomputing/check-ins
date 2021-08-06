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

const dateFns = new DateFnsAdapter();

const propTypes = {
  request: PropTypes.object.isRequired,
}

const FeedbackRequestSubcard = (props) => {
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const recipient = selectProfile(state, props.request?.recipientId);
  const submitDate = props.request?.submitDate ? dateFns.format(new Date(props.request.submitDate.join("-")), "LLLL dd, yyyy") : null;

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
            <Grid item xs={4} className="align-end">
              <Typography>{props.request?.submitDate ? `Submitted ${submitDate}` : "Not submitted"}</Typography>
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
              {props.request.submitDate && props.request.id
                ? <Link to={`/feedback/view/responses/?request=${props.request.id}`} className="response-link">View response</Link>
                : null}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
}

FeedbackRequestSubcard.propTypes = propTypes;

export default FeedbackRequestSubcard;