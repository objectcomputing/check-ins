import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./CongratulationsPage.css";

const Congratulations = () => {
  return (
    <div>
      <center>
        <h1>Congratulations!</h1>
      </center>
      <Link to="/documents">
        <Button variant="contained" size="large" id="signing">
          Document Signing
        </Button>
      </Link>
    </div>
  );
};

export default Congratulations;
