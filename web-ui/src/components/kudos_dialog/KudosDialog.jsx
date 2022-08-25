import React, {useCallback, useContext, useState} from "react";
import PropTypes from "prop-types";
import {Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Typography} from "@mui/material";

import "./KudosDialog.css";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectCurrentUser} from "../../context/selectors";
import {createKudos} from "../../api/kudos";
import {UPDATE_TOAST} from "../../context/actions";
import {Link} from "react-router-dom";

const propTypes = {
  open: PropTypes.bool.isRequired,
  recipient: PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired
  }).isRequired,
  onClose: PropTypes.func
};

const KudosDialog = ({ open, recipient, onClose }) => {

  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [message, setMessage] = useState("");
  const [created, setCreated] = useState(false);

  const currentUser = selectCurrentUser(state);

  const handleSubmit = useCallback(() => {

    const saveKudos = async (kudos) => {
      const res = await createKudos(kudos, csrf);
      const data = res?.payload?.data;
      if (!data || res.error) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to give kudos"
          }
        });
      }

      return data;
    }

    if (message.trim().length > 0 && csrf && recipient?.id) {
      const kudos = {
        message: message,
        senderId: currentUser.id,
        recipientId: recipient?.id
      };

      saveKudos(kudos).then(res => {
        if (res) {
          setCreated(true);
        }
      });
    }
  }, [csrf, dispatch, message, recipient, currentUser]);

  const handleClose = useCallback((_, reason) => {
    if (created) {
      onClose();
      setCreated(false);
      setMessage("");
    } else {
      // Do not close dialog on backdrop click
      if (reason !== "backdropClick") {
        onClose();
      }
    }
  }, [onClose, created]);

  return (
    <Dialog
      className="kudos-dialog"
      open={open}
      onClose={handleClose}
    >
      <DialogTitle><b>🎉</b> Give Kudos to <b>{recipient?.name}</b></DialogTitle>
      <DialogContent className="kudos-dialog-content">
        {created
          ? <div style={{ textAlign: "center", marginTop: "2rem" }}>
            <Typography variant="h5" fontWeight="bold">Thank you for sending kudos!</Typography>
            <Link to="/kudos" style={{ textDecoration: "none" }}>
              <Button variant="outlined" style={{ marginTop: "1rem" }}>View Kudos</Button>
            </Link>
          </div>
          : <>
            <TextField
              variant="outlined"
              label="Message"
              placeholder="Write a message discussing how this person has earned some kudos!"
              multiline
              fullWidth
              rows={5}
              style={{ marginTop: "0.5rem" }}
              value={message}
              onChange={(event) => setMessage(event.target.value)}
            />
            <Alert severity="info" style={{ marginTop: "1rem" }}>
              Kudos will be visible to admins for approval, then sent to the recipient.
            </Alert>
          </>
        }
      </DialogContent>
      <DialogActions>
        {created
          ? <Button onClick={handleClose}>Close</Button>
          : <>
            <Button style={{ color: "gray" }} onClick={handleClose}>Cancel</Button>
            <Button
              disabled={message.trim().length === 0}
              onClick={handleSubmit}>
              Give Kudos
            </Button>
          </>
        }
      </DialogActions>
    </Dialog>
  );

};

KudosDialog.propTypes = propTypes;

export default KudosDialog;