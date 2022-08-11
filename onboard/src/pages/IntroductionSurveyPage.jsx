import React from "react";
import RadioGroup from "@mui/material/RadioGroup";
import FormControl from "@mui/material/FormControl";
import TextField from "@mui/material/TextField";
import {
  Checkbox,
  Grid,
  Box,
  Radio,
  FormControlLabel,
  FormLabel,
} from "@mui/material";
import "./IntroductionSurveyPage.css";

const IntroductionSurveyPage = () => {
  function handleSaveInformation(e) {
    e.preventDefault();
    // console.log("TODO: Submit data to backend!");
  }

  return (
    <Box sx={{ width: "100%", textAlign: "left" }}>
      <form autoComplete="off" onSubmit={handleSaveInformation}>
        <Grid
          container
          rowSpacing={1}
          columnSpacing={{ xs: 1, sm: 2, md: 3 }}
          sx={{ marginTop: 3 }}
        >
          <Grid item xs={6} md={6}>
            <Grid item xs={12}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>1. What T-Shirt size do you prefer?</FormLabel>
                <RadioGroup>
                  <FormControlLabel value="XS" control={<Radio />} label="XS" />
                  <FormControlLabel value="S" control={<Radio />} label="S" />
                  <FormControlLabel value="M" control={<Radio />} label="M" />
                  <FormControlLabel value="L" control={<Radio />} label="L" />
                  <FormControlLabel value="XL" control={<Radio />} label="XL" />
                  <FormControlLabel value="XXL" control={<Radio />} label="XXL" />
                </RadioGroup>
              </FormControl>
            </Grid>
            <Grid item xs={12} sx={{ mt: 4 }}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>2. Over the course of the next couple of weeks you will receive an email from Google asking you to set up your OCI Google Account. Please watch for this as it expires 48 hours after it is sent. Once set up, you will then have access to all of the tools listed below. Please identify which, if any, you would like training on:</FormLabel>
                <FormControlLabel value="gmail" control={<Checkbox />} label="Gmail" />
                <FormControlLabel value="google_calendar" control={<Checkbox />} label="Google Calendar" />
                <FormControlLabel value="google_meet" control={<Checkbox />} label="Google Meet" />
                <FormControlLabel value="google_docs" control={<Checkbox />} label="Google Docs" />
                <FormControlLabel value="google_sheets" control={<Checkbox />} label="Google Sheets" />
                <FormControlLabel value="google_drive" control={<Checkbox />} label="Google Drive" />
                <FormControlLabel value="google_chat" control={<Checkbox />} label="Google Chat" />
                <FormControlLabel value="know_how_use_all" control={<Checkbox />} label="I know how to use all the above" />
              </FormControl>
            </Grid>
            <Grid item xs={12} sx={{ mt: 4 }}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>3. Please help us introduce you to the team by providing a short introduction to yourself.  This could include such things as your background, interests (professional as well as personal), pets/family/friends, reasons for joining OCI, etc.  </FormLabel>
                <TextField
                />
              </FormControl>
            </Grid>
          </Grid>
          <Grid item xs={6} md={6}>
            <Grid item xs={12}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>OCI is committed to ensuring the safety of our team members and our global neighbors. With this in mind, we have instituted a limited office opening. Until further notice, the office is open only to individuals who present to HR a receipt demonstrating that they have been fully immunized with the COVID vaccine.</FormLabel>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>4. Have you received all the vaccinations required by the manufacturer?</FormLabel>
                <RadioGroup>
                  <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
              </FormControl>
            </Grid>
            <Grid item xs={12} sx={{ mt: 4 }}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>5. Has it been at least 2 weeks since you received your final dosage?</FormLabel>
                <RadioGroup>
                  <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
              </FormControl>
            </Grid>
            <Grid item xs={12} sx={{ mt: 4 }}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>6. Based on your understanding of your role, is there any other training that you feel would be helpful?</FormLabel>
                <TextField

                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sx={{ mt: 4 }}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>7. What are some additional skills, interests, etc. that you have that you would like to expand upon while working at OCI?  </FormLabel>
                <TextField
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sx={{ mt: 4 }}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>8. Do you have any special certifications or training that you believe we should be aware of?  </FormLabel>
                <TextField
                />
              </FormControl>
            </Grid>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
};

export default IntroductionSurveyPage;