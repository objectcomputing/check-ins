import React, {useEffect, useContext, useCallback, useState, useRef} from "react";
import PropTypes from "prop-types";
import { useLocation, useHistory } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import AddCommentIcon from '@mui/icons-material/AddComment';
import ListItemSecondaryAction from '@mui/material/ListItemSecondaryAction';
import Avatar from '@mui/material/Avatar';
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import Divider from '@mui/material/Divider';
import queryString from 'query-string';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import FormControlLabel from '@mui/material/FormControlLabel';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Switch from "@mui/material/Switch";
import Typography from "@mui/material/Typography";
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar'
import Tooltip from '@mui/material/Tooltip';
import Skeleton from '@mui/material/Skeleton';
import TeamMemberReview from "./TeamMemberReview";
import SelectUserModal from "./SelectUserModal";
import { UPDATE_REVIEW_PERIODS, UPDATE_TOAST } from "../../context/actions";
import { AppContext } from "../../context/AppContext";
import { getReviewPeriods } from "../../api/reviewperiods.js";
import { createFeedbackRequest, findReviewRequestsByPeriodAndTeamMember, findSelfReviewRequestsByPeriodAndTeamMember } from "../../api/feedback.js";
import {
  selectCsrfToken,
  selectReviewPeriod,
  selectProfile,
  selectCurrentUser,
  selectIsAdmin,
  selectMyTeam,
  selectCurrentMembers,
  selectSubordinates,
} from "../../context/selectors";
import { getAvatarURL } from "../../api/api.js";
import DateFnsUtils from "@date-io/date-fns";
const dateUtils = new DateFnsUtils();

const propTypes = {
  teamMembers: PropTypes.arrayOf(PropTypes.shape({id: PropTypes.string, firstName: PropTypes.string, lastName: PropTypes.string,})),
  periodId: PropTypes.string,
};
const displayName = "TeamReviews";

const PREFIX = displayName;
const classes = {
  actionButtons: `${PREFIX}-actionButtons`,
  headerContainer: `${PREFIX}-headerContainer`,
  periodModal: `${PREFIX}-periodModal`,
};

const Root = styled('div')(({theme}) => ({
  [`& .${classes.actionButtons}`]: {
    margin: "0.5em 0 0 1em",
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      padding: "0",
    },
  },
  [`& .${classes.headerContainer}`]: {
    display: "flex",
    'flex-direction': "row",
    'justify-content': "space-between",
    'align-items': "center",
    margin: "0 0 1em 0",
    ['@media (max-width:800px)']: { // eslint-disable-line no-useless-computed-key
      margin: "0",
      'justify-content': "center",
    }
  },
}));

const TeamReviews = ({ periodId }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const history = useHistory();
  const currentUser = selectCurrentUser(state);
  const currentMembers = selectCurrentMembers(state);
  const myTeam = selectMyTeam(state);
  const subordinates = selectSubordinates(state, currentUser?.id);
  const isAdmin = selectIsAdmin(state);
  const period = selectReviewPeriod(state, periodId);
  const [teamMembers, setTeamMembers] = useState(null);
  const [selfReviews, setSelfReviews] = useState({});
  const [reviews, setReviews] = useState(null);
  const [query, setQuery] = useState({});
  const [selectedTeamMember, setSelectedTeamMember] = useState(null);
  const selectedMemberProfile = selectProfile(state, selectedTeamMember);
  const [newRequestOpen, setNewRequestOpen] = useState(false);
  const [includeAll, setIncludeAll] = useState(false);
  const loadingReviews = useRef(false);
  const loadedReviews = useRef(false);
  const creatingReview = useRef(false);

  const handleOpenNewRequest = useCallback(() => setNewRequestOpen(true), [setNewRequestOpen]);
  const handleCloseNewRequest = useCallback(() => setNewRequestOpen(false), [setNewRequestOpen]);

  useEffect(() => {
    if(currentMembers && currentMembers.length > 0) {
      isAdmin && includeAll ? setTeamMembers(currentMembers.filter((member) => member?.id !== currentUser?.id)) : includeAll ? setTeamMembers(subordinates) : setTeamMembers(myTeam);
    }
  }, [isAdmin, includeAll, subordinates, currentMembers, myTeam, currentUser?.id]);

  const getReviewStatus = useCallback((teamMemberId) => {
    let reviewStates = { submitted: false, inProgress: false };
    if(reviews && reviews[teamMemberId]) {
      reviewStates = reviews[teamMemberId].reduce((states, review) => {
        switch(review?.status) {
          case "submitted":
            states.submitted = true;
            break;
          case "sent":
          case "pending":
            states.inProgress = true;
            break;
          case "cancelled":
          case "canceled":
          default:
            break;
        }
        return states;
      }, reviewStates);
      if(reviewStates.inProgress) {
        if(reviews[teamMemberId]?.length > 1) {
          return "Reviews in progress";
        }
        return "Review in progress";
      } else if(reviewStates.submitted) {
        if(reviews[teamMemberId]?.length > 1) {
          return "All reviews submitted";
        }
        return "Review submitted";
      } else return "No reviews started";
    } else {
      return "No reviews started";
    }
  },[reviews]);

  const getSelfReviewStatus = useCallback((teamMemberId) => {
    let status = "Not started";
    switch(selfReviews[teamMemberId]?.status) {
      case "submitted":
        status = "Submitted";
        break;
      case "sent":
      case "pending":
        status = "In progress";
        break;
      case "cancelled":
      case "canceled":
      default:
        break;
    }
    return status;
  },[selfReviews]);

  useEffect(() => {
    const params = queryString.parse(location?.search);
    setQuery(params);
  }, [location.search]);

  const hasTeamMember = useCallback(() => {
    return !!query.teamMember;
  }, [query.teamMember]);

  const getTeamMember = useCallback(() => {
    if (hasTeamMember())
      return query.teamMember;
    else return null;
  }, [query.teamMember, hasTeamMember]);

  useEffect(() => {
    setSelectedTeamMember(getTeamMember());
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
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: data});
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch]);

  useEffect(() => {
    const createReview = async () => {
      const res = await createFeedbackRequest({
        creatorId: currentUser.id,
        requesteeId: selectedMemberProfile.id,
        recipientId: currentUser.id,
        templateId: period.reviewTemplateId,
        reviewPeriodId: period.id,
        sendDate: dateUtils.format(new Date(), 'yyyy-MM-dd'),
        status: "pending",
      }, csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 201 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        setReviews({...reviews, [selectedMemberProfile.id]: [data] });
      }
      creatingReview.current = false;
    }

    if(csrf && selectedMemberProfile?.id && reviews && (!reviews[selectedMemberProfile.id] || reviews[selectedMemberProfile.id].length === 0) && currentUser?.id && period && !creatingReview.current && loadedReviews.current) {
      if(currentUser?.id === selectedMemberProfile?.supervisorid) {
        creatingReview.current = true;
        createReview();
      }
    }
  }, [csrf, reviews, currentUser, period, selectedMemberProfile]);

  const handleQueryChange = useCallback((key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({ ...location, search: queryString.stringify(newQuery) });
  }, [history, location, query]);

  const onTeamMemberSelected = useCallback((teamMemberId) => {
    handleQueryChange("teamMember", teamMemberId);
  }, [handleQueryChange]);

  const loadReviews = useCallback(async () => {
    let newSelfReviews = {};
    let newReviews = {};
    const getSelfReviewRequest = async (teamMember) => {
      const res = await findSelfReviewRequestsByPeriodAndTeamMember(period, teamMember.id, csrf);
      let data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && data.length > 0) {
        data = data.filter((review)=>"canceled".toUpperCase() !== review?.status?.toUpperCase());
        newSelfReviews[teamMember.id] = data[0];
      }
    };

    const getReviewRequest = async (teamMember) => {
      const res = await findReviewRequestsByPeriodAndTeamMember(period, teamMember.id, csrf);
      let data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && data.length > 0) {
        data = data.filter((review)=>"canceled".toUpperCase() !== review?.status?.toUpperCase());
        newReviews[teamMember.id] = data;
      }
    };

    if (csrf && teamMembers && teamMembers.length > 0 && period && !loadingReviews.current) {
      loadingReviews.current = true;
      setSelfReviews({});
      setReviews(null);
      const promises = teamMembers.map(getSelfReviewRequest);
      promises.push(...teamMembers.map(getReviewRequest));

      Promise.all(promises).then((res) => {
        loadingReviews.current = false;
        loadedReviews.current = true;
        setSelfReviews({...newSelfReviews});
        setReviews({...newReviews});
      });
    }
  }, [csrf, period, teamMembers]);

  useEffect(loadReviews, [loadReviews]);

  const reloadReviews = useCallback(() => {
    loadedReviews.current = false;
    loadReviews();
  }, [loadReviews]);

  const toggleIncludeAll = useCallback(() => {
    loadedReviews.current = false;
    setIncludeAll(!includeAll);
  }, [includeAll, setIncludeAll]);

  const handleNewRequest = useCallback((assignee) => {
    const createNewRequest = async () => {
      if(!selectedMemberProfile?.supervisorid) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "This team member does not have an assigned supervisor. Please assign one before creating a review.",
          },
        });
      } else {
        const res = await createFeedbackRequest({
          creatorId: selectedMemberProfile?.supervisorid,
          requesteeId: selectedMemberProfile?.id,
          recipientId: assignee?.id,
          templateId: period.reviewTemplateId,
          reviewPeriodId: period.id,
          sendDate: dateUtils.format(new Date(), 'yyyy-MM-dd'),
          status: "pending",
        }, csrf);
        const data =
          res &&
          res.payload &&
          res.payload.data &&
          res.payload.status === 201 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          const newReviews = {...reviews}
          newReviews[selectedMemberProfile?.id].push(data);
          setReviews(newReviews);
        } else {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: "An error has occurred while submitting the review request.",
            },
          });
        }
        return data;
      }
    };

    handleCloseNewRequest();
    if (csrf && selectedMemberProfile && period) {
      createNewRequest().then((res) => {
        if(res) {
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "success",
              toast: "Review request sent!",
            },
          });
        }
      });
    }
  }, [csrf, period, selectedMemberProfile, dispatch, handleCloseNewRequest, reviews]);

  const createSecondary = (teamMember) => getReviewStatus(teamMember?.id) + ", Self-review: " + getSelfReviewStatus(teamMember?.id);

  return (
    <Root>
      <div className={classes.headerContainer}>
        <Typography variant="h4">Team Reviews</Typography>
        {!selectedTeamMember && (
          <FormControlLabel control={
            <Switch
              checked={includeAll}
              onChange={toggleIncludeAll}
            />
          } label="Show All" />
        )}
        {selectedTeamMember && (
          <Button onClick={handleOpenNewRequest} className={classes.actionButtons} endIcon={<AddCircleIcon />} variant="contained" color="primary">
            Request Review
          </Button>
        )}
      </div>
      {!selectedTeamMember && loadedReviews.current && (
      <>
      <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
        { teamMembers && teamMembers.length > 0 ? teamMembers.sort((a, b) => {
          return ('' + a?.lastName).toUpperCase().localeCompare(b?.lastName.toUpperCase());
        })
        .filter((teamMember) => {
          return reviews && (!reviews[teamMember.id] || reviews[teamMember.id].length === 0 || !reviews[teamMember.id]?.reduce((status, review) => (status && review.status === 'submitted'), true));
        })
        .map((teamMember, i) => (
          <>
          <ListItem onClick={() => onTeamMemberSelected(teamMember?.id)}
            key={`teamMember-${teamMember?.id}`}
          >
            <ListItemAvatar key={`teamMember-lia-${teamMember?.id}`}>
              <Avatar src={getAvatarURL(teamMember?.workEmail)} />
            </ListItemAvatar>
            <ListItemText key={`teamMember-lit-${teamMember?.id}`} primary={teamMember?.firstName + " " + teamMember?.lastName} secondary={createSecondary(teamMember)} />
            <ListItemSecondaryAction>
              <Tooltip title="Request Feedback">
              <IconButton>
                <AddCommentIcon onClick={(e) => {
                  e.stopPropagation();
                  history.push(`/feedback/request?for=${teamMember?.id}`);
                }}/>
              </IconButton>
              </Tooltip>
            </ListItemSecondaryAction>
          </ListItem>
          <Divider key={`divider-${teamMember?.id}`}/>
          </>
        ))
        : null }
      </List>
      <Accordion style={{marginTop:"1rem"}}>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel1a-content"
          id="panel1a-header"
        >
          <Typography>Completed Reviews</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
            { teamMembers && teamMembers.length > 0 ? teamMembers.sort((a, b) => {
              return ('' + a?.lastName).toUpperCase().localeCompare(b?.lastName.toUpperCase());
            })
            .filter((teamMember) => {
              return reviews && reviews[teamMember.id] && reviews[teamMember.id].length !== 0 && reviews[teamMember.id]?.reduce((status, review) => (status && review.status === 'submitted'), true);
            })
            .map((teamMember, i) => (
              <>
              <ListItem onClick={() => onTeamMemberSelected(teamMember?.id)}
                key={`teamMember-${teamMember?.id}`}
              >
                <ListItemAvatar key={`teamMember-lia-${teamMember?.id}`}>
                  <Avatar src={getAvatarURL(teamMember?.workEmail)} />
                </ListItemAvatar>
                <ListItemText key={`teamMember-lit-${teamMember?.id}`} primary={teamMember?.firstName + " " + teamMember?.lastName} secondary={createSecondary(teamMember)} />
                <ListItemSecondaryAction>
                  <Tooltip title="Request Feedback"><IconButton>
                    <AddCommentIcon onClick={(e) => {
                      e.stopPropagation();
                      history.push(`/feedback/request?for=${teamMember?.id}`);
                    }}/>
                  </IconButton></Tooltip>
                </ListItemSecondaryAction>
              </ListItem>
              <Divider key={`divider-${teamMember?.id}`}/>
              </>
            ))
            : null }
          </List>
        </AccordionDetails>
      </Accordion>
      </>)}
      {!selectedTeamMember && loadingReviews.current && (<>
        <ListItem key="skeleton-period">
          <ListItemAvatar>
            <Skeleton animation="wave" variant="circular" width={40} height={40} />
          </ListItemAvatar>
          <ListItemText primary={(<Skeleton variant="text" sx={{ fontSize: '1rem' }} />)} secondary={(<Skeleton variant="text" sx={{ fontSize: '1rem' }} />)} />
        </ListItem>
      </>)}
      {!!selectedTeamMember && reviews && (
        <TeamMemberReview reloadReviews={reloadReviews} memberProfile={selectedMemberProfile} selfReview={selfReviews[selectedTeamMember]} reviews={reviews[selectedTeamMember]} />
      )}
      <SelectUserModal userLabel="Reviewer" open={newRequestOpen} onSelect={handleNewRequest} onClose={handleCloseNewRequest} />
    </Root>
  );
};

TeamReviews.propTypes = propTypes;
TeamReviews.displayName = displayName;

export default TeamReviews;
