import React from "react";

import { Card, CardContent, CardHeader, Typography } from "@material-ui/core";

const OpportunityCard = ({ opportunity }) => {
  console.log({ opportunity });
  const { description, expiresOn, name, pending, url } = opportunity;
  return (
    <Card className="opportunity" key={name}>
      <CardHeader
        title={
          <div className="opportunity-header">
            <Typography variant="h4" component="h3">
              {name}
            </Typography>
          </div>
        }
      />
      <CardContent className="opportunity-card">
        {description || ""}
        <br />
        {url ? `More Information` && <a href={url} /> : null}
        <br />
        {expiresOn ? `Expires on ${new Date(expiresOn)}` : null}
        <br />
        {pending ? `Pending: ${pending}` : null}
      </CardContent>
    </Card>
  );
};

export default OpportunityCard;
