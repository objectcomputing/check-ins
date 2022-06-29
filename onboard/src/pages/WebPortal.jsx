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
    title: "Lorem ipsum",
    completed: false,
    child: <CultureVideoPage />,
  },
  {
    index: 1,
    name: "About You Survey",
    title: "Lorem ipsum",
    completed: false,
    child: <BackgroundInformationPage />,
  },
  {
    index: 2,
    name: "Work Preference",
    title: "Lorem ipsum",
    completed: false,
    child: <WorkingLocationPage />,
  },
  {
    index: 3,
    name: "Computer and Accessories",
    title: "Lorem ipsum",
    completed: false,
    child: <EquipmentPage />,
  },
  {
    index: 4,
    name: "Internal Document Signing",
    title: "Lorem ipsum",
    completed: false,
    child: <DocumentSigningPage />,
  },
  {
    index: 5,
    name: "Check-Ins Skills",
    title: "Lorem ipsum",
    completed: false,
    child: <IntroductionSurveyPage />,
  },
  {
    index: 6,
    name: "Cake!",
    title: "Lorem ipsum",
    completed: false,
    child: <Congratulations />,
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
            minHeight: "calc(100vh - 64px)",
            padding: "0px !important",
          }}
        >
          <RightSidebar
            currentPageIndex={currentPageIndex}
            handleChange={handleChange}
            data={data}
            handleChangeSidebar={handleChangeSidebar}
          />
          <Button onClick={submitHandler}>Submit</Button>
        </Container>
      </div>
    </div>
  );
}

export default WebPortal;
