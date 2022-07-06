import React from "react";
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
  return (
    <>
      <Box sx={{ width: "100%" }}>
        <Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }}>
          <Grid item xs={6}>
            <Item>
              <FormControl>
                <FormLabel id="18-yrs-or-not">
                  Are you 18 years of age?
                </FormLabel>
                <RadioGroup
                  aria-labelledby="18-yrs-or-not"
                  defaultValue="no"
                  name="18-yrs-radio-buttons-group"
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
                  <Typography style={{ wordWrap: "break-word" }}>
                    If hired, can you present evidence of U.S. citizenship or
                    your legal right to live and work in the United States?
                  </Typography>
                </FormLabel>
                <RadioGroup
                  aria-labelledby="us-citizen-or-not"
                  defaultValue="no"
                  name="us-citizen-radio-buttons-group"
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
              <TextField id="outlined-basic" variant="outlined" />
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <TextField
                type="date"
                id="outlined-basic"
                label="Date of Expiration"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={6}>
            <Item>
              <FormControl>
                <FormLabel id="felony-or-not">
                  <Typography style={{ wordWrap: "break-word" }}>
                    Have you ever been convicted of a felony?
                  </Typography>
                </FormLabel>
                <RadioGroup
                  aria-labelledby="elony-or-not"
                  defaultValue="no"
                  name="felony-or-not"
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
              />
            </Item>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default EmploymentEligbility;
