import React, {useCallback, useContext, useState} from "react";
import PropTypes from "prop-types";
import {
  Alert, AppBar, Autocomplete, Avatar, Button, Checkbox, Chip, Dialog,
  FormGroup, FormControlLabel, IconButton, MenuItem, Slide, 
  TextField, Toolbar, Tooltip, Typography
} from "@mui/material";

import "./KudosDialog.css";
import {AppContext} from "../../context/AppContext";
import {
  selectCsrfToken,
  selectCurrentUser,
  selectNormalizedTeams,
  selectOrderedCurrentMemberProfiles,
  selectProfile,
} from "../../context/selectors";
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
  const [publicCheckin, setPublicCheckin] = useState(true); //TODO: Allow toggle for public/private kudos
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [selectedMembers, setSelectedMembers] = useState(recipient ? [recipient] : []);

  const currentUser = selectCurrentUser(state);
  const teams = selectNormalizedTeams(state, "");
  const memberProfiles = selectOrderedCurrentMemberProfiles(state);
  console.log(memberProfiles);
  console.log(selectedMembers);

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

    if (message.trim().length > 0 && csrf) {

      let recipients;
      if (recipientType === "TEAM") {
        recipients = selectedTeam.teamMembers.map(teamMember => {
          return selectProfile(state, teamMember.memberId);
        });
      } else {
        recipients = selectedMembers;
      }
      if (recipients && recipients.length > 0){
        const kudos = {
          message: message,
          senderId: currentUser.id,
          teamId: recipientType === "TEAM" ? selectedTeam.id : null,
          recipientMembers: recipients
        };

        saveKudos(kudos).then(res => {
          if (res) {
            setCreated(true);
          }
        });
      }
      else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "You must select a recipient"
          }
        });
      }
    }
  }, [state, csrf, dispatch, message, recipient, currentUser, recipientType, selectedMembers, selectedTeam]);

  const handleMessageChange = useCallback((event) => {
    setMessage(event.target.value);
  }, []);

  const getTeamMembers = useCallback(() => {
    return selectedTeam.teamMembers?.sort((a, b) => {
      // Leads come first, then sort by name
      if ((a.lead && b.lead) || (!a.lead && !b.lead)) {
        return a.name.localeCompare(b.name);
      }
      return a.lead ? -1 : 1;
    }).map(teamMember => {
      const profile = selectProfile(state, teamMember?.memberId);
      const chip = (
        <Chip
          key={profile?.id}
          label={profile?.name}
          avatar={<Avatar src={getAvatarURL(profile?.workEmail)}/>}
          style={{ border: teamMember?.lead ? "1px solid gray" : "none" }}
        />
      );
      return teamMember?.lead
        ? <Tooltip key={profile?.id} arrow title="Team Lead">{chip}</Tooltip>
        : chip;
    })
  }, [selectedTeam, state]);

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
          ? <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", height: "100%" }}>
            <Typography variant="h1">🎉</Typography>
            <Typography variant="h4" fontWeight="bold" marginTop="2rem">Thank you for sending kudos!</Typography>
            <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center", gap: "1rem", margin: "2rem" }}>
              <Button variant="outlined" onClick={handleClose}>Close</Button>
              <Link to="/kudos" style={{ textDecoration: "none" }}>
                <Button variant="outlined">View Kudos</Button>
              </Link>
            </div>
          </div>
          : <div className="kudos-dialog-form">
            <div className="kudos-recipient-container">
              {recipientType === "TEAM" &&
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
                      label="Select Team"
                    />
                  )}
                />
              }
              {recipientType === "MEMBERS" &&
                <Autocomplete
                  multiple
                  options={memberProfiles}
                  getOptionLabel={(member) => member?.name}
                  value={selectedMembers}
                  onChange={(event, members) => {
                    setSelectedMembers(members);
                  }}
                  fullWidth
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="outlined"
                      label="Select Members"
                      value={(member) => member.name}
                    />
                  )}
                />
              }
              <FormGroup>
                <FormControlLabel control={<Checkbox defaultChecked />} label="Public" />
              </FormGroup>
              <TextField
                select
                variant="outlined"
                label="Recipient Type"
                value={recipientType}
                onChange={(event) => {
                  setSelectedTeam(null);
                  setSelectedMembers([]);
                  setRecipientType(event.target.value);
                }}
              >
                <MenuItem value="TEAM">Team</MenuItem>
                <MenuItem value="MEMBERS">Members</MenuItem>
              </TextField>
            </div>
            {selectedTeam &&
              <div style={{ marginTop: "1rem", display: "flex", flexDirection: "row", gap: "0.5rem" }}>
                {getTeamMembers()}
              </div>
            }
            <TextField
              variant="outlined"
              label="Message"
              placeholder={
              `Write a message discussing how ${recipientType === "TEAM" 
                ? "this team has" 
                : (selectedMembers.length === 1 ? "this member has" : "these members have")} earned some kudos!`
              }
              multiline
              fullWidth
              rows={7}
              style={{ marginTop: "2rem" }}
              value={message}
              onChange={handleMessageChange}
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