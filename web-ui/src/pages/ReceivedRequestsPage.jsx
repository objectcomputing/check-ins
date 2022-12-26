import React, { useContext, useEffect, useState } from "react";
import { styled } from "@mui/material/styles";
import { AppContext } from "../context/AppContext";
import {
  selectCsrfToken,
  selectCurrentUserId,
  selectProfile,
} from "../context/selectors";
import FormControl from "@mui/material/FormControl";
import TextField from "@mui/material/TextField";
import MenuItem from "@mui/material/MenuItem";
import Typography from "@mui/material/Typography";
import { Search as SearchIcon } from "@mui/icons-material";
import { Collapse, IconButton, InputAdornment } from "@mui/material";
import ReceivedRequestCard from "../components/received_request_card/ReceivedRequestCard";
import { getFeedbackRequestsByRecipient } from "../api/feedback";
import "./ReceivedRequestsPage.css";
import { UPDATE_TOAST } from "../context/actions";
import Divider from "@mui/material/Divider";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader";

const PREFIX = "ReceivedRequestsPage";
const classes = {
  pageTitle: `${PREFIX}-pageTitle`,
  textField: `${PREFIX}-textField`,
  searchField: `${PREFIX}-searchField`,
  formControl: `${PREFIX}-formControl`,
  notFoundMessage: `${PREFIX}-notFoundMessage`,
  expandClose: `${PREFIX}-expandClose`,
  expandOpen: `${PREFIX}-expandOpen`,
};

const Root = styled("div")({
  [`& .${classes.pageTitle}`]: {
    paddingRight: "0.4em",
    minWidth: "330px",
    ["@media screen and (max-width: 600px)"]: {
      // eslint-disable-line no-useless-computed-key
      fontSize: "30px",
      width: "100%",
      padding: 0,
      textAlign: "center",
      minWidth: "10px",
    },
  },
  [`& .${classes.textField}`]: {
    width: "100%",
  },
  [`& .${classes.searchField}`]: {
    width: "100%",
    ["@media screen and (max-width: 840px)"]: {
      // eslint-disable-line no-useless-computed-key
      marginBottom: "1em",
    },
  },
  [`& .${classes.formControl}`]: {
    marginRight: "1em",
  },
  [`& .${classes.notFoundMessage}`]: {
    color: "gray",
    marginTop: "4em",
    textAlign: "center",
  },
  [`& .${classes.expandClose}`]: {
    transform: "rotate(0deg)",
    marginLeft: "auto",
    transition: "transform 0.1s linear",
  },
  [`& .${classes.expandOpen}`]: {
    transform: "rotate(180deg)",
    transition: "transform 0.1s linear",
    marginLeft: "auto",
  },
});

const SortOption = {
  SEND_DATE_DESCENDING: "send_date_descending",
  SEND_DATE_ASCENDING: "send_date_ascending",
  DUE_DATE: "due_date",
};

const ReceivedRequestsPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUserId = selectCurrentUserId(state);

  const [searchText, setSearchText] = useState("");
  const [sortValue, setSortValue] = useState(SortOption.SEND_DATE_DESCENDING);
  const [isLoading, setIsLoading] = useState(true);

  const [canceledRequests, setCanceledRequests] = useState([]);
  const [receivedRequests, setReceivedRequests] = useState([]);
  const [submittedRequests, setSubmittedRequests] = useState([]);

  const [filteredCanceledRequests, setFilteredCanceledRequests] = useState([]);
  const [filteredReceivedRequests, setFilteredReceivedRequests] = useState([]);
  const [filteredSubmittedRequests, setFilteredSubmittedRequests] = useState(
    []
  );

  const [canceledRequestsExpanded, setCanceledRequestsExpanded] =
    useState(false);
  const [receivedRequestsExpanded, setReceivedRequestsExpanded] =
    useState(true);
  const [submittedRequestsExpanded, setSubmittedRequestsExpanded] =
    useState(false);

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
            toast: res.error,
          },
        });
      }
    };

    if (csrf && currentUserId) {
      getAllFeedbackRequests().then((data) => {
        if (data) {
          setCanceledRequests(data.filter((req) => req.status === "canceled"));
          setReceivedRequests(
            data.filter(
              (req) => req.submitDate === undefined && req.status !== "canceled"
            )
          );
          setSubmittedRequests(
            data.filter(
              (req) =>
                req.submitDate &&
                req.submitDate.length === 3 &&
                req.status !== "canceled"
            )
          );
          setIsLoading(false);
        }
      });
    }
  }, [csrf, currentUserId]);
  useEffect(() => {
    let filteredCanceled = [...canceledRequests];
    let filteredReceived = [...receivedRequests];
    let filteredSubmitted = [...submittedRequests];

    // Search for intersection of multiple queries separated by commas
    const queries = searchText
      .split(",")
      .map((search) => search.trim().toLowerCase());

    if (searchText.trim()) {
      for (let query of queries) {
        const setFiltered = (filteredOption) => {
          filteredOption = filteredOption.filter((request) => {
            const creatorName = selectProfile(
              state,
              request.creatorId
            ).name.toLowerCase();
            const requesteeName = selectProfile(
              state,
              request.requesteeId
            ).name.toLowerCase();
            return creatorName.includes(query) || requesteeName.includes(query);
          });
        };
        setFiltered(filteredCanceled);
        setFiltered(filteredReceived);
        setFiltered(filteredSubmitted);
        // filteredCanceled = filteredCanceled.filter((request) => {
        //   const creatorName = selectProfile(state, request.creatorId).name.toLowerCase();
        //   const requesteeName = selectProfile(state, request.requesteeId).name.toLowerCase();
        //   return creatorName.includes(query) || requesteeName.includes(query);
        // });

        // filteredReceived = filteredReceived.filter((request) => {
        //   const creatorName = selectProfile(state, request.creatorId).name.toLowerCase();
        //   const requesteeName = selectProfile(state, request.requesteeId).name.toLowerCase();
        //   return creatorName.includes(query) || requesteeName.includes(query);
        // });

        // filteredSubmitted = filteredSubmitted.filter((request) => {
        //   const creatorName = selectProfile(state, request.creatorId).name.toLowerCase();
        //   const requesteeName = selectProfile(state, request.requesteeId).name.toLowerCase();
        //   return creatorName.includes(query) || requesteeName.includes(query);
        // });
      }
    }

    // Sort according to selected sort option
    let sortMethod;
    switch (sortValue) {
      case SortOption.SEND_DATE_ASCENDING:
        sortMethod = (a, b) =>
          new Date(a.sendDate) > new Date(b.sendDate) ? 1 : -1;
        break;
      case SortOption.SEND_DATE_DESCENDING:
        sortMethod = (a, b) =>
          new Date(a.sendDate) > new Date(b.sendDate) ? -1 : 1;
        break;
      case SortOption.DUE_DATE:
        sortMethod = (a, b) => {
          if (a.dueDate && b.dueDate) {
            return new Date(a.dueDate) > new Date(b.dueDate) ? 1 : -1;
          }
          return !a.dueDate && b.dueDate ? 1 : -1;
        };
        break;
      default:
        console.warn(
          `Invalid sort option ${sortValue} provided for received requests`
        );
    }

    filteredCanceled.sort(sortMethod);
    filteredReceived.sort(sortMethod);
    filteredSubmitted.sort(sortMethod);

    setFilteredCanceledRequests(filteredCanceled);
    setFilteredReceivedRequests(filteredReceived);
    setFilteredSubmittedRequests(filteredSubmitted);
  }, [
    state,
    canceledRequests,
    receivedRequests,
    submittedRequests,
    searchText,
    sortValue,
  ]);

  return (
    <Root className="received-requests-page">
      <div className="received-requests-header-container">
        <Typography variant="h4" className={classes.pageTitle}>
          Received Feedback Requests
        </Typography>
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
              endAdornment: (
                <InputAdornment style={{ color: "gray" }} position="end">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />

          <FormControl className={classes.textField} value={sortValue}>
            <TextField
              id="select-sort-method"
              select
              size="small"
              label="Sort by"
              fullWidth
              onChange={(e) => setSortValue(e.target.value)}
              defaultValue={SortOption.SEND_DATE_ASCENDING}
              variant="outlined"
            >
              <MenuItem value={SortOption.SEND_DATE_DESCENDING}>
                Send date (least recent)
              </MenuItem>
              <MenuItem value={SortOption.SEND_DATE_ASCENDING}>
                Send date (most recent)
              </MenuItem>
              <MenuItem value={SortOption.DUE_DATE}>Due date</MenuItem>
            </TextField>
          </FormControl>
        </div>
      </div>
      <div className="request-section-header">
        <Typography variant="h5">Received Requests</Typography>
        <IconButton
          onClick={() => setReceivedRequestsExpanded(!receivedRequestsExpanded)}
          aria-label="show more"
          className={
            receivedRequestsExpanded ? classes.expandOpen : classes.expandClose
          }
          size="large"
        >
          <ExpandMoreIcon />
        </IconButton>
      </div>
      <Divider />
      <Collapse in={!receivedRequestsExpanded} timeout="auto" unmountOnExit>
        <div style={{ marginTop: "1em" }} className="no-requests-message">
          <Typography variant="body1">
            {receivedRequests.length} received request
            {receivedRequests.length === 1 ? "" : "s"} currently hidden
          </Typography>
        </div>
      </Collapse>
      <Collapse in={receivedRequestsExpanded} timeout="auto" unmountOnExit>
        {isLoading && (
          <div style={{ marginTop: "1em" }}>
            {Array.from({ length: 1 }).map((_, index) => (
              <SkeletonLoader key={index} type="received_requests" />
            ))}
          </div>
        )}
        {!isLoading && (
          <div className="received-requests-container">
            {receivedRequests.length === 0 ||
              (receivedRequests.length > 0 &&
                filteredReceivedRequests.length === 0 && (
                  <div className="no-requests-message">
                    <Typography variant="body1">
                      No received feedback requests
                    </Typography>
                  </div>
                ))}
            {filteredReceivedRequests.map((request) => (
              <ReceivedRequestCard key={request.id} request={request} />
            ))}
          </div>
        )}
      </Collapse>
      <div className="request-section-header">
        <Typography variant="h5">Submitted Requests</Typography>
        <IconButton
          onClick={() =>
            setSubmittedRequestsExpanded(!submittedRequestsExpanded)
          }
          aria-label="show more"
          className={
            submittedRequestsExpanded ? classes.expandOpen : classes.expandClose
          }
          size="large"
        >
          <ExpandMoreIcon />
        </IconButton>
      </div>
      <Divider />
      <Collapse in={!submittedRequestsExpanded} timeout="auto" unmountOnExit>
        {isLoading && (
          <div style={{ marginTop: "1em" }}>
            {Array.from({ length: 1 }).map((_, index) => (
              <SkeletonLoader key={index} type="received_requests" />
            ))}
          </div>
        )}
        {!isLoading && (
          <div style={{ marginTop: "1em" }} className="no-requests-message">
            <Typography variant="body1">
              {submittedRequests.length} submitted request
              {submittedRequests.length === 1 ? "" : "s"} currently hidden
            </Typography>
          </div>
        )}
      </Collapse>
      <Collapse in={submittedRequestsExpanded} timeout="auto" unmountOnExit>
        {!isLoading && (
          <div className="submitted-requests-container">
            {submittedRequests.length === 0 && (
              <div className="no-requests-message">
                <Typography variant="body1">
                  No submitted feedback requests
                </Typography>
              </div>
            )}
            {submittedRequests.length > 0 &&
              filteredSubmittedRequests.length === 0 && (
                <div className="no-requests-message">
                  <Typography variant="body1">
                    No submitted feedback requests
                  </Typography>
                </div>
              )}
            {filteredSubmittedRequests.map((request) => (
              <ReceivedRequestCard key={request.id} request={request} />
            ))}
          </div>
        )}
      </Collapse>
      <div className="request-section-header">
        <Typography variant="h5">Canceled Requests</Typography>
        <IconButton
          onClick={() => setCanceledRequestsExpanded(!canceledRequestsExpanded)}
          aria-label="show more"
          className={
            canceledRequestsExpanded ? classes.expandOpen : classes.expandClose
          }
          size="large"
        >
          <ExpandMoreIcon />
        </IconButton>
      </div>
      <Divider />
      <Collapse in={!canceledRequestsExpanded} timeout="auto" unmountOnExit>
        {isLoading && (
          <div style={{ marginTop: "1em" }}>
            {Array.from({ length: 1 }).map((_, index) => (
              <SkeletonLoader key={index} type="received_requests" />
            ))}
          </div>
        )}
        {!isLoading && (
          <div style={{ marginTop: "1em" }} className="no-requests-message">
            <Typography variant="body1">
              {canceledRequests.length} canceled request
              {canceledRequests.length === 1 ? "" : "s"} currently hidden
            </Typography>
          </div>
        )}
      </Collapse>
      <Collapse in={canceledRequestsExpanded} timeout="auto" unmountOnExit>
        {!isLoading && (
          <div className="canceled-requests-container">
            {canceledRequests.length === 0 && (
              <div className="no-requests-message">
                <Typography variant="body1">
                  No canceled feedback requests
                </Typography>
              </div>
            )}
            {canceledRequests.length > 0 &&
              filteredCanceledRequests.length === 0 && (
                <div className="no-requests-message">
                  <Typography variant="body1">
                    No canceled feedback requests
                  </Typography>
                </div>
              )}
            {filteredCanceledRequests.map((request) => (
              <ReceivedRequestCard key={request.id} request={request} />
            ))}
          </div>
        )}
      </Collapse>
    </Root>
  );
};

export default ReceivedRequestsPage;
