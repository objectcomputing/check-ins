import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";

const WorkingLocationPage = () => {
  return (
    <div>
      <center>
        <h1>Where would you PREFER to work?</h1>
      </center>
      <Link to="/survey">
        <Button>Go to introductiong survey</Button>
      </Link>
      <Link to="/equipment">
        <Button>Go to work equipment page</Button>
      </Link>
    </div>
  );
};

export default WorkingLocationPage;
