import React, { useContext, useState } from 'react';

import { styled } from '@mui/material/styles';
import GuildSummaryCard from './GuildSummaryCard';
import { AppContext } from '../../context/AppContext';
import GuildsActions from './GuildsActions';
import PropTypes from 'prop-types';
import { TextField } from '@mui/material';
import './GuildResults.css';
import SkeletonLoader from '../skeleton_loader/SkeletonLoader';

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
  const { state } = useContext(AppContext);
  const { guilds } = state;
  const [searchText, setSearchText] = useState('');

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
        <GuildsActions />
      </div>
      <div className="guilds">
        {guilds?.length
          ? guilds?.map((guild, index) =>
              guild.name.toLowerCase().includes(searchText.toLowerCase()) ? (
                <GuildSummaryCard
                  key={`guild-summary-${guild.id}`}
                  index={index}
                  guild={guild}
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
