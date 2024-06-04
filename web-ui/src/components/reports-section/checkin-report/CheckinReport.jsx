import React, { useContext, useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import { getAvatarURL } from '../../../api/api.js';
import { AppContext } from '../../../context/AppContext.jsx';
import { selectFilteredCheckinsForTeamMemberAndPDL } from '../../../context/selectors.js';

import ExpandMore from '../../expand-more/ExpandMore.jsx';
import HorizontalLinearStepper from './HorizontalLinearStepper.jsx';
import TeamMemberMap from './TeamMemberMap.jsx';
import { statusForPeriodByMemberScheduling } from './checkin-utils.js';

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
import dayjs from 'dayjs';

const CheckinsReport = ({ closed, pdl, planned, reportDate }) => {
  const { state } = useContext(AppContext);
  const [expanded, setExpanded] = useState(true);
  const [statusForPeriod, setStatusForPeriod] = useState(
    /** @type CheckinStatus */ ('Not Started')
  );

  const { name, id, members, workEmail, title, terminationDate } = pdl;
  const handleExpandClick = () => setExpanded(!expanded);

  /**
   * Determine the status of the check-ins for the period based on the members.
   * @param {MemberProfile[]} members - The members of the PDL.
   * @returns {CheckinStatus} The status of the check-ins for the period.
   */
  const statusForPeriodByMembers = (members = []) => {
    if (members.length === 0) return 'No Members';

    const allMembersCompleted = members.every(
      member =>
        statusForPeriodByMemberScheduling(
          selectFilteredCheckinsForTeamMemberAndPDL(
            state,
            member.id,
            id,
            closed,
            planned
          ),
          reportDate
        ) === 'Completed'
    );

    // Done when all PDL team members have completed check-ins
    if (allMembersCompleted) return 'Done';

    const anyMembersCompleted = members.some(
      member =>
        statusForPeriodByMemberScheduling(
          selectFilteredCheckinsForTeamMemberAndPDL(
            state,
            member.id,
            id,
            closed,
            planned
          ),
          reportDate
        ) === 'Completed'
    );

    const anyInProgress = members.some(
      member =>
        statusForPeriodByMemberScheduling(
          selectFilteredCheckinsForTeamMemberAndPDL(
            state,
            member.id,
            id,
            closed,
            planned
          ),
          reportDate
        ) === 'Scheduled'
    );

    // In progress when there is at least one scheduled check-in
    if (anyMembersCompleted || anyInProgress) return 'In Progress';

    // Not started when no check-ins are scheduled
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

  // Set status for the period based on members and termination date
  useEffect(() => {
    terminationDate
      ? setStatusForPeriod('Terminated')
      : setStatusForPeriod(statusForPeriodByMembers(members));
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
                {statusForPeriod === 'Terminated' ? (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    Effective {dayjs(terminationDate).format('MMM D, YYYY')}
                    <Chip label={statusForPeriod} color="error" />
                  </Box>
                ) : (
                  <Chip label={statusForPeriod} />
                )}
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
    title: PropTypes.string,
    terminationDate: PropTypes.instanceOf(Date)
  }),
  planned: PropTypes.bool,
  reportDate: PropTypes.instanceOf(Date)
};

CheckinsReport.propTypes = propTypes;
export default CheckinsReport;
