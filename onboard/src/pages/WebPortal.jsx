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
import { Container } from "@mui/material";

const menuList = [
  {
    index: 0,
    name: "Culture Video",
    title: "At OCI We Care",
    completed: false,
    child: <CultureVideoPage />,
    visited: true,
  },
  {
    index: 1,
    name: "Background Information",
    title: "Please enter in your background information:",
    completed: false,
    child: <BackgroundInformationPage />,
    visited: false,
  },
  {
    index: 2,
    name: "Computer & Work Preferences",
    title: "Computer & Work Preference",
    completed: false,
    child: <EquipmentPage />,
    visited: false,
  },
  {
    index: 3,
    name: "Internal Document Signing",
    title: "Internal Document Signing",
    completed: false,
    child: <DocumentSigningPage />,
    visited: false,
  },
  {
    index: 4,
    name: "About You",
    title: "You Know About Us Now! Tell Us About You!",
    completed: false,
    child: <IntroductionSurveyPage />,
    visited: false,
  },
  {
    index: 5,
    name: "Cake!",
    title: "Congratulations!",
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
              handleNextButton={submitHandler}
            />
          </div>
        </Container>
      </div>
    </div>
  );
}

export default WebPortal;
