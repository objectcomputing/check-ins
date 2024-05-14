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
import { selectFilteredCheckinsForTeamMemberAndPDL } from '../../../context/selectors.js';
import {
  getCheckinDateForPeriod,
  getLastCheckinDate,
  statusForPeriodByMemberScheduling
} from './checkin-utils.js';
import LinkSection from './LinkSection.jsx';
import './TeamMemberMap.css';

const TeamMemberMap = ({ members, id, closed, planned, reportDate }) => {
  const { state } = useContext(AppContext);

  return (
    <Box className="team-member-map">
      {members?.length > 0 ? (
        members.map(member => {
          const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
            state,
            member.id,
            id,
            closed,
            planned
          );

          return (
            <Accordion
              key={member.id + id}
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
                  <Typography
                    variant="caption"
                    component={'time'}
                    dateTime={getLastCheckinDate(checkins).toISOString()}
                    sx={{ display: { xs: 'none', sm: 'grid' } }}
                    className="team-member-map-summmary-latest-activity"
                  >
                    <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                      <Typography variant="overline" sx={{ mb: -1 }}>
                        Check-In date:
                      </Typography>
                      {getCheckinDateForPeriod(
                        checkins,
                        reportDate
                      ).getFullYear() === 1969 ? (
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
      ) : (
        <div className="team-member-map-no-data">
          <Typography>No team members associated with this PDL.</Typography>
        </div>
      )}
    </Box>
  );
};

export default TeamMemberMap;
