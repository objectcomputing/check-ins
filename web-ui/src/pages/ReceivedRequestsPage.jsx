import React, {useContext, useEffect, useState} from "react";
import {AppContext} from "../context/AppContext";
import {selectCsrfToken, selectCurrentUserId, selectProfile} from "../context/selectors";
import FormControl from "@mui/material/FormControl";
import TextField from "@mui/material/TextField";
import MenuItem from "@mui/material/MenuItem";
import makeStyles from '@mui/styles/makeStyles';
import Typography from "@mui/material/Typography";
import {Search as SearchIcon} from "@mui/icons-material";
import {Collapse, IconButton, InputAdornment} from "@mui/material";
import ReceivedRequestCard from "../components/received_request_card/ReceivedRequestCard";
import {getFeedbackRequestsByRecipient} from "../api/feedback";
import "./ReceivedRequestsPage.css";
import {UPDATE_TOAST} from "../context/actions";
import Divider from "@mui/material/Divider";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

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
    ['@media screen and (max-width: 840px)']: { // eslint-disable-line no-useless-computed-key
      marginBottom: "1em"
    },
  },
  formControl: {
    marginRight: "1em"
  },
  notFoundMessage: {
    color: "gray",
    marginTop: "4em",
    textAlign: "center"
  },
  expandClose: {
    transform: 'rotate(0deg)',
    marginLeft: 'auto',
    transition: "transform 0.1s linear",
  },
  expandOpen: {
    transform: 'rotate(180deg)',
    transition: "transform 0.1s linear",
    marginLeft: 'auto',
  }
});

const SortOption = {
  DATE_DESCENDING: "date_descending",
  DATE_ASCENDING: "date_ascending"
};

const ReceivedRequestsPage = () => {
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const classes = useStyles();
  const [searchText, setSearchText] = useState("");
  const [sortValue, setSortValue] = useState(SortOption.DATE_DESCENDING);
  const [receivedRequests, setReceivedRequests] = useState([]);
  const [filteredReceivedRequests, setFilteredReceivedRequests] = useState([]);
  const [submittedRequests, setSubmittedRequests] = useState([]);
  const [filteredSubmittedRequests, setFilteredSubmittedRequests] = useState([]);
  const [receivedRequestsExpanded, setReceivedRequestsExpanded] = useState(true);
  const [submittedRequestsExpanded, setSubmittedRequestsExpanded] = useState(false);
  const currentUserId =  selectCurrentUserId(state);

  useEffect(() => {
    const getAllFeedbackRequests = async () => {
      let res = await getFeedbackRequestsByRecipient(currentUserId, csrf);
      if (res && res.payload && res.payload.data && !res.error) {
        return res.payload.data;
      } else {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: res.error
          },
        });
      }
    }

    if (csrf && currentUserId) {
      getAllFeedbackRequests().then((data) => {
        if (data) {
          setReceivedRequests(data.filter((req) => req.submitDate === undefined));
          setSubmittedRequests(data.filter((req) => req.submitDate && req.submitDate.length === 3));
        }
      });
    }

  }, [csrf, currentUserId]);

  useEffect(() => {
    let filteredReceived = [...receivedRequests];
    let filteredSubmitted = [...submittedRequests];

    // Search for intersection of multiple queries separated by commas
    const queries = searchText.split(",").map((search) => search.trim().toLowerCase());

    if (searchText.trim()) {
      for (let query of queries) {
        filteredReceived = filteredReceived.filter((request) => {
          const creatorName = selectProfile(state, request.creatorId).name.toLowerCase();
          const requesteeName = selectProfile(state, request.requesteeId).name.toLowerCase();
          return creatorName.includes(query) || requesteeName.includes(query);
        });

        filteredSubmitted = filteredSubmitted.filter((request) => {
          const creatorName = selectProfile(state, request.creatorId).name.toLowerCase();
          const requesteeName = selectProfile(state, request.requesteeId).name.toLowerCase();
          return creatorName.includes(query) || requesteeName.includes(query);
        });
      }
    }

    // Sort according to selected sort option
    let sortMethod;
    if (sortValue === SortOption.DATE_ASCENDING) {
      sortMethod = (a, b) => (new Date(a.sendDate) > new Date(b.sendDate) ? 1 : -1);
    } else if (sortValue === SortOption.DATE_DESCENDING) {
      sortMethod = (a, b) => (new Date(a.sendDate) > new Date(b.sendDate) ? -1 : 1);
    }

    filteredReceived.sort(sortMethod);
    filteredSubmitted.sort(sortMethod);

    setFilteredReceivedRequests(filteredReceived);
    setFilteredSubmittedRequests(filteredSubmitted);

  }, [state, receivedRequests, submittedRequests, searchText, sortValue]);

  return (
    <div className="received-requests-page">
      <div className="received-requests-header-container">
        <Typography variant="h4" className={classes.pageTitle}>Received Feedback Requests</Typography>
        <div className="received-requests-filter-container">
          <TextField
            className={classes.searchField}
            placeholder="Alice, Bob, Eve..."
            label="Search..."
            value={searchText}
            onChange={(event) => {
              setSearchText(event.target.value);
              setReceivedRequestsExpanded(true);
              setSubmittedRequestsExpanded(true);
            }}
            InputProps={{
              endAdornment: <InputAdornment style={{color: "gray"}} position="end"><SearchIcon/></InputAdornment>
            }}
          />

          <FormControl
            className={classes.textField}
            value={sortValue}
          >
            <TextField
              id="select-sort-method"
              select
              size="small"
              label="Sort by"
              fullWidth
              onChange={(e) => setSortValue(e.target.value)}
              defaultValue={SortOption.DATE_ASCENDING}
              variant="outlined"
            >
              <MenuItem value={SortOption.DATE_DESCENDING}>Send date (least recent)</MenuItem>
              <MenuItem value={SortOption.DATE_ASCENDING}>Send date (most recent)</MenuItem>
            </TextField>
          </FormControl>
        </div>
      </div>
      <div className="request-section-header">
        <Typography variant="h5">Received Requests</Typography>
        <IconButton
          onClick={() => setReceivedRequestsExpanded(!receivedRequestsExpanded)}
          aria-label="show more"
          className={receivedRequestsExpanded ? classes.expandOpen : classes.expandClose}
          size="large">
          <ExpandMoreIcon/>
        </IconButton>
      </div>
      <Divider/>
      <Collapse in={!receivedRequestsExpanded} timeout="auto" unmountOnExit>
        <div style={{marginTop: "1em"}} className="no-requests-message">
          <Typography variant="body1">{receivedRequests.length} received request{receivedRequests.length === 1 ? "" : "s"} currently hidden</Typography>
        </div>
      </Collapse>
      <Collapse in={receivedRequestsExpanded} timeout="auto" unmountOnExit>
        <div className="received-requests-container">
          {receivedRequests.length === 0 &&
          <div className="no-requests-message"><Typography variant="body1">No received feedback requests</Typography></div>
          }
          {receivedRequests.length > 0 && filteredReceivedRequests.length === 0 &&
          <div className="no-requests-message"><Typography variant="body1">No matching feedback requests</Typography></div>
          }
          {filteredReceivedRequests.map((request) => (
            <ReceivedRequestCard key={request.id} request={request}/>
          ))}
        </div>
      </Collapse>
      <div className="request-section-header">
        <Typography variant="h5">Submitted Requests</Typography>
        <IconButton
          onClick={() => setSubmittedRequestsExpanded(!submittedRequestsExpanded)}
          aria-label="show more"
          className={submittedRequestsExpanded ? classes.expandOpen : classes.expandClose}
          size="large">
          <ExpandMoreIcon/>
        </IconButton>
      </div>
      <Divider/>
      <Collapse in={!submittedRequestsExpanded} timeout="auto" unmountOnExit>
        <div style={{marginTop: "1em"}} className="no-requests-message">
          <Typography variant="body1">{submittedRequests.length} submitted request{submittedRequests.length === 1 ? "" : "s"} currently hidden</Typography>
        </div>
      </Collapse>
      <Collapse in={submittedRequestsExpanded} timeout="auto" unmountOnExit>
        <div className="submitted-requests-container">
          {submittedRequests.length === 0 &&
          <div className="no-requests-message"><Typography variant="body1">No submitted feedback requests</Typography></div>
          }
          {submittedRequests.length > 0 && filteredSubmittedRequests.length === 0 &&
          <div className="no-requests-message"><Typography variant="body1">No submitted feedback requests</Typography></div>
          }
          {filteredSubmittedRequests.map((request) => (
            <ReceivedRequestCard key={request.id} request={request}/>
          ))}
        </div>
      </Collapse>
    </div>
  );

}

export default ReceivedRequestsPage;