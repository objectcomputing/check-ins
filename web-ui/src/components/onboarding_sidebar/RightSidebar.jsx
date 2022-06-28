import React, { useState } from "react";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import "./RightSidebar.css";
import { Checkbox } from "@mui/material";
import { lightBlue } from "@mui/material/colors";

function RightSidebar({ menuList }) {
  const [val, setValue] = useState(0);
  const [isCompleted, setIsCompleted] = useState(false);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const label = { inputProps: { "aria-label": "Checkbox demo" } };

  function TabPanel(props) {
    const { children, value, index, ...other } = props;

    return (
      <div
        role="tabpanel"
        hidden={value !== index}
        id={`vertical-tabpanel-${index}`}
        aria-labelledby={`vertical-tab-${index}`}
        {...other}
      >
        {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
      </div>
    );
  }

  return (
    <Box
      sx={{
        flexGrow: 1,
        bgcolor: "background.paper",
        display: "flex",
        minHeight: "calc(100vh - 64px)"
      }}
    >
      <Tabs
        orientation="vertical"
        variant="scrollable"
        value={val}
        onChange={handleChange}
        sx={{
          borderRight: 1,
          borderColor: "divider",
          backgroundColor: "#dfdfdf",
          color: "#000",
          "& .Mui-selected": { color: "Yellow !important", fontWeight: 'bold' },
          "& .MuiButtonBase-root": {
            alignItems: "flex-start",
            textTransform: "none",
            minWidth: "50px",
          },
        }}
        TabIndicatorProps={{
          sx: {
            backgroundColor: "#ffe0a7",
          },
        }}
      >
        {menuList &&
          menuList.map((menuItem, i) => {
            return (
              <Tab
                sx={{
                  color: "#000",
                  width: "300px !important",
                  paddingRight: "0px !important",
                }}
                key={menuItem.index}
                label={
                  <div className="sidebar__item">
                    <div className="sidebar__check">
                      {!isCompleted && (
                        <Checkbox
                          {...label}
                          sx={{
                            color: lightBlue[800],
                            "&.Mui-checked": {
                              color: lightBlue[600],
                            },
                          }}
                          disabled
                        />
                      )}
                      {isCompleted && (
                        <Checkbox
                          {...label}
                          sx={{
                            color: lightBlue[800],
                            "&.Mui-checked": {
                              color: lightBlue[600],
                            },
                          }}
                          disabled Check
                        />
                      )}
                    </div>

                    <div className="sidebar__title">
                      <p> {menuItem.name}</p>
                    </div>
                  </div>
                }
                value={menuItem.index}
              />
            );
          })}
      </Tabs>
      {menuList &&
        menuList.map((menuItem, i) => {
          return (
            <TabPanel
              value={val}
              index={menuItem.index}
              key={menuItem.index}
              className="wrapPanel"
            >
              <h2>{menuItem.title}</h2>
              {menuItem.child}
            </TabPanel>
          );
        })}
    </Box>
  );
}

export default RightSidebar;
