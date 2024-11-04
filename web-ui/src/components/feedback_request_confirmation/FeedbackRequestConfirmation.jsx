import React, { useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import {
    selectProfile,
    selectHasCreateFeedbackPermission,
    noPermission, selectFeedbackExternalRecipient,
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { Link, useLocation } from 'react-router-dom';
import queryString from 'query-string';
import DateFnsUtils from '@date-io/date-fns';
import './FeedbackRequestConfirmation.css';
import { green } from '@mui/material/colors';
import Button from '@mui/material/Button';
import {getFeedbackTemplate} from "../../api/feedbacktemplate.js";
import {UPDATE_TOAST} from "../../context/actions.js"; // Import the action type

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

    function getRecipientNames() {
        if (fromQuery !== undefined) {
            let fromArray = fromQuery.split(',');
            let recipientProfiles = [];
            if (fromArray.length !== 0) {
                for (let i = 0; i < fromArray.length; ++i) {
                    let element = fromArray[i];
                    recipientProfiles.push(element);
                }
            } else {
                recipientProfiles.push(fromQuery);
            }
            return recipientProfiles;
        }
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
                                ? selectFeedbackExternalRecipient(state, recipient)?.firstName + " " + selectFeedbackExternalRecipient(state, recipient)?.lastName + " (" + selectFeedbackExternalRecipient(state, recipient)?.companyName + ") "
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
