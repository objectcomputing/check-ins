import React, { useContext, useState } from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';

import { getAvatarURL } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectFilteredCheckinsForTeamMemberAndPDL } from '../../context/selectors';

import ExpandMore from '../expand-more/ExpandMore';

import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Avatar,
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Collapse,
  Container,
  Divider,
  Typography
} from '@mui/material';

import './CheckinReport.css';

const propTypes = {
  closed: PropTypes.bool,
  pdl: PropTypes.shape({
    name: PropTypes.string,
    id: PropTypes.string,
    members: PropTypes.array,
    workEmail: PropTypes.string,
    title: PropTypes.string
  }),
  planned: PropTypes.bool
};

const CheckinsReport = ({ closed, pdl, planned }) => {
  const { state } = useContext(AppContext);
  const [expanded, setExpanded] = useState(true);

  const { name, id, members, workEmail, title } = pdl;

  const handleExpandClick = () => setExpanded(!expanded);

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
              expandIcon={<ExpandMore />}
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
    } else
      return (
        <div className="checkin-report-no-data">
          <Typography>
            No assigned check-ins available for display during this period.
          </Typography>
        </div>
      );
  };

  return (
    <Box display="flex" flexWrap="wrap">
      <Card className="checkin-report-card">
        <CardHeader
          title={
            <div className="checkin-report-card-title-container">
              <Typography
                className="checkin-report-card-name"
                variant="h5"
                noWrap
              >
                {name}
              </Typography>
              <Typography
                className="checkin-report-card-title"
                variant="h6"
                color="gray"
              >
                {title}
              </Typography>
            </div>
          }
          disableTypography
          avatar={<Avatar id="pdl-large" src={getAvatarURL(workEmail)} />}
          action={
            <ExpandMore
              expand={expanded}
              onClick={handleExpandClick}
              aria-expanded={expanded}
              aria-label={expanded ? 'show less' : 'show more'}
            />
          }
        />
        <Divider />
        <Collapse in={expanded}>
          <CardContent>
            <Container fixed>
              <TeamMemberMap />
            </Container>
          </CardContent>
        </Collapse>
      </Card>
    </Box>
  );
};

CheckinsReport.propTypes = propTypes;
export default CheckinsReport;
