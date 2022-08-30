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

    if (isSending) {
      return;
    }

    setIsSending(true);
    await signRequest();

    if (isMounted.current) {
      setIsSending(false);
    }
  }, [isSending]);

  return (
    <div>
      <center>
        <h1>Click Button to Send SignRequest Form</h1>
        <Button
          variant="contained"
          size="large"
          id="submitCode"
          onClick={sendRequest}
        >
          Send SignRequest
        </Button>
      </center>
    </div>
  );
};

export default SendRequest;
