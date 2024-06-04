import PropTypes from 'prop-types';
import React, { useCallback, useEffect, useState } from 'react';

import { Card, CardContent, CardHeader } from '@mui/material';

import './CertificationBadges.css';

const certificationBaseUrl = 'http://localhost:3000/certification';

const propTypes = {
  memberId: PropTypes.string
};
const CertificationBadges = ({ memberId }) => {
  const [certifications, setCertifications] = useState([]);

  const loadCertifications = useCallback(async () => {
    try {
      const res = await fetch(certificationBaseUrl + '/' + memberId);
      const certifications = await res.json();
      setCertifications(certifications);
    } catch (err) {
      console.error(err);
    }
  }, []);

  useEffect(() => {
    loadCertifications();
  }, []);

  if (certifications.length === 0) return null;

  return (
    <Card id="certification-badges">
      <CardHeader
        title="Certifications"
        titleTypographyProps={{ variant: 'h5', component: 'h1' }}
      />
      <CardContent>
        {certifications.map(cert => {
          return <img alt={cert.name} key={cert.id} src={cert.badgeImageUrl} />;
        })}
      </CardContent>
    </Card>
  );
};

CertificationBadges.propTypes = propTypes;

export default CertificationBadges;
