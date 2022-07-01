import React from "react";
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
      <center>
        <h1>Please select your computer accessories</h1>
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
