import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./EquipmentPage.css";

const EquipmentPage = () => {
  return (
    <div>
      <center>
        <h1>Please select your computer Prefrence</h1>
      </center>
      <Link to="/worklocation">
        <Button variant="contained" size="large" id="location">
          Work Location
        </Button>
      </Link>
      <Link to="/documents">
        <Button variant="contained" size="large" id="documentSigning">
          Document Signing
        </Button>
      </Link>
    </div>
  );
};

export default EquipmentPage;
