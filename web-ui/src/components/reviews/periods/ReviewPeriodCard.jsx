import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import {
  BorderColor,
  DoorFront,
  HourglassTop,
  MeetingRoom,
  QuestionMark
} from '@mui/icons-material';

import {
  Avatar,
  Card,
  CardActions,
  Collapse,
  ListItemAvatar,
  ListItemText
} from '@mui/material';

import ExpandMore from '../../expand-more/ExpandMore';
import { resolve } from '../../../api/api.js';
import { AppContext } from '../../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentMembers,
  selectHasUpdateReviewAssignmentsPermission,
  selectReviewPeriod,
  selectReviewPeriods,
  selectHasViewReviewAssignmentsPermission,
} from '../../../context/selectors';
import { titleCase } from '../../../helpers/strings.js';

import './ReviewPeriodCard.css';

const propTypes = {
  mode: PropTypes.string,
  onSelect: PropTypes.func.isRequired,
  periodId: PropTypes.string.isRequired,
  selfReviews: PropTypes.object.isRequired
};
const displayName = 'ReviewPeriodCard';

const ReviewStatus = {
  PLANNING: 'PLANNING',
  AWAITING_APPROVAL: 'AWAITING_APPROVAL',
  OPEN: 'OPEN',
  CLOSED: 'CLOSED',
  UNKNOWN: 'UNKNOWN'
};

const reviewStatusIconMap = {
  [ReviewStatus.PLANNING]: <BorderColor />,
  [ReviewStatus.AWAITING_APPROVAL]: <HourglassTop />,
  [ReviewStatus.OPEN]: <MeetingRoom />,
  [ReviewStatus.CLOSED]: <DoorFront />,
  [ReviewStatus.UNKNOWN]: <QuestionMark />
};

const ReviewPeriodCard = ({ mode, onSelect, periodId, selfReviews }) => {
  const { state } = useContext(AppContext);
  const [approvalStats, setApprovalStats] = useState([]);
  const [overallApprovalPercentage, setOverallApprovalPercentage] = useState(0);
  const [expanded, setExpanded] = useState(false);

  const csrf = selectCsrfToken(state);
  const currentMembers = selectCurrentMembers(state);
  const canUpdateReviewAssignments =
    selectHasUpdateReviewAssignmentsPermission(state);
  const periods = selectReviewPeriods(state);

  const period = periods.find(period => period.id === periodId);
  const showPercentages =
    canUpdateReviewAssignments &&
    period.reviewStatus === ReviewStatus.AWAITING_APPROVAL;
  const { name, reviewStatus } = period;

  const handleExpandClick = () => setExpanded(!expanded);

  const loadApprovalStats = async () => {
    if (!selectHasViewReviewAssignmentsPermission(state)) {
      return;
    }

    // Get all the review assignments for this period.
    const res = await resolve({
      method: 'GET',
      url: `/services/review-assignments/period/${periodId}`,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const assignments = res.payload.data;
    for (const assignment of assignments) {
      // Use the reviewee supervisor to track who needs to approve reviewers.
      const member = currentMembers.find(m => m.id === assignment.revieweeId);
      if (member?.supervisorid) {
        assignment.revieweeSupervisorId = member.supervisorid;
      } else {
        // If this person does not have a supervisor, use the reviewer for
        // the calculations below.
        assignment.revieweeSupervisorId = assignment.reviewerId;
      }
    }

    const approvedCount = assignments.filter(a => a.approved).length;
    setOverallApprovalPercentage((100 * approvedCount) / assignments.length);

    // Get a list of all the supervisors in this period.
    const supervisorIds = new Set(assignments.map(a => a.revieweeSupervisorId));
    const supervisors = [...supervisorIds].filter(id => !!id).map(id =>
      currentMembers.find(m => m.id === id)
    );
    supervisors.sort((a, b) => a.name.localeCompare(b.name));

    // Build an array containing statistics for each supervisor.
    const stats = supervisors.map(supervisor => {
      const { id } = supervisor;
      const assignmentsForSupervisor = assignments.filter(
        assignment => assignment.revieweeSupervisorId === id
      );
      const approved = assignmentsForSupervisor.filter(
        assignment => assignment.approved
      ).length;
      return {
        name: supervisor.name,
        percent:
          ((100 * approved) / assignmentsForSupervisor.length).toFixed(0) + '%'
      };
    });

    setApprovalStats(stats);
  };

  useEffect(() => {
    if (csrf && currentMembers.length) loadApprovalStats();
  }, [csrf, currentMembers]);

  const getSecondaryLabel = useCallback(
    periodId => {
      if (mode === 'self') {
        if (
          selectReviewPeriod(state, periodId)?.reviewStatus ===
          ReviewStatus.OPEN
        ) {
          if (!selfReviews[periodId] || selfReviews[periodId] === null) {
            return 'Click to start your review.';
          } else {
            if (selfReviews[periodId].status.toUpperCase() === 'SUBMITTED') {
              return 'Your review has been submitted. Thank you!';
            } else {
              return 'Click to finish your review.';
            }
          }
        } else {
          return 'This review period is closed.';
        }
      }
    },
    [mode, selfReviews, state]
  );

  return (
    <Card className="review-period-card" key={`period-${periodId}`}>
      <div className="row top-row">
        <ListItemAvatar
          key={`period-lia-${periodId}`}
          onClick={() => onSelect(periodId)}
        >
          <Avatar>{reviewStatusIconMap[reviewStatus]}</Avatar>
        </ListItemAvatar>
        <ListItemText
          key={`period-lit-${periodId}`}
          onClick={() => onSelect(periodId)}
          primary={`${name} - ${titleCase(ReviewStatus[reviewStatus])}`}
          secondary={getSecondaryLabel(periodId)}
        />
        {showPercentages && (
          <div className="row">
            <ListItemText
              key={`period-percent-${periodId}`}
              primary={overallApprovalPercentage.toFixed(0) + '%'}
            />
            <CardActions disableSpacing>
              <ExpandMore
                expand={expanded}
                onClick={handleExpandClick}
                aria-expanded={expanded}
                aria-label={expanded ? 'show less' : 'show more'}
                size="large"
              />
            </CardActions>
          </div>
        )}
      </div>
      {showPercentages && (
        <Collapse
          className="bottom-row"
          in={expanded}
          timeout="auto"
          unmountOnExit
        >
          {approvalStats.map(stats => (
            <div key={stats.name}>
              {stats.name} - {stats.percent}
            </div>
          ))}
        </Collapse>
      )}
    </Card>
  );
};

ReviewPeriodCard.propTypes = propTypes;
ReviewPeriodCard.displayName = displayName;

export default ReviewPeriodCard;
