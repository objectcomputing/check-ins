import React from "react";
import { Link } from "react-router-dom";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import "./AccessCodePage.css";

function AccessCodePage() {
  return (
    <div>
      <center>
        <h1>Please Enter in your Access Code Below</h1>
      </center>

      <div class="inputCode">Access Code:</div>

      <div class="inputField">
        <TextField
          required
          error
          id="outlined-error-helper-text"
          label="Error"
          defaultValue="Input Code"
          helperText="Incorrect entry."
          size="small"
        />
      </div>

      <Link to="/onboarding">
        <Button variant="contained" size="large" id="cultureVideo">
          Culture Video
        </Button>
      </Link>

      <Button variant="contained" size="large" id="submitCode">
        Submit
      </Button>
    </div>
  );
}

export default AccessCodePage;
