import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";

const Congratulations = () => {
  return (
    <div>
      <center>
        <h1>Congratulations</h1>
      </center>
      <Link to="/documents">
        <Button>back</Button>
      </Link>
    </div>
  );
};

export default Congratulations;