import React from "react";
import { Button } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import "./WorkingLocationPage.css";

const WorkingLocationPage = () => {
  return (
    <div>
      <div id="workOption">
        <ButtonGroup variant="outlined" aria-label="outlined button group">
          <Button>Office</Button>
          <Button>Remote</Button>
          <Button>Hybrid</Button>
        </ButtonGroup>
      </div>
    </div>
  );
};

export default WorkingLocationPage;
