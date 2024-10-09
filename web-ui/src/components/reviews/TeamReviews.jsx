import PropTypes from 'prop-types';
import queryString from 'query-string';
import React, {
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState
} from 'react';
import { useLocation, Link } from 'react-router-dom';

import {
  AddCircle,
  Archive,
  Delete,
  Search,
  Unarchive
} from '@mui/icons-material';
import {
  Alert,
  Button,
  Chip,
  FormControlLabel,
  IconButton,
  InputAdornment,
  List,
  ListItem,
  ListItemText,
  Switch,
  TextField,
  Tooltip,
  Typography
} from '@mui/material';
import { styled } from '@mui/material/styles';

import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import { resolve } from '../../api/api.js';
import {
  findReviewRequestsByPeriod,
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
  selectCurrentMembers,
  selectCurrentUser,
  selectCurrentUserSubordinates,
  selectHasDeleteReviewPeriodPermission,
  selectHasLaunchReviewPeriodPermission,
  selectHasUpdateReviewPeriodPermission,
  selectIsAdmin,
  selectReviewPeriod,
  selectSupervisors,
  selectProfile,
  selectTeamMembersBySupervisorId
} from '../../context/selectors';

import MemberSelector from '../member_selector/MemberSelector';
import MemberSelectorDialog from '../member_selector/member_selector_dialog/MemberSelectorDialog';

import DatePickerField from '../date-picker-field/DatePickerField.jsx';
import '../date-picker-field/DatePickerField.css';
import './TeamReviews.css';

const propTypes = {
  onBack: PropTypes.func,
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

const TeamReviews = ({ onBack, periodId }) => {
  const { state, dispatch } = useContext(AppContext);
  const location = useLocation();

  const [openMode, setOpenMode] = useState(false);
  const [approvalMode, setApprovalMode] = useState(false);
  const [assignments, setAssignments] = useState([]);
  const [canUpdate, setCanUpdate] = useState(false);
  const [confirmApproveAllOpen, setConfirmApproveAllOpen] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [confirmationText, setConfirmationText] = useState('');
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [memberSelectorOpen, setMemberSelectorOpen] = useState(false);
  const [nameQuery, setNameQuery] = useState('');
  const [query, setQuery] = useState({});
  const [reviewerSelectorOpen, setReviewerSelectorOpen] = useState(false);
  const [reviews, setReviews] = useState(null);
  const [selectedMember, setSelectedMember] = useState(null);
  const [selectedReviewers, setSelectedReviewers] = useState([]);
  const [selfReviews, setSelfReviews] = useState({});
  const [showAll, setShowAll] = useState(false);
  const [teamMembers, setTeamMembers] = useState([]);
  const [toDelete, setToDelete] = useState(null);
  const [unapproved, setUnapproved] = useState([]);
  const [validationMessage, setValidationMessage] = useState(null);
  const [confirmRevieweesWithNoSupervisorOpen, setConfirmRevieweesWithNoSupervisorOpen] = useState(false);
  const [confirmRevieweesWithNoSupervisorQuestion, setConfirmRevieweesWithNoSupervisorQuestionText] = useState('');

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
  const period = selectReviewPeriod(state, periodId);

  const reviewAssignmentsUrl = '/services/review-assignments';

  useEffect(() => {
    loadAssignments();
  }, [currentMembers]);

  useEffect(() => {
    const myId = currentUser?.id;
    const supervisors = selectSupervisors(state);
    const isManager = supervisors.some(s => s.id === myId);
    const period = selectReviewPeriod(state, periodId);
    if (period) {
      setApprovalMode(
        isManager && period.reviewStatus === ReviewStatus.AWAITING_APPROVAL
      );
      setOpenMode(isManager && period.reviewStatus === ReviewStatus.OPEN);
    }

    setCanUpdate(selectHasUpdateReviewPeriodPermission(state) &&
                 period?.reviewStatus !== ReviewStatus.OPEN);
  }, [state]);

  useEffect(() => {
    loadTeamMembers();
  }, [approvalMode, assignments, showAll]);

  const editReviewers = member => {
    setSelectedMember(member);
    const reviewers = member ? getReviewers(member) : [];
    setSelectedReviewers(reviewers);
    setReviewerSelectorOpen(true);
  };

  const loadAssignments = async () => {
    const myId = currentUser?.id;
    const res = await resolve({
      method: 'GET',
      url: `${reviewAssignmentsUrl}/period/${periodId}`,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const assignments = res.payload.data;
    // logAssignments(assignments); // for debugging
    setAssignments(assignments);
  };

  const loadTeamMembers = () => {
    let members = [];

    let source;
    if (!approvalMode || (isAdmin && showAll)) {
      source = currentMembers;
    } else {
      // Get the direct reports of the current user who is a manager.
      const myId = currentUser?.id;
      source = showAll
        ? selectCurrentUserSubordinates(state)
        : selectTeamMembersBySupervisorId(state, myId);
    }

    // Always filter the members down to existing selected assignments.
    // We do not want to add members that were not already selected.
    const memberIds = assignments.map(a => a.revieweeId);
    members = source.filter(m => memberIds.includes(m.id));

    setTeamMembers(members);
  };

  const updateTeamMembers = async teamMembers => {
    const data = teamMembers.map(tm => ({
      revieweeId: tm.id,
      reviewerId: tm.supervisorid,
      reviewPeriodId: periodId,
      approved: false
    }));

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
    if (res.error) return;

    setTeamMembers(teamMembers);
    addAssignmentForMemberWithNone(teamMembers);

    // Now that teamMembers has been updated, we need to make sure that the
    // assignments reflects the set of team members.
    const ids = teamMembers.map(m => m.id);
    setAssignments(assignments.filter(a => a.revieweeId && ids.includes(a.revieweeId)));
  };

  const addAssignmentForMemberWithNone = async (members) => {
    members.forEach(member => {
      const exists = assignments.some(
          a => a.revieweeId === member.id
      );
      if (!!!exists && member.supervisorid) {
        const reviewers = [{ id: member.supervisorid }];
        updateReviewers(member, reviewers);
      }
    });
  }

  const getReviewStatus = useCallback(
    review => {
      if (review) {
        switch (review?.status) {
          case 'submitted':
            return 'Review submitted';
          case 'sent':
          case 'pending':
            return 'Review in progress';
          case 'cancelled':
          case 'canceled':
          default:
            break;
        }
      }
      return 'No review started';
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
    setConfirmDeleteOpen(true);
  }, [period, setToDelete, setConfirmDeleteOpen]);

  const handleConfirmDeleteClose = useCallback(() => {
    setToDelete(null);
    setConfirmDeleteOpen(false);
  }, [setToDelete, setConfirmDeleteOpen]);

  const handleConfirmApproveAllClose = useCallback(() => {
    setConfirmApproveAllOpen(false);
  }, [setToDelete, setConfirmApproveAllOpen]);

  const deleteReviewPeriod = useCallback(async () => {
    if (!csrf) return;

    await removeReviewPeriod(toDelete, csrf);
    dispatch({
      type: DELETE_REVIEW_PERIOD,
      payload: toDelete
    });
    handleConfirmDeleteClose();
    onBack();
  }, [csrf, dispatch, toDelete, handleConfirmDeleteClose]);

  const getReviewers = useCallback(
    reviewee => {
      if (!reviewee) return [];
      const { id } = reviewee;
      const as = assignments.filter(a => a.revieweeId === id) ?? [];
      const reviewerIdSet = new Set();
      const reviewers = [];
      for (const as of assignments) {
        if (as.revieweeId === id) {
          const member = { ...memberMap[as.reviewerId], approved: as.approved };
          if (!reviewerIdSet.has(member.id)) {
            reviewerIdSet.add(member.id);
            reviewers.push(member);
          }
        }
      }
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
            toast: res?.error?.message ?? 'Error updating review period'
          }
        });
      }
    },
    [csrf, dispatch, period, state]
  );

  const handleLaunchDateChange = (val, period) => {
    const newDate = val?.$d;
    const isoDate = newDate?.toISOString() ?? null;
    const newPeriod = { ...period, launchDate: isoDate };

    // Clear dates that are not correctly ordered.
    const selfReviewCloseDate = new Date(period.selfReviewCloseDate);
    const closeDate = new Date(period.closeDate);
    const periodStartDate = new Date(period.periodStartDate);
    if (selfReviewCloseDate <= newDate) newPeriod.selfReviewCloseDate = null;
    if (closeDate <= newDate) newPeriod.closeDate = null;
    if (periodStartDate >= newDate) newPeriod.periodStartDate = null;

    updateReviewPeriodDates(newPeriod);
  };

  const handleSelfReviewDateChange = (val, period) => {
    const newDate = val?.$d;
    const isoDate = newDate?.toISOString() ?? null;
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
    const isoDate = newDate?.toISOString() ?? null;
    const newPeriod = { ...period, closeDate: isoDate };

    // Clear dates that are not correctly ordered.
    const launchDate = new Date(period.launchDate);
    const selfReviewCloseDate = new Date(period.selfReviewCloseDate);
    const periodEndDate = new Date(period.periodEndDate);
    if (launchDate >= newDate) newPeriod.launchDate = null;
    if (selfReviewCloseDate >= newDate) newPeriod.selfReviewCloseDate = null;
    if (periodEndDate > newDate) newPeriod.periodEndDate = null;

    updateReviewPeriodDates(newPeriod);
  };

  const handlePeriodStartDateChange = (val, period) => {
    const newDate = val?.$d;
    const isoDate = newDate?.toISOString() ?? null;
    const newPeriod = { ...period, periodStartDate: isoDate };

    // Clear dates that are not correctly ordered.
    const periodEndDate = new Date(period.periodEndDate);
    if (newDate > periodEndDate) newPeriod.periodEndDate = null;

    updateReviewPeriodDates(newPeriod);
  };

  const handlePeriodEndDateChange = (val, period) => {
    const newDate = val?.$d;
    const isoDate = newDate?.toISOString() ?? null;
    const newPeriod = { ...period, periodEndDate: isoDate };

    // Clear dates that are not correctly ordered.
    const periodStartDate = new Date(period.periodStartDate);
    const closeDate = new Date(period.closeDate);
    if (newDate < periodStartDate) newPeriod.periodStartDate = null;
    if (newDate > closeDate) newPeriod.closeDate = null;

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

    const getReviewRequests = async () => {
      const res = await findReviewRequestsByPeriod(period, csrf);
      const data = res?.payload?.status === 200 && !res.error ?
                          res.payload.data : null;

      if (data?.length) {
        data.filter(review => review?.status?.toUpperCase() !== 'CANCELED')
            .forEach(request => {
          if (!newReviews[request.recipientId]) {
            newReviews[request.recipientId] = [];
          }
          newReviews[request.recipientId].push(request);
        });
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
      await getReviewRequests();
      loadingReviews.current = false;
      loadedReviews.current = true;
      setSelfReviews({ ...newSelfReviews });
      setReviews({ ...newReviews });
    }
  }, [csrf, period, teamMembers]);

  useEffect(() => {
    loadReviews();
  }, [loadReviews]);

  const deleteReviewer = async (member, reviewer) => {
    const assignment = assignments.find(
      a =>
        a.reviewPeriodId === period.id &&
        a.reviewerId === reviewer.id &&
        a.revieweeId === member.id
    );
    if (!assignment) return;

    const { id, revieweeId, reviewerId } = assignment;
    if (id) {
      const res = await resolve({
        method: 'DELETE',
        url: `${reviewAssignmentsUrl}/${id}`,
        headers: { 'X-CSRF-Header': csrf }
      });

      if (res.error) {
        console.error('Error deleting assignment:', res.error);
        return;
      }

      setAssignments(assignments.filter(a => a.id !== id));
    } else {
      // This reviewer does not have an assignment id.  Therefore, we just
      // need to remove the reviewer from the assignment list.
      setAssignments(assignments.filter(a =>
        !(a.revieweeId === revieweeId && a.reviewerId === reviewerId)));
    }
  };

  const validateReviewPeriod = period => {
    if (!period) return 'No review period was created.';
    if (!period.launchDate) return 'No launch date was specified.';
    if (!period.selfReviewCloseDate) return 'No self-review date was specified.';
    if (!period.closeDate) return 'No close date was specified.';
    if (!period.periodStartDate) return 'No period-start-date was specified.';
    if (!period.periodEndDate) return 'No period-end-date was specified.';
    if (teamMembers.length === 0) return 'No members were added.';
    const haveReviewers = teamMembers.every(
      member => getReviewers(member).length > 0
    );
    if (!haveReviewers) return 'One or more members have no reviewer.';
    return null; // no validation errors
  };

  const updateReviewPeriodStatus = async reviewStatus => {
    const res = await resolve({
      method: 'PUT',
      url: '/services/review-periods',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
        'X-CSRF-Header': csrf
      },
      data: {
        ...period,
        reviewStatus
      }
    });
    if (res.error) return;

    onBack();
  };

  // This is only used for debugging.
  const logAssignments = assignments => {
    if (currentMembers.length === 0) return;
    for (const assignment of assignments) {
      const reviewee = currentMembers.find(m => m.id === assignment.revieweeId);
      const reviewer = currentMembers.find(m => m.id === assignment.reviewerId);
      if (reviewee && reviewer) {
        console.log(reviewee.name, 'is reviewed by', reviewer.name);
      }
    }
  };

  const requestApproval = async () => {
    const msg = validateReviewPeriod(period);
    setValidationMessage(msg);
    if (msg) return;

    const uniqueNamesWithNoSupervisor = [...new Set(
        visibleTeamMembers()
            .filter(member => member.supervisorid === null)  // Filter by null supervisorid
            .map(member => member.name)                      // Map to the name property
    )].join(', ');
    if (uniqueNamesWithNoSupervisor.trim().length > 0) {
      setConfirmRevieweesWithNoSupervisorQuestionText(uniqueNamesWithNoSupervisor);
      setConfirmRevieweesWithNoSupervisorOpen(true);
    } else {
      return requestApprovalPost();
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
    if (res.error) return;

    newAssignments = sortMembers(res.payload.data);
    setAssignments(newAssignments);
  };

  const closeReviewerDialog = () => {
    setSelectedMember(null);
    setSelectedReviewers([]);
    setReviewerSelectorOpen(false);
  };

  const isMemberApproved = member => {
    const reviewer = getReviewers(member)[0];
    return reviewer && reviewer.approved;
  };

  const getReviewRequest = (member, reviewer) => {
    if (reviews && reviews[reviewer.id]) {
      return reviews[reviewer.id].find((r) => r.requesteeId === member.id);
    }
    return null;
  };

  const getSelfReviewRequest = (member) => {
    if (reviews && reviews[member.id]) {
      return reviews[member.id].find((r) => r.recipientId === member.id &&
                                            r.requesteeId === member.id);
    }
    return null;
  };

  const getReviewerURL = (request, selfReviewRequest) => {
    let url;
    if (openMode && (request || selfReviewRequest)) {
      const recipientProfile = selectProfile(state, request?.recipientId);
      const manages = recipientProfile?.id === currentUser?.id ||
                      recipientProfile?.supervisorid === currentUser?.id;

      const submitted = request?.status == 'submitted';
      const selfSubmitted = selfReviewRequest?.status == 'submitted';
      if (manages && (submitted || selfSubmitted)) {
        let separator = '?';
        url = "/feedback/submit";
        if (submitted) {
          url += `${separator}request=${request.id}`;
          separator = '&';
        }
        if (selfSubmitted) {
          url += `${separator}selfrequest=${selfReviewRequest.id}`;
          separator = '&';
        }
      }
    }
    return url;
  };

  const renderReviewer = (member, reviewer) => {
    const backgroundColor = reviewer.approved ?
                              'var(--checkins-palette-action-green)' :
                              'var(--checkins-palette-action-yellow)';
    const request = getReviewRequest(member, reviewer);
    const selfReviewRequest = getSelfReviewRequest(member);
    const variant = 'outlined';
    const statusLabel = `${reviewer.name}: ${getReviewStatus(request)}`;
    const url = getReviewerURL(request, selfReviewRequest);

    return (url ?
            <Link to={url}>
            <Chip
              key={reviewer.id}
              label={statusLabel}
              variant={variant}
              style={{backgroundColor: backgroundColor}}
            />
          </Link> :
          <Chip
            key={reviewer.id}
            label={openMode ? statusLabel : reviewer.name}
            variant={variant}
            onDelete={canUpdate && !openMode ?
                          () => deleteReviewer(member, reviewer) : null}
            style={{backgroundColor: backgroundColor}}
          />);
  };

  const REVIEWER_LIMIT = 2;
  const renderReviewers = member => {
    // Sort the list of reviewers such that the current user comes first.
    // In the event that the number of reviewers exceeds the limit, we need
    // to ensure that the current user is still visible so that the chip
    // link can be created (if in the open mode) in renderReviewer().
    let reviewers = getReviewers(member).sort((l, r) =>
      (l.id === currentUser?.id ? -1 : (r.id === currentUser?.id ? 1 : 0))
    );
    const count = reviewers.length;
    const excess = count - REVIEWER_LIMIT;
    if (excess > 0) reviewers = reviewers.slice(0, REVIEWER_LIMIT);
    return (
      <>
        {reviewers.map(reviewer => renderReviewer(member, reviewer))}
        {excess > 0 && <div>and {excess} more </div>}
      </>
    );
  };

  const renderSelfReviewStatus = member => {
    const recipientProfile = selectProfile(state, member.id);
    const manages = recipientProfile.supervisorid == currentUser?.id;
    if (manages) {
      const selfReviewRequest = getSelfReviewRequest(member);
      return (
        <Chip
          key={member.id}
          label={"Self-Review: " + getReviewStatus(selfReviewRequest)}
          variant="outlined"
        />);
    } else {
      return (<></>);
    }
  };

  const approvalButton = () => {
    switch (period.reviewStatus) {
      case ReviewStatus.PLANNING:
        return <Button onClick={requestApproval}>Request Approval</Button>;
      case ReviewStatus.AWAITING_APPROVAL:
        return selectHasLaunchReviewPeriodPermission(state) ? (
          <Button onClick={requestApproval}>Launch Review</Button>
        ) : null;
      default:
        return null;
    }
  };

  const approveAll = () => {
    visibleTeamMembers().map(member => approveMember(member, true));
    setConfirmApproveAllOpen(false);
  };

  const approveAllAndLaunch = () => {
    if (unapproved.length) approveAll();
    updateReviewPeriodStatus(ReviewStatus.OPEN);
    setConfirmationDialogOpen(false);
    onBack();
  };

  const requestApprovalPost = async () => {
    if (period.reviewStatus === ReviewStatus.PLANNING) {
      updateReviewPeriodStatus(ReviewStatus.AWAITING_APPROVAL);
    } else if (period.reviewStatus === ReviewStatus.AWAITING_APPROVAL) {
      const visibleIds = new Set(visibleTeamMembers().map(m => m.id));
      const unapproved = assignments.filter(
          a => !a.approved && visibleIds.has(a.revieweeId)
      );
      // logAssignments(unapproved); // for debugging
      setUnapproved(unapproved);
      setConfirmationText(
          unapproved.length === 0
              ? 'Are you sure you want to launch the review period?'
              : unapproved.length === 1
                  ? 'There is one visible, unapproved review assignment. ' +
                  'Would you like to approve it and launch this review period?'
                  : `There are ${unapproved.length} visible, unapproved review assignments. ` +
                  'Would you like to approve all of them and launch this review period?'
      );
      setConfirmationDialogOpen(true);
    }
  }

  const unapproveAll = () => {
    visibleTeamMembers().map(member => approveMember(member, false));
  };

  const toggleApproval = async member => {
    const toApprove = assignments.filter(
      assignment =>
        assignment.revieweeId === member.id &&
        assignment.reviewPeriodId === period.id
    );
    if (toApprove.length === 0) return;

    const { approved } = toApprove[0];
    approveMember(member, !approved);
  };

  const approveMember = async (member, approved) => {
    const toApprove = assignments.filter(
      assignment =>
        assignment.revieweeId === member.id &&
        assignment.reviewPeriodId === period.id
    );
    if (toApprove.length === 0) return;

    const promises = toApprove.map(assignment =>
      approveReviewAssignment(assignment, approved)
    );
    await Promise.all(promises);

    // Update the UI by updating the assigments state.
    for (const assignment of toApprove) {
      assignment.approved = approved;
    }
    setAssignments([...assignments]);
  };

  const approveReviewAssignment = async (assignment, approved) => {
    const res = await resolve({
      method: assignment.id === null ? 'POST' : 'PUT',
      url: '/services/review-assignments',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8',
        'X-CSRF-Header': csrf
      },
      data: { ...assignment, approved }
    });
  };

  const visibleTeamMembers = () => {
    const query = nameQuery.trim().toLowerCase();
    if (!approvalMode || query.length === 0) return teamMembers;

    return teamMembers.filter(member =>
      member.name.toLowerCase().includes(query)
    );
  };

  return (
    <Root className="team-reviews">
      <div className={classes.headerContainer}>
        <Typography variant="h4">{period?.name ?? ''} Team Reviews</Typography>
      </div>
      {period && (
        <div className="date-pickers-row">
          <div className="date-pickers-container">
            <DatePickerField
              date={period.launchDate}
              setDate={val => handleLaunchDateChange(val, period)}
              label="Launch Date"
              disabled={!canUpdate}
            />
            <DatePickerField
              date={period.selfReviewCloseDate}
              setDate={val => handleSelfReviewDateChange(val, period)}
              label="Self-Review Date"
              disabled={!canUpdate}
            />
            <DatePickerField
              date={period.closeDate}
              setDate={val => handleCloseDateChange(val, period)}
              label="Close Date"
              disabled={!canUpdate}
            />
            <DatePickerField
                date={period.periodStartDate}
                setDate={val => handlePeriodStartDateChange(val, period)}
                label="Period Start Date"
                disabled={!canUpdate}
            />
            <DatePickerField
                date={period.periodEndDate}
                setDate={val => handlePeriodEndDateChange(val, period)}
                label="Period End Date"
                disabled={!canUpdate}
            />
          </div>
          {approvalButton()}
        </div>
      )}
      {validationMessage && (
        <Alert severity="error" style={{ marginBottom: '1rem' }}>
          {validationMessage}
        </Alert>
      )}

      {approvalMode && (
        <div id="approval-row">
          <TextField
            className="name-search-field"
            label="Name"
            placeholder="Search by member name"
            variant="outlined"
            value={nameQuery}
            onChange={event => setNameQuery(event.target.value)}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end" color="gray">
                  <Search />
                </InputAdornment>
              )
            }}
          />
          {period && isAdmin && (
              <div>
                {approvalMode && (
                    <FormControlLabel
                        control={
                          <Switch
                              checked={showAll}
                              onChange={() => setShowAll(b => !b)}
                          />
                        }
                        label="Show All"
                        sx={{ marginLeft: '0.5rem' }}
                    />
                )}
              </div>
          )}
          {canUpdate && (
            <div>
              <Button onClick={() => setConfirmApproveAllOpen(true)}>
                Approve All
              </Button>
              <Button onClick={unapproveAll}>Unapprove All</Button>
            </div>
          )}
        </div>
      )}

      {canUpdate && (
        <MemberSelector
          className="team-skill-member-selector"
          exportable
          onChange={updateTeamMembers}
          selected={teamMembers}
        />
      )}

      <List dense role="list">
        {visibleTeamMembers().map(member => (
          <ListItem key={member.id} role="listitem" disablePadding>
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
              {openMode && renderSelfReviewStatus(member)}

              <Typography>Reviewers:</Typography>
              {renderReviewers(member)}

              {canUpdate && (
                <IconButton
                  aria-label="Edit Reviewers"
                  onClick={() => editReviewers(member)}
                >
                  <AddCircle />
                </IconButton>
              )}

              {canUpdate && approvalMode && (
                <Button onClick={() => toggleApproval(member)}>
                  {isMemberApproved(member) ? 'Unapprove' : 'Approve'}
                </Button>
              )}
            </div>
          </ListItem>
        ))}
      </List>

      <MemberSelectorDialog
        open={memberSelectorOpen}
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
      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={deleteReviewPeriod}
        question={`Are you sure you want to delete the review period ${selectReviewPeriod(state, toDelete)?.name}?`}
        setOpen={setConfirmDeleteOpen}
        title="Delete this review period?"
      />
      <ConfirmationDialog
        open={confirmApproveAllOpen}
        onYes={approveAll}
        question={`Are you sure you want to approve all the visible review assignments?`}
        setOpen={setConfirmApproveAllOpen}
        title="Approve Visible Review Assignments"
      />
      <ConfirmationDialog
        open={confirmationDialogOpen}
        onYes={approveAllAndLaunch}
        question={confirmationText}
        setOpen={setConfirmationDialogOpen}
        title="Approve and Launch"
      />
      <ConfirmationDialog
          open={confirmRevieweesWithNoSupervisorOpen}
          onYes={requestApprovalPost}
          question={confirmRevieweesWithNoSupervisorQuestion}
          setOpen={setConfirmRevieweesWithNoSupervisorOpen}
          title="These reviewees have no supervisor. Continue?"
      />
    </Root>
  );
};

TeamReviews.propTypes = propTypes;
TeamReviews.displayName = displayName;

export default TeamReviews;
