import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";

const BackgroundInformationPage = () => {
  return (
    <div>
      <center>
        <h1>Please enter in your background information</h1>
      </center>
      <Link to="/culturevideo">
        <Button>Go to culture video.</Button>
      </Link>
      <Link to="/survey">
        <Button>Go to introductiong survey</Button>
      </Link>
    </div>
  );
};

export default BackgroundInformationPage;
