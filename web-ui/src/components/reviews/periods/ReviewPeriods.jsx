import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import {
  Archive,
  BorderColor,
  Delete,
  DoorFront,
  HourglassTop,
  MeetingRoom,
  QuestionMark,
  Unarchive
} from '@mui/icons-material';

import {
  Avatar,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  IconButton,
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
  Tooltip,
  Typography
} from '@mui/material';

import { useQueryParameters } from '../../../helpers/query-parameters';
import { UPDATE_TOAST } from '../../../context/actions';

import { styled } from '@mui/material/styles';
import './DatePickerField.css';

import { findSelfReviewRequestsByPeriodAndTeamMember } from '../../../api/feedback.js';
import { getAllFeedbackTemplates } from '../../../api/feedbacktemplate.js';
import {
  createReviewPeriod,
  getReviewPeriods,
  removeReviewPeriod,
  updateReviewPeriod
} from '../../../api/reviewperiods.js';
import { AppContext } from '../../../context/AppContext';
import {
  ADD_REVIEW_PERIOD,
  UPDATE_REVIEW_PERIODS
} from '../../../context/actions';
import {
  selectCsrfToken,
  selectCurrentUserId,
  selectReviewPeriod,
  selectReviewPeriods,
  selectUserProfile
} from '../../../context/selectors';
import DatePickerField from './DatePickerField.jsx';

import { titleCase } from '../../../helpers/strings.js';

const propTypes = {
  message: PropTypes.string,
  onSelect: PropTypes.func
};
const displayName = 'ReviewPeriods';
const feedbackTemplateUrl = '/services/feedback/templates';

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

const reviewStatusIconMap = {
  [ReviewStatus.PLANNING]: <BorderColor />,
  [ReviewStatus.AWAITING_APPROVAL]: <HourglassTop />,
  [ReviewStatus.OPEN]: <MeetingRoom />,
  [ReviewStatus.CLOSED]: <DoorFront />,
  [ReviewStatus.UNKNOWN]: <QuestionMark />
};

const ReviewPeriods = ({ onPeriodSelected, mode }) => {
  const { state, dispatch } = useContext(AppContext);

  const [canSave, setCanSave] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const [reviewStatus, setReviewStatus] = useState(ReviewStatus.CLOSED);
  const [periodToAdd, setPeriodToAdd] = useState({
    name: '',
    reviewStatus: ReviewStatus.OPEN,
    launchDate: null,
    selfReviewCloseDate: null,
    closeDate: null
  });
  const [selfReviews, setSelfReviews] = useState(null);
  const [templates, setTemplates] = useState([]);
  const [toDelete, setToDelete] = useState(null);

  const currentUserId = selectCurrentUserId(state);
  const csrf = selectCsrfToken(state);
  const periods = selectReviewPeriods(state);
  const userProfile = selectUserProfile(state);
  const isAdmin = userProfile?.role?.includes('ADMIN');

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
      if (!csrf) {
        return;
      }
      const alreadyExists = findPeriodByName(periodToAdd?.name);
      if (!alreadyExists) {
        handleOpen();
        const res = await createReviewPeriod(periodToAdd, csrf);
        const data = res?.payload?.data ?? null;
        if (data) {
          dispatch({ type: ADD_REVIEW_PERIOD, payload: data });
        } else {
          console.log(res?.error);
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
        closeDate: null
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

  const toggleReviewPeriod = useCallback(
    async id => {
      if (!csrf) {
        return;
      }
      const toUpdate = selectReviewPeriod(state, id);
      toUpdate.reviewStatus =
        toUpdate?.reviewStatus === ReviewStatus.CLOSED
          ? ReviewStatus.OPEN
          : ReviewStatus.CLOSED;
      const res = await updateReviewPeriod(toUpdate, csrf);
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: UPDATE_REVIEW_PERIODS, payload: [...periods] });
    },
    [csrf, state, periods, dispatch]
  );

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
    [selfReviews, state, mode]
  );

  const handleConfirmClose = useCallback(() => {
    setToDelete(null);
    setConfirmOpen(false);
  }, [setToDelete, setConfirmOpen]);

  const deleteReviewPeriod = useCallback(async () => {
    if (!csrf) {
      return;
    }

    await removeReviewPeriod(toDelete, csrf);
    dispatch({
      type: UPDATE_REVIEW_PERIODS,
      payload: periods.filter(period => period?.id !== toDelete)
    });
    handleConfirmClose();
  }, [csrf, periods, dispatch, toDelete, handleConfirmClose]);

  const updateReviewPeriodDates = useCallback(
    async period => {
      if (!csrf) {
        return;
      }
      const res = await updateReviewPeriod(period, csrf);
      const data = res?.payload?.data ?? null;
      if (data) {
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: [...periods] });
      } else {
        console.log(res?.error);
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Error updating review period'
          }
        });
      }
      setPeriodToAdd(period);
    },
    [csrf, state, periods, dispatch]
  );

  const loadFeedbackTemplates = useCallback(async () => {
    const res = await getAllFeedbackTemplates(csrf);
    const templates = res.payload.data;
    templates?.sort((t1, t2) => t1.title.localeCompare(t2.title));
    setTemplates(templates);
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
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: data });
        setLoading(false);
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch, setLoading]);

  useEffect(() => {
    const getSelfReviews = async () => {
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
          }
        })
      ).then(() => setSelfReviews(reviews));
    };
    if (
      csrf &&
      periods &&
      periods.length > 0 &&
      currentUserId &&
      selfReviews == null
    ) {
      getSelfReviews();
    }
  }, [csrf, periods, currentUserId, selfReviews]);

  const onPeriodClick = useCallback(
    id => {
      if (selectReviewPeriod(state, id)?.reviewStatus === ReviewStatus.OPEN) {
        onPeriodSelected(id);
      }
    },
    [state, onPeriodSelected]
  );

  const confirmDelete = useCallback(
    id => {
      setToDelete(id);
      setConfirmOpen(true);
    },
    [setToDelete, setConfirmOpen]
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

  const handleLaunchDateChange = (val, period) => {
    const isoDate = val?.$d.toISOString() ?? null;
    updateReviewPeriodDates({
      ...period,
      launchDate: isoDate
    });
  };

  const handleSelfReviewDateChange = (val, period) => {
    const isoDate = val?.$d.toISOString() ?? null;
    updateReviewPeriodDates({
      ...period,
      selfReviewCloseDate: isoDate
    });
  };

  const handleCloseDateChange = (val, period) => {
    const isoDate = val?.$d.toISOString() ?? null;
    updateReviewPeriodDates({
      ...period,
      closeDate: isoDate
    });
  };

  return (
    <Root>
      <div className={classes.headerContainer}>
        <Typography variant="h4">Review Periods</Typography>
        {isAdmin ? (
          <Button
            onClick={handleOpen}
            className={classes.actionButtons}
            variant="contained"
            color="primary"
          >
            Add Review Period
          </Button>
        ) : null}
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
              return a.reviewStatus === b.reviewStatus
                ? ('' + a.name).localeCompare(b.name)
                : a.reviewStatus === ReviewStatus.OPEN
                  ? -1
                  : 1;
            })
            .map(
              (
                {
                  name,
                  reviewStatus,
                  id,
                  launchDate,
                  selfReviewCloseDate,
                  closeDate
                },
                i
              ) => (
                <div key={i} className="reviewPeriodSection">
                  <ListItem
                    secondaryAction={
                      isAdmin && (
                        <>
                          <Tooltip
                            title={
                              reviewStatus === ReviewStatus.OPEN
                                ? 'Archive'
                                : 'Unarchive'
                            }
                          >
                            <IconButton
                              onClick={() => toggleReviewPeriod(id)}
                              aria-label={
                                reviewStatus === ReviewStatus.OPEN
                                  ? 'Archive'
                                  : 'Unarchive'
                              }
                            >
                              {reviewStatus === ReviewStatus.OPEN ? (
                                <Archive />
                              ) : (
                                <Unarchive />
                              )}
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Delete">
                            <IconButton
                              onClick={() => confirmDelete(id)}
                              edge="end"
                              aria-label="Delete"
                            >
                              <Delete />
                            </IconButton>
                          </Tooltip>
                        </>
                      )
                    }
                    key={`period-${id}`}
                  >
                    <ListItemAvatar
                      key={`period-lia-${id}`}
                      onClick={() => onPeriodClick(id)}
                    >
                      <Avatar>{reviewStatusIconMap[reviewStatus]}</Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      key={`period-lit-${id}`}
                      onClick={() => onPeriodClick(id)}
                      primary={`${name} - ${titleCase(reviewStatus)}`}
                      secondary={getSecondaryLabel(id)}
                    />
                    <div className="datePickerFlexWrapper">
                      <DatePickerField
                        date={launchDate}
                        setDate={val =>
                          handleLaunchDateChange(val, {
                            id,
                            name,
                            reviewStatus,
                            launchDate,
                            selfReviewCloseDate,
                            closeDate
                          })
                        }
                        label="Launch Date"
                        disabled={!isAdmin}
                        open={reviewStatus === ReviewStatus.PLANNING}
                      />
                      <DatePickerField
                        date={selfReviewCloseDate}
                        setDate={val =>
                          handleSelfReviewDateChange(val, {
                            id,
                            name,
                            reviewStatus,
                            launchDate,
                            selfReviewCloseDate,
                            closeDate
                          })
                        }
                        label="Self-Review Date"
                        disabled={!isAdmin}
                      />
                      <DatePickerField
                        date={closeDate}
                        setDate={val =>
                          handleCloseDateChange(val, {
                            id,
                            name,
                            reviewStatus,
                            launchDate,
                            selfReviewCloseDate,
                            closeDate
                          })
                        }
                        label="Close Date"
                        disabled={!isAdmin}
                      />
                    </div>
                  </ListItem>
                </div>
              )
            )
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
      <Dialog
        open={confirmOpen}
        onClose={handleConfirmClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {'Delete this review period?'}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure that you would like to delete period{' '}
            {selectReviewPeriod(state, toDelete)?.name}?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleConfirmClose}>No</Button>
          <Button onClick={deleteReviewPeriod} autoFocus>
            Yes
          </Button>
        </DialogActions>
      </Dialog>
    </Root>
  );
};

ReviewPeriods.propTypes = propTypes;
ReviewPeriods.displayName = displayName;

export default ReviewPeriods;
