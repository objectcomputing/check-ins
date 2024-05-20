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
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Typography
} from '@mui/material';

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

// Returns a random, integer score between 1 and 5.
const randomScore = previousScore => {
  if (!previousScore) return Math.ceil(Math.random() * 5);

  const atLow = previousScore === 1;
  const atHigh = previousScore === 5;
  const spread = atLow || atHigh ? 1 : 2;
  const delta = Math.round(Math.random() * spread);
  return atLow
    ? previousScore + delta
    : atHigh
      ? previousScore - delta
      : previousScore - 1 + delta;
};

let responseFrequencies = [];
const PulseReportPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const initialDateFrom = new Date();
  initialDateFrom.setMonth(initialDateFrom.getMonth() - 3);
  const [dateFrom, setDateFrom] = useState(initialDateFrom);
  const [dateTo, setDateTo] = useState(new Date());

  const [pulses, setPulses] = useState(null);
  const [teamMembers, setTeamMembers] = useState([]);

  const [barChartData, setBarChartData] = useState([]);
  const [lineChartData, setLineChartData] = useState([]);

  // This generates random data to use in the line chart
  // since we do not yet have data in the database.
  useEffect(() => {
    const data = [];
    let internal = null;
    let external = null;
    let date = new Date(dateFrom);
    while (date < dateTo) {
      internal = randomScore(internal);
      external = randomScore(external);
      data.push({
        date: format(date, `yyyy-MM-dd`),
        internal,
        external
      });
      date.setDate(date.getDate() + 1);
    }
    setLineChartData(data);

    const frequencies = [];
    for (let i = 1; i <= 5; i++) {
      frequencies.push({ score: i, internal: 0, external: 0 });
    }
    for (const d of data) {
      frequencies[d.internal - 1].internal++;
      frequencies[d.external - 1].external++;
    }
    console.log('PulseReportPage.jsx : frequencies =', frequencies);
    setBarChartData(frequencies);
  }, [dateFrom, dateTo]);

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
    console.log('PulseReportPage.jsx handleDateFromChange: date =', date);
    setDateFrom(date);
    if (date > dateTo) setDateTo(date);
  };

  const handleDateToChange = dayJsDate => {
    const date = new Date(dayJsDate.valueOf());
    console.log('PulseReportPage.jsx handleDateToChange: date =', date);
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

      <Card>
        <CardHeader
          title={'Average pulse scores for "At Work" and "Outside Work"'}
          titleTypographyProps={{ variant: 'h5', component: 'h2' }}
        />
        <CardContent>
          <ResponsiveContainer width="100%" aspect={3.0}>
            <LineChart data={lineChartData} height={300}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                angle={-90}
                dataKey="date"
                height={100}
                padding={{ left: 30, right: 30 }}
                tickMargin={45}
              />
              <YAxis domain={[1, 5]} ticks={[1, 2, 3, 4, 5]} />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                dataKey="internal"
                stroke="#8884d8"
                dot={false}
              />
              <Line
                dataKey="external"
                dot={false}
                stroke="#82ca9d"
                type="monotone"
              />
            </LineChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>

      <Card>
        <CardHeader
          title="Distribution of pulse scores for selected team members"
          titleTypographyProps={{ variant: 'h5', component: 'h2' }}
        />
        <CardContent>
          <BarChart
            width={500}
            height={300}
            data={barChartData}
            margin={{
              top: 5,
              right: 30,
              left: 20,
              bottom: 5
            }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="score" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Bar dataKey="internal" fill="#8884d8" />
            <Bar dataKey="external" fill="#82ca9d" />
          </BarChart>
        </CardContent>
      </Card>
    </div>
  );
};

PulseReportPage.displayName = 'PulseReportPage';

export default PulseReportPage;
