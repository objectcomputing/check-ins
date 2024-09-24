import React, { useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { Avatar, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField } from '@mui/material';
import { AppContext } from '../../context/AppContext';
import { selectProfileMap } from '../../context/selectors';
import { getAvatarURL, resolve } from '../../api/api.js';
import { getMember } from '../../api/member';

const PREFIX = 'Profile';

const classes = {
  profileInfo: `${PREFIX}-profileInfo`,
  profileImage: `${PREFIX}-profileImage`,
  flexRow: `${PREFIX}-flexRow`,
  header: `${PREFIX}-header`,
  title: `${PREFIX}-title`,
  smallAvatar: `${PREFIX}-smallAvatar`
};

const Root = styled('div')(() => ({
  [`& .${classes.profileInfo}`]: {
    display: 'flex',
    flexDirection: 'row',
    margin: '14px'
  },
  [`& .${classes.profileImage}`]: {
    marginRight: '20px',
    marginTop: '10px',
    marginBottom: '10px',
    cursor: 'pointer',
    width: '160px',
    height: '160px'
  },
  [`&.${classes.flexRow}`]: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: '16px'
  },
  [`& .${classes.header}`]: {
    display: 'flex',
    flexDirection: 'row',
    marginBottom: '16px',
    alignItems: 'center'
  },
  [`& .${classes.title}`]: {
    display: 'flex',
    flexDirection: 'column'
  },
  [`& .${classes.smallAvatar}`]: {
    marginRight: '16px'
  }
}));

const Profile = ({ memberId, pdlId, checkinPdlId }) => {
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const userProfile = selectProfileMap(state)[memberId];

  const { workEmail, name, title, location, supervisorid } = userProfile ? userProfile : {};

  const [pdl, setPDL] = useState('');
  const [checkinPdl, setCheckinPdl] = useState('');
  const [supervisor, setSupervisor] = useState('');

  const [organizationDialogOpen, setOrganizationDialogOpen] = useState(false);
  const [eventDialogOpen, setEventDialogOpen] = useState(false);
  const [newOrganization, setNewOrganization] = useState({ name: '', description: '', website: '' });
  const [newEvent, setNewEvent] = useState({ relationshipId: '', eventDate: '', hours: 0, notes: '' });

  const areSamePdls = checkinPdl && pdl && checkinPdl === pdl;

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId, csrf);
        let pdlProfile = res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : '');
      }
    }
    if (csrf) {
      getPDLName();
    }
  }, [csrf, pdlId]);

  // Get Checkin PDL's name
  useEffect(() => {
    async function getCheckinPDLName() {
      if (checkinPdlId) {
        let res = await getMember(checkinPdlId, csrf);
        let checkinPdlProfile = res.payload.data && !res.error ? res.payload.data : undefined;
        setCheckinPdl(checkinPdlProfile ? checkinPdlProfile.name : '');
      }
    }
    if (csrf) {
      getCheckinPDLName();
    }
  }, [csrf, checkinPdlId]);

  // Get Supervisor's name
  useEffect(() => {
    async function getSupervisorName() {
      if (supervisorid) {
        let res = await getMember(supervisorid, csrf);
        let supervisorProfile = res.payload.data && !res.error ? res.payload.data : undefined;
        setSupervisor(supervisorProfile ? supervisorProfile.name : '');
      }
    }
    if (csrf) {
      getSupervisorName();
    }
  }, [csrf, supervisorid]);

  const handleSaveNewOrganization = async () => {
    const res = await resolve({
      method: 'POST',
      url: '/services/volunteer/organization',
      headers: {
        'X-CSRF-Header': csrf,
        'Content-Type': 'application/json'
      },
      data: newOrganization
    });

    if (res.error) {
      // Handle error
      return;
    }

    const newOrg = res.payload.data;
    setOrganizationDialogOpen(false);
    // Optionally, reset newOrganization fields or update the local state
  };

  const handleSaveNewEvent = async () => {
    const res = await resolve({
      method: 'POST',
      url: '/services/volunteer/event',
      headers: {
        'X-CSRF-Header': csrf,
        'Content-Type': 'application/json'
      },
      data: newEvent
    });

    if (res.error) {
      // Handle error
      return;
    }

    setEventDialogOpen(false);
    // Optionally, reset newEvent fields or update the local state
  };

  // Update to prevent focus/scroll to "Add New" buttons when switching tabs
  useEffect(() => {
    const addNewOrgButton = document.querySelector('button[aria-label="Add New Organization"]');
    if (addNewOrgButton) {
      addNewOrgButton.blur(); // Ensure the button isn't focused
    }

    const addNewEventButton = document.querySelector('button[aria-label="Add New Event"]');
    if (addNewEventButton) {
      addNewEventButton.blur(); // Ensure the button isn't focused
    }
  }, [organizationDialogOpen, eventDialogOpen]); // Run this effect when opening/closing dialogs

  return (
    <Root className={classes.flexRow}>
      <Avatar
        className={classes.profileImage}
        alt="Profile"
        src={getAvatarURL(workEmail)}
        sx={{ display: { xs: 'none', sm: 'flex' } }}
      />
      <div className={classes.profileInfo}>
        <div>
          <div className={classes.header}>
            <Avatar
              className={classes.smallAvatar}
              src={getAvatarURL(workEmail)}
              sx={{ display: { sm: 'none', xs: 'flex' } }}
            />
            <div className={classes.title}>
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
              <Typography color="textSecondary" component="h3">
                {title}
              </Typography>
            </div>
          </div>
          <Typography variant="body2" color="textSecondary" component="p">
            <a href={`mailto:${workEmail}`} target="_blank" rel="noopener noreferrer">
              {workEmail}
            </a>
            <br />
            Location: {location}
            <br />
            Supervisor: {supervisor}
            <br />
            Current PDL: {pdl}
            <br />
            {checkinPdl && !areSamePdls && `PDL @ Time of Check-In: ${checkinPdl}`}
          </Typography>

          {/* Button to Add Organization */}
          <Button
            variant="contained"
            onClick={() => setOrganizationDialogOpen(true)}
            style={{ marginTop: '20px' }}
            aria-label="Add New Organization" // Added aria-label for easy querying
          >
            Add New Organization
          </Button>

          {/* Button to Add Event */}
          <Button
            variant="contained"
            onClick={() => setEventDialogOpen(true)}
            style={{ marginTop: '20px', marginLeft: '10px' }}
            aria-label="Add New Event" // Added aria-label for easy querying
          >
            Add New Event
          </Button>
        </div>
      </div>

      {/* Organization Creation Dialog */}
      <Dialog open={organizationDialogOpen} onClose={() => setOrganizationDialogOpen(false)}>
        <DialogTitle>Create New Organization</DialogTitle>
        <DialogContent>
          <TextField
            label="Name"
            fullWidth
            margin="dense"
            value={newOrganization.name}
            onChange={e => setNewOrganization({ ...newOrganization, name: e.target.value })}
          />
          <TextField
            label="Description"
            fullWidth
            margin="dense"
            value={newOrganization.description}
            onChange={e => setNewOrganization({ ...newOrganization, description: e.target.value })}
          />
          <TextField
            label="Website URL"
            fullWidth
            margin="dense"
            value={newOrganization.website}
            onChange={e => setNewOrganization({ ...newOrganization, website: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOrganizationDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSaveNewOrganization}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* Event Creation Dialog */}
      <Dialog open={eventDialogOpen} onClose={() => setEventDialogOpen(false)}>
        <DialogTitle>Create New Event</DialogTitle>
        <DialogContent>
          <TextField
            label="Event Date"
            fullWidth
            margin="dense"
            value={newEvent.eventDate}
            onChange={e => setNewEvent({ ...newEvent, eventDate: e.target.value })}
          />
          <TextField
            label="Hours"
            fullWidth
            margin="dense"
            type="number"
            value={newEvent.hours}
            onChange={e => setNewEvent({ ...newEvent, hours: e.target.value })}
          />
          <TextField
            label="Notes"
            fullWidth
            margin="dense"
            value={newEvent.notes}
            onChange={e => setNewEvent({ ...newEvent, notes: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEventDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSaveNewEvent}>Save</Button>
        </DialogActions>
      </Dialog>
    </Root>
  );
};

export default Profile;
