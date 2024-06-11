import PropTypes from 'prop-types';
import React, { useCallback, useEffect, useState } from 'react';

import { Card, CardContent, CardHeader, Tooltip } from '@mui/material';

import { resolve } from '../../api/api.js';

import './CertificationBadges.css';

const certificationBaseUrl = '/services/certification';

const propTypes = {
  memberId: PropTypes.string
};
const CertificationBadges = ({ memberId }) => {
  const [certifications, setCertifications] = useState([]);

  const loadCertifications = useCallback(async () => {
    try {
      const res = await resolve({
        method: 'GET',
        url: certificationBaseUrl + '/' + memberId,
        headers: {
          'X-CSRF-Header': csrf,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
      if (res.error) throw new Error(res.error.message);

      const certifications = res.payload.data;
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
    <Card className="certification-badges">
      <CardHeader
        title="Certifications"
        titleTypographyProps={{ variant: 'h5', component: 'h1' }}
      />
      <CardContent>
        {certifications.map(cert => (
          <Tooltip title={cert.name}>
            <img alt={cert.name} key={cert.id} src={cert.badgeUrl} />
          </Tooltip>
        ))}
      </CardContent>
    </Card>
  );
};

CertificationBadges.propTypes = propTypes;

export default CertificationBadges;
