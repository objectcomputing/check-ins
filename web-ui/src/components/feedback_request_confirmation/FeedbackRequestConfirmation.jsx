import React, { useContext } from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { selectProfile } from '../../context/selectors';
import { AppContext } from '../../context/AppContext';
import { Link, useLocation } from 'react-router-dom';
import queryString from 'query-string';
import DateFnsUtils from '@date-io/date-fns';
import './FeedbackRequestConfirmation.css';
import { green } from '@mui/material/colors';
import Button from '@mui/material/Button';

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
  const { state } = useContext(AppContext);
  const location = useLocation();
  const query = queryString.parse(location?.search);
  const forQuery = query.for?.toString();
  const fromQuery = query.from?.toString();
  const sendQuery = query.send?.toString();
  const requestee = selectProfile(state, forQuery);
  let recipientInfo = getRecipientNames();
  let sendDate = dateUtils.parse(sendQuery, 'MM/dd/yyyy', new Date());

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
  return (
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
          (member, index) =>
            `${selectProfile(state, member)?.name}${index === recipientInfo.length - 1 ? '' : ', '}`
        )}
      </Typography>
      <Link style={{ marginTop: '4em', textDecoration: 'none' }} to="/">
        <Button variant="outlined">Return home</Button>
      </Link>
    </Root>
  );
};

export default FeedbackRequestConfirmation;
