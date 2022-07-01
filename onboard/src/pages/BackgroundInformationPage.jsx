import React from "react";
import Typography from "@mui/material/Typography";
import Accordion from "../components/Accordion";
import PersonalInformation from "./subsections/backgroundInformation/Personalnformation";
import { isArrayPresent } from "../utils/helperFunctions";
import "./BackgroundInformationPage.css";

const accordionArr = [
  {
    title: "Personal Information",
    content: <PersonalInformation />,
  },
  {
    title: "Employment Eligbility",
    content: <PersonalInformation />,
  },
  {
    title: "Employment Desired and Avaiablity",
    content: <PersonalInformation />,
  },
  {
    title: "Education",
    content: <PersonalInformation />,
  },
  {
    title: "Employment History",
    content: <PersonalInformation />,
  },
  {
    title: "Referral Type",
    content: <PersonalInformation />,
  },
];

const BackgroundInformationPage = () => {
  return (
    <div>
      <center>
        <Typography variant="h3">
          Please enter in your background information
        </Typography>
      </center>
      {isArrayPresent(accordionArr) &&
        accordionArr.map((arr, i) => {
          return (
            <Accordion
              title={arr.title}
              open={i === 0 ? true : false}
              index={i}
              content={arr.content}
            />
          );
        })}
    </div>
  );
};

export default BackgroundInformationPage;
