import React, { useContext, useState } from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';

import { getAvatarURL } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectFilteredCheckinsForTeamMemberAndPDL } from '../../context/selectors';

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
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
  Stepper,
  Step,
  StepLabel,
  Typography
} from '@mui/material';

/**
 * @typedef {Object} Checkin
 * @property {string} id - The ID of the check-in.
 * @property {boolean} completed - Indicates whether the check-in is completed.
 * @property {Array} checkinDate - The date of the check-in.
 * @property {string} pdlId - The ID of the PDL.
 * @property {string} teamMemberId - The ID of the team member.
 */

/**
 * @typedef {("Done" | "In Progress" | "Not Started")} CheckinStatus
 * @typedef {("Not Yet Scheduled" | "Scheduled" | "Completed")} SchedulingStatus
 */

/** @type {CheckinStatus} */
const steps = ['Not Started', 'In Progress', 'Done'];

/** @type {SchedulingStatus} */
const schedulingSteps = ['Not Yet Scheduled', 'Scheduled', 'Completed'];

function HorizontalLinearStepper({ step = 0 }) {
  const [activeStep, setActiveStep] = React.useState(step);
  const [skipped, setSkipped] = React.useState(new Set());

  const isStepOptional = step => step === -1;
  const isStepSkipped = step => skipped.has(step);

  return (
    <Box sx={{ width: '100%', my: 1 }}>
      <Stepper activeStep={activeStep}>
        {steps.map((label, index) => {
          const stepProps = {};
          const labelProps = {};
          if (isStepOptional(index)) {
            labelProps.optional = (
              <Typography variant="caption">Optional</Typography>
            );
          }
          if (isStepSkipped(index)) {
            stepProps.completed = false;
          }
          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
    </Box>
  );
}

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
  const [statusForPeriod, setStatusForPeriod] = useState(
    /** @type CheckinStatus */ ('Not Started')
  );

  const { name, id, members, workEmail, title } = pdl;

  const handleExpandClick = () => setExpanded(!expanded);

  const getCheckinDate = checkin => {
    if (!checkin || !checkin.checkInDate) return;
    const [year, month, day, hour, minute] = checkin.checkInDate;
    return new Date(year, month - 1, day, hour, minute, 0);
  };

  /**
   * Determine the status of the check-ins for a member.
   * @param {Object} params - The parameters object.
   * @param {Checkin[]} params.checkins - Checkins for a member.
   * @returns {CheckinStatus} The status of check-ins.
   */
  const statusForPeriodByMember = ({ checkins = [] }) => {
    const now = new Date();
    if (checkins.length === 0) return 'Not Started';
    const completed = checkins.filter(checkin => checkin.completed);
    if (completed.length === checkins.length) return 'Done';
    const inProgress = checkins.filter(
      checkin => !checkin.completed && getCheckinDate(checkin) < now
    );
    if (inProgress.length > 0) return 'Open';
    return 'Not Started';
  };

  /**
   * Get the date of the last check-in.
   * @param {Checkin[]} checkins - Check-ins for a member.
   * @returns {Date} The date of the last check-in.
   */
  const getLastCheckinDate = checkins => {
    if (checkins.length === 0) return;
    return checkins.reduce((acc, checkin) => {
      const checkinDate = getCheckinDate(checkin);
      return checkinDate > acc ? checkinDate : acc;
    }, new Date(0));
  };

  /**
   * Determine the status of the check-ins for a PDL.
   * @param {Object} params - The parameters object.
   * @param {Array} params.members - Members of the PDL.
   * @returns {CheckinStatus} The status of check-ins.
   */
  const statusForPeriodByPDL = ({ members = [] }) => {
    // const debug = ['Done', 'In Progress', 'Not Started'][0];
    const now = new Date();
    if (members.length === 0) return 'Not Started';
    const completed = members.filter(member => {
      const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
        state,
        member.id,
        id,
        closed,
        planned
      );
      return (
        checkins.filter(checkin => checkin.completed).length === checkins.length
      );
    });
    if (completed.length === members.length) return 'Done';
    const inProgress = members.filter(member => {
      const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
        state,
        member.id,
        id,
        closed,
        planned
      );
      return (
        checkins.filter(
          checkin => !checkin.completed && getCheckinDate(checkin) < now
        ).length > 0
      );
    });
    if (inProgress.length > 0) return 'In Progress';
    return 'Not Started';
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
          <Accordion className="member-sub-card" key={member.id + id}>
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              className="checkin-report-accordion-summary"
            >
              <div className="member-sub-card-summmary-content">
                <Avatar
                  className={'large'}
                  src={getAvatarURL(member.workEmail)}
                />
                <Typography>{member.name}</Typography>
                <Typography
                  variant="caption"
                  component={'time'}
                  dateTime={getLastCheckinDate(checkins).toISOString()}
                  sx={{ display: { xs: 'none', sm: 'flex' } }}
                  className="last-connected"
                >
                  Last connected:{' '}
                  {getLastCheckinDate(checkins).toLocaleDateString(
                    navigator.language,
                    {
                      year: 'numeric',
                      month: '2-digit',
                      day: 'numeric'
                    }
                  )}
                </Typography>
                <Chip
                  label={statusForPeriodByMember({ checkins })}
                  color={
                    statusForPeriodByMember({ checkins }) === 'Done'
                      ? 'secondary'
                      : 'primary'
                  }
                />
              </div>
            </AccordionSummary>
            <AccordionDetails
              id="accordion-checkin-date"
              className="member-sub-card-accordion-details"
            >
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
          avatar={<Avatar src={getAvatarURL(workEmail)} />}
          action={
            <div className="checkin-report-card-actions">
              <Chip label={statusForPeriodByPDL({ members })} />
              <ExpandMore
                expand={expanded}
                onClick={handleExpandClick}
                aria-expanded={expanded}
                aria-label={expanded ? 'show less' : 'show more'}
              />
            </div>
          }
        />
        <Divider />
        <Collapse in={expanded}>
          <CardContent>
            <Container fixed>
              <div className="checkin-report-stepper">
                <HorizontalLinearStepper
                  step={
                    statusForPeriodByPDL({ members }) === 'Done'
                      ? 2
                      : statusForPeriodByPDL({ members }) === 'In Progress'
                        ? 1
                        : 0
                  }
                />
              </div>
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
