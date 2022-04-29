import React, {useContext, useCallback, useState} from "react";
import { styled } from "@mui/material/styles";
import PropTypes from "prop-types";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { Link } from "react-router-dom";
import Divider from "@mui/material/Divider";
import { sendReminderNotification } from "../../../api/notifications";
import {deleteFeedbackRequestById, updateFeedbackRequest} from "../../../api/feedback";
import IconButton from "@mui/material/IconButton";
import NotificationsActiveIcon from "@mui/icons-material/NotificationsActive";
import TrashIcon from "@mui/icons-material/Delete";
import { AppContext } from "../../../context/AppContext";
import { selectCsrfToken, selectProfile } from "../../../context/selectors";
import {Avatar, Button, Card, CardActions, CardContent, CardHeader, Modal, Menu, Tooltip, Alert} from "@mui/material";
import { UPDATE_TOAST } from "../../../context/actions";
import DateFnsAdapter from "@date-io/date-fns";
import { getAvatarURL } from "../../../api/api";
import { makeStyles } from "@mui/styles";
import MoreIcon from "@mui/icons-material/MoreVert";
import MenuItem from "@mui/material/MenuItem";

import "./FeedbackRequestSubcard.css";

const PREFIX = "FeedbackRequestSubcard";
const classes = {
  redTypography: `${PREFIX}-redTypography`,
  yellowTypography: `${PREFIX}-yellowTypography`,
  greenTypography: `${PREFIX}-greenTypography`,
  darkGrayTypography: `${PREFIX}-darkGrayTypography`,
};

// TODO jss-to-styled codemod: The Fragment root was replaced by div. Change the tag if needed.
const Root = styled("div")({
  [`& .${classes.redTypography}`]: {
    color: "#FF0000",
  },
  [`& .${classes.yellowTypography}`]: {
    color: "#EE8C00",
  },
  [`& .${classes.greenTypography}`]: {
    color: "#006400",
  },
  [`& .${classes.darkGrayTypography}`]: {
    color: "#333333",
  },
});

const useResponsiveStyles = makeStyles({
  marginMobile: {
    '@media (max-width:960px)': {
      marginBottom: "0.5vh",
      marginTop: "2vh",
    },
  },
});
const dateFns = new DateFnsAdapter();

const propTypes = {
  request: PropTypes.object.isRequired,
};

const FeedbackRequestSubcard = ({ request }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const responsiveClasses = useResponsiveStyles();
  let { submitDate, dueDate, sendDate } = request;
  const recipient = selectProfile(state, request?.recipientId);
  submitDate = submitDate
    ? dateFns.format(new Date(submitDate.join("/")), "LLLL dd, yyyy")
    : null;
  dueDate = dueDate
    ? dateFns.format(new Date(dueDate.join("/")), "LLLL dd, yyyy")
    : null;
  sendDate = dateFns.format(new Date(sendDate.join("/")), "LLLL dd, yyyy");

  const [expandMenuAnchor, setExpandMenuAnchor] = useState(null);
  const [enableEditsDialog, setEnableEditsDialog] = useState(false);

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
        res && res.payload && res.payload.status === 200 && !res.error;
      if (reminderResponse) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "success",
            toast: "Feedback request deleted.",
          },
        });
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast:
              "There was an error deleting the feedback request. Please contact your administrator.",
          },
        });
      }
    };
    if (csrf) {
      handleDeleteFeedback();
    }
  }, [recipientId, csrf]);
  
  const handleEnableEditsClick = useCallback(() => {
    setEnableEditsDialog(false);

    const enableFeedbackEdits = async () => {
      const requestWithEditsEnabled = {
        ...request,
        status: "sent",
        submitDate: null
      }

      const res = await updateFeedbackRequest(requestWithEditsEnabled, csrf);
      return res && res.payload && res.payload.data && !res.error ? res.payload.data : null;
    }

    if (csrf) {
      enableFeedbackEdits().then((res) => {
        if (res) {
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "success",
              toast: `Enabled edits for ${recipient?.name}'s feedback`
            }
          });
        } else {
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: "Could not enable edits for this feedback request"
            }
          });
        }
      });
    }

  }, [csrf, recipient, request]);

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
    <Root>
      <Divider className="person-divider" />
      <Grid
        container
        spacing={8}
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
            <Grid item s={2}>
              <Avatar
                style={{ marginRight: "1em" }}
                src={getAvatarURL(recipientEmail)}
              />
            </Grid>
            <Grid item xs={8} lg className="small-margin">
              <Typography className="person-name">{recipient?.name}</Typography>
              <Typography className="position-text">
                {recipient?.title}
              </Typography>
            </Grid>
            <Grid item xs={12} lg className={responsiveClasses.marginMobile}>
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
            <Grid item xs={6} md>
              <Submitted />
            </Grid>
            <Grid item xs={6} md className="align-end">
              {request && !request.submitDate && (
                <>
                  <Tooltip
                    title={"Delete Request"}
                    aria-label={"Delete Request"}
                  >
                    <IconButton
                      onClick={handleDeleteClick}
                      aria-label="Delete Request"
                      label="Delete Request"
                    >
                      <TrashIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title={"Send Reminder"} aria-label={"Send Reminder"}>
                    <IconButton
                      onClick={handleReminderClick}
                      aria-label="Send Reminder"
                      label="Send Reminder"
                      size="large"
                    >
                      <NotificationsActiveIcon />
                    </IconButton>
                  </Tooltip>
                </>
              )}
              {request && request.submitDate && request.id ? (
                <>
                  <Link
                    to={`/feedback/view/responses/?request=${request.id}`}
                    className="response-link"
                    style={{ background: "transparent" }}
                  >
                    <Button>View Response</Button>
                  </Link>
                  <IconButton onClick={(event) => setExpandMenuAnchor(event.currentTarget)}>
                    <MoreIcon/>
                  </IconButton>
                  <Menu
                    anchorEl={expandMenuAnchor}
                    open={!!expandMenuAnchor}
                    onClose={() => setExpandMenuAnchor(false)}
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "center"
                    }}
                    transformOrigin={{
                      vertical: "top",
                      horizontal: "center"
                    }}
                  >
                    <MenuItem onClick={() => {
                      setExpandMenuAnchor(null);
                      setEnableEditsDialog(true);
                    }}>Enable Edits</MenuItem>
                  </Menu>
                  <Modal open={enableEditsDialog} onClose={() => setEnableEditsDialog(false)}>
                    <Card className="feedback-request-enable-edits-modal">
                      <CardHeader title={<Typography variant="h5" fontWeight="bold">Enable Feedback Edits</Typography>}/>
                      <CardContent>
                        <Typography variant="body1">
                          Are you sure you want <b>{recipient?.name}</b> to be able to make changes to this feedback submission?
                          This will allow them to edit their original responses and resubmit them.
                        </Typography>
                        <Alert style={{marginTop: "1rem"}} severity="warning">
                          {recipient?.name}'s feedback for this request will be inaccessible until they submit their answers again.
                        </Alert>
                      </CardContent>
                      <CardActions>
                        <Button style={{ color: "gray" }} onClick={() => setEnableEditsDialog(false)}>Cancel</Button>
                        <Button color="primary" onClick={handleEnableEditsClick}>Enable Edits</Button>
                      </CardActions>
                    </Card>
                  </Modal>
                </>
              ) : null}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Root>
  );
};

FeedbackRequestSubcard.propTypes = propTypes;

export default FeedbackRequestSubcard;
