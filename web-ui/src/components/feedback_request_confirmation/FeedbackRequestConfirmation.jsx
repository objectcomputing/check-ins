import React, { useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import {
    selectProfile,
    selectHasCreateFeedbackPermission,
    noPermission,
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { Link, useLocation } from 'react-router-dom';
import queryString from 'query-string';
import DateFnsUtils from '@date-io/date-fns';
import './FeedbackRequestConfirmation.css';
import { green } from '@mui/material/colors';
import Button from '@mui/material/Button';
import {getFeedbackTemplate} from "../../api/feedbacktemplate.js";
import {UPDATE_TOAST} from "../../context/actions.js";
import {getExternalRecipients} from "../../api/feedback.js"; // Import the action type

const dateUtils = new DateFnsUtils();
const PREFIX = 'FeedbackRequestConfirmation';
const classes = {
    announcement: `${PREFIX}-announcement`,
    checkmark: `${PREFIX}-checkmark`
};

const Root = styled('div')({
    [`& .${classes.announcement}`]: {
        textAlign: 'center',
        ['@media (max-width:820px)']: {
            // eslint-disable-line no-useless-computed-key
            fontSize: 'x-large'
        }
    },
    [`& .${classes.checkmark}`]: {
        ['@media (max-width:820px)']: {
            // eslint-disable-line no-useless-computed-key
            width: '65%'
        }
    }
});

let today = new Date();

const FeedbackRequestConfirmation = () => {
    const { state, dispatch } = useContext(AppContext);
    const location = useLocation();
    const query = queryString.parse(location?.search);
    const forQuery = query.for?.toString();
    const fromQuery = query.from?.toString();
    const sendQuery = query.send?.toString();
    const templateQuery = query.template?.toString();
    const requestee = selectProfile(state, forQuery);
    const [templateIsForExternalRecipient, setTemplateIsForExternalRecipient] = useState(false);
    const [externalRecipients, setExternalRecipients] = useState([]);

    useEffect(() => {
        async function fetchTemplateDetails() {
            if (!templateQuery) return;
            let res = await getFeedbackTemplate(templateQuery);
            let templateResponse =
                res.payload &&
                res.payload.data &&
                res.payload.status === 200 &&
                !res.error
                    ? res.payload.data
                    : null;

            if (templateResponse === null) {
                dispatch({
                    type: UPDATE_TOAST,
                    payload: {
                        severity: 'error',
                        toast: 'The ID for the template you selected does not exist.'
                    }
                });
            } else {
                setTemplateIsForExternalRecipient(templateResponse.isForExternalRecipient);
            }
        }
        fetchTemplateDetails();
    }, [templateQuery, dispatch]);

    useEffect(() => {
            async function fetchExternalRecipients() {
            let res = await getExternalRecipients();
            let externalRecipientsResponse =
                res.payload && res.payload.data && res.payload.status === 200 && !res.error
                    ? res.payload.data
                    : null
            ;
            if (externalRecipientsResponse) {
                setExternalRecipients(externalRecipientsResponse);
            }
        }
        fetchExternalRecipients();
    }, [templateIsForExternalRecipient]);

    function getRecipientNames() {
        let recipientProfiles = [];
        if (templateIsForExternalRecipient) {
            if (fromQuery !== undefined) {
                let fromArray = fromQuery.split(',');
                fromArray.forEach(id => {
                    let recipient = externalRecipients.find(recipient => recipient.id === id);
                    if (recipient) {
                        recipientProfiles.push(recipient.firstName + ' ' + recipient.lastName + (recipient.companyName ? ' (' + recipient.companyName + ')' : ''));
                    }
                });
            }
        } else {
            if (fromQuery !== undefined) {
                let fromArray = fromQuery.split(',');
                fromArray.forEach(id => {
                    recipientProfiles.push(id);
                });
            }
        }
        return recipientProfiles;
    }

    let recipientInfo = getRecipientNames();
    let sendDate = dateUtils.parse(sendQuery, 'MM/dd/yyyy', new Date());

    return selectHasCreateFeedbackPermission(state) ? (
        <Root className="request-confirmation">
            <CheckCircleIcon style={{ color: green[500], fontSize: '40vh' }}>
                checkmark-image
            </CheckCircleIcon>
            <Typography className={classes.announcement} variant="h3">
                <b>
                    Feedback request{' '}
                    {dateUtils.isBefore(today, sendDate)
                        ? ' scheduled on: ' + sendQuery
                        : ' sent'}{' '}
                    for {requestee?.name}{' '}
                </b>
            </Typography>
            <Typography className="recipients-list" variant="h6">
                <b>Sent to: </b>
                {recipientInfo?.map(
                    (recipient, index) =>
                        `
                        ${
                            templateIsForExternalRecipient
                                ? recipient
                                : selectProfile(state, recipient)?.name
                        }                                
                        ${index === recipientInfo.length - 1 ? '' : ', '}
                        `
                )}
            </Typography>
            <Link style={{ marginTop: '4em', textDecoration: 'none' }} to="/">
                <Button variant="outlined">Return home</Button>
            </Link>
        </Root>
    ) : (
        <h3>{noPermission}</h3>
    );
};

export default FeedbackRequestConfirmation;
