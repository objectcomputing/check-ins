import React from "react";
import TextField from "@mui/material/TextField";
import "./IntroductionSurveyPage.css";

const IntroductionSurveyPage = () => {
  return (
    <div>
      <center>
        <h1>You Know About Us Now! Tell Us About You!</h1>
      </center>

      <div className="aboutYourself" id="shortIntro">
        <strong>1.</strong> Please give us a short introduction about yourself
        <div className="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>

      <div className="aboutYourself" id="hobbies">
        <strong>2.</strong> What are some of your hobbies?
        <div className="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>

      <div className="aboutYourself" id="funFact">
        <strong>3.</strong> Can you tell us a fun fact about yourself?
        <div className="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>

      <div className="aboutYourself" id="specialCertificates">
        <strong>4.</strong> Are there any special certificates we would need to
        know?
        <div className="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>
    </div>
  );
};

export default IntroductionSurveyPage;
