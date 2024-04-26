import React, { useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import MemberSummaryCard from '../components/member-directory/MemberSummaryCard';
import { AppContext } from '../context/AppContext';
import {
  selectMemberProfilesLoading,
  selectNormalizedMembers
} from '../context/selectors';
import { TextField, Grid } from '@mui/material';
import './PeoplePage.css';
import SkeletonLoader from '../components/skeleton_loader/SkeletonLoader';

const PREFIX = 'PeoplePage';
const classes = {
  search: `${PREFIX}-search`,
  searchInput: `${PREFIX}-searchInput`,
  members: `${PREFIX}-members`
};

const Root = styled('div')({
  [`& .${classes.search}`]: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  [`& .${classes.searchInput}`]: {
    width: '20em'
  },
  [`& .${classes.members}`]: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-evenly',
    width: '100%'
  }
});

const PeoplePage = () => {
  const { state } = useContext(AppContext);
  const loading = selectMemberProfilesLoading(state);

  const [searchText, setSearchText] = useState('');

  const normalizedMembers = selectNormalizedMembers(state, searchText);

  useEffect(() => {
    const url = new URL(location.href);
    const search = url.searchParams.get('search') || '';
    setSearchText(search);
  }, []);

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    if (searchText) params.search = searchText;
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, [searchText]);

  const createMemberCards = normalizedMembers.map((member, index) => {
    return (
      <MemberSummaryCard
        key={`${member.name}-${member.id}`}
        index={index}
        member={member}
      />
    );
  });

  return (
    <Root className="people-page">
      <Grid container spacing={3}>
        <Grid item xs={12} className={classes.search}>
          <TextField
            className={classes.searchInput}
            label="Select employees..."
            placeholder="Member Name"
            value={searchText}
            onChange={e => {
              setSearchText(e.target.value);
            }}
          />
        </Grid>
        <Grid item className={classes.members}>
          {loading
            ? Array.from({ length: 20 }).map((_, index) => (
                <SkeletonLoader key={index} type="people" />
              ))
            : !loading
              ? createMemberCards
              : null}
        </Grid>
      </Grid>
    </Root>
  );
};

export default PeoplePage;
