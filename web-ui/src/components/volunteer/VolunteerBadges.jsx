import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { Card, CardContent, CardHeader } from '@mui/material';

import { resolve } from '../../api/api.js';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';

const organizationBaseUrl = '/services/volunteer/organization';
const relationshipBaseUrl = '/services/volunteer/relationship';

const propTypes = {
  memberId: PropTypes.string
};

const VolunteerBadges = ({ memberId }) => {
  const [organizationMap, setOrganizationMap] = useState({});
  const [relationships, setRelationships] = useState([]);

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const loadOrganizations = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: organizationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const organizations = res.payload.data;
    setOrganizationMap(
      organizations.reduce((acc, org) => ({ ...acc, [org.id]: org }), {})
    );
  }, [csrf]);

  const loadRelationships = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: relationshipBaseUrl + '?=memberId=' + memberId,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const relationships = res.payload.data;
    setRelationships(relationships);
  }, [csrf]);

  useEffect(() => {
    if (csrf) {
      loadOrganizations();
      loadRelationships();
    }
  }, [csrf]);

  const relationshipRow = useCallback(
    relationship => {
      const org = organizationMap[relationship.organizationId];
      return (
        <div key={relationship.id}>
          <a alt={org.name} href={org.website} target="_blank">
            {org.name}
          </a>{' '}
          from {relationship.startDate}
        </div>
      );
    },
    [organizationMap]
  );

  if (relationships.length === 0) return null;
  if (Object.keys(organizationMap).length === 0) return null;

  return (
    <Card className="volunteer-badges">
      <CardHeader
        title="Volunteering"
        titleTypographyProps={{ variant: 'h5', component: 'h1' }}
      />
      <CardContent>{relationships.map(relationshipRow)}</CardContent>
    </Card>
  );
};

VolunteerBadges.propTypes = propTypes;

export default VolunteerBadges;
