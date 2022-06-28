import React,{useState} from "react";
import "./WebPortal.css";
import RightSidebar from "../components/onboarding_sidebar/RightSidebar";
import CultureVideoPage from "./CultureVideoPage";
import BackgroundInformationPage from "./BackgroundInformationPage";
import IntroductionSurveyPage from "./IntroductionSurveyPage";
import WorkingLocationPage from "./WorkingLocationPage";
import EquipmentPage from "./EquipmentPage";
import DocumentSigningPage from "./DocumentSigningPage";
import Congratulations from "./CongratulationsPage";
import { Container } from "@mui/material";



function WebPortal() {
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

  const fillCheckMark = () => {};

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
          <RightSidebar menuList={menuList} />
        </Container>
      </div>
    </div>
  );
}

export default WebPortal;
