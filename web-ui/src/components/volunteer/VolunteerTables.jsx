import PropTypes from 'prop-types';
import React, { useContext, useReducer, useState } from 'react';
import { Box, Tab, Tabs } from '@mui/material';

import {
  selectHasVolunteeringEventsPermission,
  selectHasVolunteeringOrganizationsPermission,
  selectHasVolunteeringRelationshipsPermission,
  noPermission,
} from '../../context/selectors';
import { AppContext } from '../../context/AppContext';

import Organizations from './Organizations';
import VolunteerEvents from './VolunteerEvents';
import VolunteerRelationships from './VolunteerRelationships';

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
          selectHasVolunteeringOrganizationsPermission(state) ||
          selectHasVolunteeringRelationshipsPermission(state)) ? (
    <div className="volunteer-tables">
      <Tabs
        indicatorColor="secondary"
        onChange={(event, index) => setTabIndex(index)}
        textColor="inherit"
        value={tabIndex}
        variant="fullWidth"
      >
        {/* Add sx prop to style each Tab */}
        {selectHasVolunteeringOrganizationsPermission(state) && <Tab
          label="Organizations"
          {...a11yProps(tabCount++)}
          sx={{
            minWidth: '150px',  // Increase the min width of the tab
            whiteSpace: 'nowrap' // Prevent text wrapping
          }}
        />}
        {selectHasVolunteeringRelationshipsPermission(state) && <Tab
          label={onlyMe ? 'My Orgs' : 'Relationships'}
          {...a11yProps(tabCount++)}
          sx={{
            minWidth: '150px',
            whiteSpace: 'nowrap'
          }}
        />}
        {selectHasVolunteeringEventsPermission(state) && <Tab
          label="Events"
          {...a11yProps(tabCount++)}
          sx={{
            minWidth: '150px',
            whiteSpace: 'nowrap'
          }}
        />}
      </Tabs>
      {selectHasVolunteeringOrganizationsPermission(state) && <TabPanel index={tabContent++} value={tabIndex}>
        <Organizations
          forceUpdate={forceUpdate}
          key={'org' + n}
          onlyMe={onlyMe}
        />
      </TabPanel>}
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
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

VolunteerReportPage.propTypes = propTypes;

export default VolunteerReportPage;
