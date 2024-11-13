import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { Card, CardContent, CardHeader, Tooltip } from '@mui/material';

import { resolve } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';

import './CertificationBadges.css';

const certificationBaseUrl = '/services/certification';

const propTypes = {
  memberId: PropTypes.string
};
const CertificationBadges = ({ memberId }) => {
  const [certifications, setCertifications] = useState([]);

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const loadCertifications = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: certificationBaseUrl + '?memberId=' + memberId,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const certifications = res.payload.data;
    setCertifications(certifications);
  }, [csrf]);

  useEffect(() => {
    if (csrf) loadCertifications();
  }, [csrf]);

  if (certifications.length === 0) return null;

  return (
    <Card className="certification-badges">
      <CardHeader
        title="Certifications"
        titleTypographyProps={{ variant: 'h5', component: 'h1' }}
      />
      <CardContent>
        {certifications.map(cert => (
          <Tooltip key={cert.id} title={cert.name}>
            <img alt={cert.name} key={cert.id} src={cert.badgeUrl} />
          </Tooltip>
        ))}
      </CardContent>
    </Card>
  );
};

CertificationBadges.propTypes = propTypes;

export default CertificationBadges;
