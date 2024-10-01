import React, { useContext, useReducer, useState } from 'react';
import { Button } from '@mui/material';

import {
  selectHasEarnedCertificationsPermission,
  noPermission,
} from '../context/selectors';
import { AppContext } from '../context/AppContext';
import Certifications from '../components/certifications/Certifications';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';
import './CertificationReportPage.css';

const CertificationReportPage = () => {
  const { state } = useContext(AppContext);
  const [n, forceUpdate] = useReducer(n => n + 1, 0);
  const [dialogOpen, setDialogOpen] = useState(false);

  return selectHasEarnedCertificationsPermission(state) ? (
    <div className="certification-report-page">
      <Button
        classes={{ root: 'manage-btn' }}
        onClick={() => setDialogOpen(true)}
      >
        Manage Certifications
      </Button>
      <EarnedCertificationsTable forceUpdate={forceUpdate} key={n} />
      <Certifications
        forceUpdate={forceUpdate}
        key={n + 1}
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
      />
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default CertificationReportPage;
