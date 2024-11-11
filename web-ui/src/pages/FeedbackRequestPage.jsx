import React, {
  useContext,
  useCallback,
  useEffect,
  useState,
  useRef
} from 'react';
import { styled } from '@mui/material/styles';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { useLocation, useHistory } from 'react-router-dom';
import { UPDATE_TOAST } from '../context/actions';
import queryString from 'query-string';
import FeedbackTemplateSelector from '../components/feedback_template_selector/FeedbackTemplateSelector';
import FeedbackRecipientSelector from '../components/feedback_recipient_selector/FeedbackRecipientSelector';
import SelectDate from '../components/feedback_date_selector/SelectDate';
import { AppContext } from '../context/AppContext';
import {createFeedbackRequest, getExternalRecipients} from '../api/feedback';
import {
  selectProfile,
  selectCsrfToken,
  selectCurrentUser,
  selectCurrentMemberIds,
  selectHasCreateFeedbackPermission,
  noPermission,
} from '../context/selectors';
import DateFnsUtils from '@date-io/date-fns';
import { getFeedbackTemplate, softDeleteAdHocTemplates } from '../api/feedbacktemplate';

import './FeedbackRequestPage.css';
import FeedbackRequestForExternalRecipientPage from "./FeedbackRequestForExternalRecipientPage.jsx";
import FeedbackExternalRecipientSelector from "../components/feedback_external_recipient_selector/FeedbackExternalRecipientSelector.jsx";

const dateUtils = new DateFnsUtils();
const PREFIX = 'FeedbackRequestPage';
const classes = {
  root: `${PREFIX}-root`,
  requestHeader: `${PREFIX}-requestHeader`,
  stepContainer: `${PREFIX}-stepContainer`,
  appBar: `${PREFIX}-appBar`,
  media: `${PREFIX}-media`,
  expand: `${PREFIX}-expand`,
  expandOpen: `${PREFIX}-expandOpen`,
  actionButtons: `${PREFIX}-actionButtons`,
  backButton: `${PREFIX}-backButton`
};

const Root = styled('div')(({ theme }) => ({
  [`&.${classes.root}`]: {
    backgroundColor: 'transparent',
    ['@media (max-width:767px)']: {
      // eslint-disable-line no-useless-computed-key
      width: '100%',
      padding: 0
    }
  },
  [`& .${classes.requestHeader}`]: {
    ['@media (max-width:820px)']: {
      // eslint-disable-line no-useless-computed-key
      fontSize: 'x-large',
      marginBottom: '1em'
    }
  },
  [`& .${classes.stepContainer}`]: {
    ['@media min-width(321px) and (max-width:767px)']: {
      // eslint-disable-line no-useless-computed-key
      width: '80%'
    },
    ['@media max-width(320px)']: {
      // eslint-disable-line no-useless-computed-key
      display: 'none'
    },
    backgroundColor: 'transparent'
  },
  [`& .${classes.appBar}`]: {
    position: 'relative'
  },
  [`& .${classes.media}`]: {
    height: 0
  },
  [`& .${classes.expand}`]: {
    justifyContent: 'right',
    transition: theme.transitions.create('transform', {
      duration: theme.transitions.duration.shortest
    })
  },
  [`& .${classes.expandOpen}`]: {
    justifyContent: 'right'
  },
  [`& .${classes.actionButtons}`]: {
    margin: '0.5em 0 0 1em',
    ['@media (max-width:820px)']: {
      // eslint-disable-line no-useless-computed-key
      padding: '0'
    }
  },
  [`& .${classes.backButton}`]: {
    backgroundColor: '#e0e0e0',
    color: '#000000',
    '&:hover': {
      backgroundColor: '#d5d5d5'
    }
  }
}));

function getSteps() {
  return ['Select template', 'Select recipients', 'Set dates'];
}

const FeedbackRequestPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const steps = getSteps();
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const location = useLocation();
  const history = useHistory();
  const csrf = selectCsrfToken(state);
  const [query, setQuery] = useState([]);
  const queryLoaded = useRef(false);
  const [readyToProceed, setReadyToProceed] = useState(false);
  const [templateIsValid, setTemplateIsValid] = useState();
  const [templateIsForExternalRecipient, setTemplateIsForExternalRecipient] = useState();
  const [requestee, setRequestee] = useState({});
  const [memberIds, setMemberIds] = useState([]);
  const [externalRecipientIds, setExternalRecipientIds] = useState([]);
  const [activeStep, setActiveStep] = useState(1);

  const handleQueryChange = useCallback(
    (key, value) => {
      let newQuery = {
        ...query,
        [key]: value
      };
      history.push({ ...location, search: queryString.stringify(newQuery) });
    },
    [history, location, query]
  );

  const getStep = useCallback(() => {
    if (!query.step || query.step < 1 || !/^\d+$/.test(query.step)) return 1;
    else return parseInt(query.step);
  }, [query.step]);

  const hasFor = useCallback(() => {
    if (!memberIds.length) return true;
    return !!query.for && memberIds.includes(query.for);
  }, [query.for, memberIds]);

  const hasFrom = useCallback(() => {
    if (templateIsForExternalRecipient) {
      if (!externalRecipientIds.length) return true;
      let from = query.from;
      if (from) {
        from = Array.isArray(from) ? from : [from];
        for (let externalRecipientId of from) {
          if (!externalRecipientIds.includes(externalRecipientId)) {
            dispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'error',
                toast: 'External Recipient ID in URL is invalid'
              }
            });
            handleQueryChange('from', undefined);
            return false;
          }
        }
        return true;
      }
      return false;
    } else {
      if (!memberIds.length) return true;
      let from = query.from;
      if (from) {
        from = Array.isArray(from) ? from : [from];
        for (let recipientId of from) {
          if (!memberIds.includes(recipientId)) {
            dispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'error',
                toast: 'Member ID in URL is invalid'
              }
            });
            handleQueryChange('from', undefined);
            return false;
          }
        }
        return true;
      }
      return false;
    }

  }, [memberIds, query, dispatch, handleQueryChange, externalRecipientIds])
  ;

  const addExternalRecipientId = (id) => {
    setExternalRecipientIds((prevIds) => [...prevIds, id]);
  };

  const isValidDate = useCallback(dateString => {
    const today = new Date().setHours(0, 0, 0, 0);
    const timeStamp = Date.parse(dateString);
    if (timeStamp < today) return false;
    else return !isNaN(timeStamp);
  }, []);

  const hasSend = useCallback(() => {
    const dueTimestamp = Date.parse(query.due);
    const sendTimestamp = Date.parse(query.send);

    const isValidPair = query.due ? dueTimestamp >= sendTimestamp : true;
    return query.send && isValidDate(query.send) && isValidPair;
  }, [query.send, isValidDate, query.due]);

  const canProceed = useCallback(() => {
    if (query && Object.keys(query).length > 0) {
      if (activeStep === 1) {
        return hasFor() && templateIsValid;
      } else if (activeStep === 2) {
        return hasFor() && templateIsValid && hasFrom();
      } else if (activeStep === 3) {
        const dueQueryValid = query.due ? isValidDate(query.due) : true;
        console.log("FeedbackRequestPage, canProceed: ",{
          hasFor: hasFor(),
          templateIsValid,
          hasFrom: hasFrom(),
          hasSend: hasSend(),
          dueQueryValid
        });
        return (
          hasFor() && templateIsValid && hasFrom() && hasSend() && dueQueryValid
        );
      } else {
        return false;
      }
    }
    return false;
  }, [
    hasFor,
    hasFrom,
    hasSend,
    isValidDate,
    query,
    templateIsValid,
    activeStep
  ]);

  const sendFeedbackRequest = async feedbackRequest => {
    if (csrf) {
      let res = await createFeedbackRequest(feedbackRequest, csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        // If the request was successful, set created ad-hoc templates to inactive
        await softDeleteAdHoc(currentUserId);
        const newLocation = {
          pathname: '/feedback/request/confirmation',
          search: queryString.stringify(query),
        };
        history.push(newLocation);
      } else if (res.error || data === null) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'An error has occurred while submitting your request.'
          }
        });
      }
    }
  };

  const handleSubmit = () => {
    const from = query.from
      ? Array.isArray(query.from)
        ? query.from
        : [query.from]
      : [];
    const sendDate = query.send
      ? dateUtils.format(
          dateUtils.parse(query.send, 'MM/dd/yyyy', new Date()),
          'yyyy-MM-dd'
        )
      : new Date();
    const dueDate = query.due
      ? dateUtils.format(
          dateUtils.parse(query.due, 'MM/dd/yyyy', new Date()),
          'yyyy-MM-dd'
        )
      : undefined;
    for (const recipient of from) {
      const feedbackRequest = {
        id: null,
        creatorId: currentUserId,
        requesteeId: query.for,
        recipientId: (templateIsForExternalRecipient) ? null : recipient,
        externalRecipientId: (templateIsForExternalRecipient) ? recipient : null,
        templateId: query.template,
        sendDate,
        dueDate,
        status: 'pending',
        submitDate: null
      };
      sendFeedbackRequest(feedbackRequest);
    }
  };

  const onNextClick = useCallback(() => {
    if (!canProceed()) return;
    if (activeStep === steps.length) {
      handleSubmit();
      return;
    }
    query.step = `${activeStep + 1}`;
    if (query.step == 2) {
      query.from = null;
    }
    history.push({ ...location, search: queryString.stringify(query) });
  }, [canProceed, steps.length, query, location, history])
  ; // eslint-disable-line react-hooks/exhaustive-deps

  const onBackClick = useCallback(() => {
    if (activeStep === 1) return;
    query.step = `${activeStep - 1}`;
    history.push({ ...location, search: queryString.stringify(query) });
  }, [query, location, history, activeStep]);

  const softDeleteAdHoc = useCallback(
    async creatorId => {
      if (csrf) {
        await softDeleteAdHocTemplates(creatorId, csrf);
      }
    },
    [csrf]
  );

  const urlIsValid = useCallback(() => {
    if (query) {
      if (activeStep === 1) {
        return hasFor();
      } else if (activeStep === 2) {
        return hasFor() && templateIsValid;
      } else if (activeStep === 3) {
        return hasFor() && templateIsValid && hasFrom();
      } else {
        return false;
      }
    }
    return false;
  }, [hasFor, hasFrom, query, templateIsValid, activeStep]);

  useEffect(() => {
    setActiveStep(getStep());
  }, [query.step, getStep]);

  useEffect(() => {
    const members = selectCurrentMemberIds(state);
    if (members) {
      setMemberIds(members);
    }
  }, [state]);

  useEffect(() => {
    async function fetchExternalRecipients() {
      let res = await getExternalRecipients();
      let externalRecipientsResponse =
          res.payload && res.payload.data && res.payload.status === 200 && !res.error
              ? res.payload.data
              : null
      ;
      if (externalRecipientsResponse) {
        setExternalRecipientIds(externalRecipientsResponse.map(recipient => recipient.id));
      }
    }
    fetchExternalRecipients();
  }, [state]);

  useEffect(() => {
    const params = queryString.parse(location?.search);
    setQuery(params);
  }, [location.search]);

  useEffect(() => {
    async function isTemplateValid() {
      if (!query.template || !csrf) {
        return { isValid: false, additionalData: null };
      }

      let res = await getFeedbackTemplate(query.template, csrf);
      let templateResponse =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
              ? res.payload.data
              : null;

      if (templateResponse === null) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'The ID for the template you selected does not exist.'
          }
        });
        return { isValid: false, templateIsForExternalRecipientParam: false };
      } else {
        return { isValid: true, templateIsForExternalRecipientParam: templateResponse.isForExternalRecipient };
      }
    }

    if (queryLoaded.current && csrf) {
      isTemplateValid().then(({ isValid, templateIsForExternalRecipientParam: templateIsForExternalRecipientParam }) => {
        setTemplateIsValid(isValid);
        setTemplateIsForExternalRecipient(templateIsForExternalRecipientParam);
      });
    } else {
      queryLoaded.current = true;
    }
  }, [csrf, query, queryLoaded])
  ;

  useEffect(() => {
    if (!queryLoaded.current || templateIsValid === undefined) return;

    if (query.for) {
      setRequestee(selectProfile(state, query.for));
    }
    if (!urlIsValid()) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'An error has occurred with the URL'
        }
      });
      softDeleteAdHoc(currentUserId).then(() => {
        history.push('/checkins');
      });
    }
  }, [
    history,
    state,
    query,
    currentUserId,
    dispatch,
    softDeleteAdHoc,
    urlIsValid,
    templateIsValid
  ]);

  useEffect(() => {
    setReadyToProceed(canProceed());
  }, [canProceed]);

  return selectHasCreateFeedbackPermission(state) ? (
      <Root className="feedback-request-page">
        <div className="header-container">
          <Typography className={classes.requestHeader} variant="h4">
            Feedback Request for <b>{requestee?.name}</b>
          </Typography>
          <div>
            <Button
                className={`${classes.backButton} ${classes.actionButtons}`}
                onClick={onBackClick}
                disabled={activeStep <= 1}
                variant="contained"
            >
              Back
            </Button>
            <Button
                className={classes.actionButtons}
                onClick={onNextClick}
                variant="contained"
                disabled={!readyToProceed}
                color="primary"
            >
              {activeStep === steps.length ? 'Submit' : 'Next'}
            </Button>
          </div>
        </div>
        <div className={classes.stepContainer}>
          <Stepper
              activeStep={activeStep - 1}
              className={classes.root}
              style={{ padding: 24 }}
          >
            {steps.map(label => (
                <Step key={label}>
                  <StepLabel key={label}>{label}</StepLabel>
                </Step>
            ))}
          </Stepper>
        </div>
        <div className="current-step-content">
          {activeStep === 1 && (
              <FeedbackTemplateSelector
                  changeQuery={(key, value) => handleQueryChange(key, value)}
                  query={query.template}
              />
          )}
          {activeStep === 2 && (
              templateIsForExternalRecipient ? (
                  <FeedbackExternalRecipientSelector
                      forQuery={query.for}
                      changeQuery={(key, value) => handleQueryChange(key, value)}
                      fromQuery={
                        query.from
                            ? Array.isArray(query.from)
                                ? query.from
                                : [query.from]
                            : []
                      }
                      addExternalRecipientId={addExternalRecipientId}
                  />
              ) : (
                  <FeedbackRecipientSelector
                      forQuery={query.for}
                      changeQuery={(key, value) => handleQueryChange(key, value)}
                      fromQuery={
                        query.from
                            ? Array.isArray(query.from)
                                ? query.from
                                : [query.from]
                            : []
                      }
                  />
              )
          )}
          {activeStep === 3 && (
              <SelectDate
                  changeQuery={(key, value) => handleQueryChange(key, value)}
                  sendDateQuery={query.send}
                  dueDateQuery={query.due}
              />
          )}
        </div>
      </Root>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default FeedbackRequestPage;
