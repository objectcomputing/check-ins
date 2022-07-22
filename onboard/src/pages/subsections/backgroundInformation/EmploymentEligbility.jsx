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

  const [visaStatusHelper, setVisaStatusHelper] = useState(
    "Please enter your Visa Status"
  );
  const [age18YrsHelper, setAge18YrsHelper] = useState(
    "Please select a valid option"
  );
  const [citizenHelper, setCitizenHelper] = useState(
    "Please select a valid option"
  );
  const [felonyHelper, setFelonyHelper] = useState(
    "Please select a valid option"
  );
  const [felonyExplanationHelper, setFelonyExplanationHelper] = useState("");
  const [dateOfExpirationHelper, setDateOfExpirationHelper] = useState("");

  const [dateOfExpirationError, setDateOfExpirationError] = useState("");
  const [felonyExplanationError, setFelonyExplanationError] = useState(false);

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    if (name === "18-or-not") {
      setAge18Yrs(val);
      setAge18YrsHelper("");
    } else if (name === "us-Citizen-or-not") {
      setUSCitizen(val);
      setCitizenHelper("");
    } else if (name === "visaStatus") {
      setVisaStatus(val);
      if (val.length > 0 && dateOfExpiration === "") {
        setVisaStatusHelper("");
        setDateOfExpirationError(true);
        setDateOfExpirationHelper(
          "Since you entered in your visa status, please enter in a valid expiratation date"
        );
      } else {
        setDateOfExpirationError(false);
        setDateOfExpirationHelper("");
      }
    } else if (name === "dateOfExpiration") {
      setDateOfExpiration(val);
      if (val.length > 0) {
        setDateOfExpirationError(false);
        setDateOfExpirationHelper("");
      } else {
        setDateOfExpirationError(true);
        setDateOfExpirationHelper("Please enter in an expiration date");
      }
    } else if (name === "felonyStatus") {
      setFelonyStatus(val);
      setFelonyHelper("");
      if (val === "no") {
        setFelonyExplanationError(false);
        setFelonyExplanationHelper("");
      } else if (val === "yes") {
        setFelonyExplanationError(true);
        setFelonyExplanationHelper(
          "Since you selected yes in the previous question please explain"
        );
      }
    } else if (name === "felonyExplaination") {
      setFelonyExplanation(val);
      if (felonyStatus === "yes" && val.length === 0) {
        setFelonyExplanationError(true);
        setFelonyExplanationHelper(
          "Since you selected yes in the previous question please explain"
        );
      } else {
        setFelonyExplanationError(false);
        setFelonyExplanationHelper("");
      }
    }
  }

  function handleSaveInformation(e) {
    e.preventDefault();
    //console.log("TODO: Submit data to backend!");
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
          <Grid item xs={12} sm={12} md={12} lg={6}>
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
                autoFocus={true}
                name="18-or-not"
                value={age18Yrs}
                onChange={handleChange}
              >
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{age18YrsHelper}</FormHelperText>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
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
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{citizenHelper}</FormHelperText>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
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
                title="Visa Status:"
                id="visaStatus"
                value={visaStatus}
                autoFocus={true}
                onChangeHandler={handleChange}
                label="Visa Status if applicable"
                type="text"
                helperMessage={visaStatusHelper}
              />
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <FormLabel>Date of Expiration:</FormLabel>
              <InputField
                id="dateOfExpiration"
                value={dateOfExpiration}
                error={dateOfExpirationError}
                helperMessage={dateOfExpirationHelper}
                onChangeHandler={handleChange}
                placeholder="dd/mm/yyyy"
                type="date"
              />
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <FormLabel>Have you ever been convicted of a felony?</FormLabel>
              <RadioGroup
                name="felonyStatus"
                value={felonyStatus}
                onChange={handleChange}
              >
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{felonyHelper}</FormHelperText>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
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
                title="If you have been convicted of a felony, please explain:"
                id="felonyExplaination"
                value={felonyExplanation}
                error={felonyExplanationError}
                onChangeHandler={handleChange}
                helperMessage={felonyExplanationHelper}
                label="If yes, please Explain"
                type="text"
              />
            </FormControl>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}

export default EmploymentEligbility;
