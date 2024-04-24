import React, { useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { Avatar, Typography } from '@mui/material';
import { AppContext } from '../../context/AppContext';
import { selectProfileMap } from '../../context/selectors';
import { getAvatarURL } from '../../api/api.js';
import { getMember } from '../../api/member';

const PREFIX = 'Profile';

const classes = {
  profileInfo: `${PREFIX}-profileInfo`,
  profileImage: `${PREFIX}-profileImage`,
  flexRow: `${PREFIX}-flexRow`,
  header: `${PREFIX}-header`,
  title: `${PREFIX}-title`,
  smallAvatar: `${PREFIX}-smallAvatar`
};

const Root = styled('div')(() => ({
  [`& .${classes.profileInfo}`]: {
    display: 'flex',
    flexDirection: 'row',
    margin: '14px'
  },
  [`& .${classes.profileImage}`]: {
    marginRight: '20px',
    marginTop: '10px',
    marginBottom: '10px',
    cursor: 'pointer',
    width: '160px',
    height: '160px'
  },
  [`&.${classes.flexRow}`]: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: '16px'
  },
  [`& .${classes.header}`]: {
    display: 'flex',
    flexDirection: 'row',
    marginBottom: '16px',
    alignItems: 'center'
  },
  [`& .${classes.title}`]: {
    display: 'flex',
    flexDirection: 'column'
  },
  [`& .${classes.smallAvatar}`]: {
    marginRight: '16px'
  }
}));

const Profile = ({ memberId, pdlId, checkinPdlId }) => {
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const userProfile = selectProfileMap(state)[memberId];

  const { workEmail, name, title, location, supervisorid } = userProfile
    ? userProfile
    : {};

  const [pdl, setPDL] = useState('');
  const [checkinPdl, setCheckinPdl] = useState('');
  const [supervisor, setSupervisor] = useState('');

  const areSamePdls = checkinPdl && pdl && checkinPdl === pdl;

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId, csrf);
        let pdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setPDL(pdlProfile ? pdlProfile.name : '');
      }
    }
    if (csrf) {
      getPDLName();
    }
  }, [csrf, pdlId]);

  // Get Checkin PDL's name
  useEffect(() => {
    async function getCheckinPDLName() {
      if (checkinPdlId) {
        let res = await getMember(checkinPdlId, csrf);
        let checkinPdlProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setCheckinPdl(checkinPdlProfile ? checkinPdlProfile.name : '');
      }
    }
    if (csrf) {
      getCheckinPDLName();
    }
  }, [csrf, checkinPdlId]);

  // Get Supervisor's name
  useEffect(() => {
    async function getSupervisorName() {
      if (supervisorid) {
        let res = await getMember(supervisorid, csrf);
        let supervisorProfile =
          res.payload.data && !res.error ? res.payload.data : undefined;
        setSupervisor(supervisorProfile ? supervisorProfile.name : '');
      }
    }
    if (csrf) {
      getSupervisorName();
    }
  }, [csrf, supervisorid]);

  return (
    <Root className={classes.flexRow}>
      <Avatar
        className={classes.profileImage}
        alt="Profile"
        src={getAvatarURL(workEmail)}
        sx={{ display: { xs: 'none', sm: 'flex' } }}
      />
      <div className={classes.profileInfo}>
        <div>
          <div className={classes.header}>
            <Avatar
              className={classes.smallAvatar}
              src={getAvatarURL(workEmail)}
              sx={{ display: { sm: 'none', xs: 'flex' } }}
            />
            <div className={classes.title}>
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
              <Typography color="textSecondary" component="h3">
                {title}
              </Typography>
            </div>
          </div>
          <Typography variant="body2" color="textSecondary" component="p">
            <a
              href={`mailto:${workEmail}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              {workEmail}
            </a>
            <br />
            Location: {location}
            <br />
            Supervisor: {supervisor}
            <br />
            Current PDL: {pdl}
            <br />
            {checkinPdl &&
              !areSamePdls &&
              `PDL @ Time of Check-In: ${checkinPdl}`}
          </Typography>
        </div>
      </div>
    </Root>
  );
};

export default Profile;
