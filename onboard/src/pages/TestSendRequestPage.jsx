// import React from "react";
// import { Link } from "react-router-dom";
// import TextField from "@mui/material/TextField";

// import signRequest from "../api/signRequest.js";

// const SendRequest = () => {
//   return (
//     <div>
//       <center>
//         <h1>Click Button to Send SignRequest Form</h1>
//       </center>
//       <Button
//         variant="contained"
//         size="large"
//         id="submitCode"
//         onClick={sendSignRequest}
//       >
//         Send SignRequest
//       </Button>
//     </div>
//   );
// };

// function sendSignRequest() {
//   console.log("Sent Sign Request");
//   signRequest.executeSendSignRequest();
// }

// export default SendRequest;

import React, { useState, useEffect } from "react";
import { Button } from "@mui/material";
import axios from "axios";
import getSignRequest from "../api/signRequest.js";

const request = require("superagent");
const baseUrl = "https://ocitest.signrequest.com/api/v1";

const data = {
  document:
    "https://ocitest.signrequest.com/api/v1/documents/152408ef-4b71-40a4-9416-92011a443450/",
  signers: [
    {
      email: "lib@objectcomputing.com",
    },
  ],
  from_email: "librandon0706@gmail.com",
  message: "Please sign this document",
};

const SendingRequestPage = () => {
  const request = require("superagent");
  const [document, setDocument] = useState({});

  useEffect(() => {
    async function getData() {
      let res = await getSignRequest();
      let document;
      if (res && res.payload) {
        document =
          res?.payload?.data && !res.error ? res.payload.data : undefined;
        if (document) {
          setDocument(document);
        }
      }
    }
    getData();
  }, []);

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
    request
      .post(`${baseUrl}/signrequest-quick-create/`)
      //.post("https://signrequest.com/static/demo/SignRequestDemoDocument.pdf")
      .set("Authorization", "Token 4c22605811560d976c23554f5b8b01f57cc11401")
      .send(data)
      .then((response) => {
        console.log("Response: ", response.body);
      })
      .catch(console.error);
  }
};

export default SendingRequestPage;
