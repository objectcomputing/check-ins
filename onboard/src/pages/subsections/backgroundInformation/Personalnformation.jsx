import React, { useState } from "react";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import { FormControl } from "@mui/material";
import InputField from "../../../components/inputs/InputField";
import {
  validAddress,
  validPhoneNum,
  validSSN,
} from "../../../components/Regex";

function PersonalInformation() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [middleInital, setMiddleInital] = useState("");
  const [phoneNum, setPhoneNum] = useState("");
  const [secondaryPhoneNum, setSecondaryPhoneNum] = useState("");
  const [ssn, setSSN] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState(new Date());
  const [currentAddress, setCurrentAddress] = useState("");
  const [permanentAddress, setPermanentAddress] = useState("");

  const [dateOfBirthHelper, setDateOfBirthHelper] = useState("");
  const [firstNameHelper, setFirstNameHelper] = useState("");
  const [lastNameHelper, setLastNameHelper] = useState("");
  const [phoneNumHelper, setPhoneNumHelper] = useState("");
  const [middleInitalHelper, setMiddleInitalHelper] = useState("");
  const [secondaryPhoneNumHelper, setSecondaryPhoneNumHelper] = useState("");
  const [ssnHelper, setSSNHelper] = useState("");
  const [currentAddressHelper, setCurrentAddressHelper] = useState("");
  const [permanentAddressHelper, setPermanentAddressHelper] = useState("");

  const [dateOfBirthError, setDateOfBirthError] = useState(false);
  const [firstNameError, setFirstNameError] = useState(false);
  const [lastNameError, setLastNameError] = useState(false);
  const [middleInitalError, setMiddleInitalError] = useState(false);
  const [phoneNumError, setPhoneNumError] = useState(false);
  const [secondaryPhoneNumError, setSecondaryPhoneNumError] = useState(false);
  const [ssnError, setSSNError] = useState(false);
  const [currentAddressError, setCurrentAddressError] = useState(false);
  const [permanentAddressError, setPermanentAddressError] = useState(false);

  function isDateValid(date) {
    let dateValid = false;
    let currentDate = new Date();
    let inputDate = new Date(date.split("-"));
    let currentYear = currentDate.getFullYear();
    let inputYear = inputDate.getFullYear();

    let isInputYearCurrentOrFuture = inputYear >= currentYear;
    let olderThan100 = currentYear - 100 > inputYear;
    dateValid = !isInputYearCurrentOrFuture && !olderThan100;

    if (dateValid) {
      return true;
    } else {
      return false;
    }
  }

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    // Event handler for the fields
    if (name === "firstName") {
      setFirstName(val);
      if (val.length >= 2) {
        setFirstNameError(false);
        setFirstNameHelper("");
      } else {
        setFirstNameError(true);
        setFirstNameHelper("Please enter a full first name.");
      }
    } else if (name === "lastName") {
      setLastName(val);
      if (val.length >= 2) {
        setLastNameError(false);
        setLastNameHelper("");
      } else {
        setLastNameError(true);
        setLastNameHelper("Please enter a full last name.");
      }
    } else if (name === "middleInitial") {
      setMiddleInital(val);
      if (val.length === 1) {
        setMiddleInitalError(false);
        setMiddleInitalHelper("");
      } else {
        setMiddleInitalError(true);
        setMiddleInitalHelper("Please enter a one letter initial.");
      }
    } else if (name === "ssn") {
      setSSN(val);
      if (validSSN.test(val)) {
        setSSNError(false);
        setSSNHelper("");
      } else {
        setSSNError(true);
        setSSNHelper("Please enter a valid SSN.");
      }
    } else if (name === "dob") {
      setDateOfBirth(val);
      if (isDateValid(val) && val.length > 0) {
        setDateOfBirthError(false);
        setDateOfBirthHelper("");
      } else {
        setDateOfBirthError(true);
        setDateOfBirthHelper("Plase enter in a valid date");
      }
    } else if (name === "currentAddress") {
      setCurrentAddress(val);
      if (validAddress.test(val)) {
        setCurrentAddressError(false);
        setCurrentAddressHelper("");
      } else {
        setCurrentAddressError(true);
        setCurrentAddressHelper("Please enter in a valid address");
      }
    } else if (name === "permanentAddress") {
      setPermanentAddress(val);
      if (validAddress.test(val)) {
        setPermanentAddressError(false);
        setPermanentAddressHelper("");
      } else {
        setPermanentAddressError(true);
        setPermanentAddressHelper("Please enter in a valid address");
      }
    } else if (name === "phoneNum") {
      setPhoneNum(val);
      if (validPhoneNum.test(val)) {
        setPhoneNumError(false);
        setPhoneNumHelper("");
      } else {
        setPhoneNumError(true);
        setPhoneNumHelper("Please enter in a valid phone number");
      }
    } else if (name === "secondaryPhoneNum") {
      setSecondaryPhoneNum(val);
      if (validPhoneNum.test(val)) {
        setSecondaryPhoneNumError(false);
        setSecondaryPhoneNumHelper("");
      } else {
        setSecondaryPhoneNumError(true);
        setSecondaryPhoneNumHelper("Please enter in a valid phone number");
      }
    }
  }

  function handleSaveInformation(e) {
    e.preventDefault();
    console.log("TODO: Submit data to backend!");
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
              <InputField
                autocomplete="first-name"
                title="First Name:"
                id="firstName"
                value={firstName}
                autoFocus={true}
                error={firstNameError}
                onChangeHandler={handleChange}
                placeholder="John"
                type="text"
                helperMessage={firstNameHelper}
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
              <InputField
                autocomplete="last-name"
                title="Last Name:"
                id="lastName"
                value={lastName}
                error={lastNameError}
                onChangeHandler={handleChange}
                placeholder="Doe"
                type="text"
                helperMessage={lastNameHelper}
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
              <InputField
                autocomplete="middle-initial"
                title="Middle Initial:"
                id="middleInitial"
                value={middleInital}
                error={middleInitalError}
                onChangeHandler={handleChange}
                placeholder="H"
                type="text"
                helperMessage={middleInitalHelper}
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
              <InputField
                autocomplete="social-security-number"
                title="Social Security Number:"
                id="ssn"
                value={ssn}
                error={ssnError}
                onChangeHandler={handleChange}
                placeholder="xxx-xx-xxxx"
                type="password"
                helperMessage={ssnHelper}
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
              <InputField
                autocomplete="date-of-birth"
                title="Date of Birth:"
                id="dob"
                value={dateOfBirth}
                error={dateOfBirthError}
                onChangeHandler={handleChange}
                type="date"
                helperMessage={dateOfBirthHelper}
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
              <InputField
                autocomplete="current-address"
                title="Current Address:"
                id="currentAddress"
                value={currentAddress}
                error={currentAddressError}
                onChangeHandler={handleChange}
                placeholder="4391 Clover Drive, Colorado Springs, CO 80903"
                type="text"
                helperMessage={currentAddressHelper}
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
              <InputField
                autocomplete="permanent-address"
                title="Permanent Address:"
                id="permanentAddress"
                value={permanentAddress}
                error={permanentAddressError}
                onChangeHandler={handleChange}
                placeholder="4477 Hidden Valley Road, Buffalo, NY 14216"
                type="text"
                helperMessage={permanentAddressHelper}
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
              <InputField
                autocomplete="phone-number"
                title="Primary Phone Number:"
                id="phoneNum"
                value={phoneNum}
                error={phoneNumError}
                onChangeHandler={handleChange}
                placeholder="123-456-7890"
                type="text"
                helperMessage={phoneNumHelper}
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
              <InputField
                autocomplete="secondary-phone-number"
                title="Secondary Phone Number:"
                id="secondaryPhoneNum"
                value={secondaryPhoneNum}
                error={secondaryPhoneNumError}
                onChangeHandler={handleChange}
                placeholder="123-456-7890"
                type="text"
                helperMessage={secondaryPhoneNumHelper}
              />
            </FormControl>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}

export default PersonalInformation;
