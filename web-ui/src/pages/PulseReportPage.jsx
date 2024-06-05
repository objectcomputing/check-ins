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
  Card,
  CardContent,
  CardHeader,
  Collapse,
  FormControl,
  IconButton,
  MenuItem,
  Modal,
  TextField,
  Typography
} from '@mui/material';

import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';

import { getAvatarURL, resolve } from '../api/api.js';
import MemberSelector from '../components/member_selector/MemberSelector';
import { AppContext } from '../context/AppContext.jsx';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectHasViewPulseReportPermission,
  selectProfileMap
} from '../context/selectors.js';
import ExpandMore from '../components/expand-more/ExpandMore';

import './PulseReportPage.css';

// Recharts doesn't support using CSS variables, so we can't
// easily use color variables defined in variables.css.
const ociDarkBlue = '#2c519e';
//const ociLightBlue = '#76c8d4'; // not currently used
// const ociOrange = '#f8b576'; // too light
const orange = '#b26801';

const ScoreOption = {
  INTERNAL: 'Internal',
  EXTERNAL: 'External',
  COMBINED: 'Combined'
};

const propertyMap = {
  [ScoreOption.INTERNAL]: 'internalAverage',
  [ScoreOption.EXTERNAL]: 'externalAverage',
  [ScoreOption.COMBINED]: 'combinedAverage'
};

/*
// Returns a random, integer score between 1 and 5.
// We may want to uncomment this later for testing.
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

  // Mock the date if under test so the snapshot stays consistent
  const today = window.process?.env?.VITEST_WORKER_ID
    ? new Date(2024, 5, 4)
    : new Date();
  const initialDateFrom = new Date(today);
  initialDateFrom.setMonth(initialDateFrom.getMonth() - 3);
  const [dateFrom, setDateFrom] = useState(initialDateFrom);
  const [dateTo, setDateTo] = useState(today);

  const [averageData, setAverageData] = useState({});
  const [barChartData, setBarChartData] = useState([]);
  const [expanded, setExpanded] = useState(false);
  const [lineChartData, setLineChartData] = useState([]);
  const [pulses, setPulses] = useState([]);
  const [scope, setScope] = useState('Individual');
  const [scoreType, setScoreType] = useState(ScoreOption.COMBINED);
  const [selectedPulse, setSelectedPulse] = useState(null);
  const [showComments, setShowComments] = useState(false);
  const [teamMembers, setTeamMembers] = useState([]);

  /*
  // This generates random data to use in the line chart.
  // We may want to uncomment this later for testing.
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

  const average = arr => arr.reduce((a, b) => a + b, 0) / arr.length;

  // This creates data in the format that recharts needs from pulse data.
  useEffect(() => {
    const averageData = {}; // key is member id
    const lineChartData = [];
    const frequencies = [];
    for (let i = 1; i <= 5; i++) {
      frequencies.push({ score: i, internal: 0, external: 0 });
    }
    const teamMemberIds = teamMembers.map(member => member.id);

    const managerMode = scope === 'Manager';

    for (const pulse of pulses) {
      const memberId = pulse.teamMemberId;
      if (!teamMemberIds.includes(memberId)) continue;

      const { externalScore, internalScore, submissionDate } = pulse;
      const [year, month, day] = submissionDate;
      const monthPadded = month.toString().padStart(2, '0');
      const dayPadded = day.toString().padStart(2, '0');
      lineChartData.push({
        date: `${year}-${monthPadded}-${dayPadded}`,
        internal: internalScore,
        external: externalScore
      });

      frequencies[internalScore - 1].internal++;
      frequencies[externalScore - 1].external++;

      const member = memberMap[memberId];
      const { supervisorid } = member;
      const memberIdToUse = managerMode ? supervisorid : memberId;

      /* For debugging ...
      if (supervisorid) {
        const supervisor = memberMap[supervisorid];
        console.log(`The supervisor of ${member.name} is ${supervisor.name}`);
      } else {
        console.log(`${member.name} has no supervisor`);
      }
      */

      // When in manager mode, if the member
      // doesn't have a supervisor then skip this data.
      if (memberIdToUse) {
        let averages = averageData[memberIdToUse];
        if (!averages) {
          averages = averageData[memberIdToUse] = {
            memberId: memberIdToUse,
            externalScores: [],
            internalScores: []
          };
        }
        averages.externalScores.push(externalScore);
        averages.internalScores.push(internalScore);
      }
    }

    setLineChartData(lineChartData);
    setBarChartData(frequencies);

    for (const memberId of Object.keys(averageData)) {
      const averages = averageData[memberId];
      averages.externalAverage = average(averages.externalScores);
      averages.internalAverage = average(averages.internalScores);
      averages.combinedAverage = average([
        ...averages.externalScores,
        ...averages.internalScores
      ]);
    }
    setAverageData(averageData);
  }, [pulses, scope, teamMembers]);

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
      setPulses(pulses);
    } catch (err) {
      console.error('PulseReportPage.jsx loadPulses:', err);
    }
  };

  useEffect(() => {
    // TODO: Uncomment this check after PR #2429 is merged.
    // if (selectHasViewPulseReportPermission(state)) {
    loadPulses();
    // }
  }, [csrf, dateFrom, dateTo]);

  useEffect(() => {
    setTeamMembers(state.memberProfiles || []);
  }, [csrf, state]);

  const averageRow = scores => {
    const { memberId } = scores;
    const member = memberMap[memberId];
    const property = propertyMap[scoreType];
    return (
      <tr key={memberId}>
        <td>
          <Avatar src={getAvatarURL(member.workEmail)} />
        </td>
        <td>
          {member.name}
          <br />
          {member.title}
        </td>
        <td className="score">{scores[property].toFixed(1)}</td>
      </tr>
    );
  };

  const averageScores = () => (
    <Card>
      <CardContent>
        <div className="average-header row">
          <Typography variant="h5">Average Scores</Typography>
          <FormControl style={{ width: '8rem' }}>
            <TextField
              select
              size="small"
              label="Score Type"
              onChange={e => setScoreType(e.target.value)}
              sx={{ width: '8rem' }}
              value={scoreType}
              variant="outlined"
            >
              <MenuItem value={ScoreOption.INTERNAL}>
                {ScoreOption.INTERNAL}
              </MenuItem>
              <MenuItem value={ScoreOption.EXTERNAL}>
                {ScoreOption.EXTERNAL}
              </MenuItem>
              <MenuItem value={ScoreOption.COMBINED}>
                {ScoreOption.COMBINED}
              </MenuItem>
            </TextField>
          </FormControl>
          <FormControl style={{ width: '7.5rem' }}>
            <TextField
              select
              size="small"
              label="Scope"
              onChange={e => setScope(e.target.value)}
              sx={{ width: '7.5rem' }}
              value={scope}
              variant="outlined"
            >
              <MenuItem value="Individual">Individual</MenuItem>
              <MenuItem value="Manager">Manager</MenuItem>
            </TextField>
          </FormControl>
        </div>
        <div className="row">
          {scoreCard(true)}
          {scoreCard(false)}
        </div>
      </CardContent>
    </Card>
  );

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

  const handleCommentClick = pulse => {
    setSelectedPulse(pulse);
    setShowComments(true);
  };

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

  const scoreCard = highest => {
    const label = scope === 'Manager' ? 'Team' : 'Individual';
    const property = propertyMap[scoreType];
    const scoresToShow = Object.values(averageData)
      .sort((a, b) => {
        const aValue = a[property];
        const bValue = b[property];
        return highest ? bValue - aValue : aValue - bValue;
      })
      .slice(0, 5);
    const title = `${highest ? 'Highest' : 'Lowest'} ${label} Scores`;

    return (
      <div>
        <Typography variant="h6">{title}</Typography>
        <table>
          <tbody>{scoresToShow.map(scores => averageRow(scores))}</tbody>
        </table>
      </div>
    );
  };

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

  const responseSummary = () => {
    let filteredPulses = pulses;
    const teamMemberIds = teamMembers.map(member => member.id);
    if (teamMemberIds.length) {
      filteredPulses = pulses.filter(pulse =>
        teamMemberIds.includes(pulse.teamMemberId)
      );
    }

    return (
      <>
        {filteredPulses.map(pulse => {
          const memberId = pulse.teamMemberId;
          const member = memberMap[memberId];
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
          const key = `${memberId}-${year}-${month}-${day}`;
          return (
            <div className="row" key={key}>
              <Avatar src={getAvatarURL(member.workEmail)} />
              {year}-{month}-{day}, {member.name}, {member.title}, internal:{' '}
              {internalScore}, external: {externalScore}
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
          {averageScores()}
          {barChart()}
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
        </>
      )}
    </div>
  );
};

PulseReportPage.displayName = 'PulseReportPage';

export default PulseReportPage;
