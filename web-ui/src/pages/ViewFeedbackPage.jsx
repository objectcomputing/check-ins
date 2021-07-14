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
  textField: {

  },
  formControl: {
    marginRight: "1em",
  },
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

  useEffect(() => {
    setFeedbackRequests(sampleFeedbackRequests);
  }, []);

  const getFilteredFeedbackRequests = useCallback(() => {
    if (feedbackRequests === undefined) {
      return null;
    } else if (feedbackRequests.length === 0) {
      return <Typography variant="h2">No feedback requests found</Typography>
    }

    let requestsToDisplay = feedbackRequests;
    if (searchText) {
      const filtered = feedbackRequests.filter((request) => (
        request.requestee?.toLowerCase().includes(searchText) ||
        request.template?.toLowerCase().includes(searchText)
      ));
      if (filtered.length === 0) {
        return <Typography variant="h2">No matching feedback requests</Typography>
      } else {
        requestsToDisplay = filtered;
      }
    }

    return requestsToDisplay.map((request) => (
      <FeedbackRequestCard
        requesteeName={request.requestee}
        requesteeTitle={request.requesteeTitle}
        templateName={request.template}/>
    ))
  }, [searchText, feedbackRequests]);

  return (
    <div className="view-feedback-page">
      <div className="input-row">
        <TextField
          className={classes.textField}
          placeholder="Search..."
          onChange={(event) => setSearchText(event.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment style={{color: "gray"}} position="start">
                <Search/>
              </InputAdornment>
            ),
          }}
        />
        <FormControl className={classes.formControl}>
          <TextField
            id="select-time"
            select
            fullWidth
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

        <FormControl>
          <TextField
            id="select-sort-method"
            select
            fullWidth
            label="Sort by"
            value={"sent_date"}
            variant="outlined"
          >
            <MenuItem value={"submission_date"}>Date feedback was submitted</MenuItem>
            <MenuItem value={"sent_date"}>Date request was sent</MenuItem>
          </TextField>
        </FormControl>
      </div>
      <div className="feedback-requests-list-container">
        {getFilteredFeedbackRequests()}
      </div>
    </div>
  )
}
export default ViewFeedbackPage;
