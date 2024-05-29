import React, { useReducer } from 'react';
import Certifications from '../components/certifications/Certifications';
import EarnedCertificationsTable from '../components/certifications/EarnedCertificationsTable';
import './CertificationReportPage.css';

const CertificationReportPage = () => {
  const [n, forceUpdate] = useReducer(n => n + 1, 0);

  return (
    <div className="certification-report-page">
      <EarnedCertificationsTable forceUpdate={forceUpdate} key={n} />
      <Certifications forceUpdate={forceUpdate} key={n + 1} />
    </div>
  );
};

export default CertificationReportPage;
