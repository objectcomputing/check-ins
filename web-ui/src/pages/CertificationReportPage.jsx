import React, { useReducer, useState } from 'react';
import { Button } from '@mui/material';

import Certifications from '../components/certifications/Certifications';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';
import './CertificationReportPage.css';

const CertificationReportPage = () => {
  const [n, forceUpdate] = useReducer(n => n + 1, 0);
  const [dialogOpen, setDialogOpen] = useState(false);

  return (
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
  );
};

export default CertificationReportPage;
