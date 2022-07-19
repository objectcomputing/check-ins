import React from "react";
import { Typography } from "@mui/material";
import "./CongratulationsPage.css";
import cakeImg from "./../assets/img/cake.png";

const Congratulations = () => {
  return (
    <div>
      <center>
        <Typography dispay="block" variant="body1" component={"div"}>
          <br />
          Thank you for completing Onboarding! 
          <Typography display="block" variant="body1"> <br />
            You will receive an email from HR regarding accesing you ADP account within the next 48 hours. 
          </Typography> <br />
          Be ready to receive some OCI swag on your first day! We're excited for your arrival and hope you have a great time with us here at OCI!
        </Typography> 
        <img src={cakeImg} alt="Cake" class="Cake"/>
      </center>
    </div>
  );
};

export default Congratulations;
