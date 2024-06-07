import PropTypes from 'prop-types';
import React, { useCallback, useEffect, useState } from 'react';

import { Card, CardContent, CardHeader, Tooltip } from '@mui/material';

const organizationBaseUrl = 'http://localhost:3000/organization';
const relationshipBaseUrl = 'http://localhost:3000/volunteer-relationship';

const propTypes = {
  memberId: PropTypes.string
};

const VolunteerBadges = ({ memberId }) => {
  const [organizationMap, setOrganizationMap] = useState({});
  const [relationships, setRelationships] = useState([]);

  const loadOrganizations = useCallback(async () => {
    try {
      const res = await fetch(organizationBaseUrl);
      const organizations = await res.json();
      setOrganizationMap(
        organizations.reduce((acc, org) => ({ ...acc, [org.id]: org }), {})
      );
    } catch (err) {
      console.error(err);
    }
  }, []);

  const loadRelationships = useCallback(async () => {
    try {
      const res = await fetch(relationshipBaseUrl + '/' + memberId);
      const relationships = await res.json();
      setRelationships(relationships);
    } catch (err) {
      console.error(err);
    }
  }, [organizationMap]);

  useEffect(() => {
    loadOrganizations();
    loadRelationships();
  }, []);

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
