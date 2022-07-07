import React, { useState } from "react";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import TextField from "@mui/material/TextField";
import { Typography } from "@mui/material";
import { styled } from "@mui/material/styles";
import Grid from "@mui/material/Grid";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === "dark" ? "#1A2027" : "#fff",
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: "center",
  color: theme.palette.text.secondary,
}));

function EmploymentEligbility() {
  const [age18Yrs, setAge18Yrs] = useState("");
  const [usCitizen, setUSCitizen] = useState("");
  const [visaStatus, sestVisaStatus] = useState("");
  const [dateOfExpiration, setDateOfExpiration] = useState("");
  const [felonyStatus, setFelonyStatus] = useState("");
  const [felonyExplanation, setFelonyExplanation] = useState("");

  return (
    <>
      <Box sx={{ width: "100%" }}>
        <Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }}>
          <Grid item xs={6}>
            <Item>
              <FormControl>
                <FormLabel id="18-yrs-or-not"> Are you 18 years old?</FormLabel>
                <RadioGroup
                  aria-labelledby="18-yrs-or-not"
                  name="18-yrs-or-not"
                  value={age18Yrs}
                  onChange={(e) => {
                    setAge18Yrs(e.target.value);
                  }}
                >
                  <FormControlLabel
                    value="yes"
                    control={<Radio />}
                    label="Yes"
                  />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
              </FormControl>
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <FormControl>
                <FormLabel id="us-citizen-or-not">
                  If hired, can you present evidence of U.S citizenship or your
                  legal right to live and work in the United States?
                </FormLabel>
                <RadioGroup
                  aria-labelledby="us-citizen-or-not"
                  name="us-citizen-or-not"
                  value={usCitizen}
                  onChange={(e) => {
                    setUSCitizen(e.target.value);
                  }}
                >
                  <FormControlLabel
                    value="yes"
                    control={<Radio />}
                    label="Yes"
                  />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
              </FormControl>
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <Typography>Visa Status if Applicable</Typography>
              <TextField
                id="outlined-basic"
                variant="outlined"
                value={visaStatus}
                onChange={(e) => {
                  sestVisaStatus(e.target.value);
                }}
              />
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <TextField
                type="date"
                id="outlined-basic"
                label="Date of Expiration"
                variant="outlined"
                value={dateOfExpiration}
                onChange={(e) => {
                  setDateOfExpiration(e.target.value);
                }}
              />
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <FormControl>
                <FormLabel id="felony-or-not">
                  Have you ever been convecited of a felony?
                </FormLabel>
                <RadioGroup
                  aria-labelledby="felony-or-not"
                  name="felony-or-not"
                  value={felonyStatus}
                  onChange={(e) => {
                    setFelonyStatus(e.target.value);
                  }}
                >
                  <FormControlLabel
                    value="yes"
                    control={<Radio />}
                    label="Yes"
                  />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
              </FormControl>
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <TextField
                id="outlined-basic"
                label="If yes, please explain:"
                variant="outlined"
                value={felonyExplanation}
                onChange={(e) => {
                  setFelonyExplanation(e.target.value);
                }}
              />
            </Item>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default EmploymentEligbility;
