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

  const [positionHelper, setPositionHelper] = useState(
    "Please enter the position you are applying for"
  );
  const [desiredSalaryHelper, setDesiredSalaryHelper] = useState(
    "Please enter in your desired salary"
  );
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
  const [startDateHelper, setStartDateHelper] = useState();
  const [expirationDateHelper, setExpirationDateHelper] = useState();

  const [positionError, setPositionError] = useState(false);
  const [desiredSalaryError, setDesiredSalaryError] = useState(false);
  const [expirationDateError, setExpirationDateError] = useState(false);
  const [startDateError, setStartDateError] = useState(false);

  function isDateInTheFuture(startDate) {
    let date = startDate;
    let currentDate = new Date();
    let inputDate = new Date(date.split("-"));

    if (inputDate.getTime() > currentDate.getTime()) {
      return true;
    } else {
      return false;
    }
  }

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    if (name === "position") {
      setPosition(val);
      if (val.length > 0) {
        setPositionHelper("");
        setPositionError(false);
      } else {
        setPositionHelper("Please enter the position you are applying for");
        setPositionError(true);
      }
    } else if (name === "startDate") {
      setStartDate(val);
      if (isDateInTheFuture(val)) {
        setStartDateError(false);
        setStartDateHelper("");
      } else {
        setStartDateError(true);
        setStartDateHelper("Please enter in a valid start date");
      }
    } else if (name === "desiredSalary") {
      setDesiredSalary(val);
      if (val.length > 0) {
        setDesiredSalaryHelper("");
        setDesiredSalaryError(false);
      } else {
        setDesiredSalaryHelper("Please enter in your desired salary");
        setDesiredSalaryError(true);
      }
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
      if (val === "yes" && expirationDate === "") {
        setExpirationDateError(true);
        setExpirationDateHelper(
          "Since you selected yes in the previous question please enter in a valid date"
        );
      } else {
        setExpirationDateError(false);
        setExpirationDateHelper("");
      }
      setNonCompeteStatusHelper("");
    } else if (name === "expirationDate") {
      setExpirationDate(val);
      if (nonCompeteStatus === "yes" && !isDateInTheFuture(val)) {
        if (val.length === 0) {
          setExpirationDateError(true);
          setExpirationDateHelper("Please enter in a valid date");
        }
        setExpirationDateError(true);
        setExpirationDateHelper("Please enter in a valid date");
      } else {
        setExpirationDateError(false);
        setExpirationDateHelper("");
      }
    }
  }

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
                error={positionError}
                onChangeHandler={handleChange}
                helperMessage={positionHelper}
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
                id="startDate"
                value={startDate}
                error={startDateError}
                helperMessage={startDateHelper}
                onChangeHandler={handleChange}
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
                error={desiredSalaryError}
                helperMessage={desiredSalaryHelper}
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
                helperMessage={expirationDateHelper}
                error={expirationDateError}
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
