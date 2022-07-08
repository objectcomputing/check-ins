import React, { useState } from "react";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import Grid from "@mui/material/Grid";
import TextField from "../../../components/inputs/TextField";
import InputField from "../../../components/inputs/InputField";
import Box from "@mui/material/Box";
import { FormHelperText } from "@mui/material";

function EmploymentEligbility() {
  const [age18Yrs, setAge18Yrs] = useState("");
  const [usCitizen, setUSCitizen] = useState("");
  const [visaStatus, setVisaStatus] = useState("");
  const [dateOfExpiration, setDateOfExpiration] = useState("");
  const [felonyStatus, setFelonyStatus] = useState("");
  const [felonyExplanation, setFelonyExplanation] = useState("");

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    console.log(val);
    console.log(name);

    if (name === "18-or-not") {
      setAge18Yrs(val);
    } else if (name === "us-Citizen-or-not") {
      setUSCitizen(val);
    } else if (name === "visaStatus") {
      setVisaStatus(val);
    } else if (name === "dateOfExpiration") {
      setDateOfExpiration(val);
    } else if (name === "felonyStatus") {
      setFelonyStatus(val);
    } else if (name === "felonyExplaination") {
      setFelonyExplanation(val);
    }
  }

  function handleSaveInformation(e) {
    e.preventDefault();
    console.log("TODO: Submit data to backend!");
  }

  return (
    <>
      <Box sx={{ width: "100%" }}>
        <Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }}>
          <Grid item xs={8}>
            <form autoComplete="off" onSubmit={handleSaveInformation}>
              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>Are you at least 18yrs of age?</FormLabel>
                <RadioGroup
                  name="18-or-not"
                  value={age18Yrs}
                  onChange={handleChange}
                >
                  <FormControlLabel
                    value="yes"
                    control={<Radio />}
                    label="Yes"
                  />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
                <FormHelperText>
                  {"Please select a valid option"}
                </FormHelperText>
              </FormControl>

              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>
                  If hired, can you present evidence of U.S. citizenship or your
                  legal right to live and work in the United States?
                </FormLabel>
                <RadioGroup
                  name="us-Citizen-or-not"
                  value={usCitizen}
                  onChange={handleChange}
                >
                  <FormControlLabel
                    value="yes"
                    control={<Radio />}
                    label="Yes"
                  />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
                <FormHelperText>
                  {"Please select a valid option"}
                </FormHelperText>
              </FormControl>

              <FormControl
                sx={{
                  marginTop: 3,
                  marginBottom: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <InputField
                  title="Visa Status"
                  id="visaStatus"
                  value={visaStatus}
                  autoFocus={true}
                  onChangeHandler={handleChange}
                  label="Visa Status if applicable"
                  type="text"
                  helperMessage={"Please enter your Visa Status"}
                />
              </FormControl>

              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>Date of Expiration</FormLabel>
                <InputField
                  id="dateOfExpiration"
                  value={dateOfExpiration}
                  onChangeHandler={handleChange}
                  placeholder="dd/mm/yyyy"
                  type="date"
                />
              </FormControl>

              <FormControl
                sx={{
                  my: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <FormLabel>Have you ever been convected of a felony</FormLabel>
                <RadioGroup
                  name="felonyStatus"
                  value={felonyStatus}
                  onChange={handleChange}
                >
                  <FormControlLabel
                    value="yes"
                    control={<Radio />}
                    label="Yes"
                  />
                  <FormControlLabel value="no" control={<Radio />} label="No" />
                </RadioGroup>
                <FormHelperText>
                  {"Please select a valid option"}
                </FormHelperText>
              </FormControl>

              <FormControl
                sx={{
                  marginTop: 3,
                  marginBottom: 1,
                  marginLeft: 3,
                  width: "90%",
                  maxWidth: "500px",
                }}
              >
                <TextField
                  title="If yes, please Explain:"
                  id="felonyExplaination"
                  value={felonyExplanation}
                  autoFocus={true}
                  onChangeHandler={handleChange}
                  label="If yes, please Explain"
                  type="text"
                />
              </FormControl>
            </form>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default EmploymentEligbility;
