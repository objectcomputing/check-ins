import React, { useState, useEffect, useCallback, useRef } from "react";
import signRequest from "../api/signrequest_send";
import { Button } from "@mui/material";

const SendRequest = () => {
  const [isSending, setIsSending] = useState(false);
  const isMounted = useRef(true);

  // Set isMounted to false when we unmount the component
  useEffect(() => {
    return () => {
      isMounted.current = false;
    };
  }, []);

  const sendRequest = useCallback(async () => {
    console.log("Sending SignRequest!");
    // Don't send again while we are sending
    if (isSending) {
      return;
    }

    // Update state
    setIsSending(true);

    // Send the actual request
    await signRequest();
    // Once the request is sent, update state again

    if (isMounted.current) {
      // Only update if we are still mounted
      setIsSending(false);
    }
  }, [isSending]); // Update the callback if the sate changes

  return (
    <div>
      <center>
        <h1>Click Button to Send SignRequest Form</h1>
      </center>
      <Button
        variant="contained"
        size="large"
        id="submitCode"
        onClick={sendRequest}
      >
        Send SignRequest
      </Button>
    </div>
  );
};

export default SendRequest;
