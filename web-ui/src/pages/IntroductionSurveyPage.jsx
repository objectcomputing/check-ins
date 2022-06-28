import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
const IntroductionSurveyPage = () => {
  return (
    <div>
      <center>
        <h1>You Know About Us Now! Tell Us About You!</h1>
      </center>
      <Link to="/backgroundinformation">
        <Button>Go to background information survey</Button>
      </Link>
      <Link to="/worklocation">
        <Button>Go to work location page</Button>
      </Link>
    </div>
  );
};

export default IntroductionSurveyPage;
