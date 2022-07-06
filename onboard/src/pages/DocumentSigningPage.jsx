import React, { useState, useEffect } from "react";
import "./DocumentSigningPage.css";
import getSigner from "../api/signer.js";

const DocumentSigningPage = () => {
  const [document, setDocument] = useState({});

  useEffect(() => {
    async function getData() {
      let res = await getSigner();
      let document;
      if (res && res.payload) {
        document = res?.payload?.data && !res.error ? res.payload.data : undefined;
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
        <h1>Internal Document Signing</h1>
        {document.status === "se" || document.status === "si" ? "Signed Baby!" : "Not Signed. :("}
      </center>
    </div>
  );
};
export default DocumentSigningPage;
