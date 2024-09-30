import PropTypes from 'prop-types';
import React, { useReducer, useState } from 'react';
import { Box, Tab, Tabs } from '@mui/material';

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
  const [n, forceUpdate] = useReducer(n => n + 1, 0);
  const [tabIndex, setTabIndex] = useState(0);

  return (
    <div className="volunteer-tables">
      <Tabs
        indicatorColor="secondary"
        onChange={(event, index) => setTabIndex(index)}
        textColor="inherit"
        value={tabIndex}
        variant="fullWidth"
      >
        {/* Add sx prop to style each Tab */}
        <Tab
          label="Organizations"
          {...a11yProps(0)}
          sx={{
            minWidth: '150px',  // Increase the min width of the tab
            whiteSpace: 'nowrap' // Prevent text wrapping
          }}
        />
        <Tab
          label={onlyMe ? 'My Orgs' : 'Relationships'}
          {...a11yProps(1)}
          sx={{
            minWidth: '150px',
            whiteSpace: 'nowrap'
          }}
        />
        <Tab
          label="Events"
          {...a11yProps(2)}
          sx={{
            minWidth: '150px',
            whiteSpace: 'nowrap'
          }}
        />
      </Tabs>
      <TabPanel index={0} value={tabIndex}>
        <Organizations
          forceUpdate={forceUpdate}
          key={'org' + n}
          onlyMe={onlyMe}
        />
      </TabPanel>
      <TabPanel index={1} value={tabIndex}>
        <VolunteerRelationships
          forceUpdate={forceUpdate}
          key={'vr' + n}
          onlyMe={onlyMe}
        />
      </TabPanel>
      <TabPanel index={2} value={tabIndex}>
        <VolunteerEvents
          forceUpdate={forceUpdate}
          key={'vh' + n}
          onlyMe={onlyMe}
        />
      </TabPanel>
    </div>
  );
};

VolunteerReportPage.propTypes = propTypes;

export default VolunteerReportPage;