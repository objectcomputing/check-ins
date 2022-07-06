import React, { useState } from "react";
import { Button } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import "./EquipmentPage.css";

const equipmentList = [
  {
    id: "e1",
    name: "Keyboard",
    isClicked: false,
  },
  {
    id: "e2",
    name: "Mouse",
    isClicked: false,
  },
  { id: "e3", name: "Monitor", isClicked: false },
];
const EquipmentPage = () => {
  const [isMacActive, setIsMacActive] = useState(false);
  const handleClick = () => {
    setIsMacActive((current) => !current);
  };
  return (
    <div>
      <center>
        <h1>Please select your computer Prefrence</h1>
        <ButtonGroup
          variant="outlined"
          aria-label="outlined button group"
          sx={{
            fullWidth: "true",
          }}
        >
          <Button
            sx={{
              "&:hover": {
                color: "white",
                backgroundColor: "lightgreen",
              },
              color: !isMacActive ? "white" : "#1666b6",
              backgroundColor: !isMacActive ? "#1666b6" : "white",
            }}
            onClick={handleClick}
          >
            Windows
          </Button>
          <Button
            sx={{
              "&:hover": {
                color: "white",
                backgroundColor: "lightgreen",
              },
              color: isMacActive ? "white" : "#1666b6",
              backgroundColor: isMacActive ? "#1666b6" : "white",
            }}
            onClick={handleClick}
          >
            Mac OS
          </Button>
        </ButtonGroup>
      </center>
      <center>
        <h1>Please select your computer accessories</h1>
        <ButtonGroup variant="outlined" aria-label="outlined button group">
          {equipmentList &&
            equipmentList.map((item) => {
              return (
                <Button>
                  {item.name}
                </Button>
              );
            })}
        </ButtonGroup>
      </center>
    </div>
  );
};

export default EquipmentPage;
