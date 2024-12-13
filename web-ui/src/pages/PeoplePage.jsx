import React, { useContext, useState } from 'react';
import { TextField, Grid, Button } from '@mui/material';
import { styled } from '@mui/material/styles';

import MemberSummaryCard from '../components/member-directory/MemberSummaryCard';
import SkeletonLoader from '../components/skeleton_loader/SkeletonLoader';
import { AppContext } from '../context/AppContext';
import {
  selectMemberProfilesLoading,
  selectNormalizedMembers
} from '../context/selectors';
import './PeoplePage.css';
import { useQueryParameters } from '../helpers/query-parameters';
import KudosDialog from '../components/kudos_dialog/KudosDialog';
import StarIcon from '@mui/icons-material/Star';

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

  const [kudosDialogOpen, setKudosDialogOpen] = useState(false);
  const [searchText, setSearchText] = useState('');

  const normalizedMembers = selectNormalizedMembers(state, searchText);

  useQueryParameters([
    {
      name: 'search',
      default: '',
      value: searchText,
      setter: setSearchText
    }
  ]);

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
            label="Search employees..."
            placeholder="Member Name"
            value={searchText}
            onChange={e => {
              setSearchText(e.target.value);
            }}
          />
          <KudosDialog
            open={kudosDialogOpen}
            onClose={() => setKudosDialogOpen(false)}
          />
          <Button
            className="kudos-dialog-open"
            variant="outlined"
            startIcon={<StarIcon/>}
            onClick={() => setKudosDialogOpen(true)}
          >
            Give Kudos
          </Button>
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
