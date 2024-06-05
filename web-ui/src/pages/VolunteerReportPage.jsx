import React, { useReducer } from 'react';
import { Button } from '@mui/material';

import Organizations from '../components/volunteer/Organizations';
import './VolunteerReportPage.css';

const VolunteerReportPage = () => {
  const [n, forceUpdate] = useReducer(n => n + 1, 0);

  return (
    <div className="volunteer-report-page">
      <Organizations forceUpdate={forceUpdate} key={n + 1} />
    </div>
  );
};

export default VolunteerReportPage;
