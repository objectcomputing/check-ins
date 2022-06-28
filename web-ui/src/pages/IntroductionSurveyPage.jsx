import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./IntroductionSurveyPage.css";

const IntroductionSurveyPage = () => {
  return (
    <div>
      <center>
        <h1>You Know About Us Now! Tell Us About You!</h1>
      </center>
      <Link to="/backgroundinformation">
        <Button variant="contained" size="large" id="backgroundInformation">
          Background Information Survey
        </Button>
      </Link>
      <Link to="/worklocation">
        <Button variant="contained" size="large" id="workLocation">
          Work Location
        </Button>
      </Link>
    </div>
  );
};

export default IntroductionSurveyPage;
