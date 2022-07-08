import React, { useState } from "react";
import "./WebPortal.css";
import RightSidebar from "./../components/sidebar/RightSidebar";
import CultureVideoPage from "./CultureVideoPage";
import BackgroundInformationPage from "./BackgroundInformationPage";
import IntroductionSurveyPage from "./IntroductionSurveyPage";
import WorkingLocationPage from "./WorkingLocationPage";
import EquipmentPage from "./EquipmentPage";
import DocumentSigningPage from "./DocumentSigningPage";
import Congratulations from "./CongratulationsPage";
import { Button, Container } from "@mui/material";

const menuList = [
  {
    index: 0,
    name: "Culture Video",
    title: "",
    completed: false,
    child: <CultureVideoPage />,
    visited: true,
  },
  {
    index: 1,
    name: "About You Survey",
    title: "",
    completed: false,
    child: <BackgroundInformationPage />,
    visited: false,
  },
  {
    index: 2,
    name: "Work Preference",
    title: "",
    completed: false,
    child: <WorkingLocationPage />,
    visited: false,
  },
  {
    index: 3,
    name: "Computer and Accessories",
    title: "",
    completed: false,
    child: <EquipmentPage />,
    visited: false,
  },
  {
    index: 4,
    name: "Internal Document Signing",
    title: "",
    completed: false,
    child: <DocumentSigningPage />,
    visited: false,
  },
  {
    index: 5,
    name: "Check-Ins Skills",
    title: "",
    completed: false,
    child: <IntroductionSurveyPage />,
    visited: false,
  },
  {
    index: 6,
    name: "Cake!",
    title: "",
    completed: false,
    child: <Congratulations />,
    visited: false,
  },
];

function WebPortal() {
  const [data, setData] = useState(menuList);
  const [currentPageIndex, setCurrentPageIndex] = useState(0);

  const handleChange = (event, newIndex) => {
    setCurrentPageIndex(newIndex);
  };

  const submitHandler = () => {
    const newData = data.map((obj) => {
      if (obj.index === currentPageIndex) {
        return {
          index: currentPageIndex,
          name: obj.name,
          title: obj.title,
          completed: true,
          child: obj.child,
          visited: true,
        };
      } else if (obj.index === currentPageIndex + 1) {
        return {
          index: currentPageIndex + 1,
          name: obj.name,
          title: obj.title,
          completed: obj.completed,
          child: obj.child,
          visited: true,
        };
      }
      return obj;
    });
    setData(newData);
    setCurrentPageIndex(currentPageIndex + 1);
  };

  const handleChangeSidebar = (num) => {
    setCurrentPageIndex(num);
  };

  return (
    <div className="rootStyle">
      <div className="wrapApp">
        <Container
          maxWidth={false}
          sx={{
            minHeight: "100vh",
            padding: "0px !important",
          }}
        >
          <div className="container_content">
            <RightSidebar
              currentPageIndex={currentPageIndex}
              handleChange={handleChange}
              data={data}
              handleChangeSidebar={handleChangeSidebar}
            />
          </div>
          <Button
            variant="contained"
            color="success"
            // sx={{
            //   "&:hover": {
            //     color: "gray",
            //     backgroundColor: "lightgray",
            //   },
            //   maxWidth: 800,
            //   fontSize: 40,
            //   bgcolor: "lightgreen",
            //   color: "white",

            //   //button positioning
            //   zIndex: "modal",
            //   position: "absolute",
            //   bottom: "15%",
            //   right: "20%",
            // }}
            className="submitButton"
            onClick={submitHandler}
          >
            Next
          </Button>
        </Container>
      </div>
    </div>
  );
}

export default WebPortal;
