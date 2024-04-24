import React, { useContext } from 'react';
import { Link } from 'react-router-dom';

import { getAvatarURL } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectFilteredCheckinsForTeamMemberAndPDL } from '../../context/selectors';

import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Avatar,
  Box,
  Card,
  CardHeader,
  CardContent,
  Chip,
  Container,
  Typography
} from '@mui/material';

import './CheckinReport.css';

const CheckinsReport = ({ closed, pdl, planned }) => {
  const { state } = useContext(AppContext);
  const { name, id, members, workEmail } = pdl;

  const getCheckinDate = checkin => {
    if (!checkin || !checkin.checkInDate) return;
    const [year, month, day, hour, minute] = checkin.checkInDate;
    return new Date(year, month - 1, day, hour, minute, 0);
  };

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
        <div className="checkin-report-link">
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
  const TeamMemberMap = () => {
    const filtered =
      members &&
      members.filter(member => {
        const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
          state,
          member.id,
          id,
          closed,
          planned
        );
        return checkins && checkins.length > 0;
      });
    if (filtered && filtered.length > 0) {
      return filtered.map(member => {
        const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
          state,
          member.id,
          id,
          closed,
          planned
        );
        return (
          <Accordion id="member-sub-card" key={member.id + id}>
            <AccordionSummary
              aria-controls="panel1a-content"
              id="accordion-summary"
            >
              <Avatar
                className={'large'}
                src={getAvatarURL(member.workEmail)}
              />
              <Typography>{member.name}</Typography>
            </AccordionSummary>
            <AccordionDetails id="accordion-checkin-date">
              {checkins.map(checkin => (
                <LinkSection
                  checkin={checkin}
                  key={checkin.id}
                  member={member}
                />
              ))}
            </AccordionDetails>
          </Accordion>
        );
      });
    } else return null;
  };

  return (
    <Box display="flex" flexWrap="wrap">
      <Card id="pdl-card">
        <CardHeader
          title={
            <Typography variant="h5" component="h2">
              {name}
            </Typography>
          }
          disableTypography
          avatar={<Avatar id="pdl-large" src={getAvatarURL(workEmail)} />}
        />
        <CardContent>
          <Container fixed>
            <TeamMemberMap />
          </Container>
        </CardContent>
      </Card>
    </Box>
  );
};
export default CheckinsReport;
