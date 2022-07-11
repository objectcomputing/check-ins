import React from "react";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import Footer from "../footer/Footer";
import "./RightSidebar.css";
import { Checkbox } from "@mui/material";
import { lightBlue } from "@mui/material/colors";

function RightSidebar({
  handleChange,
  data,
  currentPageIndex,
  handleNextButton,
}) {
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
        {value === index && (
          <Box
            sx={{
              p: 3,
              display: "flex",
              flexDirection: "column",
              flexWrap: "nowrap",
              justifyContent: "space-between",
              alignItems: "stretch",
              alignContent: "center",
              width: "calc(100% - 48px)",
              minHeight: "calc(100vh - 48px)",
            }}
          >
            {children}
          </Box>
        )}
      </div>
    );
  }

  return (
    <Box
      sx={{
        flexGrow: 1,
        bgcolor: "background.paper",
        display: "flex",
        minHeight: "100vh",
      }}
    >
      <Tabs
        orientation="vertical"
        variant="scrollable"
        value={currentPageIndex}
        onChange={handleChange}
        sx={{
          borderRight: 1,
          borderColor: "divider",
          backgroundColor: "#dfdfdf",
          color: "#000",
          "& .Mui-selected": {
            color: "#1666b6 !important",
            fontWeight: "bold",
          },
          "& .MuiButtonBase-root": {
            alignItems: "flex-start",
            textTransform: "none",
            minWidth: "50px",
          },
        }}
        TabIndicatorProps={{
          sx: {
            backgroundColor: "#1666b6",
          },
        }}
      >
        {data &&
          data.map((menuItem, i) => {
            return (
              <Tab
                disabled={!menuItem.visited}
                sx={{
                  color: "#000",
                  width: "300px !important",
                  paddingRight: "0px !important",
                }}
                key={menuItem.index}
                label={
                  <div className="sidebar__item">
                    <div className="sidebar__check">
                      <Checkbox
                        checked={menuItem.completed}
                        {...label}
                        sx={{
                          color: lightBlue[800],
                          "&.Mui-checked": {
                            color: lightBlue[600],
                          },
                        }}
                      />
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
      {data &&
        data.map((menuItem, i) => {
          return (
            <TabPanel
              value={currentPageIndex}
              index={menuItem.index}
              key={menuItem.index}
              className="wrapPanel"
            >
              <div className="wrapTabPane">
                <div className="tabInterior">
                  <h2>{menuItem.title}</h2>
                  {menuItem.child}
                </div>
              </div>
              <Footer handleNextButton={handleNextButton} />
            </TabPanel>
          );
        })}
    </Box>
  );
}

export default RightSidebar;
