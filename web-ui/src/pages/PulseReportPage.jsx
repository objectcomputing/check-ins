import { format } from 'date-fns';
import React, { useContext, useEffect, useState } from 'react';
import { Button, Typography } from '@mui/material';

import { resolve } from '../api/api.js';
import Pulse from '../components/pulse/Pulse.jsx';
import { AppContext } from '../context/AppContext.jsx';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectHasPulseReportPermission
} from '../context/selectors.js';

import './PulsePage.css';

const PulseReportPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [pulses, setPulses] = useState(null);
  console.log('PulseReportPage.jsx: pulses =', pulses);

  const loadPulses = async () => {
    if (!csrf) return;

    const dateTo = new Date();
    const dateFrom = new Date();
    dateFrom.setMonth(dateTo.getMonth() - 3);
    const query = {
      dateFrom: format(dateFrom, `yyyy-MM-dd`),
      dateTo: format(dateTo, `yyyy-MM-dd`)
    };
    const queryString = Object.entries(query)
      .map(([key, value]) => `${key}=${value}`)
      .join('&');

    try {
      const res = await resolve({
        method: 'GET',
        url: `/services/pulse-responses?${queryString}`,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      if (res.error) throw new Error(res.error.message);
      const pulses = res.payload.data;
      //TODO: Currently these objects only contain the comment text value,
      //      not scores, but story 2345 will add those.
      setPulses(pulses);
    } catch (err) {
      console.error('PulsePage.jsx loadTodayPulse:', err);
    }
  };

  useEffect(() => {
    //TODO: Skipping the permission check during testing
    //      because this permission has not been implemented yet.
    // if (selectHasPulseReportPermission(state)) loadPulses();
    loadPulses();
  }, [csrf]);

  return <div className="pulse-report-page">Pulse report goes here!</div>;
};

PulseReportPage.displayName = 'PulseReportPage';

export default PulseReportPage;
