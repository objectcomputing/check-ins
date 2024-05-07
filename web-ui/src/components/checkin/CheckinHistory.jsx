import React, { useContext } from 'react';
import { useHistory, useParams } from 'react-router-dom';
import { UPDATE_CHECKIN } from '../../context/actions';
import { AppContext } from '../../context/AppContext';
import { updateCheckin } from '../../api/checkins';
import {
  selectCsrfToken,
  selectCheckinsForMember,
  selectProfile
} from '../../context/selectors';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { MobileDateTimePicker } from '@mui/x-date-pickers';
import TextField from '@mui/material/TextField';

import './Checkin.css';

const CheckinsHistory = () => {
  const { state, dispatch } = useContext(AppContext);
  const { checkinId, memberId } = useParams();
  const history = useHistory();
  const csrf = selectCsrfToken(state);
  const selectedProfile = selectProfile(state, memberId);

  const checkins = selectCheckinsForMember(state, memberId);
  const index = checkins.findIndex(checkin => checkin.id === checkinId);

  const getCheckinDate = () => {
    if (checkins && checkins[index]?.checkInDate) {
      const [year, month, day, hour, minute] = checkins[index].checkInDate;
      return new Date(year, month - 1, day, hour, minute, 0);
    }
    // return new date unless you are running a Jest test
    let currentDate = null;
    try {
      currentDate = process?.env?.VITEST_WORKER_ID
        ? new Date(2020, 9, 21)
        : new Date();
    } catch (e) {
      currentDate = new Date();
    }

    return currentDate;
  };

  const leftArrowClass = 'arrow ' + (index > 0 ? 'enabled' : 'disabled');
  const rightArrowClass =
    'arrow ' + (index < checkins.length - 1 ? 'enabled' : 'disabled');

  const previousCheckin = () => {
    if (index > 0) {
      history.push(`/checkins/${memberId}/${checkins[index - 1].id}`);
    }
  };

  const nextCheckin = () => {
    if (index < checkins.length - 1) {
      history.push(`/checkins/${memberId}/${checkins[index + 1].id}`);
    }
  };

  const pickDate = async date => {
    if (csrf) {
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hours = date.getHours();
      const minutes = date.getMinutes();
      const checkin = checkins[index];
      const dateArray = [year, month, day, hours, minutes, 0];
      const res = await updateCheckin(
        {
          ...checkin,
          pdlId: selectedProfile?.pdlId,
          checkInDate: dateArray
        },
        csrf
      );
      const updatedCheckin =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      updatedCheckin &&
        dispatch({ type: UPDATE_CHECKIN, payload: updatedCheckin });
    }
  };

  return (
    getCheckinDate() && (
      <div className="date-picker">
        <IconButton
          disabled={index <= 0}
          aria-label="Previous Check-in`"
          onClick={previousCheckin}
          size="large"
        >
          <ArrowBackIcon
            className={leftArrowClass}
            style={{
              fontSize: '1.2em',
              fill:
                index <= 0
                  ? 'var(--checkins-palette-action-disabled)'
                  : 'var(--checkins-palette-action)'
            }}
          />
        </IconButton>
        <MobileDateTimePicker
          slotProps={{ textField: { style: { width: '18em' } } }}
          format="MMMM dd, yyyy @hh:mm aaaa"
          value={getCheckinDate()}
          onChange={pickDate}
          label="Check-In Date"
          showTodayButton
          disabled={!checkins?.length || checkins[index]?.completed}
        />
        <IconButton
          disabled={index >= checkins.length - 1}
          aria-label="Next Check-in`"
          onClick={nextCheckin}
          size="large"
        >
          <ArrowForwardIcon
            className={rightArrowClass}
            style={{
              fontSize: '1.2em',
              fill:
                index <= 0
                  ? 'var(--checkins-palette-action-disabled)'
                  : 'var(--checkins-palette-action)'
            }}
          />
        </IconButton>
      </div>
    )
  );
};

export default CheckinsHistory;
