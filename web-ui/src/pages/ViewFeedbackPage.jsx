import React, {useCallback, useContext, useEffect, useRef, useState} from 'react';
import {makeStyles} from '@material-ui/core/styles';
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import TextField from "@material-ui/core/TextField";
import FeedbackRequestCard from '../components/feedback_request_card/FeedbackRequestCard';
import Typography from "@material-ui/core/Typography";

import "./ViewFeedbackPage.css";
import {getFeedbackRequestsByCreator} from "../api/feedback";
import {AppContext} from "../context/AppContext";
import {selectCsrfToken, selectCurrentUserId, selectProfile} from "../context/selectors";
import {getFeedbackTemplate} from "../api/feedbacktemplate";

const useStyles = makeStyles({
  pageTitle: {
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
  textField: {
    width: "100%",
  },
  searchField: {
    width: "100%",
    alignSelf: "start",
    marginTop: "30px"
  },
  formControl: {
    marginRight: "1em",
  },
  notFoundMessage: {
    color: "gray",
    marginTop: "4em",
    textAlign: "center"
  }
});

const ViewFeedbackPage = () => {

  const classes = useStyles();
  const [feedbackRequests, setFeedbackRequests] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [searchFocused, setSearchFocused] = useState(false);
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUserId =  selectCurrentUserId(state);
  const gotRequests = useRef(false);
  const [sortValue, setSortValue] = useState("sent_date")

  const selectSortChangeHandler = (sortValue) => {
    setSortValue(sortValue)

  }

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
      //use returned feedback request information to then get template information, bind request
      //and associated template info together
      for (let i = 0; i < feedbackRequests.length; i++) {
        feedbackRequests[i].templateInfo = await getTemplateInfo(feedbackRequests[i].templateId);
      }
      return feedbackRequests;
    }

    getRequestAndTemplateInfo(currentUserId).then(requestList => {
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

      />
      ));
  }, [searchText, sortValue, feedbackRequests, classes.notFoundMessage]);

  return (
    <div className="view-feedback-page">
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
          <FormControl className={classes.textField}>
            <TextField
              id="select-time"
              select
              fullWidth
              size="small"
              label="Show requests sent within"
              value={"3mo"}
              variant="outlined"
            >
              <MenuItem value={"3mo"}>Past 3 months</MenuItem>
              <MenuItem value={"6mo"}>Past 6 months</MenuItem>
              <MenuItem value={"1yr"}>Past year</MenuItem>
              <MenuItem value={"all"}>All time</MenuItem>
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
              onChange={(e) => selectSortChangeHandler(e.target.value)}
              defaultValue={"sent_date"}
              size="small"
              label="Sort by"
              variant="outlined"
            >
              <MenuItem value={"submission_date"}>Date feedback was submitted</MenuItem>
              <MenuItem value={"sent_date"}>Date request was sent</MenuItem>
              <MenuItem value={"recipient_name_alphabetical"}>Recipient name (A-Z)</MenuItem>
              <MenuItem value={"recipient_name_reverse_alphabetical"}>Recipient name (Z-A)</MenuItem>
            </TextField>
          </FormControl>
        </div>
      </div>
      <div className="feedback-requests-list-container">
        {getFilteredFeedbackRequests()}
      </div>
    </div>
  )
}
export default ViewFeedbackPage;
