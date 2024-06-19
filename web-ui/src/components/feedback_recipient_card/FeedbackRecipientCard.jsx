import React, { useContext } from 'react';
import { styled } from '@mui/material/styles';
import { AppContext } from '../../context/AppContext';
import { selectProfileMap } from '../../context/selectors';
import { getAvatarURL } from '../../api/api.js';
import { Box, Card, CardHeader, CardContent, Container, Typography } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import PriorityHighIcon from '@mui/icons-material/PriorityHigh';
import { green } from '@mui/material/colors';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import Divider from '@mui/material/Divider';

import './FeedbackRecipientCard.css';

const PREFIX = 'FeedbackRecipientCard';
const classes = {
  root: `${PREFIX}-root`,
  header: `${PREFIX}-header`,
  cardContent: `${PREFIX}-cardContent`,
  divider: `${PREFIX}-divider`,
  recommendationText: `${PREFIX}-recommendationText`
};

const StyledBox = styled(Box)({
  [`&.${classes.root}`]: {
    // currently defined but not used
    minWidth: '10em',
    maxWidth: '20em',
    marginRight: '2em',
    marginBottom: '2em',
    cursor: 'pointer',
    '@media (max-width:767px)': {
      marginTop: '1em',
      height: '40%',
      width: '80%'
    }
  },
  [`& .${classes.header}`]: {
    cursor: 'pointer'
  },
  [`& .${classes.cardContent}`]: {
    // currently defined but not used
    display: 'flex',
    alignItems: 'center',
    alignContent: 'center',
    flexDirection: 'column',
    justifyContent: 'center',
    textAlign: 'center'
  },
  [`& .${classes.divider}`]: {
    backgroundColor: 'grey',
    width: '90%',
    marginBottom: '1em',
    marginTop: '1em'
  },
  [`& .${classes.recommendationText}`]: {
    color: '#333333'
  }
});

const FeedbackRecipientCard = ({
  recipientProfile,
  selected,
  reason = null,
  onClick
}) => {
  const { state } = useContext(AppContext);
  const supervisorProfile =
    selectProfileMap(state)[recipientProfile?.supervisorid];
  const pdlProfile = selectProfileMap(state)[recipientProfile?.pdlId];

  return (
    <StyledBox display="flex" flexWrap="wrap">
      <Card onClick={onClick} className="member-card" selected={selected}>
        <CardHeader
          className={classes.header}
          title={
            <Typography variant="h5" component="h2">
              {recipientProfile?.name}
            </Typography>
          }
          action={
            selected ? (
              <CheckCircleIcon style={{ color: green[500] }}>
                checkmark-image
              </CheckCircleIcon>
            ) : null
          }
          subheader={
            <Typography color="textSecondary" component="h3">
              {recipientProfile?.title}
            </Typography>
          }
          disableTypography
          avatar={
            !recipientProfile?.terminationDate ? (
              <Avatar
                className="large"
                src={getAvatarURL(recipientProfile?.workEmail)}
              />
            ) : (
              <Avatar className="large">
                <PriorityHighIcon />
              </Avatar>
            )
          }
        />
        <CardContent>
          <Container fixed className="info-container">
            <Typography variant="body2" color="textSecondary" component="p">
              <a
                href={`mailto:${recipientProfile?.workEmail}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {recipientProfile?.workEmail}
              </a>
              <br />
              Location: {recipientProfile?.location}
              <br />
              Supervisor: {supervisorProfile?.name}
              <br />
              PDL: {pdlProfile?.name}
              <br />
            </Typography>
            {reason && (
              <div className="reason">
                <Divider variant="middle" className={classes.divider} />
                <Typography
                  id="rec_reason"
                  name="rec_reason"
                  component="p"
                  className={classes.recommendationText}
                >
                  {reason}
                </Typography>
              </div>
            )}
          </Container>
        </CardContent>
      </Card>
    </StyledBox>
  );
};

export default FeedbackRecipientCard;
