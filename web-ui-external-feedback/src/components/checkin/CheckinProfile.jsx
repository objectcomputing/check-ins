import React, { useContext, useEffect, useState } from 'react';
import { getMember } from '../../api/member';
import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api.js';

import Avatar from '@mui/material/Avatar';

import './Checkin.css';
const displayName = 'CheckinProfile';

const CheckinProfile = () => {
  const { state } = useContext(AppContext);
  const { csrf, selectedProfile, userProfile } = state;
  const { name, pdlId, title, workEmail } = selectedProfile
    ? selectedProfile
    : userProfile && userProfile.memberProfile
      ? userProfile.memberProfile
      : {};
  const [pdl, setPDL] = useState();

  // Get PDL's name
  useEffect(() => {
    async function getPDLName() {
      if (pdlId) {
        let res = await getMember(pdlId, csrf);
        let pdlProfile =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : undefined;
        setPDL(pdlProfile ? pdlProfile.name : '');
      }
    }
    if (csrf) {
      getPDLName();
    }
  }, [csrf, pdlId]);

  return (
    <div className="profile-section">
      <Avatar
        src={getAvatarURL(workEmail)}
        style={{ height: '120px', width: '120px' }}
      />
      <div className="info">
        <p>{name}</p>
        <p>Job Title: {title}</p>
        <p>PDL: {pdl}</p>
        <p>Company Email: {workEmail}</p>
      </div>
    </div>
  );
};

CheckinProfile.displayName = displayName;
export default CheckinProfile;
