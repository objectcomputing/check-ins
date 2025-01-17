import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import {Card, CardContent, CardHeader, Chip, Tooltip} from '@mui/material';

import { resolve } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';

import './EarnedCertificationBadges.css';
import certifications from "../certifications/Certifications.jsx";

const earnedCertificationBaseUrl = '/services/earned-certification';

const propTypes = {
  memberId: PropTypes.string, certifications: PropTypes.array,
};
const EarnedCertificationBadges = ({ memberId, certifications }) => {
  const [earnedCertifications, setEarnedCertifications] = useState([]);

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const loadCertifications = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: earnedCertificationBaseUrl + '?memberId=' + memberId,
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

  if (earnedCertifications.length === 0 || !certifications) return null;
  return (
      <Card className="earned-certification-badges">
        <CardHeader
            title="Earned Certifications"
            titleTypographyProps={{ variant: 'h5', component: 'h1' }}
        />
        <CardContent>
          {earnedCertifications.map(earnedCert => {
            // Find the corresponding certification using earnedCert.certificationId
            const cert = certifications.find(c => c.id === earnedCert.certificationId);
            // If no matching cert is found, skip rendering for that earnedCert
            if (!cert) return null;
            if (cert.badgeUrl && cert.badgeUrl.trim().length > 0) {
              return (
                  <Tooltip
                      key={earnedCert.id}
                      title={
                        <>
                          {cert.name} <br />
                          Issued on: {earnedCert.earnedDate} <br />
                          Expires on: {earnedCert.expirationDate}
                        </>
                      }
                  >
                    {earnedCert.validationUrl ? (
                        <a href={earnedCert.validationUrl} target="_blank" rel="noopener noreferrer">
                          <img
                              alt={`${cert.name}, Issued on: ${earnedCert.earnedDate}, Expires on: ${earnedCert.expirationDate}`}
                              src={cert.badgeUrl}
                          />
                        </a>
                    ) : (
                        <img
                            alt={`${cert.name}, Issued on: ${earnedCert.earnedDate}, Expires on: ${earnedCert.expirationDate}`}
                            src={cert.badgeUrl}
                        />
                    )}
                  </Tooltip>
              );
            } else {
              return (
                  <>
                    {earnedCert.validationUrl ? (
                        <a href={earnedCert.validationUrl} target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none' }}>
                          <Chip
                              sx={{
                                height: 'auto',
                                '& .MuiChip-label': {
                                  display: 'block',
                                  whiteSpace: 'normal',
                                },
                              }}
                              className="chip"
                              color="primary"
                              key={earnedCert.id}
                              label={
                                <>
                                  {cert.name} <br />
                                  Issued on: {earnedCert.earnedDate}<br />
                                  Expires on: {earnedCert.expirationDate}
                                </>
                              }
                          />
                        </a>
                    ) : (
                        <Chip
                            sx={{
                              height: 'auto',
                              '& .MuiChip-label': {
                                display: 'block',
                                whiteSpace: 'normal',
                              },
                            }}
                            className="chip"
                            color="primary"
                            key={earnedCert.id}
                            label={
                              <>
                                {cert.name} <br />
                                Issued on: {earnedCert.earnedDate}<br />
                                Expires on: {earnedCert.expirationDate}
                              </>
                            }
                        />
                    )}
                  </>
              );
            }
          })}
        </CardContent>
      </Card>
  );

};

EarnedCertificationBadges.propTypes = propTypes;

export default EarnedCertificationBadges;
