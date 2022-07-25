import React, { useState } from "react";
import FormControl from "@mui/material/FormControl";
import Grid from "@mui/material/Grid";
import InputField from "../../../components/inputs/InputField";
import TextField from "../../../components/inputs/TextField";
import Box from "@mui/material/Box";

function EmploymentHistory() {
  const [company, setCompany] = useState("");
  const [comapanyAddress, setCompanyAddress] = useState("");
  const [jobTitle, setJobIitle] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [reason, setReason] = useState("");

  const [companyHelper, setCompanyHelper] = useState("");
  const [companyAddressHelper, setCompanyAddressHelper] = useState("");
  const [jobTitleHelper, setJobIitleHelper] = useState("");
  const [startDateHelper, setStartDateHelper] = useState("");
  const [endDateHelper, setEndDateHelper] = useState("");
  const [reasonHelper, setReasonHelper] = useState("");

  const [companyError, setComapnyError] = useState(false);
  const [companyAddressError, setCompanyAddressError] = useState(false);
  const [jobTitleError, setJobTitleError] = useState(false);
  const [startDateError, setStartDateError] = useState(false);
  const [endDateError, setEndDateError] = useState(false);
  const [reasonError, setReasonError] = useState(false);

  function handleSaveInformation(e) {
    e.preventDefault();
    // console.log("TODO: Submit data to backend!");
  }

  function handleChnage(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    if (name === "company") {
      setCompany(val);
      if (val.length > 0) {
        setComapnyError(false);
        setCompanyHelper("");
      } else {
        setComapnyError(true);
        setCompanyHelper(
          "Please enter in the name of the company you previously worked for"
        );
      }
    } else if (name === "companyAddress") {
      setCompanyAddress(val);
      if (val.length > 0) {
        setCompanyAddressError(false);
        setCompanyAddressHelper("");
      } else {
        setCompanyAddressError(true);
        setCompanyAddressHelper(
          "Please enter in the address of the company you previously worked for"
        );
      }
    } else if (name === "jobTitle") {
      setJobIitle(val);
      if (val.length > 0) {
        setJobTitleError(false);
        setJobIitleHelper("");
      } else {
        setJobTitleError(true);
        setJobIitleHelper(
          "Please enter in the title of the job you had previously"
        );
      }
    } else if (name === "startDate") {
      setStartDate(val);
    } else if (name === "endDate") {
      setEndDate(val);
    } else if (name === "reason") {
      setReason(val);
      if (val.length > 0) {
        setReasonError(false);
        setReasonHelper("");
      } else {
        setReasonError(true);
        setReasonHelper(
          "Please enter in your reason for leaving your previous job"
        );
      }
    }
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
                title={"Company"}
                id="company"
                value={company}
                autoFocus={true}
                error={companyError}
                onChangeHandler={handleChnage}
                helperMessage={companyHelper}
                label="Company"
                type="text"
              ></InputField>
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
                title={"Company Address"}
                id="companyAddress"
                value={comapanyAddress}
                error={companyAddressError}
                onChangeHandler={handleChnage}
                helperMessage={companyAddressHelper}
                label="Company Address"
                type="text"
              ></InputField>
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
                title={"Job Title"}
                id="jobTitle"
                value={jobTitle}
                error={jobTitleError}
                onChangeHandler={handleChnage}
                helperMessage={jobTitleHelper}
                label="Job Title"
                type="text"
              ></InputField>
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
                title={"Start Date"}
                id="startDate"
                value={startDate}
                error={startDateError}
                onChangeHandler={handleChnage}
                helperMessage={startDateHelper}
                label="Start Date"
                type="date"
              ></InputField>
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
                title={"End Date"}
                id="endDate"
                value={endDate}
                error={endDateError}
                onChangeHandler={handleChnage}
                helperMessage={endDateError}
                label="End Date"
                type="date"
              ></InputField>
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
                title={"Reason For Leaving"}
                id="reason"
                value={reason}
                error={reasonError}
                onChangeHandler={handleChnage}
                helperMessage={reasonHelper}
                label="Reason For Leaving"
                type="text"
              ></TextField>
            </FormControl>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}

export default EmploymentHistory;
