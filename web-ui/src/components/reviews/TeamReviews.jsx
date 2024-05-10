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
  Download,
  ExpandMore,
  Unarchive
} from '@mui/icons-material';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Avatar,
  Button,
  Chip,
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
import './TeamReviews.css';

const propTypes = {
  teamMembers: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string
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

  const [assignments, setAssignments] = useState([]);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [includeAll, setIncludeAll] = useState(false);
  const [memberFilters, setMemberFilters] = useState([]);
  const [memberSelectorOpen, setMemberSelectorOpen] = useState(false);
  const [newRequestOpen, setNewRequestOpen] = useState(false);
  const [query, setQuery] = useState({});
  const [reviewerSelectorOpen, setReviewerSelectorOpen] = useState(false);
  const [reviews, setReviews] = useState(null);
  const [selectedMember, setSelectedMember] = useState(null);
  const [selectedReviewers, setSelectedReviewers] = useState([]);
  const [selfReviews, setSelfReviews] = useState({});
  const [teamMembers, setTeamMembers] = useState([]);
  const [toDelete, setToDelete] = useState(null);

  const creatingReview = useRef(false);
  const loadedReviews = useRef(false);
  const loadingReviews = useRef(false);

  const csrf = selectCsrfToken(state);
  const currentMembers = selectCurrentMembers(state);
  const memberMap = currentMembers.reduce((map, member) => {
    map[member.id] = member;
    return map;
  }, {});
  const currentUser = selectCurrentUser(state);
  const isAdmin = selectIsAdmin(state);
  const myTeam = selectMyTeam(state);
  const period = selectReviewPeriod(state, periodId);
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

  useEffect(() => {
    const reviewers = selectedMember ? getReviewers(selectedMember) : [];
    setSelectedReviewers(reviewers);
  }, [selectedMember]);

  const editReviewers = member => {
    setSelectedMember(member);
    setReviewerSelectorOpen(true);
  };

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
      setAssignments(assignments);
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

  const getMember = id => {
    return teamMembers.find(m => m.id === id);
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
    history.goBack();
  }, [csrf, dispatch, toDelete, handleConfirmClose]);

  const getReviewers = useCallback(
    reviewee => {
      if (!reviewee) return [];
      const { id } = reviewee;
      const as = assignments.filter(a => a.revieweeId === id) ?? [];
      const reviewerIds = new Set();
      as.forEach(a => reviewerIds.add(a.reviewerId));
      const reviewers = [...reviewerIds].map(id => memberMap[id]);
      return sortMembers(reviewers);
    },
    [assignments]
  );

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
    const newDate = val?.$d;
    const isoDate = newDate.toISOString() ?? null;
    const newPeriod = { ...period, launchDate: isoDate };

    // Clear dates that are not correctly ordered.
    const selfReviewCloseDate = new Date(period.selfReviewCloseDate);
    const closeDate = new Date(period.closeDate);
    if (selfReviewCloseDate <= newDate) newPeriod.selfReviewCloseDate = null;
    if (closeDate <= newDate) newPeriod.closeDate = null;

    updateReviewPeriodDates(newPeriod);
  };

  const handleSelfReviewDateChange = (val, period) => {
    const newDate = val?.$d;
    const isoDate = newDate.toISOString() ?? null;
    const newPeriod = { ...period, selfReviewCloseDate: isoDate };

    // Clear dates that are not correctly ordered.
    const launchDate = new Date(period.launchDate);
    const closeDate = new Date(period.closeDate);
    if (launchDate >= newDate) newPeriod.launchDate = null;
    if (closeDate <= newDate) newPeriod.closeDate = null;

    updateReviewPeriodDates(newPeriod);
  };

  const handleCloseDateChange = (val, period) => {
    const newDate = val?.$d;
    const isoDate = newDate.toISOString() ?? null;
    const newPeriod = { ...period, closeDate: isoDate };

    // Clear dates that are not correctly ordered.
    const launchDate = new Date(period.launchDate);
    const selfReviewCloseDate = new Date(period.selfReviewCloseDate);
    if (launchDate >= newDate) newPeriod.launchDate = null;
    if (selfReviewCloseDate >= newDate) newPeriod.selfReviewCloseDate = null;

    updateReviewPeriodDates(newPeriod);
  };

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

  const createSecondary = teamMember =>
    getReviewStatus(teamMember?.id) +
    ', Self-review: ' +
    getSelfReviewStatus(teamMember?.id);

  const deleteReviewer = async (member, reviewer) => {
    const assignment = assignments.find(
      a =>
        a.reviewPeriodId === period.id &&
        a.reviewerId === reviewer.id &&
        a.revieweeId === member.id
    );
    if (!assignment) return;

    try {
      const { id } = assignment;
      const res = await resolve({
        method: 'DELETE',
        url: `${reviewAssignmentsUrl}/${id}`,
        headers: { 'X-CSRF-Header': csrf }
      });
      if (res.error) throw new Error(res.error.message);
      setAssignments(assignments.filter(a => a.id !== id));
    } catch (err) {
      console.error('TeamReviews.jsx deleteReviewer:', err);
    }
  };

  const compareStrings = (s1, s2) => (s1 || '').localeCompare(s2 || '');

  const sortMembers = members =>
    members.sort((a, b) => {
      let compare = compareStrings(a.lastName, b.lastName);
      if (compare === 0) compare = compareStrings(a.firstName, b.firstName);
      return compare;
    });

  const updateReviewers = async (member, reviewers) => {
    const memberId = member.id;

    let newAssignments = [...assignments];

    // Remove all assignments for this member.
    newAssignments = newAssignments.filter(a => a.revieweeId !== memberId);

    // Add assignments for these reviewers if they don't already exist.
    // All objects in the assignments array are for the current review period.
    for (const reviewer of reviewers) {
      const exists = newAssignments.some(
        a => a.reviewerId === reviewer.id && a.revieweeId === memberId
      );
      if (!exists) {
        newAssignments.push({
          reviewPeriodId: periodId,
          reviewerId: reviewer.id,
          revieweeId: member.id
        });
      }
    }

    try {
      const res = await resolve({
        method: 'POST',
        url: `${reviewAssignmentsUrl}/${periodId}`,
        data: newAssignments,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      if (res.error) throw new Error(res.error.message);
      newAssignments = sortMembers(res.payload.data);
      setAssignments(newAssignments);
    } catch (err) {
      console.error('TeamReviews.jsx updateTeamMembers:', err);
    }
  };

  const closeReviewerDialog = () => {
    setSelectedMember(null);
    setSelectedReviewers([]);
    setReviewerSelectorOpen(false);
  };

  return (
    <Root className="team-reviews">
      <div className={classes.headerContainer}>
        <Typography variant="h4">{period?.name ?? ''} Team Reviews</Typography>

        {period && isAdmin && (
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
      </div>
      {period && (
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
      )}
      <MemberSelector
        className="team-skill-member-selector"
        exportable
        onChange={updateTeamMembers}
        selected={teamMembers}
      />

      <List dense role="list" sx={{ height: '50%', overflowY: 'scroll' }}>
        {teamMembers.map(member => (
          <ListItem
            key={member.id}
            role="listitem"
            disablePadding
            onClick={() => {}}
          >
            <ListItemText
              className="name-title"
              primary={<Typography fontWeight="bold">{member.name}</Typography>}
              secondary={
                <Typography color="textSecondary" component="h6">
                  {member.title}
                </Typography>
              }
            />
            <div className="chip-row">
              <Typography>Reviewers:</Typography>
              {getReviewers(member).map(reviewer => (
                <Chip
                  key={reviewer.id}
                  label={reviewer.name}
                  variant="outlined"
                  onDelete={() => deleteReviewer(member, reviewer)}
                />
              ))}
              <IconButton
                aria-label="Edit Reviewers"
                onClick={() => editReviewers(member)}
              >
                <AddCircle />
              </IconButton>
            </div>
          </ListItem>
        ))}
      </List>

      <MemberSelectorDialog
        open={memberSelectorOpen}
        initialFilters={memberFilters}
        memberDescriptor="Members"
        selectedMembers={teamMembers}
        onClose={() => setMemberSelectorOpen(false)}
        onSubmit={members => setTeamMembers(members)}
      />
      <MemberSelectorDialog
        open={reviewerSelectorOpen}
        memberDescriptor="Reviewers"
        selectedMembers={selectedReviewers}
        onClose={closeReviewerDialog}
        onSubmit={reviewers => {
          updateReviewers(selectedMember, reviewers);
          closeReviewerDialog();
        }}
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
