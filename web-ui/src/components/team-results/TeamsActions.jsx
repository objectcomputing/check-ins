import React, { useContext, useState } from 'react';

import AddTeamModal from './EditTeamModal';
import { createTeam } from '../../api/team';
import { AppContext } from '../../context/AppContext';
import { ADD_TEAM } from '../../context/actions';

import { Button } from '@mui/material';
import GroupIcon from '@mui/icons-material/Group';

import './TeamResults.css';

const displayName = 'TeamsActions';

const TeamsActions = () => {
  const { state, dispatch } = useContext(AppContext);
  const [open, setOpen] = useState(false);

  const { csrf } = state;

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <div className="team-actions">
      <Button startIcon={<GroupIcon />} onClick={handleOpen}>
        Add Team
      </Button>
      <AddTeamModal
        open={open}
        onClose={handleClose}
        onSave={async team => {
          if (csrf) {
            let res = await createTeam(team, csrf);
            let data =
              res.payload && res.payload.data && !res.error
                ? res.payload.data
                : null;
            if (data) {
              dispatch({ type: ADD_TEAM, payload: data });
            }
            handleClose();
          }
        }}
        headerText="Add A New Team"
      />
    </div>
  );
};

TeamsActions.displayName = displayName;

export default TeamsActions;
