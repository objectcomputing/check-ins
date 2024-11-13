import React, { useContext } from 'react';
import { styled } from '@mui/material/styles';
import { AppContext } from '../../context/AppContext.jsx';
import { selectCsrfToken, selectProfileMap } from '../../context/selectors.js';
import { Box, Card, CardHeader, CardContent, Container, Typography, IconButton, Tooltip } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import { green } from '@mui/material/colors';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CloseIcon from '@mui/icons-material/Close';

import './FeedbackExternalRecipientCard.css';
import FeedbackExternalRecipientSelector
  from "../feedback_external_recipient_selector/FeedbackExternalRecipientSelector.jsx";
import PropTypes from "prop-types";

const PREFIX = 'FeedbackExternalRecipientCard';
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

const FeedbackExternalRecipientCard = ({
                                         recipientProfile,
                                         selected,
                                         onClick,
                                         onInactivateHandle
                                       }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const supervisorProfile = selectProfileMap(state)[recipientProfile?.supervisorid];
  const pdlProfile = selectProfileMap(state)[recipientProfile?.pdlId];

  async function handleInactivate() {
      onInactivateHandle();
  }

  return (
      <StyledBox display="flex" flexWrap="wrap">
        <Card onClick={onClick} className="member-card" selected={selected}>
          <CardHeader
              className={classes.header}
              title={
                <Typography variant="h5" component="h2">
                  {recipientProfile?.firstName} {recipientProfile?.lastName}
                </Typography>
              }
              action={
                <>
                  {selected ? (
                      <CheckCircleIcon style={{ color: green[500] }}>
                        checkmark-image
                      </CheckCircleIcon>
                  ) : (
                      <Tooltip title="Inactivate" arrow>
                        <IconButton
                            onClick={(e) => {
                              e.stopPropagation();
                              handleInactivate();
                            }}
                        >
                          <CloseIcon />
                        </IconButton>
                      </Tooltip>
                  )}
                </>
              }
              disableTypography
          />
          <CardContent>
            <Container fixed className="info-container">
              <Typography variant="body2" color="textSecondary" component="p">
                <a
                    href={`mailto:${recipientProfile?.email}`}
                    target="_blank"
                    rel="noopener noreferrer"
                >
                  {recipientProfile?.email}
                </a>
                <br />
                Company: {recipientProfile?.companyName}
                <br />
              </Typography>
            </Container>
          </CardContent>
        </Card>
      </StyledBox>
  );
};

export default FeedbackExternalRecipientCard;
