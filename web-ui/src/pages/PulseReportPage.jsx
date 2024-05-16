import { format } from 'date-fns';
import dayjs from 'dayjs';
import React, { useContext, useEffect, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import { Button, Typography } from '@mui/material';

import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';

import { resolve } from '../api/api.js';
import Pulse from '../components/pulse/Pulse.jsx';
import MemberSelector from '../components/member_selector/MemberSelector';
import { AppContext } from '../context/AppContext.jsx';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectHasPulseReportPermission
} from '../context/selectors.js';

import './PulseReportPage.css';

const data = [
  {
    name: 'Page A',
    uv: 4000,
    pv: 2400,
    amt: 2400
  },
  {
    name: 'Page B',
    uv: 3000,
    pv: 1398,
    amt: 2210
  },
  {
    name: 'Page C',
    uv: 2000,
    pv: 9800,
    amt: 2290
  },
  {
    name: 'Page D',
    uv: 2780,
    pv: 3908,
    amt: 2000
  },
  {
    name: 'Page E',
    uv: 1890,
    pv: 4800,
    amt: 2181
  },
  {
    name: 'Page F',
    uv: 2390,
    pv: 3800,
    amt: 2500
  },
  {
    name: 'Page G',
    uv: 3490,
    pv: 4300,
    amt: 2100
  }
];
const PulseReportPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const initialDateFrom = new Date();
  initialDateFrom.setMonth(initialDateFrom.getMonth() - 3);
  const [dateFrom, setDateFrom] = useState(initialDateFrom);

  const [dateTo, setDateTo] = useState(new Date());

  const [pulses, setPulses] = useState(null);
  if (pulses) console.log('PulseReportPage.jsx: pulses =', pulses);
  const [teamMembers, setTeamMembers] = useState([]);

  const loadPulses = async () => {
    if (!csrf) return;

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
  }, [csrf, dateFrom, dateTo]);

  const handleDateFromChange = dayJsDate => {
    const date = new Date(dayJsDate.valueOf());
    setDateFrom(date);
    if (date > dateTo) setDateTo(date);
  };

  const handleDateToChange = dayJsDate => {
    const date = new Date(dayJsDate.valueOf());
    if (date < dateFrom) setDateFrom(date);
    setDateTo(date);
  };

  const handleTeamMembersChange = members => {
    setTeamMembers(members);
  };

  return (
    <div className="pulse-report-page">
      <div className="date-pickers">
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DatePicker
            format="YYYY-MM-DD"
            label="Start Date"
            onChange={handleDateFromChange}
            value={dayjs(dateFrom)}
          />
          <DatePicker
            format="YYYY-MM-DD"
            label="End Date"
            onChange={handleDateToChange}
            value={dayjs(dateTo)}
          />
        </LocalizationProvider>
      </div>

      {/* TODO: Permissions should affect which members can be selected. */}
      <MemberSelector
        onChange={handleTeamMembersChange}
        selected={teamMembers}
      />

      <LineChart width={500} height={300} data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" padding={{ left: 30, right: 30 }} />
        <YAxis />
        <Tooltip />
        <Legend />
        <Line
          type="monotone"
          dataKey="pv"
          stroke="#8884d8"
          activeDot={{ r: 8 }}
        />
        <Line type="monotone" dataKey="uv" stroke="#82ca9d" />
      </LineChart>

      <BarChart
        width={500}
        height={300}
        data={data}
        margin={{
          top: 5,
          right: 30,
          left: 20,
          bottom: 5
        }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="pv" fill="#8884d8" />
        <Bar dataKey="uv" fill="#82ca9d" />
      </BarChart>
    </div>
  );
};

PulseReportPage.displayName = 'PulseReportPage';

export default PulseReportPage;
