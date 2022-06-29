import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./CultureVideoPage.css";

function CultureVideoPage() {
  return (
    <div>
      <center>
        <h1>At OCI We Care</h1>
      </center>
      <center>
        <iframe
          width="1280"
          height="720"
          src="https://www.youtube.com/embed/D7Ka15wQL5U"
          title="YouTube video player"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
      </center>
      <Link to="/backgroundinformation">
        <Button variant="contained" size="large" id="backgroundInformation">
          Background Information
        </Button>
      </Link>
    </div>
  );
}

export default CultureVideoPage;
