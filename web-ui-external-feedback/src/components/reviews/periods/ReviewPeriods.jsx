import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import {
  Box,
  Button,
  FormControl,
  InputLabel,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  MenuItem,
  Modal,
  Select,
  Skeleton,
  TextField,
  Typography
} from '@mui/material';

import { useQueryParameters } from '../../../helpers/query-parameters';
import { ADD_REVIEW_PERIOD, UPDATE_REVIEW_PERIODS, UPDATE_TOAST } from '../../../context/actions';

import { styled } from '@mui/material/styles';

import { findSelfReviewRequestsByPeriodAndTeamMember } from '../../../api/feedback.js';
import { getAllFeedbackTemplates } from '../../../api/feedbacktemplate.js';
import { createReviewPeriod, getReviewPeriods } from '../../../api/reviewperiods.js';
import { AppContext } from '../../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentUserId,
  selectHasCreateReviewPeriodPermission,
  selectReviewPeriod,
  selectReviewPeriods,
  selectUserProfile
} from '../../../context/selectors';

import ReviewPeriodCard from './ReviewPeriodCard.jsx';

const propTypes = {
  message: PropTypes.string,
  onSelect: PropTypes.func
};
const displayName = 'ReviewPeriods';

const PREFIX = displayName;
const classes = {
  actionButtons: `${PREFIX}-actionButtons`,
  headerContainer: `${PREFIX}-headerContainer`,
  periodModal: `${PREFIX}-periodModal`
};

const modalStyles = {
  position: 'absolute',
  minWidth: '400px',
  maxWidth: '600px',
  backgroundColor: 'background.paper',
  top: '50%',
  left: '50%',
  padding: '1rem',
  transform: 'translate(-50%, -50%)',
  border: '2px solid #fff',
  // flex-box related properties
  display: 'flex',
  flexDirection: 'column',
  gap: '1rem'
};

const modalActionStyles = {
  width: 'calc(100% - 1rem)',
  display: 'flex',
  flexDirection: 'row',
  justifyContent: 'flex-end'
};

const Root = styled('div')(({ theme }) => ({
  [`& .${classes.actionButtons}`]: {
    margin: '0.5em 0 0 1em',
    ['@media (max-width:820px)']: {
      // eslint-disable-line no-useless-computed-key
      padding: '0'
    }
  },
  [`& .${classes.headerContainer}`]: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    margin: '0 0 1em 0'
  }
}));

const ReviewStatus = {
  PLANNING: 'PLANNING',
  AWAITING_APPROVAL: 'AWAITING_APPROVAL',
  OPEN: 'OPEN',
  CLOSED: 'CLOSED',
  UNKNOWN: 'UNKNOWN'
};

// mode will be either "self" or undefined.
const selfReviewMode = "self";

const ReviewPeriods = ({ onPeriodSelected, mode }) => {
  const { state, dispatch } = useContext(AppContext);

  const [canSave, setCanSave] = useState(false);
  const [loading, setLoading] = useState(false);
  const [periods, setPeriods] = useState([]);
  const [periodToAdd, setPeriodToAdd] = useState({
    name: '',
    reviewStatus: ReviewStatus.PLANNING,
    launchDate: null,
    selfReviewCloseDate: null,
    closeDate: null,
    periodStartDate: null,
    periodEndDate: null
  });
  const [reviewStatus, setReviewStatus] = useState(ReviewStatus.CLOSED);
  const [selfReviews, setSelfReviews] = useState({});
  const [templates, setTemplates] = useState([]);

  const currentUserId = selectCurrentUserId(state);
  const csrf = selectCsrfToken(state);
  const userProfile = selectUserProfile(state);

  useEffect(() => {
    setPeriods(selectReviewPeriods(state)
                 .filter((r) => mode !== selfReviewMode ||
                                r.reviewStatus === ReviewStatus.OPEN ||
                                r.reviewStatus === ReviewStatus.CLOSED));
  }, [state, mode]);

  useQueryParameters([
    {
      name: 'add',
      default: false,
      value: reviewStatus,
      setter(open) {
        setReviewStatus(open ? ReviewStatus.OPEN : ReviewStatus.CLOSED);
      },
      toQP(reviewStatus) {
        return reviewStatus === ReviewStatus.OPEN;
      }
    }
  ]);

  const handleOpen = useCallback(
    () => setReviewStatus(ReviewStatus.OPEN),
    [setReviewStatus]
  );

  const handleClose = useCallback(
    () => setReviewStatus(ReviewStatus.CLOSED),
    [setReviewStatus]
  );

  const findPeriodByName = useCallback(
    name =>
      periods.find(period => period?.name.toUpperCase() === name.toUpperCase()),
    [periods]
  );

  const addReviewPeriod = useCallback(
    async name => {
      if (!csrf) return;

      const alreadyExists = findPeriodByName(periodToAdd?.name);
      if (!alreadyExists) {
        handleOpen();
        const res = await createReviewPeriod(periodToAdd, csrf);
        const data = res?.payload?.data ?? null;
        if (data) {
          dispatch({ type: ADD_REVIEW_PERIOD, payload: data });
        } else {
          console.error(res?.error);
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'error',
              toast: 'Error adding review period'
            }
          });
        }
      }
      handleClose();
      setPeriodToAdd({
        name: '',
        reviewStatus: ReviewStatus.OPEN,
        launchDate: null,
        selfReviewCloseDate: null,
        closeDate: null,
        periodStartDate: null,
        periodEndDate: null
      });
    },
    [
      csrf,
      dispatch,
      periodToAdd,
      handleOpen,
      handleClose,
      setPeriodToAdd,
      findPeriodByName
    ]
  );

  const loadFeedbackTemplates = useCallback(async () => {
    const res = await getAllFeedbackTemplates(csrf);
    const templates = res?.payload?.data;
    if (templates) {
      templates?.sort((t1, t2) => t1.title.localeCompare(t2.title));
      setTemplates(templates);
    } else {
      console.error(res?.error);
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Error fetching feedback templates'
        }
      });
    }
  }, [csrf, dispatch]);

  useEffect(() => {
    const valid = Boolean(
      periodToAdd.name &&
        periodToAdd.reviewStatus &&
        periodToAdd.reviewTemplateId &&
        periodToAdd.selfReviewTemplateId
    );
    setCanSave(valid);
  }, [periodToAdd]);

  useEffect(() => {
    loadFeedbackTemplates();

    const getAllReviewPeriods = async () => {
      setLoading(true);
      const res = await getReviewPeriods(csrf);
      const data =
        res?.payload?.data && res.payload.status === 200 && !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: data });
        setLoading(false);
      } else {
        console.error(res?.error);
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Error fetching review periods'
          }
        });
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch, setLoading]);

  useEffect(() => {
    const getSelfReviews = async () => {
      setLoading(true);
      let reviews = {};
      Promise.all(
        periods.map(async period => {
          const res = await findSelfReviewRequestsByPeriodAndTeamMember(
            period,
            currentUserId,
            csrf
          );
          const data =
            res?.payload?.data && res?.payload?.status === 200 && !res?.error
              ? res.payload.data
              : null;
          if (data) {
            reviews[period.id] = data[0];
          } else {
            console.error(res?.error);
            window.snackDispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'error',
                toast: 'Error finding review request'
              }
            });
          }
        })
      ).then(() => {
               // Now that we have the reviews loaded, filter out closed
               // self-review periods in which the current user is not involved.
               if (mode == selfReviewMode) {
                 setPeriods(periods.filter((r) =>
                              r.reviewStatus !== ReviewStatus.CLOSED ||
                              !!reviews[r.id]));
               }
               setSelfReviews(reviews);
               setLoading(false);
             });
    };
    if (
      csrf &&
      periods &&
      periods.length > 0 &&
      currentUserId &&
      Object.keys(selfReviews).length === 0
    ) {
      getSelfReviews();
    }
  }, [csrf, periods, currentUserId, selfReviews ]);

  const onPeriodClick = useCallback(
    id => {
      const status = selectReviewPeriod(state, id)?.reviewStatus;
      switch (status) {
        case ReviewStatus.PLANNING:
        case ReviewStatus.AWAITING_APPROVAL:
          if (mode !== selfReviewMode) {
            onPeriodSelected(id);
          }
          break;
        case ReviewStatus.OPEN:
          onPeriodSelected(id);
          break;
        default:
          // We do nothing if the status is CLOSED or UNKNOWN.
          break;
      }
    },
    [state, onPeriodSelected]
  );

  const handleReviewTemplateChange = event => {
    const templateId = event.target.value;
    setPeriodToAdd({
      ...periodToAdd,
      reviewTemplateId: templateId
    });
  };

  const handleSelfReviewTemplateChange = event => {
    const templateId = event.target.value;
    setPeriodToAdd({
      ...periodToAdd,
      selfReviewTemplateId: templateId
    });
  };

  return (
    <Root>
      <div className={classes.headerContainer}>
        <Typography variant="h4">Review Periods</Typography>
        {selectHasCreateReviewPeriodPermission(state) && (
          <Button
            onClick={handleOpen}
            className={classes.actionButtons}
            variant="contained"
            color="primary"
          >
            Add Review Period
          </Button>
        )}
      </div>
      <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
        {loading ? (
          <>
            <ListItem key="skeleton-period">
              <ListItemAvatar>
                <Skeleton
                  animation="wave"
                  variant="circular"
                  width={40}
                  height={40}
                />
              </ListItemAvatar>
              <ListItemText
                primary={<Skeleton variant="text" sx={{ fontSize: '1rem' }} />}
                secondary={
                  <Skeleton variant="text" sx={{ fontSize: '1rem' }} />
                }
              />
            </ListItem>
          </>
        ) : periods.length > 0 ? (
          periods
            .sort((a, b) => {
              const aName = (a.name || '');
              return a.reviewStatus === b.reviewStatus
                ? aName.localeCompare(b.name)
                : a.reviewStatus === ReviewStatus.OPEN
                  ? -1
                  : (b.reviewStatus === ReviewStatus.OPEN
                    ? 1
                    : aName.localeCompare(b.name));
            })
            .map((period) => (
              <div>
                <ReviewPeriodCard
                  key={`review-period-card-${period.id}`}
                  mode={mode}
                  onSelect={onPeriodClick}
                  periodId={period.id}
                  selfReviews={selfReviews}
                />
              </div>
            ))
        ) : (
          <Typography variant="body1">
            There are currently no review periods.
          </Typography>
        )}
      </List>
      <Modal open={reviewStatus === ReviewStatus.OPEN} onClose={handleClose}>
        <Box sx={modalStyles}>
          <TextField
            className="fullWidth"
            id="reviewPeriod-name-input"
            label="Name"
            placeholder="Period Name"
            required
            value={periodToAdd.name ? periodToAdd.name : ''}
            onChange={e =>
              setPeriodToAdd({ ...periodToAdd, name: e.target.value })
            }
          />
          <div style={{ margin: '1rem 0' }}>
            <FormControl fullWidth>
              <InputLabel id="template-label" required>
                Review Template
              </InputLabel>
              <Select
                label="Review Template"
                labelId="template-label"
                onChange={handleReviewTemplateChange}
                required
                value={
                  periodToAdd.reviewTemplateId
                    ? periodToAdd.reviewTemplateId
                    : ''
                }
              >
                <MenuItem key="empty" value="">
                  -- Please Select --
                </MenuItem>
                {templates.map(template => (
                  <MenuItem key={`template-${template.id}`} value={template.id}>
                    {template.title}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </div>
          <FormControl fullWidth>
            <InputLabel id="template-label" required>
              Self-Review Template
            </InputLabel>
            <Select
              label="Self-Review Template"
              labelId="template-label"
              onChange={handleSelfReviewTemplateChange}
              required
              value={
                periodToAdd.selfReviewTemplateId
                  ? periodToAdd.selfReviewTemplateId
                  : ''
              }
            >
              <MenuItem key="empty" value="">
                -- Please Select --
              </MenuItem>
              {templates.map(template => (
                <MenuItem key={`template-${template.id}`} value={template.id}>
                  {template.title}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <div className="fullWidth" style={modalActionStyles}>
            <Button onClick={handleClose} color="secondary">
              Cancel
            </Button>
            <Button
              disabled={!canSave}
              onClick={() => addReviewPeriod(periodToAdd.name)}
              color="primary"
            >
              Save Review Period
            </Button>
          </div>
        </Box>
      </Modal>
    </Root>
  );
};

ReviewPeriods.propTypes = propTypes;
ReviewPeriods.displayName = displayName;

export default ReviewPeriods;
