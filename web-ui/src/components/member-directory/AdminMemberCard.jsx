import React, { useContext, useState } from 'react';
import { styled } from '@mui/material/styles';
import { Link } from 'react-router-dom';

import MemberModal from './MemberModal';
import { AppContext } from '../../context/AppContext';
import { UPDATE_MEMBER_PROFILES } from '../../context/actions';
import { selectProfileMap } from '../../context/selectors';
import { getAvatarURL } from '../../api/api.js';

import { Card, CardActions, CardHeader, Tooltip } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import PriorityHighIcon from '@mui/icons-material/PriorityHigh';

import './MemberSummaryCard.css';
import SplitButton from '../split-button/SplitButton';

import { updateMember, deleteMember } from '../../api/member.js';
import { DELETE_MEMBER_PROFILE, UPDATE_TOAST } from '../../context/actions.js';

import {
  Box,
  Button,
  CardContent,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Typography
} from '@mui/material';

const PREFIX = 'AdminMemberCard';
const classes = {
  header: `${PREFIX}-header`
};

const StyledBox = styled(Box)(() => ({
  [`& .${classes.header}`]: {
    cursor: 'pointer'
  }
}));

const AdminMemberCard = ({ member, index }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile, csrf } = state;
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes('ADMIN');
  const {
    location,
    name,
    workEmail,
    title,
    supervisorid,
    pdlId,
    terminationDate
  } = member;
  const memberId = member?.id;
  const supervisorProfile = selectProfileMap(state)[supervisorid];
  const pdlProfile = selectProfileMap(state)[pdlId];
  const [tooltipIsOpen, setTooltipIsOpen] = useState(false);

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);

  const [openDelete, setOpenDelete] = useState(false);
  const handleOpenDeleteConfirmation = () => setOpenDelete(true);

  const handleClose = () => setOpen(false);
  const handleCloseDeleteConfirmation = () => setOpenDelete(false);

  const options = isAdmin ? ['Edit', 'Delete'] : ['Edit'];

  const handleAction = (e, index) => {
    if (index === 0) {
      handleOpen();
    } else if (index === 1) {
      handleOpenDeleteConfirmation();
    }
  };

  const handleDeleteMember = async () => {
    let res = await deleteMember(memberId, csrf);
    if (res && res.payload && res.payload.status === 200) {
      dispatch({ type: DELETE_MEMBER_PROFILE, payload: memberId });
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Member deleted'
        }
      });
    }
    handleCloseDeleteConfirmation();
  };

  return (
    <StyledBox display="flex" flexWrap="wrap">
      <Card className={'member-card'}>
        <Link
          style={{ color: 'inherit', textDecoration: 'none' }}
          to={`/profile/${member.id}`}
        >
          <CardHeader
            className={classes.header}
            title={
              <Typography variant="h5" component="h2">
                {name}
              </Typography>
            }
            subheader={
              <Typography color="textSecondary" component="h3">
                {title}
              </Typography>
            }
            disableTypography
            avatar={
              !terminationDate ? (
                <Avatar className={'large'} src={getAvatarURL(workEmail)} />
              ) : (
                <Avatar className={'large'}>
                  <Tooltip
                    open={tooltipIsOpen}
                    onOpen={() => setTooltipIsOpen(true)}
                    onClose={() => setTooltipIsOpen(false)}
                    enterTouchDelay={0}
                    placement="top-start"
                    title={'This member has been terminated'}
                  >
                    <PriorityHighIcon />
                  </Tooltip>
                </Avatar>
              )
            }
          />
        </Link>
        <CardContent>
          <Container fixed className={'info-container'}>
            <Typography variant="body2" color="textSecondary" component="p">
              <a
                href={`mailto:${workEmail}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {workEmail}
              </a>
              <br />
              Location: {location}
              <br />
              Supervisor:{' '}
              {supervisorid && (
                <Link
                  to={`/profile/${supervisorid}`}
                  style={{
                    textDecoration: 'none',
                    color: 'inherit'
                  }}
                >
                  {supervisorProfile?.name}
                </Link>
              )}
              <br />
              PDL:{' '}
              {pdlId && (
                <Link
                  to={`/profile/${pdlId}`}
                  style={{
                    textDecoration: 'none',
                    color: 'inherit'
                  }}
                >
                  {pdlProfile?.name}
                </Link>
              )}
              <br />
            </Typography>
          </Container>
        </CardContent>
        {isAdmin && (
          <CardActions>
            <SplitButton
              className="split-button"
              options={options}
              onClick={handleAction}
            />
            <Dialog
              open={openDelete}
              onClose={handleCloseDeleteConfirmation}
              aria-labelledby="alert-dialog-title"
              aria-describedby="alert-dialog-description"
            >
              <DialogTitle id="alert-dialog-title">Delete member?</DialogTitle>
              <DialogContent>
                <DialogContentText id="alert-dialog-description">
                  Are you sure you want to delete the member's data?
                </DialogContentText>
              </DialogContent>
              <DialogActions>
                <Button onClick={handleCloseDeleteConfirmation} color="primary">
                  Cancel
                </Button>
                <Button onClick={handleDeleteMember} color="primary" autoFocus>
                  Yes
                </Button>
              </DialogActions>
            </Dialog>
            <MemberModal
              member={member}
              open={open}
              onClose={handleClose}
              onSave={async member => {
                const res = await updateMember(member, csrf);
                const data =
                  res.payload?.data && !res.error ? res.payload.data : null;
                if (data) {
                  const copy = [...memberProfiles];
                  const index = copy.findIndex(
                    profile => profile.id === data.id
                  );
                  copy[index] = data;
                  dispatch({
                    type: UPDATE_MEMBER_PROFILES,
                    payload: copy
                  });
                  handleClose();
                }
              }}
            />
          </CardActions>
        )}
      </Card>
    </StyledBox>
  );
};

export default AdminMemberCard;
