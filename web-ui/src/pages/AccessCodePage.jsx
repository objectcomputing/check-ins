import React from "react";
import { Link } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import "./AccessCodePage.css";

function AccessCodePage() {

  const nextPageHandler = () =>{

  }

  return (
    <div>
<<<<<<< HEAD
      <div className="page_body">
        <div className="page_content">
          <center>
            <h1>Please Enter in your Access Code:</h1>
          </center>
          <Link to="/culturevideo">
            <Button onClick = {nextPageHandler} variant="contained" size="large" id="cultureVideo">
              Culture Video
            </Button>
          </Link>
        </div>
      </div>
=======
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

      <Link to="/culturevideo">
        <Button variant="contained" size="large" id="cultureVideo">
          Culture Video
        </Button>
      </Link>

      <Button variant="contained" size="large" id="submitCode">
        Submit
      </Button>
>>>>>>> 0757d3feaf52bf14265be69e83085d3f812d6e9d
    </div>
  );
}

export default AccessCodePage;
