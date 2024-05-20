import React, { useContext } from 'react';
import { Link } from 'react-router-dom';

import { Card, CardHeader, Grid, Typography } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import { styled } from '@mui/material/styles';

import { getAvatarURL } from '../../api/api.js';
import SkeletonLoader from '../skeleton_loader/SkeletonLoader';

import { AppContext } from '../../context/AppContext';
import {
  selectMemberProfilesLoading,
  selectProfile
} from '../../context/selectors';
import { randomConfetti } from '../../context/util';

import { formatBirthday } from '../../helpers/celebration.js';

import './Birthdays.css';

const PREFIX = 'MemberSummaryCard';
const classes = {
  header: `${PREFIX}-header`
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

const Birthdays = ({ birthdays, xPos = 0.75 }) => {
  const { state } = useContext(AppContext);

  const loading = selectMemberProfilesLoading(state);

  const createBirthdayCards = birthdays.map((bday, index) => {
    let user = selectProfile(state, bday.userId);
    if (user) {
      return (
        <Card className={'birthdays-card'} key={index}>
          <Link
            style={{ color: 'inherit', textDecoration: 'none' }}
            to={`/profile/${bday.userId}`}
          >
            <CardHeader
              className={'birthday-card'}
              title={
                <Typography variant="h5" component="h2">
                  Happy Birthday{' '}
                  <span>{user.firstName + ' ' + user.lastName}!</span>
                </Typography>
              }
              subheader={
                <Typography color="textSecondary" component="h3">
                  {formatBirthday(bday.birthDay)}
                </Typography>
              }
              disableTypography
              avatar={
                <Avatar
                  className={'celebrations-avatar'}
                  src={getAvatarURL(user?.workEmail)}
                />
              }
            />
          </Link>
        </Card>
      );
    } else return null;
  });

  return (
    <div className="birthdays-container">
      <div className="balloons" onClick={() => randomConfetti(0.6, xPos)}>
        <div className="balloon">
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div className="balloon">
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div className="balloon">
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
      </div>
      <Root>
        <div className="bday-title">
          <h1>Birthdays</h1>
        </div>
        <Grid container columns={6} spacing={3}>
          <Grid item className={classes.members}>
            {loading
              ? Array.from({ length: 20 }).map((_, index) => (
                  <SkeletonLoader key={index} type="people" />
                ))
              : !loading
                ? createBirthdayCards
                : null}
          </Grid>
        </Grid>
      </Root>
    </div>
  );
};

export default Birthdays;
