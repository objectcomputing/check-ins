import React from "react";

import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import Typography from "@mui/material/Typography";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import TextField from "@mui/material/TextField";


import "./BackgroundInformationPage.css";

const BackgroundInformationPage = () => {
  return (
    <div>
      <center>
      <Typography variant="h3">Please enter in your background information</Typography>
      </center>

      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel1a-content"
          id="panel1a-header"
        >
          <Typography>Personal Information</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            <TextField
              id="outlined-basic"
              label="First Name"
              variant="outlined"
            />

            <TextField
              id="outlined-basic"
              label="Last Name"
              variant="outlined"
            />

            <TextField
              id="outlined-basic"
              label="Middle Initial"
              variant="outlined"
            />

            <TextField
              type="number"
              id="outlined-basic"
              label="SSN"
              variant="outlined"
            />
            <TextField
              type="date"
              id="outlined-basic"
              label="Birthdate"
              variant="outlined"
            />
            <TextField
              id="outlined-basic"
              label="Current Address"
              variant="outlined"
            />
            <TextField
              id="outlined-basic"
              label="Permanent Address"
              variant="outlined"
            />
            <TextField
              type="number"
              id="outlined-basic"
              label="Primary Phone Number"
              variant="outlined"
            />
            <TextField
              type="number"
              id="outlined-basic"
              label="Secondary Phone Number"
              variant="outlined"
            />
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel2a-content"
          id="panel2a-header"
        >
          <Typography>Employment Eligbility</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
            malesuada lacus ex, sit amet blandit leo lobortis eget.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3a-content"
          id="panel3a-header"
        >
          <Typography>Employment Desired and Avaiablity</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
            malesuada lacus ex, sit amet blandit leo lobortis eget.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel4a-content"
          id="panel4a-header"
        >
          <Typography>Education</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
            malesuada lacus ex, sit amet blandit leo lobortis eget.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel5a-content"
          id="panel5a-header"
        >
          <Typography>Previous Employment</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
            malesuada lacus ex, sit amet blandit leo lobortis eget.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel6a-content"
          id="panel6a-header"
        >
          <Typography>Referral Type and Signature</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
            malesuada lacus ex, sit amet blandit leo lobortis eget.
          </Typography>
        </AccordionDetails>
      </Accordion>

      <Link to="/culturevideo">
        <Button variant="contained" size="large" id="cultureVideo">
          Culture Video
        </Button>
      </Link>
      <Link to="/survey">
        <Button variant="contained" size="large" id="introductionSurvey">
          Introduction Survey
        </Button>
      </Link>

    </div>
  );
};

export default BackgroundInformationPage;
