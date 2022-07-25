import React from "react";
import { Button, Typography, Grid } from "@mui/material";
import "./DocumentSigningPage.css";
// import DocumentIndicator from "./DocumentIndicator";

const DocumentSigningPage = () => {
  return (
    <div>
      <Grid
        container
        justifyContent="center"
        alignItems="center"
        direction="row"
      >
        <Grid item xs={8}>
          <h2 style={{ marginTop: "1rem" }}>
            Please select the document you wish to open to view or sign. When
            you completed all the required documents please move ot the next
            screen.
          </h2>
        </Grid>
        <Grid container item xs={12} sm={12} md={6}>
          <Grid item xs={12} sm={12} md={6}>
            <Typography>Technology Release</Typography>
            <Button onClick={technologyRelease}>Open</Button>
          </Grid>
          <Grid item xs={12} sm={12} md={6}>
            <Typography>Employment Agreement</Typography>
            <Button onClick={employmentAgreement}>Open</Button>
          </Grid>
        </Grid>
      </Grid>
    </div>
  );

  function technologyRelease() {
    window.location.href = "http://localhost:3000/embed-request";
  }
  function employmentAgreement() {
    window.location.href = "http://localhost:3000/embed-request";
  }
};

export default DocumentSigningPage;
