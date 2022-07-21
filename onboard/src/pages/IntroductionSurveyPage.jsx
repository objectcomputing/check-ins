import React from "react";
import TextField from "@mui/material/TextField";
import { Grid, Typography } from "@mui/material";
import "./IntroductionSurveyPage.css";

const IntroductionSurveyPage = () => {
  return (
    <Grid
      container
      justifyContent="center"
      alingItems="center"
      direction="row"
      xs={12}
    >
      <Grid item xs={8}>
        <Typography variant="h6">
          1. Please give us a short introduction about yourself
        </Typography>
        <TextField label="" variant="outlined" />

        <Typography variant="h6">2. What are some of your hobbies?</Typography>
        <TextField label="" variant="outlined" />

        <Typography variant="h6">
          3. Can you tell us a fun fact about yourself?
        </Typography>
        <TextField label="" variant="outlined" />

        <Typography variant="h6">
          4. Are there any special certificates we would need to know?
        </Typography>
        <TextField label="" variant="outlined" />
      </Grid>
    </Grid>
  );
};

export default IntroductionSurveyPage;
