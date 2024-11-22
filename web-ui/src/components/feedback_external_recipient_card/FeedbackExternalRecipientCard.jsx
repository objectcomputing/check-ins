import React, { useState, useContext } from 'react';
import { styled } from '@mui/material/styles';
import { AppContext } from '../../context/AppContext.jsx';
import { selectCsrfToken, selectProfileMap } from '../../context/selectors.js';
import { Box, Card, CardHeader, CardContent, Container, Typography, IconButton, Tooltip, Button, Select, MenuItem } from '@mui/material';
import { green } from '@mui/material/colors';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CloseIcon from '@mui/icons-material/Close';
import PropTypes from 'prop-types';
import EditRecipientModal from './EditRecipientModal.jsx';

import './FeedbackExternalRecipientCard.css';

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
                                         recipientProfile, selected, onClick, onInactivateHandle, onEditHandle
                                       }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const supervisorProfile = selectProfileMap(state)[recipientProfile?.supervisorid];
  const pdlProfile = selectProfileMap(state)[recipientProfile?.pdlId];

  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editedProfile, setEditedProfile] = useState(recipientProfile);

  const handleEditOpen = () => {
    setEditedProfile(recipientProfile);
    setEditModalOpen(true);
  };

  const handleEditClose = () => {
    setEditModalOpen(false);
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditedProfile({ ...editedProfile, [name]: value });
  };

  const handleEditSubmit = async () => {
    console.log("FeedbackExternalRecipientCard, handleEditSubmit, editedProfile: ", editedProfile);
    onEditHandle(editedProfile);
    setEditModalOpen(false);
  };

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
              </Typography>
              <Select
                  label="Status"
                  name="inactive"
                  value={recipientProfile?.inactive ? 'Inactive' : 'Active'}
                  onChange={(e) => {
                    e.stopPropagation();
                    onEditHandle({ ...recipientProfile, inactive: e.target.value === 'Inactive' });
                  }}
                  fullWidth
                  margin="normal"
                  disabled={true}
                  sx={{
                    fontSize: '0.75rem',
                    '& .MuiSelect-select': {
                      padding: '4px 8px',
                    },
                    '& .MuiMenuItem-root': {
                      fontSize: '0.75rem',
                    },
                  }}
              >
                <MenuItem value="Active">Active</MenuItem>
                <MenuItem value="Inactive">Inactive</MenuItem>
              </Select>
              <Button onClick={(e) => {
                  e.stopPropagation();
                  handleEditOpen();
                }}
              >Edit
              </Button>
            </Container>
          </CardContent>
        </Card>

        <EditRecipientModal
            open={editModalOpen}
            onClose={handleEditClose}
            profile={editedProfile}
            onChange={handleEditChange}
            onSubmit={handleEditSubmit}
        />
      </StyledBox>
  );
};

FeedbackExternalRecipientCard.propTypes = {
  recipientProfile: PropTypes.object.isRequired,
  selected: PropTypes.bool,
  onClick: PropTypes.func.isRequired,
  onInactivateHandle: PropTypes.func.isRequired,
  onEditHandle: PropTypes.func.isRequired,
};

export default FeedbackExternalRecipientCard;
