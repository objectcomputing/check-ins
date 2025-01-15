import React, { useContext, useState } from 'react';
import {
  Accordion,
  AccordionSummary,
  Avatar,
  Chip,
  Typography,
  AccordionDetails,
  Box
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { getAvatarURL } from '../../../api/api.js';
import { AppContext } from '../../../context/AppContext.jsx';
import { selectCheckinsForMember } from '../../../context/selectors.js';
import {
  isPastCheckin,
  getCheckinDate,
  getQuarterBeginEndWithGrace,
  getCheckinDateForPeriod,
  getLastCheckinDate,
  statusForPeriodByMemberScheduling
} from './checkin-utils.js';
import LinkSection from './LinkSection.jsx';
import './TeamMemberMap.css';

const SortOption = {
  BY_MEMBER: 0,
  BY_PDL: 1,
};

const TeamMemberMap = ({ members, closed, planned, reportDate }) => {
  const { state } = useContext(AppContext);
  const [sortBy, setSortBy] = useState(SortOption.BY_MEMBER);

  const epoch = new Date(0);
  const pdls = members.reduce((map, member) => {
    if (member.pdlId && !map[member.pdlId]) {
      map[member.pdlId] = members.find((m) => m.id === member.pdlId);
    }
    return map;
  }, {});

  const sortByName = (left, right) => {
    if (left && right) {
      return `${left.lastName} ${left.firstName}`.localeCompare(
             `${right.lastName} ${right.firstName}`);
    } else {
      return left ? -1 : 1;
    }
  };

  const sortByPDLName = (a, b) => sortByName(pdls[a.pdlId], pdls[b.pdlId]);

  members.sort(sortBy == SortOption.BY_MEMBER ? sortByName : sortByPDLName);

  // TODO: Figure out how to do the column headers correctly.
  return (
    <Box className="team-member-map">
      <Box display="flex">
        <Box flex={2} onClick={() => { setSortBy(SortOption.BY_MEMBER); }}>
          <Typography variant="h5">Member</Typography>
        </Box>
        <Box flex={1} onClick={() => { setSortBy(SortOption.BY_PDL); }}>
          <Typography variant="h5">PDL</Typography>
        </Box>
        <Box flex={1}>
          <Typography variant="h5">Check-In Date</Typography>
        </Box>
        <Box flex={1}>
          <Typography variant="h5">Status</Typography>
        </Box>
      </Box>
      {
        members.map(member => {
          let pdl = pdls[member.pdlId];
          const checkins = selectCheckinsForMember(
            state,
            member.id,
          ).filter(checkin => closed || !checkin.completed)
           .filter(checkin => planned || isPastCheckin(checkin));

          // If there are checkins, we're going to sort them with the latest
          // first.  Since the member's PDL could have changed since the last
          // checkin, we are going to use the PDL id of the checkin instead
          // of the current PDL.  They may be the same, but again they may not.
          if (checkins.length > 0) {
            checkins.sort((a, b) => getCheckinDate(b) - getCheckinDate(a));
            const latest = checkins[0];
            const { startOfQuarter, endOfQuarter } =
                                      getQuarterBeginEndWithGrace(reportDate);
            const checkinDate = getCheckinDate(latest);
            if (checkinDate >= startOfQuarter && checkinDate <= endOfQuarter) {
              console.log(member.firstName);
              console.log("Current PDL: " + JSON.stringify(pdl));
              pdl = pdls[checkins[0].pdlId];
              console.log("PDL at the time: " + JSON.stringify(pdl));
            }
          }

          return (
            <Accordion
              key={member.id}
              className="team-member-map-accordion"
            >
              <AccordionSummary
                expandIcon={<ExpandMoreIcon />}
                className="team-member-map-accordion-summary"
              >
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
                    dateTime={getLastCheckinDate(checkins).toISOString()}
                    sx={{ display: { xs: 'none', sm: 'grid' } }}
                    className="team-member-map-summmary-latest-activity"
                  >
                    <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                      {getCheckinDateForPeriod(
                        checkins,
                        reportDate
                      ).getFullYear() === epoch.getFullYear() ? (
                        <Typography component="nobr" variant="h6">
                          No activity yet{' '}
                          <span role="img" aria-label="unscheduled">
                            🚫
                          </span>
                        </Typography>
                      ) : (
                        <>
                          <Typography component="nobr" variant="h6">
                            {getCheckinDateForPeriod(
                              checkins,
                              reportDate
                            ).toLocaleDateString(navigator.language, {
                              year: 'numeric',
                              month: '2-digit',
                              day: 'numeric'
                            })}{' '}
                            {getCheckinDateForPeriod(
                              checkins,
                              reportDate
                            ).getTime() > new Date().getTime() ? (
                              <span role="img" aria-label="scheduled">
                                📆
                              </span>
                            ) : (
                              <span role="img" aria-label="completed">
                                ✅
                              </span>
                            )}
                          </Typography>
                        </>
                      )}
                    </Box>
                  </Typography>
                  <Chip
                    label={statusForPeriodByMemberScheduling(
                      checkins,
                      reportDate
                    )}
                    color={
                      statusForPeriodByMemberScheduling(
                        checkins,
                        reportDate
                      ) === 'Done'
                        ? 'secondary'
                        : 'primary'
                    }
                  />
                </div>
              </AccordionSummary>
              <AccordionDetails id="accordion-checkin-date">
                {checkins.length === 0
                  ? 'No check-in activity found for this member and PDL.'
                  : checkins.map(checkin => (
                      <LinkSection
                        key={checkin.id}
                        checkin={checkin}
                        member={member}
                      />
                    ))}
              </AccordionDetails>
            </Accordion>
          );
        })
      }
    </Box>
  );
};

export default TeamMemberMap;
