import React, { useState } from "react";
import { styled } from "@mui/material/styles";
import Grid from "@mui/material/Grid";
import Paper from "@mui/material/Paper";
import Input from "@mui/material/Input";
import Box from "@mui/material/Box";
import {validPhoneNum, validSSN } from "../../../components/Regex";

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === "dark" ? "#1A2027" : "#fff",
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: "center",
  color: theme.palette.text.secondary,
}));

function PersonalInformation() {
  const [phoneNum, setPhoneNum] = useState("");
  const [secondaryPhoneNum, setSecondaryPhoneNum] = useState("");
  const [ssn, setSSN] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");

  const validate = (type) => {
    switch (type) {
      case "PhoneNum":
        if (validPhoneNum.test(phoneNum)) {
          console.log("phone num Good");

          return true;
        } else {
          console.log("phone num bad");

          return false;
        }

      case "SSN":
        if (validSSN.test(ssn)) {
          console.log("ssn Good");
          return true;
        } else {
          console.log("ssn bad");
          return false;
        }

      default:
    }
  };

  return (
    <>
      <Box sx={{ width: "100%" }}>
        <Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }}>
          <Grid item xs={4}>
            <Item>
              First Name:
              <Input
                placeholder="John"
                id="outlined-basic"
                label="First Name"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Last Name:
              <Input
                id="outlined-basic"
                label="Last Name"
                variant="outlined"
                placeholder=" Doe"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Middle Initial:
              <Input
                placeholder=" H"
                id="outlined-basic"
                label="Middle Initial"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              SSN:
              <Input
                type="password"
                id="outlined-basic"
                label="SSN"
                placeholder=" xxx-xx-xxxx"
                required="true"
                variant="outlined"
                error={!validate("SSN")}
                value={ssn}
                onChange={(e) => {
                  setSSN(e.target.value);
                  validate("SSN");
                }}
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Birth Date:
              <Input
                type="date"
                id="outlined-basic"
                label="Birthdate"
                variant="outlined"
                value={dateOfBirth}
                placeholder=" dd/mm/yyyy"
                onChange={(e) => {
                  setDateOfBirth(e.target.value);
                  
                }}
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Current Address:
              <Input
                placeholder=" 123 Main Street USA"
                id="outlined-basic"
                label="Current Address"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Permanent Address:
              <Input
                placeholder=" 123 Main Street USA"
                id="outlined-basic"
                label="Permanent Address"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Primary Phone Number:
              <Input
                required="true"
                id="outlined-basic"
                label="Primary Phone Number"
                variant="outlined"
                value={phoneNum}
                placeholder=" 123-456-7890"
                error={!validate("PhoneNum")}
                onChange={(e) => {
                  setPhoneNum(e.target.value);
                  validate("PhoneNum");
                }}
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              Secondary Phone Number
              <Input
                type="number"
                id="outlined-basic"
                label="Secondary Phone Number"
                placeholder=" 123-456-7890"
                variant="outlined"
                value={secondaryPhoneNum}
                onChange={(e) => setSecondaryPhoneNum(e.target.value)}
              />
            </Item>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default PersonalInformation;
