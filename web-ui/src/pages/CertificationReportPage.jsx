import React from 'react';
import Certifications from '../components/certifications/Certifications';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';
import './CertificationReportPage.css';

const CertificationReportPage = () => (
  <div className="certification-report-page">
    <EarnedCertificationsTable />
    <Certifications />
  </div>
);

export default CertificationReportPage;
