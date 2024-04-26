import React, { useContext, useEffect, useState } from 'react';
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
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [selectedTeamId, setSelectedTeamId] = useState('');
  const teams = selectNormalizedTeams(state, searchText);

  const teamCards = teams.map((team, index) => {
    return (
      <TeamSummaryCard
        key={`team-summary-${team.id}`}
        index={index}
        team={team}
        onTeamSelect={setSelectedTeamId}
        selectedTeamId={selectedTeamId}
      />
    );
  });

  useEffect(() => {
    const url = new URL(location.href);

    const addNew = url.searchParams.get('addNew');
    setOpen(addNew === 'true');

    const search = url.searchParams.get('search') || '';
    setSearchText(search);

    const selectedTeamId = url.searchParams.get('team') || '';
    setSelectedTeamId(selectedTeamId);
  }, []);

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    if (open) params.addNew = true;
    if (searchText) params.search = searchText;
    if (selectedTeamId) params.team = selectedTeamId;
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, [open, searchText, selectedTeamId]);

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
        <TeamsActions isOpen={open} onOpen={setOpen} />
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
