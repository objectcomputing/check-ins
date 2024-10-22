import PropTypes from 'prop-types';
import React, { useContext, useReducer, useState } from 'react';
import { Box, Tab, Tabs, Typography, Card, CardHeader, CardContent, Avatar } from '@mui/material';

import {
  selectHasVolunteeringEventsPermission,
  selectHasVolunteeringRelationshipsPermission,
  noPermission,
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';

import Organizations from './Organizations';
import VolunteerEvents from './VolunteerEvents';
import VolunteerRelationships from './VolunteerRelationships';
import GroupIcon from '@mui/icons-material/Group';
import EventIcon from '@mui/icons-material/Event';
import HandshakeIcon from '@mui/icons-material/Handshake'; // Adding Handshake Icon

import './VolunteerTables.css';

const a11yProps = index => ({
  id: `full-width-tab-${index}`,
  'aria-controls': `full-width-tabpanel-${index}`
});

const TabPanel = ({ children, value, index, ...other }) => (
  <div
    role="tabpanel"
    hidden={value !== index}
    id={`full-width-tabpanel-${index}`}
    aria-labelledby={`full-width-tab-${index}`}
    {...other}
  >
    {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
  </div>
);
TabPanel.propTypes = {
  children: PropTypes.any,
  index: PropTypes.number,
  value: PropTypes.number
};
TabPanel.displayName = 'TabPanel';

const propTypes = {
  onlyMe: PropTypes.bool
};

const VolunteerReportPage = ({ onlyMe = false }) => {
  const { state } = useContext(AppContext);
  const [n, forceUpdate] = useReducer(n => n + 1, 0);
  const [tabIndex, setTabIndex] = useState(0);

  // React requires that tabs be sequentially numbered.  Use these to ensure
  // that each tab coinsides with the correct tab content based on the
  // individual permissions.
  let tabCount = 0;
  let tabContent = 0;

  return (selectHasVolunteeringEventsPermission(state) ||
          selectHasVolunteeringRelationshipsPermission(state)) ? (
    <Card className="volunteer-activities-card">
      <CardContent className="volunteer-tables">
        <Tabs
          indicatorColor="secondary"
          onChange={(event, index) => setTabIndex(index)}
          textColor="inherit"
          value={tabIndex}
          variant="fullWidth"
        >
          {selectHasVolunteeringRelationshipsPermission(state) && <Tab
            label={
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Avatar sx={{ mr: 1 }}>
                  <HandshakeIcon />
                </Avatar>
                <Typography textTransform="none" variant='h5' component='h2'>Volunteer Orgs</Typography>
              </Box>
            }
            {...a11yProps(tabCount++)}
            sx={{
              minWidth: '150px',
              whiteSpace: 'nowrap'
            }}
          />}
          {selectHasVolunteeringEventsPermission(state) && <Tab
            label={
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Avatar sx={{ mr: 1 }}>
                  <EventIcon />
                </Avatar>
                <Typography textTransform="none" variant='h5' component='h2'>Events</Typography>
              </Box>
            }
            {...a11yProps(tabCount++)}
            sx={{
              minWidth: '150px',
              whiteSpace: 'nowrap'
            }}
          />}
        </Tabs>
        {selectHasVolunteeringRelationshipsPermission(state) && <TabPanel index={tabContent++} value={tabIndex}>
          <VolunteerRelationships
            forceUpdate={forceUpdate}
            key={'vr' + n}
            onlyMe={onlyMe}
          />
        </TabPanel>}
        {selectHasVolunteeringEventsPermission(state) && <TabPanel index={tabContent++} value={tabIndex}>
          <VolunteerEvents
            forceUpdate={forceUpdate}
            key={'vh' + n}
            onlyMe={onlyMe}
          />
        </TabPanel>}
      </CardContent>
    </Card>
  ) : (
    <h3>{noPermission}</h3>
  );
};

VolunteerReportPage.propTypes = propTypes;

export default VolunteerReportPage;
