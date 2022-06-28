import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";

const EquipmentPage = () => {
  return (
    <div>
      <center>
        <h1>Please select your computer Prefrence</h1>
      </center>
      <Link to="/worklocation">
        <Button>Go to work location page</Button>
      </Link>
      <Link to="/documents">
        <Button>Go to work document signing page</Button>
      </Link>
    </div>
  );
};

export default EquipmentPage;
