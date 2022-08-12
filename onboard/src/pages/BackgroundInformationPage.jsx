import React from "react";
import Accordion from "../components/Accordion";
import PersonalInformation from "./subsections/backgroundInformation/Personalnformation";
import Education from "./subsections/backgroundInformation/Education";
import EmploymentDesired from "./subsections/backgroundInformation/EmploymentDesired";
import EmploymentHistory from "./subsections/backgroundInformation/EmploymentHistory";
import EmploymentEligbility from "./subsections/backgroundInformation/EmploymentEligbility";
import ReferralTypeAndSignature from "./subsections/backgroundInformation/ReferralTypeAndSignature";
import { isArrayPresent } from "../utils/helperFunctions";
import { Grid } from "@mui/material";
import "./BackgroundInformationPage.css";

const accordionArr = [
  {
    title: "Personal Information",
    content: <PersonalInformation />,
  },
  {
    title: "Employment Eligbility",
    content: <EmploymentEligbility />,
  },
  {
    title: "Employment Desired and Availability",
    content: <EmploymentDesired />,
  },
  {
    title: "Education",
    content: <Education />,
  },
  {
    title: "Employment History",
    content: <EmploymentHistory />,
  },
  {
    title: "Referral Type and Signature",
    content: <ReferralTypeAndSignature />,
  },
];

const BackgroundInformationPage = () => {
  return (
    <div>
      <Grid container>
        {isArrayPresent(accordionArr) &&
          accordionArr.map((arr, i) => {
            return (
              <Accordion
                key={i}
                title={arr.title}
                open={i === 0 ? true : false}
                index={i}
                content={arr.content}
              />
            );
          })}
      </Grid>
    </div>
  );
};

export default BackgroundInformationPage;
