import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./DocumentSigningPage.css";

const DocumentSigningPage = () => {
  return (
    <div>
      <center>
        <h1>Internal Document Signing</h1>
      </center>
      <Link to="/equipment">
        <Button variant="contained" size="large" id="workEquipment">
          Work Equipment
        </Button>
      </Link>
      <Link to="/congratulations">
        <Button variant="contained" size="large" id="finish">
          Finish
        </Button>
      </Link>
    </div>
  );
};

export default DocumentSigningPage;
