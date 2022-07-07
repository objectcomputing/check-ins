import React, { useState } from "react";
import { Button, Box, Input } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import "./EquipmentPage.css";

const ariaLabel = { "aria-label": "description" };

const equipmentList = [
  { id: "e1", name: "Keyboard", isClicked: false },
  { id: "e2", name: "Mouse", isClicked: false },
  { id: "e3", name: "Monitor", isClicked: false },
  { id: "e4", name: "Headphones", isClicked: false },
  { id: "e5", name: "HDMI Cable", isClicked: false },
];

const EquipmentPage = () => {
  const [isMacActive, setIsMacActive] = useState(false);
  const [currentAccessory, setCurrentAccessory] = useState(equipmentList);

  const handleClick = () => {
    setIsMacActive((current) => !current);
  };

  const handleAccessoryClick = (index) => {
    let newList = [...currentAccessory];
    newList[index].isClicked = !newList[index].isClicked;
    setCurrentAccessory(newList);
  };

  return (
    <div>
      <center>
        <h1>Please select your computer Prefrence</h1>
        <ButtonGroup
          variant="contained"
          sx={{
            fullWidth: "true",
          }}
        >
          <Button
            sx={{
              "&:hover": {
                color: "white",
                backgroundColor: "lightblue",
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
                backgroundColor: "lightblue",
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
        <ButtonGroup
          sx={{
            gap: 1,
            display: "grid",
            gridTemplateColumns: "repeat(3, 1fr)",
          }}
        >
          {equipmentList &&
            equipmentList.map((item, i) => {
              return (
                <Button
                  variant="contained"
                  key={item.id}
                  onClick={() => handleAccessoryClick(i)}
                  sx={{
                    "&:hover": {
                      color: "white",
                      backgroundColor: "lightblue",
                    },
                    backgroundColor: item.isClicked ? "#1666b6" : "white",
                    color: !item.isClicked ? "#1666b6" : "white",
                  }}
                >
                  {item.name}
                </Button>
              );
            })}
        </ButtonGroup>
      </center>

      <Box
        component="form"
        sx={{
          "& > :not(style)": { mt: 2, width: "50vh" },
        }}
        noValidate
        autoComplete="off"
      >
        <Input
          placeholder="Please list any other equipments you would like..."
          inputProps={ariaLabel}
        />
      </Box>
    </div>
  );
};

export default EquipmentPage;
