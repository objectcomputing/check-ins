import React from 'react';
import Certifications from '../components/certifications/Certifications';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';

const CertificationReportPage = () => (
  <div>
    <EarnedCertificationsTable />
    <Certifications />
  </div>
);

export default CertificationReportPage;
