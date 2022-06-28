import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import "./WorkingLocationPage.css";

const WorkingLocationPage = () => {
  return (
    <div>
      <center>
        <h1>Where would you PREFER to work?</h1>
      </center>

      <div id="workOption">
        <ButtonGroup variant="outlined" aria-label="outlined button group">
          <Button>Office</Button>
          <Button>Remote</Button>
          <Button>Hybrid</Button>
        </ButtonGroup>
      </div>

      <Link to="/survey">
        <Button variant="contained" size="large" id="survey">
          Introduction Survey
        </Button>
      </Link>

      <Link to="/equipment">
        <Button variant="contained" size="large" id="equipment">
          Work Equipment
        </Button>
      </Link>
    </div>
  );
};

export default WorkingLocationPage;
