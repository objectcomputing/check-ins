import React, { useContext, useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import { getAvatarURL } from '../../../api/api.js';
import { AppContext } from '../../../context/AppContext.jsx';
import { selectFilteredCheckinsForTeamMemberAndPDL } from '../../../context/selectors.js';

import ExpandMore from '../../expand-more/ExpandMore.jsx';
import HorizontalLinearStepper from './HorizontalLinearStepper.jsx';
import TeamMemberMap from './TeamMemberMap.jsx';
import { getCheckinDate } from './checkin-utils.js';

import { getQuarterBeginEnd } from '../../../helpers/index.js';

import {
  Avatar,
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Collapse,
  Container,
  Divider,
  Typography,
  Badge
} from '@mui/material';

import './CheckinReport.css';

const CheckinsReport = ({ closed, pdl, planned, reportDate }) => {
  const { state } = useContext(AppContext);
  const [expanded, setExpanded] = useState(true);
  const [statusForPeriod, setStatusForPeriod] = useState(
    /** @type CheckinStatus */ ('Not Started')
  );

  const { name, id, members, workEmail, title } = pdl;

  const handleExpandClick = () => setExpanded(!expanded);

  /**
   * Determine the status of the check-ins for a PDL during the reporting period.
   * @param {Array} members - Members of the PDL.
   * @returns {string} The status of check-ins.
   */
  const statusForPeriodByMembers = (members = []) => {
    if (members.length === 0) return 'No Members';

    const isCheckinCompletedDuringPeriod = (checkin, start, end) => {
      const checkinDate = getCheckinDate(checkin);
      return checkinDate >= start && checkinDate <= end && checkin.completed;
    };

    const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(reportDate);

    const allCheckinsCompleted = member => {
      const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
        state,
        member.id,
        id,
        closed,
        planned
      );
      return checkins.every(checkin =>
        isCheckinCompletedDuringPeriod(checkin, startOfQuarter, endOfQuarter)
      );
    };

    const allMembersCompleted = members.every(allCheckinsCompleted);
    if (allMembersCompleted) return 'Done';

    const isCheckinInProgress = (checkin, start, end) => {
      const checkinDate = getCheckinDate(checkin);
      const now = new Date();
      return (
        checkinDate >= start &&
        checkinDate <= end &&
        !checkin.completed &&
        checkinDate < now
      );
    };

    const isInProgress = member => {
      let checkins = selectFilteredCheckinsForTeamMemberAndPDL(
        state,
        member.id,
        id,
        closed,
        planned
      );
      checkins = checkins.filter(checkin =>
        isCheckinInProgress(checkin, startOfQuarter, endOfQuarter)
      );
      return checkins.length > 0;
    };

    const anyInProgress = members.some(isInProgress);
    if (anyInProgress) return 'In Progress';

    return 'Not Started';
  };

  /**
   * Set the expanded state based on the status of the check-ins and number of members.
   * @param {CheckinStatus} status - The status of the check-ins.
   * @param {PDLProfile} pdl - The PDL object.
   * @modifies {expanded}
   */
  const setExpandedByStatusAndMembers = (status, pdl) => {
    const isStatusDone = status === 'Done';
    const hasMembers = pdl.members && pdl.members.length !== 0;

    if (isStatusDone || !hasMembers) {
      setExpanded(false);
    } else {
      setExpanded(true);
    }
  };

  // Set status for the period based on members
  useEffect(() => {
    setStatusForPeriod(statusForPeriodByMembers(members));
  }, [members, reportDate]);

  // Set expanded state based on status and number of members
  useEffect(() => {
    setExpandedByStatusAndMembers(statusForPeriod, pdl);
  }, [statusForPeriod, pdl]);

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
              <Badge
                badgeContent={members ? members.length : undefined}
                color="secondary"
              >
                <Chip label={statusForPeriodByMembers(members)} />
              </Badge>
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
                  key={`${id}-${statusForPeriod}`}
                  step={
                    statusForPeriod === 'Done'
                      ? 2
                      : statusForPeriod === 'In Progress'
                        ? 1
                        : 0
                  }
                />
              </div>
              <TeamMemberMap
                {...{ members, id, closed, planned, reportDate }}
              />
            </Container>
          </CardContent>
        </Collapse>
      </Card>
    </Box>
  );
};

const propTypes = {
  closed: PropTypes.bool,
  pdl: PropTypes.shape({
    name: PropTypes.string,
    id: PropTypes.string,
    members: PropTypes.array,
    workEmail: PropTypes.string,
    title: PropTypes.string
  }),
  planned: PropTypes.bool,
  reportDate: PropTypes.instanceOf(Date)
};

CheckinsReport.propTypes = propTypes;
export default CheckinsReport;
