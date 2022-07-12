import React from "react";
import "./DocumentSigningPage.css";
import DocumentIndicator from "./DocumentIndicator";
import getDocuments from "../api/signrequest_documents.js";

const DocumentSigningPage = () => {
  return (
    <div>
      <center>
        <h1>Internal Document Signing</h1>
      </center>
      <DocumentIndicator />
    </div>
  );
};
export default DocumentSigningPage;
