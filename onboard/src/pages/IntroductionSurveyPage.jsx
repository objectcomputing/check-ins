import React from "react";
import TextField from "@mui/material/TextField";
import "./IntroductionSurveyPage.css";

const IntroductionSurveyPage = () => {
  return (
    <div>
      <center>
        <h1>You Know About Us Now! Tell Us About You!</h1>
      </center>

      <div class="aboutYourself" id="shortIntro">
        <strong>1.</strong> Please give us a short introduction about yourself
        <div class="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>

      <div class="aboutYourself" id="hobbies">
        <strong>2.</strong> What are some of your hobbies?
        <div class="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>

      <div class="aboutYourself" id="funFact">
        <strong>3.</strong> Can you tell us a fun fact about yourself?
        <div class="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>

      <div class="aboutYourself" id="specialCertificates">
        <strong>4.</strong> Are there any special certificates we would need to
        know?
        <div class="aboutYouInput">
          <TextField id="outlined-basic" label="" variant="outlined" />
        </div>
      </div>
    </div>
  );
};

export default IntroductionSurveyPage;
