import React, {useCallback, useContext, useEffect, useRef, useState} from 'react';
import { styled } from '@mui/material/styles';
import InputAdornment from "@mui/material/InputAdornment";
import Search from "@mui/icons-material/Search";
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import TextField from "@mui/material/TextField";
import FeedbackRequestCard from '../components/feedback_request_card/FeedbackRequestCard';
import Typography from "@mui/material/Typography";
import "./ViewFeedbackPage.css";
import {getFeedbackRequestsByCreator} from "../api/feedback";
import {AppContext} from "../context/AppContext";
import {selectCsrfToken, selectCurrentUserId, selectProfile} from "../context/selectors";
import {getFeedbackTemplate} from "../api/feedbacktemplate";

const PREFIX = 'ViewFeedbackPage';
const classes = {
  pageTitle: `${PREFIX}-pageTitle`,
  textField: `${PREFIX}-textField`,
  searchField: `${PREFIX}-searchField`,
  formControl: `${PREFIX}-formControl`,
  notFoundMessage: `${PREFIX}-notFoundMessage`
};

const Root = styled('div')({
  [`& .${classes.pageTitle}`]: {
    paddingRight: "0.4em",
    minWidth: "330px",
    ['@media screen and (max-width: 600px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "30px",
      width: "100%",
      padding: 0,
      textAlign: "center",
      minWidth: "10px"
    },
  },
  [`& .${classes.textField}`]: {
    width: "100%",
  },
  [`& .${classes.searchField}`]: {
    width: "100%",
    alignSelf: "start",
    marginTop: "30px"
  },
  [`& .${classes.formControl}`]: {
    marginRight: "1em",
  },
  [`& .${classes.notFoundMessage}`]: {
    color: "gray",
    marginTop: "4em",
    textAlign: "center"
  }
});

const SortOption = {
  SENT_DATE: "sent_date",
  SUBMISSION_DATE: "submission_date",
  RECIPIENT_NAME_ALPHABETICAL: "recipient_name_alphabetical",
  RECIPIENT_NAME_REVERSE_ALPHABETICAL: "recipient_name_reverse_alphabetical"
};

const DateRange = {
  THREE_MONTHS: "3mo",
  SIX_MONTHS: "6mo",
  ONE_YEAR: "1yr",
  ALL_TIME: "all"
};

const ViewFeedbackPage = () => {

  const [feedbackRequests, setFeedbackRequests] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [searchFocused, setSearchFocused] = useState(false);
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUserId =  selectCurrentUserId(state);
  const gotRequests = useRef(false);
  const [sortValue, setSortValue] = useState(SortOption.SENT_DATE);
  const [dateRange, setDateRange] = useState(DateRange.THREE_MONTHS);

  useEffect(() => {
    const getFeedbackRequests = async(creatorId) => {
      if (csrf) {
        let res = await getFeedbackRequestsByCreator(creatorId, csrf);
        return res && res.payload && res.payload.data && !res.error
          ? res.payload.data
          : null;
      }
    }
    const getTemplateInfo = async(templateId) => {
      if (csrf) {
        let res = await getFeedbackTemplate(templateId, csrf);
        return res && res.payload && res.payload.data && !res.error
          ? res.payload.data
          : null;
      }
    }

    if (!currentUserId || gotRequests.current) return;

    const getRequestAndTemplateInfo = async (currentUserId) => {
      //get feedback requests
      const feedbackRequests = await getFeedbackRequests(currentUserId);
      return feedbackRequests;
    }

    const getTemplates = async (feedbackRequests) => {
      //use returned feedback request information to then get template information, bind request
      //and associated template info together
      const templateReqs = [];
      const templateIds = [];
      for(let i=0; i<feedbackRequests.length; i++) {
        if(!templateIds.includes(feedbackRequests[i].templateId)) {
          templateIds.push(feedbackRequests[i].templateId);
          templateReqs.push(getTemplateInfo(feedbackRequests[i].templateId));
        }
      }
      let templates = await Promise.all(templateReqs);
      templates = templates.reduce((map, template) => {
        map[template.id] = template;
        return map;
      }, {});
      feedbackRequests.forEach((request) => {
        request.templateInfo = templates[request.templateId];
      });
      return feedbackRequests;
    }

    getRequestAndTemplateInfo(currentUserId).then(getTemplates).then(requestList => {
      if (requestList) {
        let groups = [];
        for (let i = 0; i < requestList.length; i++) {
          let request = requestList[i];
          let filterTemp = groups.filter(element => element.requesteeId === request.requesteeId && element.templateId === request.templateId);
          //if top level organizational element does not already exist, create one
          if (filterTemp.length === 0) {
            const requesteeName = selectProfile(state, request.requesteeId)?.name;
            let newElement = {
              requesteeId: request.requesteeId,
              requesteeName: requesteeName ? requesteeName : "",
              templateId: request.templateId,
              responses: [request],
              templateInfo: request.templateInfo
            };
            groups.push(newElement);
          } else {
            //else, push response into existing responses array of top level elements
            const existingGroup = groups.findIndex((element) => element.requesteeId === filterTemp[0].requesteeId && element.templateId === filterTemp[0].templateId);
            groups[existingGroup].responses.push(request);
          }
        }
        setFeedbackRequests(groups);
      }
    });
  }, [currentUserId, csrf, state]);

  const getFilteredFeedbackRequests = useCallback(() => {
    if (feedbackRequests === undefined) {
      return null;
    } else if (feedbackRequests.length === 0) {
      return <Typography className={classes.notFoundMessage} variant="h5">No feedback requests found</Typography>
    }

    let requestsToDisplay = feedbackRequests;
    if (searchText.trim()) {
      // allow user to query multiple entries via comma-separated list
      const queryList = searchText.split(",");
      let filtered = feedbackRequests;
      queryList.forEach((query) => {
        if (query.trim()) {
          filtered = filtered?.filter((request) => {
            const requestee = request?.requesteeName;
            const template = request?.templateInfo.title;
            return requestee.toLowerCase().includes(query.trim().toLowerCase()) ||
            template?.toLowerCase().includes(query.trim().toLowerCase())
          });
        }
      });
      if (filtered.length === 0) {
        return <Typography className={classes.notFoundMessage} variant="h5">No matching feedback requests</Typography>
      } else {
        requestsToDisplay = filtered;
      }
    }

    return requestsToDisplay?.map((request) => (
      <FeedbackRequestCard
        key={`${request?.requesteeId}-${request?.templateId}`}
        requesteeId={request?.requesteeId}
        templateName={request?.templateInfo?.title}
        responses={request?.responses}
        sortType={sortValue}
        dateRange={dateRange}
      />
      ));
  }, [searchText, sortValue, dateRange, feedbackRequests]);

  return (
    <Root className="view-feedback-page">
      <div className="view-feedback-header-container">
        <Typography className={classes.pageTitle} variant="h4">Feedback Requests</Typography>
        <div className="input-row">
          <TextField
            className={classes.searchField}
            placeholder="Search..."
            helperText={searchFocused ? "Hint: Use commas to search for both name and template" : " "}
            onFocus={() => setSearchFocused(true)}
            onBlur={() => setSearchFocused(false)}
            onChange={(event) => setSearchText(event.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment style={{color: "gray"}} position="start">
                  <Search/>
                </InputAdornment>
              ),
            }}
          />
          <FormControl
            className={classes.textField}
            value={dateRange}
          >
            <TextField
              id="select-time"
              select
              fullWidth
              size="small"
              label="Show requests sent within"
              onChange={(e) => setDateRange(e.target.value)}
              defaultValue={DateRange.THREE_MONTHS}
              variant="outlined"
            >
              <MenuItem value={DateRange.THREE_MONTHS}>Past 3 months</MenuItem>
              <MenuItem value={DateRange.SIX_MONTHS}>Past 6 months</MenuItem>
              <MenuItem value={DateRange.ONE_YEAR}>Past year</MenuItem>
              <MenuItem value={DateRange.ALL_TIME}>All time</MenuItem>
            </TextField>
          </FormControl>

          <FormControl
            className={classes.textField}
            value={sortValue}
          >
            <TextField
              id="select-sort-method"
              select
              fullWidth
              size="small"
              label="Sort by"
              onChange={(e) => setSortValue(e.target.value)}
              defaultValue={SortOption.SENT_DATE}
              variant="outlined"
            >
              <MenuItem value={SortOption.SUBMISSION_DATE}>Date feedback was submitted</MenuItem>
              <MenuItem value={SortOption.SENT_DATE}>Date request was sent</MenuItem>
              <MenuItem value={SortOption.RECIPIENT_NAME_ALPHABETICAL}>Recipient name (A-Z)</MenuItem>
              <MenuItem value={SortOption.RECIPIENT_NAME_REVERSE_ALPHABETICAL}>Recipient name (Z-A)</MenuItem>
            </TextField>
          </FormControl>
        </div>
      </div>
      <div className="feedback-requests-list-container">
        {getFilteredFeedbackRequests()}
      </div>
    </Root>
  );
}

export default ViewFeedbackPage;
