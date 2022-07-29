import React, { useState } from "react";
import {
  Button,
  Box,
  OutlinedInput,
  ToggleButton,
  Grid,
} from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
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
  const [currentOS, setCurrentOS] = useState("windows");
  const [currentAccessory, setCurrentAccessory] = useState(equipmentList);

  const handleClick = (event, newOS) => {
    setCurrentOS(newOS);
  };

  const handleAccessoryClick = (index) => {
    let newList = [...currentAccessory];
    newList[index].isClicked = !newList[index].isClicked;
    setCurrentAccessory(newList);
  };

  return (
    <Grid
      container
      justifyContent="center"
      alignItems="center"
      direction="row"
    >
      <Grid item xs={8}>
        <h2 style={{ marginTop: "1rem" }}>
          Please select your computer preference:
        </h2>
        <ToggleButtonGroup
          size="medium"
          value={currentOS}
          exclusive
          onChange={handleClick}
          sx={{
            gap: 15,
            "& .Mui-selected": {
              backgroundColor: "#1666b6 !important",
              color: "white !important",
              fontWeight: "bold",
            },
            "& .MuiToggleButtonGroup-grouped:not(:first-of-type)": {
              marginLeft: "0px !important",
              borderLeft: "1px solid rgba(25, 118, 210, 0.5) !important",
            },
          }}
        >
          <ToggleButton
            value="windows"
            sx={{
              transition:
                "background-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, box-shadow 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, border-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms",
              border: "1px solid rgba(25, 118, 210, 0.5)",
              color: "rgb(25, 118, 210)",
              borderRadius: "4px !important",
              boxShadow:
                "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)",
            }}
          >
            Windows
          </ToggleButton>
          <ToggleButton
            value="mac"
            sx={{
              transition:
                "background-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, box-shadow 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, border-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms",
              border: "1px solid rgba(25, 118, 210, 0.5)",
              color: "rgb(25, 118, 210)",
              borderRadius: "4px !important",
              boxShadow:
                "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)",
            }}
          >
            Mac
          </ToggleButton>
          <ToggleButton
            value="linux"
            sx={{
              transition:
                "background-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, box-shadow 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, border-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms",
              border: "1px solid rgba(25, 118, 210, 0.5)",
              color: "rgb(25, 118, 210)",
              borderRadius: "4px !important",
              boxShadow:
                "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)",
            }}
          >
            Linux
          </ToggleButton>
        </ToggleButtonGroup>

        <h2 style={{ marginTop: "3rem" }}>
          Please select your computer accessories:
        </h2>
        <Grid
          container
          justifyContent="center"
          alignItems="center"
          direction="row"
        >
          <Grid item xs={12}>
            <ButtonGroup
              sx={{
                gap: 6,
                "& .Mui-selected": {
                  backgroundColor: "#1666b6 !important",
                  color: "white !important",
                  fontWeight: "bold",
                },
                "& .MuiButtonGroup-grouped:not(:last-of-type)": {
                  marginLeft: "0px !important",
                  border: "1px solid rgba(25, 118, 210, 0.5) !important",
                },
                ".MuiButtonGroup-grouped:hover": {
                  textDecoration: "none",
                  backgroundColor: "rgba(0, 0, 0, 0.04)",
                },
              }}
            >
              {currentAccessory &&
                currentAccessory.map((item, i) => {
                  return (
                    <Button
                      className={item.isClicked ? "Mui-selected" : ""}
                      variant="contained"
                      key={item.id}
                      onClick={() => handleAccessoryClick(i)}
                      sx={{
                        transition:
                          "background-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, box-shadow 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, border-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms",
                        border: "1px solid rgba(25, 118, 210, 0.5)",
                        color: "rgb(25, 118, 210)",
                        borderRadius: "4px !important",
                        backgroundColor: "transparent",
                      }}
                    >
                      {item.name}
                    </Button>
                  );
                })}
            </ButtonGroup>
          </Grid>
        </Grid>

        <Box
          component="form"
          sx={{
            marginTop: "3rem",
          }}
          noValidate
          autoComplete="off"
        >
          <h2>Please list any other equipment you would need:</h2>
          <Grid
            container
            justifyContent="center"
            alignItems="center"
            direction="row"
          >
            <Grid item xs={8}>
              <OutlinedInput
                fullWidth
                multiline
                maxRows={8}
                minRows={3}
                inputProps={ariaLabel}
              />
            </Grid>
          </Grid>
        </Box>
      </Grid>
    </Grid>
  );
};

export default EquipmentPage;
