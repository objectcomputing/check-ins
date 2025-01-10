import { format } from 'date-fns';
import dayjs from 'dayjs';
import React, { useContext, useEffect, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Pie,
  Cell,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import {
  Comment,
  SentimentVeryDissatisfied,
  SentimentDissatisfied,
  SentimentNeutral,
  SentimentSatisfied,
  SentimentVerySatisfied,
} from '@mui/icons-material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
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
  Typography,
  Link,
} from '@mui/material';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';

import { pSBC } from '../helpers/colors.js';
import { getAvatarURL, resolve } from '../api/api.js';
import MemberSelector from '../components/member_selector/MemberSelector';
import { AppContext } from '../context/AppContext.jsx';
import {
  selectCsrfToken,
  selectProfileMap,
  selectHasViewPulseReportPermission,
  noPermission,
} from '../context/selectors';
import ExpandMore from '../components/expand-more/ExpandMore';

import './PulseReportPage.css';

// Recharts doesn't support using CSS variables, so we can't
// easily use color variables defined in variables.css.
const ociDarkBlue = '#2c519e';
//const ociLightBlue = '#76c8d4'; // not currently used
const ociOrange = '#f8b576';

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

const ScoreOptionLabel = {
  'Internal': 'At Work',
  'External': 'Outside Work',
  'Combined': 'Both',
};

const pulsesPerPage = 15;

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
  const today = import.meta.env.VITEST_WORKER_ID
    ? new Date(2024, 5, 4)
    : new Date();
  const initialDateFrom = new Date(today);
  initialDateFrom.setMonth(initialDateFrom.getMonth() - 3);
  const [dateFrom, setDateFrom] = useState(initialDateFrom);
  const [dateTo, setDateTo] = useState(today);

  const [averageData, setAverageData] = useState({});
  const [barChartData, setBarChartData] = useState([]);
  const [expanded, setExpanded] = useState(false);
  const [scoreChartData, setScoreChartData] = useState([]);
  const [pulses, setPulses] = useState([]);
  const [pulsesPageNumber, setPulsesPageNumber] = useState(0);
  const [scope, setScope] = useState('Individual');
  const [scoreType, setScoreType] = useState(ScoreOption.COMBINED);
  const [selectedPulse, setSelectedPulse] = useState(null);
  const [showComments, setShowComments] = useState(false);
  const [teamMembers, setTeamMembers] = useState([]);
  const [internalPieChartData, setInternalPieChartData] = useState([]);
  const [externalPieChartData, setExternalPieChartData] = useState([]);

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
    setScoreChartData(data);

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
    const scoreChartDataPoints = [];
    const frequencies = [];
    for (let i = 1; i <= 5; i++) {
      frequencies.push({ score: i, internal: 0, external: 0 });
    }
    const teamMemberIds = teamMembers.map(member => member.id);

    const managerMode = scope === 'Manager';

    for (const pulse of pulses) {
      const memberId = pulse.teamMemberId;
      if (memberId && !teamMemberIds.includes(memberId)) continue;

      const { externalScore, internalScore, submissionDate } = pulse;
      const [year, month, day] = submissionDate;
      const monthPadded = month.toString().padStart(2, '0');
      const dayPadded = day.toString().padStart(2, '0');
      const date = `${year}-${monthPadded}-${dayPadded}`;
      const found = scoreChartDataPoints.find(points => points.date === date)
      if(found) {
        found?.datapoints?.push(pulse);
      } else {
        scoreChartDataPoints.push({
          date,
          datapoints: [pulse]
        });
      }

      frequencies[internalScore - 1].internal++;
      if (externalScore != null) {
        frequencies[externalScore - 1].external++;
      }

      let memberIdToUse;
      if (memberId) {
        const member = memberMap[memberId];
        const { supervisorid } = member;
        memberIdToUse = managerMode ? supervisorid : memberId;

        /* For debugging ...
        if (supervisorid) {
          const supervisor = memberMap[supervisorid];
          console.log(`The supervisor of ${member.name} is ${supervisor.name}`);
        } else {
          console.log(`${member.name} has no supervisor`);
        }
        */
      }

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

    let internalPieCounts = [
      {name: "internalVeryDissatisfied", value: 0},
      {name: "internalDissatisfied", value: 0},
      {name: "internalNeutral", value: 0},
      {name: "internalSatisfied", value: 0},
      {name: "internalVerySatisfied", value: 0},
    ];
    for(let day of scoreChartDataPoints) {
      day.datapoints.forEach(datapoint => {
        internalPieCounts[datapoint.internalScore - 1].value++;
      });
    }
    // Filter out data with a zero value so that the pie chart does not attempt
    // to display them.
    setInternalPieChartData(internalPieCounts.filter((p) => p.value != 0));

    let externalPieCounts = [
      {name: "externalVeryDissatisfied", value: 0},
      {name: "externalDissatisfied", value: 0},
      {name: "externalNeutral", value: 0},
      {name: "externalSatisfied", value: 0},
      {name: "externalVerySatisfied", value: 0},
    ];
    for(let day of scoreChartDataPoints) {
      day.datapoints.forEach(datapoint => {
        if (datapoint.externalScore != null) {
          externalPieCounts[datapoint.externalScore - 1].value++;
        }
      });
    }
    // Filter out data with a zero value so that the pie chart does not attempt
    // to display them.
    setExternalPieChartData(externalPieCounts.filter((p) => p.value != 0));

    setScoreChartData(scoreChartDataPoints.map(day => {
      const iScores = {};
      const eScores = {};

      day.datapoints.forEach(datapoint => {
        iScores[datapoint.internalScore] =
            (iScores[datapoint.internalScore] || 0) + 1;
        eScores[datapoint.externalScore] =
            (eScores[datapoint.externalScore] || 0) + 1;
      });

      return {
        date: day.date,
        internalVeryDissatisfied: iScores[1],
        internalDissatisfied: iScores[2],
        internalNeutral: iScores[3],
        internalSatisfied: iScores[4],
        internalVerySatisfied: iScores[5],
        externalVeryDissatisfied: eScores[1],
        externalDissatisfied: eScores[2],
        externalNeutral: eScores[3],
        externalSatisfied: eScores[4],
        externalVerySatisfied: eScores[5],
      };
    }));
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

    const res = await resolve({
      method: 'GET',
      url: `/services/pulse-responses?${queryString}`,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

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
    setPulsesPageNumber(0);
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

  const dataInfo = [
    {key: "internalVeryDissatisfied", stackId: "internal", color: pSBC(-.9, ociDarkBlue), },
    {key: "internalDissatisfied", stackId: "internal", color: pSBC(-.75, ociDarkBlue), },
    {key: "internalNeutral", stackId: "internal", color: pSBC(-.5, ociDarkBlue), },
    {key: "internalSatisfied", stackId: "internal", color: pSBC(-.25, ociDarkBlue), },
    {key: "internalVerySatisfied", stackId: "internal", color: ociDarkBlue, },
    {key: "externalVeryDissatisfied", stackId: "external", color: pSBC(-.9, ociOrange), },
    {key: "externalDissatisfied", stackId: "external", color: pSBC(-.75, ociOrange), },
    {key: "externalNeutral", stackId: "external", color: pSBC(-.5, ociOrange), },
    {key: "externalSatisfied", stackId: "external", color: pSBC(-.25, ociOrange), },
    {key: "externalVerySatisfied", stackId: "external", color: ociOrange, },
  ];

  const labelToSentiment = (label) => {
    const suffix = label.includes("internal")
            ? ScoreOptionLabel[ScoreOption.INTERNAL]
            : ScoreOptionLabel[ScoreOption.EXTERNAL];
    switch(label.replace("internal", "").replace("external", "")) {
      case "VeryDissatisfied":
        return <><SentimentVeryDissatisfied/> {suffix}</>;
      case "Dissatisfied":
        return <><SentimentDissatisfied/> {suffix}</>;
      case "Neutral":
        return <><SentimentNeutral/> {suffix}</>;
      case "Satisfied":
        return <><SentimentSatisfied/> {suffix}</>;
      case "VerySatisfied":
        return <><SentimentVerySatisfied/> {suffix}</>;
    }
    return "ERROR";
  };

  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="custom-tooltip">
          <p>{label}</p>
          {payload.slice().reverse().map(p => {
            return <div key={p.dataKey} style={{color: `${p.color}`}}>
                     {p.value} {p.name.props.children}
                   </div>;
          })}
        </div>
      );
    }

    return null;
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

  const sectionTitle = (prefix) => {
    let title = `${prefix} for`;
    if (scoreType == ScoreOption.COMBINED ||
        scoreType == ScoreOption.INTERNAL) {
      title += ` "${ScoreOptionLabel[ScoreOption.INTERNAL]}"`;
    }
    if (scoreType == ScoreOption.COMBINED) {
      title += " and";
    }
    if (scoreType == ScoreOption.COMBINED ||
        scoreType == ScoreOption.EXTERNAL) {
      title += ` "${ScoreOptionLabel[ScoreOption.EXTERNAL]}"`;
    }
    return title;
  };

  const pieLabelToSentiment = (label) => {
    switch(label.replace("internal", "").replace("external", "")) {
      case "VeryDissatisfied":
        return "😦";
      case "Dissatisfied":
        return "🙁";
      case "Neutral":
        return "😐";
      case "Satisfied":
        return "🙂";
      case "VerySatisfied":
        return "😀";
    }
    return "ERROR";
  };

  const RADIAN = Math.PI / 180;
  const renderPieLabel = function({ cx, cy, midAngle, innerRadius, outerRadius,
                                    percent, index, name, value }) {
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
    const x = cx + radius * Math.cos(-midAngle * RADIAN);
    const y = cy + radius * Math.sin(-midAngle * RADIAN);

    return (
      <>
      <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'}
            dominantBaseline="central">
        {pieLabelToSentiment(name)} {value}
      </text>
      </>
    );
  };

  const titleWords = (text) => {
    if (text.match(/^[a-z]+$/)) {
      // Uppercase the first letter
      text = text[0].toUpperCase() + text.substring(1);
    } else {
      // Split words and uppercase the first word
      let words = text.split(/(?<=[a-z])(?=[A-Z\d])/);
      words[0] = words[0][0].toUpperCase() + words[0].substring(1);
      text= "";
      let separator = "";
      for(let word of words) {
        text += `${separator}${word}`;
        separator = " ";
      }
    }
    return text;
  };

  const CustomPieTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="custom-tooltip">
          <p className="label">
            {titleWords(payload[0].name
                          .replace("internal", "")
                          .replace("external", ""))} : {payload[0].value}</p>
        </div>
      );
    }

    return null;
  };

  const pieSliceColor = (entry, index) => {
    return <Cell
             key={`cell-${index}`}
             fill={dataInfo.find((value) => value.key == entry.name).color} />;
  };

  const pulseScoresChart = () => (
  <>
    <Card>
      <CardHeader
        title={sectionTitle("Pulse Scores")}
        titleTypographyProps={{ variant: 'h5', component: 'h2' }}
      />
      <CardContent>
        <ResponsiveContainer width="100%" aspect={3.0}>
          <BarChart data={scoreChartData} height={300}>
            <Tooltip content={<CustomTooltip />} />
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              angle={-90}
              dataKey="date"
              height={100}
              padding={{ left: 30, right: 30 }}
              tickMargin={45}
            />
            <YAxis />
            <Tooltip />
            <Legend />
            {dataInfo.filter(o => scoreType == ScoreOption.COMBINED ||
                                  (scoreType == ScoreOption.INTERNAL &&
                                      o.key.includes("internal")) ||
                                  (scoreType == ScoreOption.EXTERNAL &&
                                      o.key.includes("external")))
                     .map((obj) => {
               return <Bar
                        key={obj.key}
                        dataKey={obj.key}
                        fill={obj.color}
                        barSize={20}
                        type="monotone"
                        stackId={obj.stackId}
                        name={labelToSentiment(obj.key)}
                      />;
              })
            }
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
    <Card>
      <CardHeader
        title={sectionTitle("Total Responses")}
        titleTypographyProps={{ variant: 'h5', component: 'h2' }}
      />
      <CardContent>
        <div style={{ display: 'flex', justifyContent: 'center'}}>
          {(scoreType == ScoreOption.COMBINED ||
            scoreType == ScoreOption.INTERNAL) &&
            <div style={{ width: '50%'}}>
              <ResponsiveContainer width="100%" aspect={2.0}>
                <PieChart>
                  <Tooltip
                    wrapperStyle={{ color: "black", backgroundColor: "white",
                                    paddingLeft: "10px", paddingRight: "10px",
                                    zIndex: 2 }}
                    content={<CustomPieTooltip />}
                  />
                  <Pie
                    data={internalPieChartData}
                    dataKey="value"
                    nameKey="name"
                    labelLine={false}
                    label={renderPieLabel}
                  >
                    {internalPieChartData.map(pieSliceColor)}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
            </div>
          }
          {(scoreType == ScoreOption.COMBINED ||
            scoreType == ScoreOption.EXTERNAL) &&
            <div style={{ width: '50%'}}>
              <ResponsiveContainer width="100%" aspect={2.0}>
                <PieChart width="50%">
                  <Tooltip
                    wrapperStyle={{ color: "black", backgroundColor: "white",
                                    paddingLeft: "10px", paddingRight: "10px",
                                    zIndex: 2 }}
                    content={<CustomPieTooltip />}
                  />
                  <Pie
                    data={externalPieChartData}
                    dataKey="value"
                    nameKey="name"
                    labelLine={false}
                    label={renderPieLabel}
                  >
                    {externalPieChartData.map(pieSliceColor)}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
            </div>
          }
        </div>
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
  </>
  );

  const responseSummary = () => {
    const leftDisabled = (pulsesPageNumber <= 0);
    const rightDisabled = (((pulsesPageNumber + 1) * pulsesPerPage) >= pulses.length);
    const start = pulsesPageNumber * pulsesPerPage;
    const pulsesSlice = pulses.slice(start, start + pulsesPerPage);

    let filteredPulses = pulsesSlice;
    const teamMemberIds = teamMembers.map(member => member.id);
    if (teamMemberIds.length) {
      filteredPulses = pulsesSlice.filter(pulse =>
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
        <Link to="#"
              style={leftDisabled ? { cursor: 'auto' } : { cursor: 'pointer' }}
              onClick={(event) => {
          event.preventDefault();
          if (!leftDisabled) {
            setPulsesPageNumber(pulsesPageNumber - 1);
          }
        }}>
          <ArrowBackIcon/>
        </Link>
        <Link to="#"
              style={rightDisabled ? { cursor: 'auto' } : { cursor: 'pointer' }}
              onClick={(event) => {
                event.preventDefault();
                if (!rightDisabled) {
                  setPulsesPageNumber(pulsesPageNumber + 1);
                }
              }}>
          <ArrowForwardIcon/>
        </Link>
      </>
    );
  };

  const toggleLabels = {
    left: {
      title: ScoreOptionLabel[ScoreOption.INTERNAL],
      value: ScoreOption.INTERNAL,
    },
    center: {
      title: ScoreOptionLabel[ScoreOption.Combined],
      value: ScoreOption.COMBINED,
    },
    right: {
      title: ScoreOptionLabel[ScoreOption.EXTERNAL],
      value: ScoreOption.EXTERNAL,
    },
  };

  const toggleChange = (event, value) => {
    setScoreType(value);
  };

  return selectHasViewPulseReportPermission(state) ? (
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
        <ToggleButtonGroup
          value={scoreType}
          exclusive
          onChange={toggleChange}
        >
          <ToggleButton value={ScoreOption.INTERNAL}>
             {ScoreOptionLabel[ScoreOption.INTERNAL]}</ToggleButton>
          <ToggleButton value={ScoreOption.COMBINED}>
             {ScoreOptionLabel[ScoreOption.COMBINED]}</ToggleButton>
          <ToggleButton value={ScoreOption.EXTERNAL}>
             {ScoreOptionLabel[ScoreOption.EXTERNAL]}</ToggleButton>
        </ToggleButtonGroup>
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
          {pulseScoresChart()}
          {averageScores()}
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
  ) : (
    <h3>{noPermission}</h3>
  );
};

PulseReportPage.displayName = 'PulseReportPage';

export default PulseReportPage;
