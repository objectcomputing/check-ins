import React, {useEffect, useContext, useCallback, useState, useRef} from "react";
import PropTypes from "prop-types";
import { useLocation, useHistory } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import Avatar from '@mui/material/Avatar';
import Divider from '@mui/material/Divider';
import queryString from 'query-string';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Typography from "@mui/material/Typography";
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import TeamMemberReview from "./TeamMemberReview";
import { UPDATE_REVIEW_PERIODS } from "../../context/actions";
import { AppContext } from "../../context/AppContext";
import { getReviewPeriods } from "../../api/reviewperiods.js";
import { createFeedbackRequest, findReviewRequestsByPeriodAndTeamMember, findSelfReviewRequestsByPeriodAndTeamMember } from "../../api/feedback.js";
import {
  selectCsrfToken,
  selectReviewPeriod,
  selectProfile,
  selectCurrentUser,
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
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: "0 0 1em 0",
  },
}));

const TeamReviews = ({ teamMembers, periodId }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const location = useLocation();
  const history = useHistory();
  const currentUser = selectCurrentUser(state);
  const period = selectReviewPeriod(state, periodId);
  const [selfReviews, setSelfReviews] = useState({});
  const [reviews, setReviews] = useState(null);
  const [query, setQuery] = useState({});
  const [selectedTeamMember, setSelectedTeamMember] = useState(null);
  const selectedMemberProfile = selectProfile(state, selectedTeamMember);
  const loadingReviews = useRef(false);
  const loadedReviews = useRef(false);
  const creatingReview = useRef(false);

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

  useEffect(() => {
    let newSelfReviews = {};
    let newReviews = {};
    const getSelfReviewRequest = async (teamMember) => {
      const res = await findSelfReviewRequestsByPeriodAndTeamMember(period, teamMember.id, csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && data.length > 0) {
        newSelfReviews[teamMember.id] = data[0];
      }
    };

    const getReviewRequest = async (teamMember) => {
      const res = await findReviewRequestsByPeriodAndTeamMember(period, teamMember.id, csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && data.length > 0) {
        newReviews[teamMember.id] = data;
      }
    };

    if (csrf && teamMembers && teamMembers.length > 0 && period && !loadingReviews.current) {
      loadingReviews.current = true;
      const promises = teamMembers.map(getSelfReviewRequest);
      promises.push(...teamMembers.map(getReviewRequest));

      Promise.all(promises).then((res) => {
        setSelfReviews({...newSelfReviews});
        setReviews({...newReviews});
        loadingReviews.current = false;
        loadedReviews.current = true;
      });
    }
  }, [csrf, period, teamMembers]);

  return teamMembers && teamMembers.length > 0 && (
    <Root>
      {!selectedTeamMember && (
      <>
      <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
        { teamMembers.length > 0 ? teamMembers.sort((a, b) => {
          return ('' + a?.lastName).toUpperCase().localeCompare(b?.lastName.toUpperCase());
        })
        .filter((teamMember) => {
          return reviews && !reviews[teamMember.id]?.reduce((status, review) => (status && review.status === 'submitted'), true);
        })
        .map((teamMember, i) => (
          <>
          <ListItem onClick={() => onTeamMemberSelected(teamMember?.id)}
            key={`teamMember-${teamMember?.id}`}
          >
            <ListItemAvatar key={`teamMember-lia-${teamMember?.id}`}>
              <Avatar src={getAvatarURL(teamMember?.workEmail)} />
            </ListItemAvatar>
            <ListItemText key={`teamMember-lit-${teamMember?.id}`} primary={teamMember?.firstName + " " + teamMember?.lastName} secondary={getReviewStatus(teamMember?.id) + ", Self-review: "+getSelfReviewStatus(teamMember?.id)} />
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
            { teamMembers.length > 0 ? teamMembers.sort((a, b) => {
              return ('' + a?.lastName).toUpperCase().localeCompare(b?.lastName.toUpperCase());
            })
            .filter((teamMember) => {
              return reviews && reviews[teamMember.id]?.reduce((status, review) => (status && review.status === 'submitted'), true);
            })
            .map((teamMember, i) => (
              <>
              <ListItem onClick={() => onTeamMemberSelected(teamMember?.id)}
                key={`teamMember-${teamMember?.id}`}
              >
                <ListItemAvatar key={`teamMember-lia-${teamMember?.id}`}>
                  <Avatar src={getAvatarURL(teamMember?.workEmail)} />
                </ListItemAvatar>
                <ListItemText key={`teamMember-lit-${teamMember?.id}`} primary={teamMember?.firstName + " " + teamMember?.lastName} secondary={getReviewStatus(teamMember?.id) + ", Self-review: "+getSelfReviewStatus(teamMember?.id)} />
              </ListItem>
              <Divider key={`divider-${teamMember?.id}`}/>
              </>
            ))
            : null }
          </List>
        </AccordionDetails>
      </Accordion>
      </>)}
      {!!selectedTeamMember && reviews && (
        <TeamMemberReview memberProfile={teamMembers.find((member) => member?.id === selectedTeamMember)} selfReview={selfReviews[selectedTeamMember]} reviews={reviews[selectedTeamMember]} />
      )}
    </Root>
  );
};

TeamReviews.propTypes = propTypes;
TeamReviews.displayName = displayName;

export default TeamReviews;
