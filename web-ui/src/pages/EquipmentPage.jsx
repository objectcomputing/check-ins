import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import "./EquipmentPage.css";

const EquipmentPage = () => {
  return (
    <div>
      <center>
        <h1>Please select your computer Prefrence</h1>
        <ButtonGroup variant="outlined" aria-label="outlined button group">
          <Button>Windows</Button>
          <Button>Mac OS</Button>
        </ButtonGroup>
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
      <center>
        <h1>
          Please select your computer accessories
        </h1>
      <ButtonGroup variant="outlined" aria-label="outlined button group">
          <Button>Keyboard</Button>
          <Button>Mouse</Button>
          <Button>Monitor</Button>
        </ButtonGroup>
      </center>
    </div>
  );
};

export default EquipmentPage;
