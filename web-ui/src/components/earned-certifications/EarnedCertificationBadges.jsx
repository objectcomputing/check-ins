import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { Card, CardContent, CardHeader, Tooltip } from '@mui/material';

import { resolve } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';

import './EarnedCertificationBadges.css';

const certificationBaseUrl = '/services/earned-certification';

const propTypes = {
  memberId: PropTypes.string
};
const EarnedCertificationBadges = ({ memberId }) => {
  const [earnedCertifications, setEarnedCertifications] = useState([]);

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
    setEarnedCertifications(certifications);
  }, [csrf]);

  useEffect(() => {
    if (csrf) loadCertifications();
  }, [csrf]);

  if (earnedCertifications.length === 0) return null;

  return (
    <Card className="earned-certification-badges">
      <CardHeader
        title="Earned Certifications"
        titleTypographyProps={{ variant: 'h5', component: 'h1' }}
      />
      <CardContent>
        {earnedCertifications.map(cert => (
          <Tooltip key={cert.id} title={cert.name}>
            <img alt={cert.name} key={cert.id} src={cert.badgeUrl} />
          </Tooltip>
        ))}
      </CardContent>
    </Card>
  );
};

EarnedCertificationBadges.propTypes = propTypes;

export default EarnedCertificationBadges;
