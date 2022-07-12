import React, { useState } from "react";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import Grid from "@mui/material/Grid";
import InputField from "../../../components/inputs/InputField";
import Box from "@mui/material/Box";
import { FormHelperText } from "@mui/material";

function EmploymentDesired() {
  const [position, setPosition] = useState("");
  const [startDate, setStartDate] = useState("");
  const [desiredSalary, setDesiredSalary] = useState("");
  const [employmentStatus, setEmploymentStatus] = useState("");
  const [contactCurrentEmployer, setContactCurrentEmployer] = useState("");
  const [employedAtOCI, setEmployedAtOCI] = useState("");
  const [nonCompeteStatus, setNonCompeteStatus] = useState("");
  const [expirationDate, setExpirationDate] = useState("");

  const [nonCompeteStatusHelper, setNonCompeteStatusHelper] = useState(
    "Please select a valid option"
  );
  const [employmentStatusHelper, setEmploymentStatusHelper] = useState(
    "Please select a valid option"
  );
  const [contactCurrentEmployerHelper, setContactCurrentEmployerHelper] =
    useState("Please select a valid option");
  const [employedAtOCIHelper, setEmployedAtOCIHelper] = useState(
    "Please select a valid option"
  );

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    console.log(val);
    console.log(name);

    if (name === "position") {
      setPosition(val);
    } else if (name === "startDate") {
      setStartDate(val);
    } else if (name === "desiredSalary") {
      setDesiredSalary(val);
    } else if (name === "contactCurrentEmployer") {
      setContactCurrentEmployer(val);
      setContactCurrentEmployerHelper("");
    } else if (name === "employmentStatus") {
      setEmploymentStatus(val);
      setEmploymentStatusHelper("");
    } else if (name === "employedAtOCI") {
      setEmployedAtOCI(val);
      setEmployedAtOCIHelper("");
    } else if (name === "nonCompeteStatus") {
      setNonCompeteStatus(val);
      setNonCompeteStatusHelper("");
    } else if (name === "expirationDate") {
      setExpirationDate(val);
    }
  }

  function handleSaveInformation(e) {
    e.preventDefault();
    console.log("TODO: Submit data to backend!");
  }

  return (
    <Box sx={{ width: "100%" }}>
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
                marginTop: 3,
                marginBottom: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <InputField
                title="For what position are you applying for?"
                id="position"
                value={position}
                autoFocus={true}
                onChangeHandler={handleChange}
                label="For what position are you applying for?"
                type="text"
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
              <FormLabel>When would you like to start?</FormLabel>
              <InputField
                id="dob"
                value={startDate}
                onChangeHandler={handleChange}
                placeholder="dd/mm/yyyy"
                type="date"
              />
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
                title="What is your desired salary?"
                id="desiredSalary"
                value={desiredSalary}
                onChangeHandler={handleChange}
                label="What is your desired salary?"
                type="text"
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
              <FormLabel>Are you currently employed?</FormLabel>
              <RadioGroup
                name="employmentStatus"
                value={employmentStatus}
                onChange={handleChange}
              >
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{employmentStatusHelper}</FormHelperText>
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
                If yes, may we contact your current employer?
              </FormLabel>
              <RadioGroup
                name="contactCurrentEmployer"
                value={contactCurrentEmployer}
                onChange={handleChange}
              >
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{contactCurrentEmployerHelper}</FormHelperText>
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
              <FormLabel>Have you ever worked for OCI in the past?</FormLabel>
              <RadioGroup
                name="employedAtOCI"
                value={employedAtOCI}
                onChange={handleChange}
              >
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{employedAtOCIHelper}</FormHelperText>
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
                Do you have an non-compete agreement in effect that might
                preclude you from being employed by OCI?
              </FormLabel>
              <RadioGroup
                name="nonCompeteStatus"
                value={nonCompeteStatus}
                onChange={handleChange}
              >
                <FormControlLabel value="yes" control={<Radio />} label="Yes" />
                <FormControlLabel value="no" control={<Radio />} label="No" />
              </RadioGroup>
              <FormHelperText>{nonCompeteStatusHelper}</FormHelperText>
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
              <FormLabel>If yes, what is the expiration date:</FormLabel>
              <InputField
                id="expirationDate"
                value={expirationDate}
                onChangeHandler={handleChange}
                placeholder="dd/mm/yyyy"
                type="date"
              />
            </FormControl>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}

export default EmploymentDesired;
