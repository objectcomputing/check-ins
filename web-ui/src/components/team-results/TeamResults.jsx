import React, { useContext, useState } from 'react';
import { styled } from '@mui/material/styles';
import TeamSummaryCard from './TeamSummaryCard';
import { AppContext } from '../../context/AppContext';
import {
  selectNormalizedTeams,
  selectTeamsLoading
} from '../../context/selectors';
import TeamsActions from './TeamsActions';
import PropTypes from 'prop-types';
import { TextField } from '@mui/material';
import './TeamResults.css';
import SkeletonLoader from '../skeleton_loader/SkeletonLoader';

const PREFIX = 'TeamResults';
const classes = {
  searchInput: `${PREFIX}-searchInput`
};

const Root = styled('div')({
  [`& .${classes.searchInput}`]: {
    width: '20em'
  }
});

const propTypes = {
  teams: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string
    })
  )
};

const displayName = 'TeamResults';

const TeamResults = () => {
  const { state } = useContext(AppContext);
  const loading = selectTeamsLoading(state);
  const [searchText, setSearchText] = useState('');
  const teams = selectNormalizedTeams(state, searchText);

  const teamCards = teams.map((team, index) => {
    return (
      <TeamSummaryCard
        key={`team-summary-${team.id}`}
        index={index}
        team={team}
      />
    );
  });

  return (
    <Root>
      <div className="team-search">
        <TextField
          className={classes.searchInput}
          label="Search teams..."
          placeholder="Team Name"
          value={searchText}
          onChange={e => {
            setSearchText(e.target.value);
          }}
        />
        <TeamsActions />
      </div>
      <div className="teams">
        {loading
          ? Array.from({ length: 20 }).map((_, index) => (
              <SkeletonLoader key={index} type="team" />
            ))
          : teams?.length && !loading
            ? teamCards
            : null}
      </div>
    </Root>
  );
};

TeamResults.propTypes = propTypes;
TeamResults.displayName = displayName;

export default TeamResults;
