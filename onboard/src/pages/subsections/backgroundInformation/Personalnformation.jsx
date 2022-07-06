import React from "react"
import TextField from "@mui/material/TextField";

function PersonalInformation () {
    return (
        <>
            <TextField
              id="outlined-basic"
              label="First Name"
              variant="outlined"
            />

            <TextField
              id="outlined-basic"
              label="Last Name"
              variant="outlined"
            />

            <TextField
              id="outlined-basic"
              label="Middle Initial"
              variant="outlined"
            />

            <TextField
              type="number"
              id="outlined-basic"
              label="SSN"
              variant="outlined"
            />
            <TextField
              type="date"
              id="outlined-basic"
              label="Birthdate"
              variant="outlined"
            />
            <TextField
              id="outlined-basic"
              label="Current Address"
              variant="outlined"
            />
            <TextField
              id="outlined-basic"
              label="Permanent Address"
              variant="outlined"
            />
            <TextField
              type="number"
              id="outlined-basic"
              label="Primary Phone Number"
              variant="outlined"
            />
            <TextField
              type="number"
              id="outlined-basic"
              label="Secondary Phone Number"
              variant="outlined"
            />
          </>
    );
}

export default PersonalInformation;