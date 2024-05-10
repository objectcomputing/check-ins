import React from 'react';
import { Link } from 'react-router-dom';
import { Chip, Typography } from '@mui/material';
import { getCheckinDate } from './checkin-utils';
import './LinkSection.css';

const LinkSection = ({ checkin, member }) => {
  const now = new Date();
  let checkinDate = new Date(getCheckinDate(checkin));
  let dateString = new Date(getCheckinDate(checkin)).toString();
  dateString = dateString.split(' ').slice(0, 5).join(' ');

  return (
    <Link
      style={{ textDecoration: 'none' }}
      to={`/checkins/${member.id}/${checkin.id}`}
    >
      <div className="link-section-link-body">
        <Typography>{dateString}</Typography>
        <Chip
          color={checkin.completed ? 'secondary' : 'primary'}
          label={
            checkin.completed
              ? 'Closed'
              : checkinDate > now
                ? 'Planned'
                : 'Open'
          }
        />
      </div>
    </Link>
  );
};

export default LinkSection;
