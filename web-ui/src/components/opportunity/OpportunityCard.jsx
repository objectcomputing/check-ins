import React, { useContext } from 'react';

import { selectIsAdmin } from '../../context/selectors';
import { AppContext } from '../../context/AppContext';

import { format } from 'date-fns';
import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import AssignmentIcon from '@mui/icons-material/Assignment';

const OpportunityCard = ({ opportunity }) => {
  const { state } = useContext(AppContext);
  const isAdmin = selectIsAdmin(state);

  const { description, expiresOn, name, pending, url } = opportunity;
  return (
    <Card className="opportunity" key={name}>
      <CardHeader
        avatar={<AssignmentIcon />}
        title={
          <div className="opportunity-header">
            <Typography variant="h4" component="h3">
              {name}
            </Typography>
          </div>
        }
      />
      <CardContent className="opportunity-card">
        {description || ''}
        <br />
        {url ? <a href={url}>More Information</a> : null}
        <br />
        {expiresOn
          ? `Expires on ${format(new Date(expiresOn), 'MM/dd/yyy')}`
          : null}
        <br />
        {isAdmin && pending ? `Pending: ${pending}` : null}
      </CardContent>
    </Card>
  );
};

export default OpportunityCard;
