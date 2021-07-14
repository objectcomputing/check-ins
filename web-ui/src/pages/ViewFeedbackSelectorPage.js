import React, {useCallback, useEffect, useState} from 'react';
import {makeStyles} from '@material-ui/core/styles';
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import TextField from "@material-ui/core/TextField";
import FeedbackRequestCard from '../components/feedback_request_card/FeedbackRequestCard';
import Typography from "@material-ui/core/Typography";

const useStyles = makeStyles({
  textField: {
    width: "15%",
    ['@media (max-width:769px)']: { // eslint-disable-line no-useless-computed-key
      width: '40%',
    },
    marginTop: "1.15em",
    marginRight: "3em",
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

const ViewFeedbackSelectorPage = () => {

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
    <React.Fragment>
      <div className="input-row">
        <TextField
          className={classes.textField}
          placeholder="Search..."
          onChange={(event) => setSearchText(event.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment className={classes.root} position="start">
                <Search/>
              </InputAdornment>
            ),
          }}
        />
        <FormControl className={classes.formControl}>
          <TextField
            id="select-time"
            select
            label="Filter by"
            value={"Past 3"}
            variant="outlined"
          >
            <MenuItem value={"Past 3"}>Past 3 months</MenuItem>
            <MenuItem value={"Past 6"}>Past 6 months</MenuItem>
            <MenuItem value={"Past Year"}>Past year</MenuItem>
            <MenuItem value={"All time"}>All time</MenuItem>
          </TextField>
        </FormControl>

        <FormControl>
          <TextField
            id="select-sort-method"
            select
            label="Sort by"
            value={"Submission"}
            variant="outlined"
          >
            <MenuItem value={"Requested"}>Submission date</MenuItem>
            <MenuItem value={"Submission"}>Request sent date</MenuItem>
          </TextField>
        </FormControl>
      </div>
      <div className="feedback-requests-list-container">
        {getFilteredFeedbackRequests()}
      </div>
    </React.Fragment>
  )
}
export default ViewFeedbackSelectorPage;
