import React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import PropTypes from 'prop-types';

const OrganizationDialog = ({ open, onClose, onSave, organization, setOrganization }) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Create New Organization</DialogTitle>
      <DialogContent>
        <TextField
          label="Name"
          fullWidth
          margin="dense"
          value={organization.name}
          onChange={e => setOrganization({ ...organization, name: e.target.value })}
        />
        <TextField
          label="Description"
          fullWidth
          margin="dense"
          value={organization.description}
          onChange={e => setOrganization({ ...organization, description: e.target.value })}
        />
        <TextField
          label="Website"
          fullWidth
          margin="dense"
          value={organization.website}
          onChange={e => setOrganization({ ...organization, website: e.target.value })}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={onSave}>Save</Button>
      </DialogActions>
    </Dialog>
  );
};

OrganizationDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired,
  organization: PropTypes.shape({
    name: PropTypes.string,
    description: PropTypes.string,
    website: PropTypes.string
  }).isRequired,
  setOrganization: PropTypes.func.isRequired
};

export default OrganizationDialog;