import React, { useContext } from 'react';
import {
  Accordion,
  AccordionSummary,
  Avatar,
  Chip,
  Typography,
  AccordionDetails
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

  const filteredMembers = members?.filter(member => {
    const checkins = selectFilteredCheckinsForTeamMemberAndPDL(
      state,
      member.id,
      id,
      closed,
      planned
    );
    return checkins && checkins.length > 0;
  });

  return (
    <>
      {filteredMembers?.length > 0 ? (
        filteredMembers.map(member => {
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
                  >
                    Activity:{' '}
                    {getLastCheckinDate(checkins).toLocaleDateString(
                      navigator.language,
                      {
                        year: 'numeric',
                        month: '2-digit',
                        day: 'numeric'
                      }
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
                {checkins.map(checkin => (
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
    </>
  );
};

export default TeamMemberMap;
