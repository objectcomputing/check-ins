import react from "react";
import { Link } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";

function AccessCodePage() {
  return (
    <div>
      <center>
        <h1>Please Enter in your Access Code:</h1>
      </center>
      <Link to="/culturevideo">
        <Button>Go to culture video.</Button>
      </Link>
    </div>
  );
}

export default AccessCodePage;
