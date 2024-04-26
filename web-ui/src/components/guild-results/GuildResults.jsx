import PropTypes from 'prop-types';
import React, { useContext, useEffect, useState } from 'react';
import GroupIcon from '@mui/icons-material/Group';
import { Button, TextField } from '@mui/material';
import { styled } from '@mui/material/styles';

import { createGuild } from '../../api/guild';
import { ADD_GUILD } from '../../context/actions';
import { AppContext } from '../../context/AppContext';
import AddGuildModal from './EditGuildModal';
import GuildSummaryCard from './GuildSummaryCard';
import SkeletonLoader from '../skeleton_loader/SkeletonLoader';
import './GuildResults.css';

const PREFIX = 'GuildResults';
const classes = {
  searchInput: `${PREFIX}-searchInput`
};

const Root = styled('div')(() => ({
  [`& .${classes.searchInput}`]: {
    width: '20em'
  }
}));

const propTypes = {
  guilds: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string
    })
  )
};

const displayName = 'GuildResults';

const GuildResults = () => {
  const { dispatch, state } = useContext(AppContext);
  const { csrf, guilds, userProfile } = state;
  const [addOpen, setAddOpen] = useState(false);
  const [openedGuildId, setOpenedGuildId] = useState('');
  const [searchText, setSearchText] = useState('');

  useEffect(() => {
    const url = new URL(location.href);

    const addOpen = url.searchParams.get('addOpen') || '';
    setAddOpen(addOpen === 'true');

    const guildId = url.searchParams.get('guild') || '';
    setOpenedGuildId(guildId);

    const search = url.searchParams.get('search') || '';
    setSearchText(search);
  }, []);

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    if (addOpen) params.addOpen = true;
    if (openedGuildId) params.guild = openedGuildId;
    if (searchText) params.search = searchText;
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, [addOpen, openedGuildId, searchText]);

  const handleOpen = () => setAddOpen(true);

  const handleClose = () => setAddOpen(false);

  const isAdmin = userProfile?.role?.includes('ADMIN');

  return (
    <Root>
      <div className="guild-search">
        <TextField
          className={classes.searchInput}
          label="Search guilds..."
          placeholder="Guild Name"
          value={searchText}
          onChange={e => {
            setSearchText(e.target.value);
          }}
        />
        <div className="guild-actions">
          {isAdmin && (
            <>
              <Button startIcon={<GroupIcon />} onClick={handleOpen}>
                Add Guild
              </Button>
              <AddGuildModal
                open={addOpen}
                onClose={handleClose}
                onSave={async guild => {
                  console.log('GuildResults.jsx onSave: guild =', guild);
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
      </div>
      <div className="guilds">
        {guilds?.length
          ? guilds?.map((guild, index) =>
              guild.name.toLowerCase().includes(searchText.toLowerCase()) ? (
                <GuildSummaryCard
                  key={`guild-summary-${guild.id}`}
                  index={index}
                  guild={guild}
                  isOpen={guild.id === openedGuildId}
                  onGuildSelect={setOpenedGuildId}
                />
              ) : null
            )
          : Array.from({ length: 20 }).map((_, index) => (
              <SkeletonLoader key={index} type="guild" />
            ))}
      </div>
    </Root>
  );
};

GuildResults.propTypes = propTypes;
GuildResults.displayName = displayName;

export default GuildResults;
