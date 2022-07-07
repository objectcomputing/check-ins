import React from "react";
import { Link } from "react-router-dom";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";

import signRequest from "../api/signRequest.js";

const SendRequest = () => {
  return (
    <div>
      <center>
        <h1>Click Button to Send SignRequest Form</h1>
      </center>
      <Button
        variant="contained"
        size="large"
        id="submitCode"
        onClick={sendSignRequest}
      >
        Send SignRequest
      </Button>
    </div>
  );
};

function sendSignRequest() {
  console.log("Sent Sign Request");
  signRequest.executeSendSignRequest();
}

export default SendRequest;
