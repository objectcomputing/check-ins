import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import "./CongratulationsPage.css";

const Congratulations = () => {
  return (
    <div>
      <center>
        <h1>Congratulations!</h1>
        <h2>
          <br />
          Thank you for completing your job application.
          <br />
          You will be receiving information about accessing your ADP account
          soon from HR via email.
          <br />
          <br />
          During your first day you will be awarded with a company t-shirt. We
          hope you have a great time at Object Computing!
        </h2>
        <img src={require("./../../src/cake.png")} alt="Cake" />
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
