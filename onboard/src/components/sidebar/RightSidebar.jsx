import React from "react";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import Footer from "../footer/Footer";
import "./RightSidebar.css";
import { Checkbox } from "@mui/material";
import { lightBlue } from "@mui/material/colors";
import { object } from "prop-types";

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
          width: "300px",
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
        <Tab
          disabled
          sx={{
            alignItems: "flex-start",
            color: "#000",
            paddingRight: "0px !important",
          }}
          label={
            <div
              className="sidebar_logo"
              style={{ display: "flex", justifyContent: "center" }}
            >
              <img
                src="img\ocicube-white.png"
                alt="Object Computing, Inc."
                style={{ width: "30%" }}
              />
            </div>
          }
        />
        {data &&
          data.map((menuItem, i) => {
            return (
              <Tab
                disabled={!menuItem.visited}
                sx={{
                  color: "#000",
                  width: "50vh !important",
                  paddingRight: "0px !important",
                }}
                key={menuItem.index}
                label={
                  <div className="sidebar__item">
                    <div className="sidebar__check">
                      <Checkbox
                        align="left"
                        disabled
                        checked={menuItem.completed || ((currentPageIndex === 6) && menuItem.visited)}
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
                      <p>
                        {" "}
                        {menuItem.index + 1}
                        {")"} {menuItem.name}
                      </p>
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
              align="center"
              value={currentPageIndex}
              index={menuItem.index}
              key={menuItem.index}
              className="wrapPanel"
            >
              <div className="wrapTabPane">
                <div className="tabInterior">
                  <div className="titleSection">
                    <h1>{menuItem.title}</h1>
                  </div>
                  <div className="bodySection">{menuItem.child}</div>
                </div>
              </div>
              {currentPageIndex !== 6 && <Footer handleNextButton={handleNextButton} />}
            </TabPanel>
          );
        })}
    </Box>
  );
}

export default RightSidebar;
