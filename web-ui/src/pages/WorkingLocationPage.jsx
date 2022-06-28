import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./WorkingLocationPage.css";

const WorkingLocationPage = () => {
  return (
    <div>
      <center>
        <h1>Where would you PREFER to work?</h1>
      </center>
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
