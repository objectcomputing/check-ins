import React, {useContext} from "react";
import PropTypes from "prop-types";
import Grid from "@material-ui/core/Grid";
import AvatarComponent from "../../avatar/Avatar";
import Typography from "@material-ui/core/Typography";
import {Link} from "react-router-dom";
import Divider from "@material-ui/core/Divider";
import {sendReminderNotification} from "../../../api/notifications";
import IconButton from "@material-ui/core/IconButton";
import NotificationsActiveIcon from "@material-ui/icons/NotificationsActive";
import {AppContext} from "../../../context/AppContext";
import {selectCsrfToken} from "../../../context/selectors";
import {Tooltip} from "@material-ui/core";
import { UPDATE_TOAST } from "../../../context/actions";

const propTypes = {
  recipientName: PropTypes.string.isRequired,
  recipientTitle: PropTypes.string.isRequired,
}

const FeedbackRequestSubcard = (props) => {
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const handleReminderNotification = async() => {
    if (csrf) {
      let res = await sendReminderNotification("1234", ["jeffedwardbrown@gmail.com"], csrf);
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
              <AvatarComponent imageUrl="../../../public/default_profile.jpg"/>
            </Grid>
            <Grid item xs className="small-margin">
              <Typography className="person-name">{props.recipientName}</Typography>
              <Typography className="position-text">{props.recipientTitle}</Typography>
            </Grid>
            <Grid item xs={4} className="align-end">
              <Typography>Submitted 5/22</Typography>
              <Tooltip title={"Send Reminder"} aria-label={"Send Reminder"}>
              <IconButton
                onClick={handleReminderClick}
                aria-label="send Reminder"
              >
                <NotificationsActiveIcon />
              </IconButton>
              </Tooltip>
              <Link to="" className="response-link">View response</Link>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
}

FeedbackRequestSubcard.propTypes = propTypes;

export default FeedbackRequestSubcard;