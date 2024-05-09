import DateFnsUtils from '@date-io/date-fns';
const dateUtils = new DateFnsUtils();

import PropTypes from 'prop-types';
import queryString from 'query-string';
import React, {
  useEffect,
  useContext,
  useCallback,
  useState,
  useRef
} from 'react';
import { useLocation, useHistory } from 'react-router-dom';

import {
  AddCircle,
  AddComment,
  Archive,
  Delete,
  ExpandMore,
  Unarchive
} from '@mui/icons-material';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Divider,
  FormControlLabel,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemSecondaryAction,
  ListItemText,
  Skeleton,
  Switch,
  Tooltip,
  Typography
} from '@mui/material';
import { styled } from '@mui/material/styles';

import { resolve } from '../../api/api.js';
import { getAvatarURL } from '../../api/api.js';
import {
  createFeedbackRequest,
  findReviewRequestsByPeriodAndTeamMembers,
  findSelfReviewRequestsByPeriodAndTeamMembers
} from '../../api/feedback.js';
import {
  getReviewPeriods,
  removeReviewPeriod,
  updateReviewPeriod
} from '../../api/reviewperiods.js';
import {
  DELETE_REVIEW_PERIOD,
  UPDATE_REVIEW_PERIOD,
  UPDATE_REVIEW_PERIODS,
  UPDATE_TOAST
} from '../../context/actions';
import { AppContext } from '../../context/AppContext';
import {
  selectCsrfToken,
  selectReviewPeriod,
  selectProfile,
  selectCurrentUser,
  selectIsAdmin,
  selectMyTeam,
  selectCurrentMembers,
  selectSubordinates
} from '../../context/selectors';

import MemberSelector from '../member_selector/MemberSelector';
import MemberSelectorDialog, {
  FilterType
} from '../member_selector/member_selector_dialog/MemberSelectorDialog';
import SelectUserModal from './SelectUserModal';
import TeamMemberReview from './TeamMemberReview';

import DatePickerField from './periods/DatePickerField.jsx';
import './periods/DatePickerField.css';

const propTypes = {
  teamMembers: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string,
      firstName: PropTypes.string,
      lastName: PropTypes.string
    })
  ),
  periodId: PropTypes.string
};
const displayName = 'TeamReviews';

const PREFIX = displayName;
const classes = {
  actionButtons: `${PREFIX}-actionButtons`,
  headerContainer: `${PREFIX}-headerContainer`,
  periodModal: `${PREFIX}-periodModal`
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
    margin: '0 0 1em 0',
    ['@media (max-width:800px)']: {
      // eslint-disable-line no-useless-computed-key
      margin: '0',
      justifyContent: 'center'
    }
  }
}));

const ReviewStatus = {
  PLANNING: 'PLANNING',
  AWAITING_APPROVAL: 'AWAITING_APPROVAL',
  OPEN: 'OPEN',
  CLOSED: 'CLOSED',
  UNKNOWN: 'UNKNOWN'
};

const TeamReviews = ({ periodId }) => {
  const { state, dispatch } = useContext(AppContext);
  const history = useHistory();
  const location = useLocation();

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [includeAll, setIncludeAll] = useState(false);
  const [memberFilters, setMemberFilters] = useState([]);
  const [memberSelectorOpen, setMemberSelectorOpen] = useState(false);
  const [newRequestOpen, setNewRequestOpen] = useState(false);
  const [query, setQuery] = useState({});
  const [reviews, setReviews] = useState(null);
  const [selectedMember, setSelectedMember] = useState(null);
  const [selfReviews, setSelfReviews] = useState({});
  const [teamMembers, setTeamMembers] = useState([]);
  const [toDelete, setToDelete] = useState(null);

  const creatingReview = useRef(false);
  const loadedReviews = useRef(false);
  const loadingReviews = useRef(false);

  const csrf = selectCsrfToken(state);
  const currentMembers = selectCurrentMembers(state);
  const currentUser = selectCurrentUser(state);
  const isAdmin = selectIsAdmin(state);
  const myTeam = selectMyTeam(state);
  const period = selectReviewPeriod(state, periodId);
  console.log('TeamReviews.jsx : period =', period);
  const selectedMemberProfile = selectProfile(state, selectedMember);
  const subordinates = selectSubordinates(state, currentUser?.id);

  const handleOpenNewRequest = useCallback(
    () => setNewRequestOpen(true),
    [setNewRequestOpen]
  );
  const handleCloseNewRequest = useCallback(
    () => setNewRequestOpen(false),
    [setNewRequestOpen]
  );

  const reviewAssignmentsUrl = '/services/review-assignments';

  useEffect(() => {
    loadTeamMembers();
  }, [currentMembers]);

  const loadTeamMembers = async () => {
    const myId = currentUser?.id;
    try {
      const res = await resolve({
        method: 'GET',
        url: `${reviewAssignmentsUrl}/period/${periodId}`,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      if (res.error) throw new Error(res.error.message);
      const assignments = res.payload.data;
      const memberIds = assignments.map(a => a.revieweeId);
      const members = currentMembers.filter(m => memberIds.includes(m.id));
      setTeamMembers(members);
    } catch (err) {
      console.error('TeamReviews.jsx loadTeamMembers:', err);
    }
  };

  const updateTeamMembers = async teamMembers => {
    const data = teamMembers.map(tm => ({
      revieweeId: tm.id,
      reviewerId: tm.supervisorid,
      reviewPeriodId: periodId,
      approved: true
    }));

    try {
      const res = await resolve({
        method: 'POST',
        url: reviewAssignmentsUrl + '/' + periodId,
        data,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      setTeamMembers(teamMembers);
    } catch (err) {
      console.error('TeamReviews.jsx updateTeamMembers:', err);
    }
  };

  const getReviewStatus = useCallback(
    teamMemberId => {
      let reviewStates = { submitted: false, inProgress: false };
      if (reviews && reviews[teamMemberId]) {
        reviewStates = reviews[teamMemberId].reduce((states, review) => {
          switch (review?.status) {
            case 'submitted':
              states.submitted = true;
              break;
            case 'sent':
            case 'pending':
              states.inProgress = true;
              break;
            case 'cancelled':
            case 'canceled':
            default:
              break;
          }
          return states;
        }, reviewStates);
        if (reviewStates.inProgress) {
          if (reviews[teamMemberId]?.length > 1) {
            return 'Reviews in progress';
          }
          return 'Review in progress';
        } else if (reviewStates.submitted) {
          if (reviews[teamMemberId]?.length > 1) {
            return 'All reviews submitted';
          }
          return 'Review submitted';
        } else return 'No reviews started';
      } else {
        return 'No reviews started';
      }
    },
    [reviews]
  );

  const getSelfReviewStatus = useCallback(
    teamMemberId => {
      let status = 'Not started';
      switch (selfReviews[teamMemberId]?.status) {
        case 'submitted':
          status = 'Submitted';
          break;
        case 'sent':
        case 'pending':
          status = 'In progress';
          break;
        case 'cancelled':
        case 'canceled':
        default:
          break;
      }
      return status;
    },
    [selfReviews]
  );

  useEffect(() => {
    const params = queryString.parse(location?.search);
    setQuery(params);
  }, [location.search]);

  const hasTeamMember = useCallback(() => {
    return !!query.teamMember;
  }, [query.teamMember]);

  const getTeamMember = useCallback(() => {
    if (hasTeamMember()) return query.teamMember;
    else return null;
  }, [query.teamMember, hasTeamMember]);

  useEffect(() => {
    setSelectedMember(getTeamMember());
  }, [getTeamMember]);

  useEffect(() => {
    const getAllReviewPeriods = async () => {
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
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch]);

  useEffect(() => {
    const createReview = async () => {
      const res = await createFeedbackRequest(
        {
          creatorId: currentUser.id,
          requesteeId: selectedMemberProfile.id,
          recipientId: currentUser.id,
          templateId: period.reviewTemplateId,
          reviewPeriodId: period.id,
          sendDate: dateUtils.format(new Date(), 'yyyy-MM-dd'),
          status: 'pending'
        },
        csrf
      );
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 201 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        setReviews({ ...reviews, [selectedMemberProfile.id]: [data] });
      }
      creatingReview.current = false;
    };

    if (
      csrf &&
      selectedMemberProfile?.id &&
      reviews &&
      (!reviews[selectedMemberProfile.id] ||
        reviews[selectedMemberProfile.id].length === 0) &&
      currentUser?.id &&
      period &&
      !creatingReview.current &&
      loadedReviews.current
    ) {
      if (currentUser?.id === selectedMemberProfile?.supervisorid) {
        creatingReview.current = true;
        createReview();
      }
    }
  }, [csrf, reviews, currentUser, period, selectedMemberProfile]);

  const confirmDelete = useCallback(() => {
    setToDelete(period.id);
    setConfirmOpen(true);
  }, [period, setToDelete, setConfirmOpen]);

  const handleConfirmClose = useCallback(() => {
    setToDelete(null);
    setConfirmOpen(false);
  }, [setToDelete, setConfirmOpen]);

  const deleteReviewPeriod = useCallback(async () => {
    if (!csrf) return;

    await removeReviewPeriod(toDelete, csrf);
    dispatch({
      type: DELETE_REVIEW_PERIOD,
      payload: toDelete
    });
    handleConfirmClose();
  }, [csrf, dispatch, toDelete, handleConfirmClose]);

  const toggleReviewPeriod = useCallback(async () => {
    if (!csrf) return;

    period.reviewStatus =
      period?.reviewStatus === ReviewStatus.CLOSED
        ? ReviewStatus.OPEN
        : ReviewStatus.CLOSED;
    const res = await updateReviewPeriod(period, csrf);
    const data = res?.payload?.data ? res.payload.data : null;
    if (data) {
      dispatch({ type: UPDATE_REVIEW_PERIOD, payload: period });
    } else {
      console.error(res?.error);
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Error selecting review period'
        }
      });
    }
  }, [csrf, period, state, dispatch]);

  const updateReviewPeriodDates = useCallback(
    async period => {
      if (!csrf) return;

      const res = await updateReviewPeriod(period, csrf);
      const data = res?.payload?.data ?? null;
      if (data) {
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: [period] });
      } else {
        console.error('Error updating review period:', res?.error);
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Error updating review period'
          }
        });
      }
    },
    [csrf, dispatch, period, state]
  );

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

  const handleQueryChange = useCallback(
    (key, value) => {
      let newQuery = {
        ...query,
        [key]: value
      };
      history.push({ ...location, search: queryString.stringify(newQuery) });
    },
    [history, location, query]
  );

  const onTeamMemberSelected = useCallback(
    teamMemberId => {
      handleQueryChange('teamMember', teamMemberId);
    },
    [handleQueryChange]
  );

  const loadReviews = useCallback(async () => {
    let newSelfReviews = {};
    let newReviews = {};
    const getSelfReviewRequests = async teamMemberIdBatches => {
      for (const teamMemberIds of teamMemberIdBatches) {
        const res = await findSelfReviewRequestsByPeriodAndTeamMembers(
          period,
          teamMemberIds,
          csrf
        );
        let data =
          res &&
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data && data.length > 0) {
          data = data.filter(
            review => 'canceled'.toUpperCase() !== review?.status?.toUpperCase()
          );
          data.forEach(
            selfReview => (newSelfReviews[selfReview.requesteeId] = selfReview)
          );
        }
      }
    };

    const getReviewRequests = async teamMemberIdBatches => {
      for (const teamMemberIds of teamMemberIdBatches) {
        const res = await findReviewRequestsByPeriodAndTeamMembers(
          period,
          teamMemberIds,
          csrf
        );
        let data =
          res &&
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data && data.length > 0) {
          data = data.filter(
            review => 'canceled'.toUpperCase() !== review?.status?.toUpperCase()
          );
          data.forEach(review => {
            if (!newReviews[review.requesteeId]) {
              newReviews[review.requesteeId] = [];
            }
            newReviews[review.requesteeId].push(review);
          });
        }
      }
    };

    if (
      csrf &&
      teamMembers &&
      teamMembers.length > 0 &&
      period &&
      !loadingReviews.current
    ) {
      const batchSize = 50;
      const teamMemberIdBatches = teamMembers.reduce((batches, member) => {
        if (
          !batches.length ||
          batches[batches.length - 1].length === batchSize
        ) {
          batches.push([]);
        }
        if (member?.id) {
          batches[batches.length - 1].push(member.id);
        }
        return batches;
      }, []);
      loadingReviews.current = true;
      setSelfReviews({});
      setReviews(null);
      await getSelfReviewRequests(teamMemberIdBatches);
      await getReviewRequests(teamMemberIdBatches);
      loadingReviews.current = false;
      loadedReviews.current = true;
      setSelfReviews({ ...newSelfReviews });
      setReviews({ ...newReviews });
    }
  }, [csrf, period, teamMembers]);

  useEffect(() => {
    loadReviews();
  }, [loadReviews]);

  const reloadReviews = useCallback(() => {
    loadedReviews.current = false;
    loadReviews();
  }, [loadReviews]);

  const toggleIncludeAll = useCallback(() => {
    loadedReviews.current = false;
    setIncludeAll(!includeAll);
  }, [includeAll, setIncludeAll]);

  const handleNewRequest = useCallback(
    assignee => {
      const createNewRequest = async () => {
        if (!selectedMemberProfile?.supervisorid) {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'error',
              toast:
                'This team member does not have an assigned supervisor. Please assign one before creating a review.'
            }
          });
        } else {
          const res = await createFeedbackRequest(
            {
              creatorId: selectedMemberProfile?.supervisorid,
              requesteeId: selectedMemberProfile?.id,
              recipientId: assignee?.id,
              templateId: period.reviewTemplateId,
              reviewPeriodId: period.id,
              sendDate: dateUtils.format(new Date(), 'yyyy-MM-dd'),
              status: 'pending'
            },
            csrf
          );
          const data =
            res &&
            res.payload &&
            res.payload.data &&
            res.payload.status === 201 &&
            !res.error
              ? res.payload.data
              : null;
          if (data) {
            const newReviews = { ...reviews };
            newReviews[selectedMemberProfile?.id].push(data);
            setReviews(newReviews);
          } else {
            dispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'error',
                toast:
                  'An error has occurred while submitting the review request.'
              }
            });
          }
          return data;
        }
      };

      handleCloseNewRequest();
      if (csrf && selectedMemberProfile && period) {
        createNewRequest().then(res => {
          if (res) {
            window.snackDispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'success',
                toast: 'Review request sent!'
              }
            });
          }
        });
      }
    },
    [
      csrf,
      dispatch,
      handleCloseNewRequest,
      period,
      reviews,
      selectedMemberProfile
    ]
  );

  const createSecondary = teamMember =>
    getReviewStatus(teamMember?.id) +
    ', Self-review: ' +
    getSelfReviewStatus(teamMember?.id);

  return (
    <Root>
      <div className={classes.headerContainer}>
        <Typography variant="h4">{period?.name ?? ''} Team Reviews</Typography>
        {!selectedMember && (
          <FormControlLabel
            control={
              <Switch checked={includeAll} onChange={toggleIncludeAll} />
            }
            label="Show All"
          />
        )}
        {selectedMember && (
          <Button
            onClick={handleOpenNewRequest}
            className={classes.actionButtons}
            endIcon={<AddCircle />}
            variant="contained"
            color="primary"
          >
            Request Review
          </Button>
        )}
      </div>
      {period && (
        <>
          {isAdmin && (
            <div>
              <Tooltip
                title={
                  period.reviewStatus === ReviewStatus.OPEN
                    ? 'Archive'
                    : 'Unarchive'
                }
              >
                <IconButton
                  onClick={toggleReviewPeriod}
                  aria-label={
                    period.reviewStatus === ReviewStatus.OPEN
                      ? 'Archive'
                      : 'Unarchive'
                  }
                >
                  {period.reviewStatus === ReviewStatus.OPEN ? (
                    <Archive />
                  ) : (
                    <Unarchive />
                  )}
                </IconButton>
              </Tooltip>
              <Tooltip title="Delete">
                <IconButton
                  onClick={confirmDelete}
                  edge="end"
                  aria-label="Delete"
                >
                  <Delete />
                </IconButton>
              </Tooltip>
            </div>
          )}
          <div className="datePickerFlexWrapper">
            <DatePickerField
              date={period.launchDate}
              setDate={val => handleLaunchDateChange(val, period)}
              label="Launch Date"
              disabled={!isAdmin}
              open={period?.reviewStatus === ReviewStatus?.PLANNING}
            />
            <DatePickerField
              date={period.selfReviewCloseDate}
              setDate={val => handleSelfReviewDateChange(val, period)}
              label="Self-Review Date"
              disabled={!isAdmin}
            />
            <DatePickerField
              date={period.closeDate}
              setDate={val => handleCloseDateChange(val, period)}
              label="Close Date"
              disabled={!isAdmin}
            />
          </div>
        </>
      )}
      <MemberSelector
        className="team-skill-member-selector"
        onChange={updateTeamMembers}
        selected={teamMembers}
      />
      {!selectedMember && loadedReviews.current && (
        <>
          <Accordion style={{ marginTop: '1rem' }}>
            <AccordionSummary
              expandIcon={<ExpandMore />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography>Completed Reviews</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
                {teamMembers && teamMembers.length > 0
                  ? teamMembers
                      .sort((a, b) => {
                        return ('' + a?.lastName)
                          .toUpperCase()
                          .localeCompare(b?.lastName.toUpperCase());
                      })
                      .filter(teamMember => {
                        return (
                          reviews &&
                          reviews[teamMember.id] &&
                          reviews[teamMember.id].length !== 0 &&
                          reviews[teamMember.id]?.reduce(
                            (status, review) =>
                              status && review.status === 'submitted',
                            true
                          )
                        );
                      })
                      .map((teamMember, i) => (
                        <>
                          <ListItem
                            onClick={() => onTeamMemberSelected(teamMember?.id)}
                            key={`teamMember-${teamMember?.id}`}
                          >
                            <ListItemAvatar
                              key={`teamMember-lia-${teamMember?.id}`}
                            >
                              <Avatar
                                src={getAvatarURL(teamMember?.workEmail)}
                              />
                            </ListItemAvatar>
                            <ListItemText
                              key={`teamMember-lit-${teamMember?.id}`}
                              primary={
                                teamMember?.firstName +
                                ' ' +
                                teamMember?.lastName
                              }
                              secondary={createSecondary(teamMember)}
                            />
                            <ListItemSecondaryAction>
                              <Tooltip title="Request Feedback">
                                <IconButton>
                                  <AddComment
                                    onClick={e => {
                                      e.stopPropagation();
                                      history.push(
                                        `/feedback/request?for=${teamMember?.id}`
                                      );
                                    }}
                                  />
                                </IconButton>
                              </Tooltip>
                            </ListItemSecondaryAction>
                          </ListItem>
                          <Divider key={`divider-${teamMember?.id}`} />
                        </>
                      ))
                  : null}
              </List>
            </AccordionDetails>
          </Accordion>
        </>
      )}
      {!selectedMember && loadingReviews.current && (
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
              secondary={<Skeleton variant="text" sx={{ fontSize: '1rem' }} />}
            />
          </ListItem>
        </>
      )}
      {!!selectedMember && reviews && (
        <TeamMemberReview
          reloadReviews={reloadReviews}
          memberProfile={selectedMemberProfile}
          selfReview={selfReviews[selectedMember]}
          reviews={reviews[selectedMember]}
        />
      )}
      <SelectUserModal
        userLabel="Reviewer"
        open={newRequestOpen}
        onSelect={handleNewRequest}
        onClose={handleCloseNewRequest}
      />
      <MemberSelectorDialog
        open={memberSelectorOpen}
        initialFilters={memberFilters}
        memberDescriptor="members"
        selectedMembers={teamMembers}
        onClose={() => setDialogOpen(false)}
        onSubmit={membersToAdd => setTeamMembers(membersToAdd)}
      />
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

TeamReviews.propTypes = propTypes;
TeamReviews.displayName = displayName;

export default TeamReviews;
