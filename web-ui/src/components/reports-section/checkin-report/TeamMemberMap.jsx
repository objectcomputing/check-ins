import React, { useContext, useState } from 'react';
import {
  Accordion,
  AccordionSummary,
  Avatar,
  Chip,
  Typography,
  AccordionDetails,
  Box,
  Card,
  CardContent,
} from '@mui/material';
import { Link } from 'react-router-dom';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { getAvatarURL } from '../../../api/api.js';
import { AppContext } from '../../../context/AppContext.jsx';
import { selectCheckinsForMember } from '../../../context/selectors.js';
import {
  isPastCheckin,
  getCheckinDate,
  statusForPeriodByMemberScheduling
} from './checkin-utils.js';
import { getQuarterBeginEnd } from '../../../helpers';
import './TeamMemberMap.css';

const SortOption = {
  BY_MEMBER: 0,
  BY_PDL: 1,
};

const TeamMemberMap = ({ members, closed, planned, reportDate }) => {
  const { state } = useContext(AppContext);
  const [sortBy, setSortBy] = useState(SortOption.BY_MEMBER);

  const epoch = new Date(0);
  const memberMap = members.reduce((map, member) => {
    map[member.id] = member;
    return map;
  }, {});

  const linkStyle={ textDecoration: 'none' };
  const checkinPath = (member, checkin) => `/checkins/${member.id}/${checkin.id}`;

  const sortByName = (left, right) => {
    if (left && right) {
      return `${left.lastName} ${left.firstName}`.localeCompare(
             `${right.lastName} ${right.firstName}`);
    } else {
      return left ? -1 : 1;
    }
  };

  const sortByPDLName = (a, b) =>
        sortByName(memberMap[a.reportDatePDLId], memberMap[b.reportDatePDLId]);

  // We're going to cache the checkins into the member data structures so that
  // we can properly sort by PDL when the PDL, in the past, is different than
  // the current PDL.
  const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(reportDate);
  members.map(member => {
    member.checkins = selectCheckinsForMember(
      state,
      member.id,
    ).filter(checkin => closed || !checkin.completed)
     .filter(checkin => planned || isPastCheckin(checkin))
     .filter(checkin => (getCheckinDate(checkin) <= endOfQuarter));

    // If there are checkins, we're going to sort them with the latest
    // first.  Since the member's PDL could have changed since the last
    // checkin, we are going to use the PDL id of the checkin instead
    // of the current PDL.  They may be the same, but again they may not.
    member.checkin = null;
    member.reportDatePDLId = member.pdlId;
    if (member.checkins.length > 0) {
      member.checkins.sort((a, b) => getCheckinDate(b) - getCheckinDate(a));
      const checkin = member.checkins[0];
      if (getCheckinDate(checkin) >= startOfQuarter) {
        member.checkin = checkin;
        member.reportDatePDLId = member.checkin.pdlId;
      }
    }
  });

  members.sort(sortBy == SortOption.BY_MEMBER ? sortByName : sortByPDLName);

  return (
    <Box className="team-member-map">
      <Box display="flex">
        <Box flex={2} onClick={() => { setSortBy(SortOption.BY_MEMBER); }}
             style={{ cursor: 'pointer' }}>
          <Typography variant="h5">Member</Typography>
        </Box>
        <Box flex={1} onClick={() => { setSortBy(SortOption.BY_PDL); }}
             style={{ cursor: 'pointer' }}>
          <Typography variant="h5">PDL</Typography>
        </Box>
        <Box flex={1}
             sx={{ display: { xs: 'none', sm: 'none', md: 'grid' } }}>
          <Typography variant="h5">Check-In Date</Typography>
        </Box>
        <Box flex={1}>
          <Typography variant="h5">Status</Typography>
        </Box>
      </Box>
      {
        members.map(member => {
          const pdl = memberMap[member.reportDatePDLId];
          return (
            <Card key={member.id} className="team-member-map-row">
              <CardContent className="team-member-map-row-summary">
                <Avatar
                  sx={{ width: 54, height: 54 }}
                  src={getAvatarURL(member.workEmail)}
                />
                <div className="team-member-map-summmary-content">
                  <hgroup>
                    <Typography variant="h6">{member.name}</Typography>
                    <Typography sx={{ color: 'var(--muted)' }} variant="body2">
                      {member.title}
                    </Typography>
                  </hgroup>
                  {pdl
                    ? <div className="team-member-map-pdl">
                        <Avatar
                          sx={{ width: 54, height: 54 }}
                          src={getAvatarURL(pdl.workEmail)}
                        />
                        <hgroup>
                          <Typography variant="h6">{pdl.name}</Typography>
                          <Typography sx={{ color: 'var(--muted)' }} variant="body2">
                            {pdl.title}
                          </Typography>
                        </hgroup>
                      </div>
                    : <Typography component="nobr" variant="h6">
                        No PDL Assigned
                      </Typography>
                  }
                  <Typography
                    variant="caption"
                    component={'time'}
                    dateTime={getCheckinDate(member.checkin).toISOString()}
                    sx={{ display: { xs: 'none', sm: 'none', md: 'grid' } }}
                    className="team-member-map-summmary-latest-activity"
                  >
                    <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                      {member.checkin == null ? (
                        <Typography component="nobr" variant="h6">
                          No activity yet{' '}
                          <span role="img" aria-label="unscheduled">
                            🚫
                          </span>
                        </Typography>
                      ) : (
                        <Link style={linkStyle}
                              to={checkinPath(member, member.checkin)}>
                          <Typography component="nobr" variant="h6">
                            {getCheckinDate(member.checkin)
                             .toLocaleDateString(navigator.language, {
                              year: 'numeric',
                              month: '2-digit',
                              day: 'numeric'
                            })}{' '}
                            {getCheckinDate(member.checkin)
                            .getTime() > new Date().getTime() ? (
                              <span role="img" aria-label="scheduled">
                                📆
                              </span>
                            ) : (
                              <span role="img" aria-label="completed">
                                ✅
                              </span>
                            )}
                          </Typography>
                        </Link>
                      )}
                      {member.checkin == null && member.checkins.length > 0 &&
                        <span>Last Activity: {
                          <Link style={linkStyle}
                                to={checkinPath(member, member.checkins[0])}>
                            {getCheckinDate(member.checkins[0]).toString()
                               .split(' ').slice(0, 5).join(' ')}
                          </Link>}
                        </span>
                      }
                    </Box>
                  </Typography>
                  <Chip
                    label={statusForPeriodByMemberScheduling(
                      member.checkin,
                      reportDate
                    )}
                    color={
                      statusForPeriodByMemberScheduling(
                        member.checkin,
                        reportDate
                      ) === 'Done'
                        ? 'secondary'
                        : 'primary'
                    }
                  />
                </div>
              </CardContent>
            </Card>
          );
        })
      }
    </Box>
  );
};

export default TeamMemberMap;
