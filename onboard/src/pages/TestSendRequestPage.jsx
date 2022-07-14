import React from "react";
import { Button } from "@mui/material";

const SendingRequestPage = () => {
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

  function sendSignRequest() {
    console.log("Hi");
  }
};

export default SendingRequestPage;
