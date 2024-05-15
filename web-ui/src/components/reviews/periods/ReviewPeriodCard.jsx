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
  CardContent,
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
  selectReviewPeriods
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
  const [expanded, setExpanded] = useState(false);

  const csrf = selectCsrfToken(state);
  const currentMembers = selectCurrentMembers(state);
  const periods = selectReviewPeriods(state);

  const period = periods.find(period => period.id === periodId);
  const { name, reviewStatus } = period;

  const handleExpandClick = () => setExpanded(!expanded);

  const getApprovalPercentages = async periodId => {
    try {
      const res = await resolve({
        method: 'GET',
        url: `/services/review-assignments/period/${periodId}`,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      if (res.error) throw new Error(res.error.message);
      const assignments = res.payload.data;
      const reviewerIds = new Set();
      for (const assignment of assignments) {
        reviewerIds.add(assignment.reviewerId);
      }
      const reviewers = [...reviewerIds].map(id =>
        currentMembers.find(m => m.id === id)
      );
      reviewers.sort((a, b) => a.name.localeCompare(b.name));
      const data = reviewers.map(reviewer => {
        const { id } = reviewer;
        const assignmentsForReviewer = assignments.filter(
          assignment => assignment.reviewerId === id
        );
        const approved = assignmentsForReviewer.filter(
          assignment => assignment.approved
        ).length;
        return {
          name: reviewer.name,
          percent:
            ((100 * approved) / assignmentsForReviewer.length).toFixed(0) + '%'
        };
      });
      console.log('ReviewPeriods.jsx getApprovalPercentages: data =', data);
    } catch (err) {
      console.error('ReviewPeriods.jsx getApprovalPercentages:', err);
    }
  };

  useEffect(() => {
    if (!csrf || currentMembers.length === 0) return;

    for (const period of periods) {
      getApprovalPercentages(period.id);
    }
  }, [csrf, currentMembers, periods]);

  const getSecondaryLabel = useCallback(
    periodId => {
      if (mode === 'self') {
        if (
          selectReviewPeriod(state, periodId)?.reviewStatus ===
          ReviewStatus.OPEN
        ) {
          if (
            !selfReviews ||
            !selfReviews[periodId] ||
            selfReviews[periodId] === null
          ) {
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

      <CardActions disableSpacing>
        <ExpandMore
          expand={expanded}
          onClick={handleExpandClick}
          aria-expanded={expanded}
          aria-label={expanded ? 'show less' : 'show more'}
          size="large"
        />
      </CardActions>
      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent style={{ padding: 0 }}>
          Reviewer percentages go here!
        </CardContent>
      </Collapse>
    </Card>
  );
};

ReviewPeriodCard.propTypes = propTypes;
ReviewPeriodCard.displayName = displayName;

export default ReviewPeriodCard;
