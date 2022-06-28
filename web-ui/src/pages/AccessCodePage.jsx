import react from "react";
import { Link } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import "./AccessCodePage.css";

function AccessCodePage() {
  return (
    <div>
      <center>
        <h1>Please Enter in your Access Code:</h1>
      </center>
      <Link to="/culturevideo">
        <Button variant="contained" size="large" id="cultureVideo">
          Culture Video
        </Button>
      </Link>
    </div>
  );
}

export default AccessCodePage;
