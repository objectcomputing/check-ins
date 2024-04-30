import React, { useCallback, useContext, useEffect, useState } from 'react';
import { styled } from '@mui/styles';
import FeedbackRequestSubcard from './feedback_request_subcard/FeedbackRequestSubcard';
import Card from '@mui/material/Card';
import { Avatar, Typography } from '@mui/material';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';
import Collapse from '@mui/material/Collapse';
import Grid from '@mui/material/Grid';
import { useHistory } from 'react-router-dom';
import PropTypes from 'prop-types';
import './FeedbackRequestCard.css';
import { selectProfile } from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api.js';
import Divider from '@mui/material/Divider';
import Button from '@mui/material/Button';
import queryString from 'query-string';

import ExpandMore from '../expand-more/ExpandMore';

const PREFIX = 'FeedbackRequestCard';
const classes = {
  root: `${PREFIX}-root`,
  expandClose: `${PREFIX}-expandClose`,
  expandOpen: `${PREFIX}-expandOpen`
};

const StyledCard = styled(Card)({
  [`&.${classes.root}`]: {
    color: 'gray',
    width: '100%',
    maxHeight: '10%',
    '@media (max-width:769px)': {
      width: '100%',
      maxWidth: '100%'
    }
  },
  '& .MuiCardContent-root': {
    paddingBottom: 0,
    paddingTop: 0,
    '&:last-child': {
      paddingBottom: 0
    }
  },
  '& .MuiCardActions-root': {
    padding: 0,
    maxHeight: '30px'
  },
  '& .MuiTypography-body1': {
    '@media (max-width:767px)': {
      fontSize: '0.7rem'
    }
  }
});

const SortOption = {
  SENT_DATE: 'sent_date',
  SUBMISSION_DATE: 'submission_date',
  RECIPIENT_NAME_ALPHABETICAL: 'recipient_name_alphabetical',
  RECIPIENT_NAME_REVERSE_ALPHABETICAL: 'recipient_name_reverse_alphabetical'
};

const DateRange = {
  THREE_MONTHS: '3mo',
  SIX_MONTHS: '6mo',
  ONE_YEAR: '1yr',
  ALL_TIME: 'all'
};

const propTypes = {
  requesteeId: PropTypes.string.isRequired,
  templateName: PropTypes.string.isRequired,
  responses: PropTypes.arrayOf(PropTypes.object).isRequired,
  sortType: PropTypes.oneOf([
    SortOption.SENT_DATE,
    SortOption.SUBMISSION_DATE,
    SortOption.RECIPIENT_NAME_ALPHABETICAL,
    SortOption.RECIPIENT_NAME_REVERSE_ALPHABETICAL
  ]).isRequired,
  dateRange: PropTypes.oneOf([
    DateRange.THREE_MONTHS,
    DateRange.SIX_MONTHS,
    DateRange.ONE_YEAR,
    DateRange.ALL_TIME
  ]).isRequired
};

const FeedbackRequestCard = ({
  requesteeId,
  templateName,
  responses,
  sortType,
  dateRange
}) => {
  const { state } = useContext(AppContext);
  const requesteeProfile = selectProfile(state, requesteeId);
  const avatarURL = getAvatarURL(requesteeProfile?.workEmail);
  const history = useHistory();
  const [expanded, setExpanded] = useState(false);
  const [sortedResponses, setSortedResponses] = useState(responses);

  const handleExpandClick = () => setExpanded(!expanded);

  const withinDateRange = useCallback(
    requestDate => {
      let oldestDate = new Date();
      switch (dateRange) {
        case DateRange.THREE_MONTHS:
          oldestDate.setMonth(oldestDate.getMonth() - 3);
          break;
        case DateRange.SIX_MONTHS:
          oldestDate.setMonth(oldestDate.getMonth() - 6);
          break;
        case DateRange.ONE_YEAR:
          oldestDate.setFullYear(oldestDate.getFullYear() - 1);
          break;
        case DateRange.ALL_TIME:
          return true;
        default:
          oldestDate.setMonth(oldestDate.getMonth() - 3);
      }

      if (Array.isArray(requestDate)) {
        requestDate = new Date(requestDate.join('/'));
        // have to do for Safari
      }
      return requestDate >= oldestDate;
    },
    [dateRange]
  );

  const noRequestsMessage = useCallback(() => {
    let message;
    switch (dateRange) {
      case DateRange.THREE_MONTHS:
        message = 'No requests in the past 3 months';
        break;
      case DateRange.SIX_MONTHS:
        message = 'No requests in the past 6 months';
        break;
      case DateRange.ONE_YEAR:
        message = 'No requests in the past year';
        break;
      default:
        message = 'No requests';
    }

    return (
      <React.Fragment>
        <Divider />
        <div
          style={{
            padding: '12px 12px',
            textAlign: 'center',
            backgroundColor: '#ececec'
          }}
        >
          <Typography variant="body1">{message}</Typography>
        </div>
      </React.Fragment>
    );
  }, [dateRange]);

  const handleViewAllResponsesClick = () => {
    if (sortedResponses.length === 0) return;
    const requestIds = sortedResponses.map(response => response.id);
    const params = {
      request: requestIds
    };

    history.push(`/feedback/view/responses/?${queryString.stringify(params)}`);
  };

  // Sort the responses by either the send date or the submit date
  useEffect(() => {
    let responsesCopy = [...responses];
    responsesCopy = responsesCopy.filter(response =>
      withinDateRange(response.sendDate)
    );

    let sortMethod;
    switch (sortType) {
      case SortOption.SENT_DATE:
        sortMethod = (a, b) =>
          new Date(a.sendDate) > new Date(b.sendDate) ? -1 : 1;
        break;
      case SortOption.SUBMISSION_DATE:
        sortMethod = (a, b) =>
          !a.submitDate || new Date(a.submitDate) > new Date(b.submitDate)
            ? -1
            : 1;
        break;
      case SortOption.RECIPIENT_NAME_ALPHABETICAL:
        sortMethod = (a, b) =>
          selectProfile(state, a.recipientId).name >
          selectProfile(state, b.recipientId).name
            ? 1
            : -1;
        break;
      case SortOption.RECIPIENT_NAME_REVERSE_ALPHABETICAL:
        sortMethod = (a, b) =>
          selectProfile(state, a.recipientId).name >
          selectProfile(state, b.recipientId).name
            ? -1
            : 1;
        break;
      default:
        sortMethod = (a, b) =>
          new Date(a.sendDate) > new Date(b.sendDate) ? -1 : 1;
        break;
    }
    responsesCopy.sort(sortMethod);
    setSortedResponses(responsesCopy);
  }, [state, sortType, dateRange, responses, withinDateRange]);

  return (
    <div className="feedback-request-card">
      <StyledCard className={classes.root}>
        <div className="has-padding-top">
          <CardContent className={classes.noBottomPadding}>
            <Grid container spacing={0}>
              <Grid item xs={12}>
                <Grid
                  container
                  direction="row"
                  alignItems="center"
                  className="no-wrap"
                >
                  <Grid item>
                    <Avatar style={{ marginRight: '1em' }} src={avatarURL} />
                  </Grid>
                  <Grid item xs className="small-margin">
                    <Typography className="person-name">
                      {requesteeProfile?.name}
                    </Typography>
                    <Typography className="position-text">
                      {requesteeProfile?.title}
                    </Typography>
                  </Grid>
                  <Grid item xs={4} className="align-end">
                    <Typography className="dark-gray-text">
                      {templateName}
                    </Typography>
                    <Button
                      className="response-link red-text"
                      onClick={handleViewAllResponsesClick}
                      disabled={
                        sortedResponses.length === 0 ||
                        sortedResponses.every(o => o.status === 'pending')
                      }
                    >
                      View all responses
                    </Button>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </CardContent>
        </div>
        <CardActions disableSpacing>
          <ExpandMore
            expand={expanded}
            onClick={handleExpandClick}
            aria-expanded={expanded}
            aria-label="show more"
            size="large"
          />
        </CardActions>
        <Collapse in={expanded} timeout="auto" unmountOnExit>
          <CardContent style={{ padding: 0 }}>
            {sortedResponses && sortedResponses.length
              ? sortedResponses.map(response => {
                  return (
                    <FeedbackRequestSubcard
                      key={response.id}
                      request={response}
                    />
                  );
                })
              : noRequestsMessage()}
          </CardContent>
        </Collapse>
      </StyledCard>
    </div>
  );
};

FeedbackRequestCard.propTypes = propTypes;

export default FeedbackRequestCard;
