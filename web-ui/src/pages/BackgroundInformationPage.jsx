import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./BackgroundInformationPage.css";

const BackgroundInformationPage = () => {
  return (
    <div>
      <center>
        <h1>Please enter in your background information</h1>
      </center>
      <Link to="/culturevideo">
        <Button variant="contained" size="large" id="cultureVideo">
          Culture Video
        </Button>
      </Link>
      <Link to="/survey">
        <Button variant="contained" size="large" id="introductionSurvey">
          Introduction Survey
        </Button>
      </Link>
    </div>
  );
};

export default BackgroundInformationPage;
