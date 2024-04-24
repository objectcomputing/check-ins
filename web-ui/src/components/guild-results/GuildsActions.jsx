import React, { useContext, useState } from 'react';

import AddGuildModal from './EditGuildModal';
import { createGuild } from '../../api/guild';
import { AppContext } from '../../context/AppContext';
import { ADD_GUILD } from '../../context/actions';

import { Button } from '@mui/material';
import GroupIcon from '@mui/icons-material/Group';

import './GuildResults.css';

const displayName = 'GuildsActions';

const GuildsActions = () => {
  const { state, dispatch } = useContext(AppContext);
  const [open, setOpen] = useState(false);

  const { csrf, userProfile } = state;

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const isAdmin = userProfile?.role?.includes('ADMIN');

  return (
    <div className="guild-actions">
      {isAdmin && (
        <>
          <Button startIcon={<GroupIcon />} onClick={handleOpen}>
            Add Guild
          </Button>
          <AddGuildModal
            open={open}
            onClose={handleClose}
            onSave={async guild => {
              if (csrf) {
                let res = await createGuild(guild, csrf);
                let data =
                  res.payload && res.payload.data && !res.error
                    ? res.payload.data
                    : null;
                if (data) {
                  dispatch({ type: ADD_GUILD, payload: data });
                }
                handleClose();
              }
            }}
            headerText="Add A New Guild"
          />
        </>
      )}
    </div>
  );
};

GuildsActions.displayName = displayName;

export default GuildsActions;
