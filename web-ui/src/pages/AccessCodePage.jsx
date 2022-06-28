import react from "react";
import { Link } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import "./AccessCodePage.css";

function AccessCodePage() {

  const nextPageHandler = () =>{

  }

  return (
    <div>
      <div className="page_body">
        <div className="page_content">
          <center>
            <h1>Please Enter in your Access Code:</h1>
          </center>
          <Link to="/culturevideo">
            <Button onClick = {nextPageHandler} variant="contained" size="large" id="cultureVideo">
              Culture Video
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}

export default AccessCodePage;
