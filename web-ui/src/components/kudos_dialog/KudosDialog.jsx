import React, {useCallback, useContext, useState} from "react";
import PropTypes from "prop-types";
import {
  Alert, AppBar, Autocomplete, Avatar,
  Button, Chip,
  Dialog,
  IconButton, MenuItem,
  Slide,
  TextField, Toolbar, Tooltip,
  Typography
} from "@mui/material";

import "./KudosDialog.css";
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken, selectCurrentUser, selectNormalizedTeams, selectProfile} from "../../context/selectors";
import {createKudos} from "../../api/kudos";
import {UPDATE_TOAST} from "../../context/actions";
import {Link} from "react-router-dom";
import CloseIcon from "@mui/icons-material/Close";
import {getAvatarURL} from "../../api/api";

const Transition = React.forwardRef((props, ref) => {
  return <Slide direction="up" ref={ref} {...props} />;
});

const propTypes = {
  open: PropTypes.bool.isRequired,
  recipient: PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired
  }),
  teamId: PropTypes.string,
  onClose: PropTypes.func
};

const KudosDialog = ({ open, recipient, teamId, onClose }) => {

  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [recipientType, setRecipientType] = useState(teamId ? "TEAM" : "MEMBERS");
  const [message, setMessage] = useState("");
  const [created, setCreated] = useState(false);
  const [selectedTeam, setSelectedTeam] = useState(null);

  const currentUser = selectCurrentUser(state);
  const teams = selectNormalizedTeams(state, "");

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

  const handleClose = useCallback(() => {
    if (created) {
      onClose();
      setCreated(false);
      setMessage("");
    } else {
      onClose();
    }
  }, [onClose, created]);

  return (
    <Dialog
      className="kudos-dialog"
      fullScreen
      open={open}
      onClose={handleClose}
      TransitionComponent={Transition}
    >
      <AppBar>
        <Toolbar>
          <IconButton
            edge="start"
            onClick={handleClose}
            color="inherit">
            <CloseIcon/>
          </IconButton>
          <Typography variant="h6" marginLeft="1rem">Give Kudos</Typography>
        </Toolbar>
      </AppBar>
      <div className="kudos-dialog-content">
        {created
          ? <div style={{ textAlign: "center", marginTop: "2rem" }}>
            <Typography variant="h1">🎉</Typography>
            <Typography variant="h5" fontWeight="bold">Thank you for sending kudos!</Typography>
            <Button variant="outlined" onClick={handleClose}>Close</Button>
            <Link to="/kudos" style={{ textDecoration: "none" }}>
              <Button variant="outlined" style={{ marginTop: "1rem" }}>View Kudos</Button>
            </Link>
          </div>
          : <div className="kudos-dialog-form">
            <div className="kudos-recipient-container">
              <Autocomplete
                options={teams ? teams : []}
                getOptionLabel={(option) => option.name || "Team name not found"}
                value={selectedTeam}
                onChange={(event, newValue) => setSelectedTeam(newValue)}
                fullWidth
                renderInput={(params) => (
                  <TextField
                    {...params}
                    variant="outlined"
                    label={`Select ${recipientType === "TEAM" ? "Team" : "Members"}`}
                  />
                )}
              />
              <TextField
                select
                variant="outlined"
                label="Recipient Type"
                value={recipientType}
                onChange={(event) => setRecipientType(event.target.value)}
              >
                <MenuItem value="TEAM">Team</MenuItem>
                <MenuItem value="MEMBERS">Members</MenuItem>
              </TextField>
            </div>
            {selectedTeam &&
              <div style={{ marginTop: "1rem", display: "flex", flexDirection: "row", gap: "0.5rem" }}>
                {selectedTeam.teamMembers?.map(teamMember => {
                  const profile = selectProfile(state, teamMember?.memberId);
                  const chip = (
                    <Chip
                      label={profile?.name}
                      avatar={<Avatar src={getAvatarURL(profile?.workEmail)}/>}
                      style={{ border: teamMember?.lead ? "1px solid gray" : "none" }}
                    />
                  );
                  return teamMember?.lead
                    ? <Tooltip arrow title="Team Lead">{chip}</Tooltip>
                    : chip;
                })}
              </div>
            }
            <TextField
              variant="outlined"
              label="Message"
              placeholder={`Write a message discussing how this ${recipientType === "TEAM" ? "team" : "member"} has earned some kudos!`}
              multiline
              fullWidth
              rows={7}
              style={{ marginTop: "2rem" }}
              value={message}
              onChange={(event) => setMessage(event.target.value)}
            />
            <Alert severity="info" style={{ marginTop: "1rem", marginBottom: "2rem" }}>
              Kudos will be visible to admins for approval, then sent to the recipient.
            </Alert>
            <Button
              variant="contained"
              disabled={message.trim().length === 0}
              onClick={handleSubmit}>
              Give Kudos
            </Button>
          </div>
        }
      </div>
    </Dialog>
  );

};

KudosDialog.propTypes = propTypes;

export default KudosDialog;