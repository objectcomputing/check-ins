import React, { useCallback, useContext, useState } from "react";
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
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField,
  FormGroup,
  FormControlLabel,
  Checkbox,
} from "@mui/material";
import {
  selectCsrfToken,
  selectActiveOrInactiveProfile,
} from "../../context/selectors";
import MemberSelector from '../member_selector/MemberSelector';
import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";
import DateFnsUtils from "@date-io/date-fns";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import EditIcon from "@mui/icons-material/Edit";
import TeamIcon from "@mui/icons-material/Groups";

import "./KudosCard.css";
import { approveKudos, deleteKudos, updateKudos } from "../../api/kudos";
import { UPDATE_TOAST } from "../../context/actions";

const dateUtils = new DateFnsUtils();

const propTypes = {
  kudos: PropTypes.shape({
    id: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired,
    senderId: PropTypes.string.isRequired,
    recipientTeam: PropTypes.object,
    dateCreated: PropTypes.array.isRequired,
    dateApproved: PropTypes.array,
    recipientMembers: PropTypes.array,
  }).isRequired,
  includeActions: PropTypes.bool,
  includeEdit: PropTypes.bool,
  onKudosAction: PropTypes.func,
};

const KudosCard = ({ kudos, includeActions, includeEdit, onKudosAction }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [expanded, setExpanded] = useState(true);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [kudosPublic, setKudosPublic] = useState(kudos.publiclyVisible);
  const [kudosMessage, setKudosMessage] = useState(kudos.message);
  const [memberSelectorOpen, setMemberSelectorOpen] = useState(false);
  const [kudosRecipientMembers, setKudosRecipientMembers] = useState(kudos.recipientMembers);

  const sender = selectActiveOrInactiveProfile(state, kudos.senderId);

  const getRecipientComponent = useCallback(() => {
    if (kudos.recipientTeam) {
      return (
        <Chip
          avatar={
            <Avatar>
              <TeamIcon />
            </Avatar>
          }
          label={kudos.recipientTeam.name}
        />
      );
    } else if (kudos.recipientMembers.length === 1) {
      const [recipient] = kudos.recipientMembers;
      return (
        <Chip
          avatar={<Avatar src={getAvatarURL(recipient?.workEmail)} />}
          label={`${recipient.firstName} ${recipient.lastName}`}
        />
      );
    }

    return (
      <AvatarGroup max={16}>
        {kudos.recipientMembers.map((member) => (
          <Tooltip
            arrow
            key={member.id}
            title={`${member.firstName} ${member.lastName}`}
          >
            <Avatar src={getAvatarURL(member.workEmail)} />
          </Tooltip>
        ))}
      </AvatarGroup>
    );
  }, [kudos]);

  const approveKudosCallback = useCallback(
    async (event) => {
      event.stopPropagation();
      const res = await approveKudos(kudos, csrf);
      if (res?.payload?.data && !res.error) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "success",
            toast: "Kudos approved",
          },
        });
        onKudosAction && onKudosAction();
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to approve kudos",
          },
        });
      }
    },
    [kudos, csrf, dispatch, onKudosAction]
  );

  const deleteKudosCallback = useCallback(async () => {
    setDeleteDialogOpen(false);
    const res = await deleteKudos(kudos.id, csrf);
    if (res?.payload?.status === 204 && !res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Pending kudos deleted",
        },
      });
      onKudosAction && onKudosAction();
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to delete kudos",
        },
      });
    }
  }, [kudos, csrf, dispatch, onKudosAction]);

  const updateKudosCallback = useCallback(async () => {
    // Close the dialog.
    setEditDialogOpen(false);

    // Update the modifiable parts.
    const proposed = {
      id: kudos.id,
      message: kudosMessage,
      publiclyVisible: kudosPublic,
      recipientMembers: kudosRecipientMembers,
    };

    // Update on the server.
    const res = await updateKudos(proposed, csrf);
    if (res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to update kudos",
        },
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Kudos Updated",
        },
      });
      onKudosAction && onKudosAction();
    }
  }, [kudos, kudosMessage, kudosPublic, kudosRecipientMembers, csrf, dispatch, onKudosAction]);

  const getStatusComponent = useCallback(() => {
    const dateApproved = kudos.dateApproved
      ? new Date(kudos.dateApproved.join("/"))
      : null;

    const info = [];
    const actions = [];
    if (includeActions) {
      actions.push(
        <Tooltip key="approve" arrow title="Approve">
          <Button
            variant="outlined"
            color="success"
            size="small"
            onClick={approveKudosCallback}
          >
            <CheckIcon />
          </Button>
        </Tooltip>
      );
    } else if (dateApproved) {
      info.push(
        <Typography key="received" color="green" variant="body2">
          Received{" "}
          {dateApproved ? dateUtils.format(dateApproved, "MM/dd/yyyy") : ""}
        </Typography>
      );
    } else {
      const dateCreated = new Date(kudos.dateCreated.join("/"));
      if (kudos.publiclyVisible) {
        info.push(
          <Typography key="pending" color="orange" variant="body2">
            Pending
          </Typography>
        );
      }
      info.push(
        <Typography key="created" variant="body2" color="gray" fontSize="10px">
          Created {dateUtils.format(dateCreated, "MM/dd/yyyy")}
        </Typography>
      );
    }
    if (includeEdit) {
      actions.push(
        <Tooltip key="edit" arrow title="Edit">
          <Button
            variant="outlined"
            size="small"
            onClick={(event) => {
              event.stopPropagation();
              reloadKudosValues();
              setEditDialogOpen(true);
            }}
          >
            <EditIcon />
          </Button>
        </Tooltip>
      );
    }
    if (includeActions || includeEdit) {
      actions.push(
        <Tooltip key="delete" arrow title="Delete">
          <Button
            variant="outlined"
            color="error"
            size="small"
            onClick={(event) => {
              event.stopPropagation();
              setDeleteDialogOpen(true);
            }}
          >
            <CloseIcon />
          </Button>
        </Tooltip>
      );
    }
    return <>
             {info.length > 0 && <div>
               {info}
             </div>}
             {actions.length > 0 && <div className="kudos-action-buttons">
               {actions}
             </div>}
           </>;
  }, [kudos, includeActions, includeEdit, approveKudosCallback]);

  const reloadKudosValues = () => {
    setKudosMessage(kudos.message);
    setKudosPublic(kudos.publiclyVisible);
    setKudosRecipientMembers(kudos.recipientMembers);
  };

  const dateApproved = kudos.dateApproved
    ? new Date(kudos.dateApproved.join("/"))
    : null;

  return (
    <>
      <Dialog open={deleteDialogOpen}>
        <DialogTitle>Delete Kudos</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to complete this action? The kudos
            will be deleted.
          </DialogContentText>
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
      <Dialog open={editDialogOpen}  maxWidth="sm" fullWidth>
        <DialogTitle>Edit Kudos</DialogTitle>
        <DialogContent>
          <MemberSelector
            onChange={setKudosRecipientMembers}
            selected={kudosRecipientMembers}
          />
          <FormGroup>
            <FormControlLabel
              control={<Checkbox checked={kudosPublic} />}
              label="Public"
              onChange={e => {
                setKudosPublic(e.target.checked);
              }}
            />
          </FormGroup>
          <TextField
            id="kudos-text"
            fullWidth
            multiline
            defaultValue={kudosMessage}
            onChange={(event) => {
              setKudosMessage(event.target.value);
            }}
            rows={5}
            style={{ marginTop: "2rem" }}
            variant="outlined"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>
            Cancel
          </Button>
          <Button onClick={updateKudosCallback}
                  disabled={kudosMessage.trim().length == 0 ||
                            kudosRecipientMembers.length == 0}
                  autoFocus>
            Save
          </Button>
        </DialogActions>
      </Dialog>
      <Paper className="kudos-card">
        <div
          className="kudos-card-header"
          onClick={() => setExpanded(!expanded)}
        >
          <div className="members-container">
            {getRecipientComponent()}
            <Typography variant="body1">received kudos from</Typography>
            <Chip
              avatar={<Avatar src={getAvatarURL(sender?.workEmail)} />}
              label={sender?.name}
            />
          </div>
          <div className="kudos-status-container">{getStatusComponent()}</div>
        </div>
        <Divider />
        <Collapse in={expanded}>
          <div className="kudos-card-content">
            <Typography variant="body1">{kudos.message}</Typography>
            {kudos.recipientMembers?.length > 1 && (
              <div className="kudos-recipient-list">
                <Typography variant="body2">
                  {kudos.recipientTeam ? "Team Members:" : "Members:"}
                </Typography>
                {kudos.recipientMembers.map((recipient) => (
                  <Chip
                    key={recipient.id}
                    avatar={<Avatar src={getAvatarURL(recipient?.workEmail)} />}
                    label={`${recipient.firstName} ${recipient.lastName}`}
                  />
                ))}
              </div>
            )}
          </div>
        </Collapse>
      </Paper>
    </>
  );
};

KudosCard.propTypes = propTypes;

export default KudosCard;
