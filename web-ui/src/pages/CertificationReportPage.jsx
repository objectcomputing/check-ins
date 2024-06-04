import React, { useReducer, useState } from 'react';
import { Button, Dialog, DialogContent, DialogTitle } from '@mui/material';

import Certifications from '../components/certifications/Certifications';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';
import './CertificationReportPage.css';

const CertificationReportPage = () => {
  const [n, forceUpdate] = useReducer(n => n + 1, 0);
  const [dialogOpen, setDialogOpen] = useState(false);

  return (
    <div className="certification-report-page">
      <EarnedCertificationsTable forceUpdate={forceUpdate} key={n} />
      <Button onClick={() => setDialogOpen(true)}>Manage Certifications</Button>

      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
      >
        <DialogTitle>Manage Certifications</DialogTitle>
        <DialogContent>
          <Certifications forceUpdate={forceUpdate} key={n + 1} />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default CertificationReportPage;
