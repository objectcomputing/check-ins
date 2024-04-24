import React, { useContext, useState } from 'react';
import { styled } from '@mui/material/styles';
import { Link } from 'react-router-dom';

import { AppContext } from '../../context/AppContext';
import { selectIsAdmin, selectProfileMap } from '../../context/selectors';
import { getAvatarURL } from '../../api/api.js';

import { Card, CardHeader } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import PriorityHighIcon from '@mui/icons-material/PriorityHigh';

import './MemberSummaryCard.css';

import {
  Box,
  CardContent,
  Container,
  Tooltip,
  Typography
} from '@mui/material';
const PREFIX = 'MemberSummaryCard';
const classes = {
  header: `${PREFIX}-header`
};

const StyledBox = styled(Box)(() => ({
  [`& .${classes.header}`]: {
    cursor: 'pointer'
  }
}));

const MemberSummaryCard = ({ member }) => {
  const { state } = useContext(AppContext);
  const isAdmin = selectIsAdmin(state);
  const {
    location,
    name,
    workEmail,
    title,
    supervisorid,
    pdlId,
    terminationDate
  } = member;
  const supervisorProfile = selectProfileMap(state)[supervisorid];
  const pdlProfile = selectProfileMap(state)[pdlId];
  const [tooltipIsOpen, setTooltipIsOpen] = useState(false);

  return (
    <StyledBox display="flex" flexWrap="wrap">
      <Card className={'member-card'}>
        <Link
          style={{ color: 'black', textDecoration: 'none' }}
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
              isAdmin && terminationDate ? (
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
              ) : (
                <Avatar className={'large'} src={getAvatarURL(workEmail)} />
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
                    color: 'rgba(0, 0, 0, 0.6)'
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
                    color: 'rgba(0, 0, 0, 0.6)'
                  }}
                >
                  {pdlProfile?.name}
                </Link>
              )}
              <br />
            </Typography>
          </Container>
        </CardContent>
      </Card>
    </StyledBox>
  );
};

export default MemberSummaryCard;
