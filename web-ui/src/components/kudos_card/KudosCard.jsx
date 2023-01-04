import React, {useCallback, useContext, useState} from "react";
import PropTypes from "prop-types";
import {
  Paper,
  Collapse,
  Divider,
  Typography,
  Avatar,
  Chip,
  Button,
  AvatarGroup,
  Tooltip,
  Dialog,
  DialogTitle, DialogContent, DialogContentText, DialogActions, TextField
} from "@mui/material";
import {selectCsrfToken, selectProfile} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {getAvatarURL} from "../../api/api";
import DateFnsUtils from "@date-io/date-fns";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import TeamIcon from "@mui/icons-material/Groups";

import "./KudosCard.css";
import {approveKudos, deleteKudos} from "../../api/kudos";
import {UPDATE_TOAST} from "../../context/actions";

const dateUtils = new DateFnsUtils();

const propTypes = {
  kudos: PropTypes.shape({
    id: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired,
    senderId: PropTypes.string.isRequired,
    recipientTeam: PropTypes.object,
    dateCreated: PropTypes.array.isRequired,
    dateApproved: PropTypes.array,
    recipientMembers: PropTypes.array
  }).isRequired,
  includeActions: PropTypes.bool,
  onKudosAction: PropTypes.func,
};

const KudosCard = ({ kudos, includeActions, onKudosAction }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [expanded, setExpanded] = useState(true);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteReason, setDeleteReason] = useState(""); // TODO: setup optional reason for deleting a kudos

  const sender = selectProfile(state, kudos.senderId);
  // console.log(kudos);

  const getRecipientComponent = useCallback(() => {
    if (kudos.recipientTeam) {
      return (
        <Chip
          avatar={<Avatar><TeamIcon/></Avatar>}
          label={kudos.recipientTeam.name}
        />
      );
    } else if (kudos.recipientMembers.length === 1) {
      const [recipient] = kudos.recipientMembers;
      return (
        <Chip
          avatar={<Avatar src={getAvatarURL(recipient?.workEmail)}/>}
          label={`${recipient.firstName} ${recipient.lastName}`}
        />
      );
    }

    return (
      <AvatarGroup max={16}>
        {kudos.recipientMembers.map(member => (
          <Tooltip arrow key={member.id} title={`${member.firstName} ${member.lastName}`}>
            <Avatar src={getAvatarURL(member.workEmail)}/>
          </Tooltip>
        ))}
      </AvatarGroup>
    );
  }, [kudos]);

  const approveKudosCallback = useCallback(async (event) => {
    event.stopPropagation();
    const res = await approveKudos(kudos, csrf);
    if (res?.payload?.data && !res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Kudos approved"
        }
      });
      onKudosAction();
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to approve kudos"
        }
      });
    }
  }, [kudos, csrf, dispatch, onKudosAction]);

  const deleteKudosCallback = useCallback(async () => {
    setDeleteDialogOpen(false);
    const res = await deleteKudos(kudos.id, csrf);
    if (res?.payload?.status === 204 && !res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Pending kudos deleted"
        }
      });
      onKudosAction();
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to delete kudos"
        }
      });
    }
  }, [kudos, csrf, dispatch, onKudosAction]);

  const getStatusComponent = useCallback(() => {

    const dateApproved = kudos.dateApproved ? new Date(kudos.dateApproved.join("/")) : null;
    const dateCreated = new Date(kudos.dateCreated.join("/"));

    if (includeActions) {
      return <div className="kudos-action-buttons">
        <Tooltip arrow title="Approve">
          <Button
            variant="outlined"
            color="success"
            size="small"
            onClick={approveKudosCallback}
          >
            <CheckIcon/>
          </Button>
        </Tooltip>
        <Tooltip arrow title="Delete">
          <Button
            variant="outlined"
            color="error"
            size="small"
            onClick={(event) => {
              event.stopPropagation()
              setDeleteDialogOpen(true);
            }}
          >
            <CloseIcon/>
          </Button>
        </Tooltip>
      </div>;
    } else if (dateApproved) {
      return <>
        <Typography color="green" variant="body2">
          Received {dateApproved ? dateUtils.format(dateApproved, "MM/dd/yyyy") : ""}
        </Typography>
      </>;
    }

    return <>
      <Typography color="orange" variant="body2">Pending</Typography>
      <Typography variant="body2" color="gray" fontSize="10px">
        Created {dateUtils.format(dateCreated, "MM/dd/yyyy")}
      </Typography>
    </>;
  }, [kudos, includeActions, approveKudosCallback]);

  return (
    <>
      <Dialog open={deleteDialogOpen}>
        <DialogTitle>Delete Kudos</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to deny approval for these kudos? The kudos will be deleted.
          </DialogContentText>
          <TextField id="reason-for-deletion"
                     fullWidth
                     multiline
                     placeholder={'Reason for deletion (optional)'}
                     rows={5}
                     style={{ marginTop: "2rem" }}
                     variant='outlined'
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setDeleteDialogOpen(false)}
            style={{ color: "gray" }}
          >
            Cancel
          </Button>
          <Button onClick={deleteKudosCallback} color="error" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
      <Paper className="kudos-card">
        <div className="kudos-card-header" onClick={() => setExpanded(!expanded)}>
          <div className="members-container">
            {getRecipientComponent()}
            <Typography variant="body1">received kudos from</Typography>
            <Chip
              avatar={<Avatar src={getAvatarURL(sender?.workEmail)}/>}
              label={sender?.name}
            />
          </div>
          <div className="kudos-status-container">
            {getStatusComponent()}
          </div>
        </div>
        <Divider/>
        <Collapse in={expanded}>
          <div className="kudos-card-content">
            <Typography variant="body1">{kudos.message}</Typography>
            {kudos.recipientMembers?.length > 1 &&
              <div className="kudos-recipient-list">
                <Typography variant="body2">
                  {kudos.recipientTeam ? "Team Members:" : "Members:"}
                </Typography>
                {kudos.recipientMembers.map(recipient => (
                  <Chip key={recipient.id}
                        avatar={<Avatar src={getAvatarURL(recipient?.workEmail)}/>}
                        label={`${recipient.firstName} ${recipient.lastName}`}
                  />
                ))}
              </div>
            }
          </div>
        </Collapse>
      </Paper>
    </>
  );
};

KudosCard.propTypes = propTypes;

export default KudosCard;