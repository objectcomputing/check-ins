import React, {useCallback, useEffect, useState} from 'react';
import {makeStyles} from '@material-ui/core/styles';
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import TextField from "@material-ui/core/TextField";
import FeedbackRequestCard from '../components/feedback_request_card/FeedbackRequestCard';
import Typography from "@material-ui/core/Typography";

import "./ViewFeedbackPage.css";

const useStyles = makeStyles({
  pageTitle: {
    paddingRight: "0.4em",
    minWidth: "330px",
    ["@media screen and (max-width: 600px)"]: { // eslint-disable-line no-useless-computed-key
      fontSize: "30px",
      width: "100%",
      padding: 0,
      textAlign: "center"
    }
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

const sampleFeedbackRequests = [
  {
    id: 1,
    requestee: "Slim Jim",
    requesteeTitle: "Member",
    template: "Dev Template 1"
  },
  {
    id: 2,
    requestee: "John Doe",
    requesteeTitle: "Engineer",
    template: "Sample Template"
  },
  {
    id: 3,
    requestee: "Bill PDL",
    requesteeTitle: "Marketing",
    template: "Ad Hoc",
  }
];

const ViewFeedbackPage = () => {

  const classes = useStyles();
  const [feedbackRequests, setFeedbackRequests] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [searchFocused, setSearchFocused] = useState(false);

  useEffect(() => {
    setFeedbackRequests(sampleFeedbackRequests);
  }, []);

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
          filtered = filtered.filter((request) => (
            request.requestee?.toLowerCase().includes(query.trim().toLowerCase()) ||
            request.template?.toLowerCase().includes(query.trim().toLowerCase())
          ));
        }
      });
      if (filtered.length === 0) {
        return <Typography className={classes.notFoundMessage} variant="h5">No matching feedback requests</Typography>
      } else {
        requestsToDisplay = filtered;
      }
    }

    return requestsToDisplay.map((request) => (
      <FeedbackRequestCard
        key={request.id}
        requesteeName={request.requestee}
        requesteeTitle={request.requesteeTitle}
        templateName={request.template}/>
    ))
  }, [searchText, feedbackRequests, classes.notFoundMessage]);

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

          <FormControl className={classes.textField}>
            <TextField
              id="select-sort-method"
              select
              fullWidth
              size="small"
              label="Sort by"
              value={"sent_date"}
              variant="outlined"
            >
              <MenuItem value={"submission_date"}>Date feedback was submitted</MenuItem>
              <MenuItem value={"sent_date"}>Date request was sent</MenuItem>
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
