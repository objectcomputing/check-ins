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
import { Comment } from '@mui/icons-material';
import {
  Avatar,
  Button,
  Card,
  CardContent,
  CardHeader,
  Collapse,
  IconButton,
  Modal,
  Typography
} from '@mui/material';

import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';

import { getAvatarURL, resolve } from '../api/api.js';
import Pulse from '../components/pulse/Pulse.jsx';
import MemberSelector from '../components/member_selector/MemberSelector';
import { AppContext } from '../context/AppContext.jsx';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectHasPulseReportPermission,
  selectProfileMap
} from '../context/selectors.js';
import ExpandMore from '../components/expand-more/ExpandMore';

import './PulseReportPage.css';

// Recharts doesn't support using CSS variables, so we can't
// easily used color variables defined in variables.css.
const ociDarkBlue = '#2c519e';
//const ociLightBlue = '#76c8d4'; // not currently used
// const ociOrange = '#f8b576'; // too light
const orange = '#b26801';

/*
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
*/

let responseFrequencies = [];
const PulseReportPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const memberMap = selectProfileMap(state);

  const initialDateFrom = new Date();
  initialDateFrom.setMonth(initialDateFrom.getMonth() - 3);
  const [dateFrom, setDateFrom] = useState(initialDateFrom);
  const [dateTo, setDateTo] = useState(new Date());

  const [barChartData, setBarChartData] = useState([]);
  const [expanded, setExpanded] = useState(false);
  const [lineChartData, setLineChartData] = useState([]);
  const [pulses, setPulses] = useState([]);
  const [selectedPulse, setSelectedPulse] = useState(null);
  const [showComments, setShowComments] = useState(false);
  const [teamMembers, setTeamMembers] = useState([]);
  console.log('PulseReportPage.jsx : teamMembers =', teamMembers);

  /*
  // This generates random data to use in the line chart.
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
    setBarChartData(frequencies);
  }, [dateFrom, dateTo]);
  */

  // This creates data in the format that recharts needs from pulse data.
  useEffect(() => {
    const data = [];
    const frequencies = [];
    for (let i = 1; i <= 5; i++) {
      frequencies.push({ score: i, internal: 0, external: 0 });
    }
    const teamMemberIds = teamMembers.map(member => member.id);

    for (const pulse of pulses) {
      if (!teamMemberIds.includes(pulse.teamMemberId)) continue;

      const { date, externalScore, internalScore, submissionDate } = pulse;
      const [year, month, day] = submissionDate;
      const monthPadded = month.toString().padStart(2, '0');
      const dayPadded = day.toString().padStart(2, '0');
      data.push({
        date: `${year}-${monthPadded}-${dayPadded}`,
        internal: internalScore,
        external: externalScore
      });
      frequencies[internalScore - 1].internal++;
      frequencies[externalScore - 1].external++;
    }

    setLineChartData(data);
    setBarChartData(frequencies);
  }, [pulses, teamMembers]);

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
      // Sort the pulses on their submission date.
      pulses.sort((p1, p2) => {
        const [year1, month1, day1] = p1.submissionDate;
        const [year2, month2, day2] = p2.submissionDate;
        let compare = year1 - year2;
        if (compare === 0) compare = month1 - month2;
        if (compare === 0) compare = day1 - day2;
        return compare;
      });
      console.log('PulseReportPage.jsx loadPulses: pulses =', pulses);
      setPulses(pulses);
    } catch (err) {
      console.error('PulseReportPage.jsx loadPulses:', err);
    }
  };

  useEffect(() => {
    //TODO: Skipping the permission check during testing
    //      because this permission has not been implemented yet.
    // if (selectHasPulseReportPermission(state)) loadPulses();
    loadPulses();
    setTeamMembers(state.memberProfiles);
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

  const responseSummary = () => {
    let filteredPulses = pulses;
    const teamMemberIds = teamMembers.map(member => member.id);
    if (teamMemberIds.length) {
      filteredPulses = pulses.filter(pulse =>
        teamMemberIds.includes(pulse.teamMemberId))
    }

    return (
      <>
        {filteredPulses.map(pulse => {
          const member = memberMap[pulse.teamMemberId];
          if (!member) return null;

          const {
            externalFeelings,
            externalScore,
            internalFeelings,
            internalScore,
            submissionDate
          } = pulse;
          const [year, month, day] = submissionDate;
          const hasComment = externalFeelings || internalFeelings;
          return (
            <div className="response-row">
              <Avatar src={getAvatarURL(member.workEmail)} />
              {year}-{month}-{day}, {member.name}, {member.title},
              internal: {internalScore}, external: {externalScore}
              {hasComment && (
                <IconButton
                  aria-label="Comment"
                  onClick={() => handleCommentClick(pulse)}
                  size="large"
                >
                  <Comment />
                </IconButton>
              )}
            </div>
          );
        })}
      </>
    );
  };

  const handleCommentClick = (pulse) => {
    setSelectedPulse(pulse);
    setShowComments(true);
  };

  const barChart = () => (
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
          <Bar dataKey="internal" fill={ociDarkBlue} />
          <Bar dataKey="external" fill={orange} />
        </BarChart>
        <ExpandMore
          expand={expanded}
          onClick={() => setExpanded(!expanded)}
          aria-expanded={expanded}
          aria-label={expanded ? 'show less' : 'show more'}
          size="large"
        />
        <Collapse
          className="bottom-row"
          in={expanded}
          timeout="auto"
          unmountOnExit
        >
          {responseSummary()}
        </Collapse>
      </CardContent>
    </Card>
  );

  const lineChart = () => (
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
              stroke={ociDarkBlue}
              dot={false}
            />
            <Line
              dataKey="external"
              dot={false}
              stroke={orange}
              type="monotone"
            />
          </LineChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );

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

      {pulses.length === 0 ? (
        <Typography variant="h5" component="h2">
          No pulses were found in the specfied date range.
        </Typography>
      ) : (
        <>
          {/* TODO: Permissions should affect which members can be selected. */}
          <MemberSelector
            onChange={handleTeamMembersChange}
            selected={teamMembers}
          />
          {lineChart()}
          {barChart()}
        </>
      )}

      <Modal open={showComments} onClose={() => setShowComments(false)}>
        <Card className="feedback-request-enable-edits-modal">
          <CardHeader
            title={
              <Typography variant="h5" fontWeight="bold">
                Pulse Comments
              </Typography>
            }
          />
          <CardContent>
            <div>Internal Feelings: {selectedPulse?.internalFeelings}</div>
            <div>External Feelings: {selectedPulse?.externalFeelings}</div>
          </CardContent>
        </Card>
      </Modal>
    </div>
  );
};

PulseReportPage.displayName = 'PulseReportPage';

export default PulseReportPage;
