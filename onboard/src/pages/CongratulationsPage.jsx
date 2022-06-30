import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import { Typography } from "@mui/material";
import "./CongratulationsPage.css";
import cakeImg from "./../assets/img/cake.png";

const Congratulations = () => {
  return (
    <div>
      <center>
        <Typography variant="h1">Congratulations!</Typography>
        <Typography dispay="block" variant="body1" component={"div"}>
          <br />
          Thank you for completing your job application.
          <Typography display="block" variant="body1">
            You will be receiving information about accessing your ADP account
            soon from HR via email.
          </Typography>
          During your first day you will be awarded with a company t-shirt. We
          hope you have a great time at Object Computing!
        </Typography>
        <img src={cakeImg} alt="Cake" />
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
