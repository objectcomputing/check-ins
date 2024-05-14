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
                  className={'large'}
                  src={getAvatarURL(member.workEmail)}
                />
                <div className="team-member-map-summmary-content">
                  <Typography>{member.name}</Typography>
                  <Typography
                    variant="caption"
                    component={'time'}
                    dateTime={getLastCheckinDate(checkins).toISOString()}
                    sx={{ display: { xs: 'none', sm: 'flex' } }}
                    className="team-member-map-summmary-latest-activity"
                  >
                    {getLastCheckinDate(checkins).getFullYear() === 1969 ? (
                      <p>No activity available.</p>
                    ) : (
                      <>
                        Latest Activity:{' '}
                        {getLastCheckinDate(checkins).toLocaleDateString(
                          navigator.language,
                          {
                            year: 'numeric',
                            month: '2-digit',
                            day: 'numeric'
                          }
                        )}
                      </>
                    )}
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
