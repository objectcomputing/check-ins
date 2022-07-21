import React from "react";
import { Typography, Grid } from "@mui/material";
import "./CongratulationsPage.css";
import cake from "./../assets/img/cake.png";

const Congratulations = () => {
  return (
    <Grid
      container
      alignItems="center"
      flexDirection="row"
      sx={{ height: "100%" }}
    >
      <Grid item xs={12}>
        <Typography
          sx={{ padding: "10px", fontSize: "20px" }}
          dispay="block"
          variant="body1"
          component={"div"}
          marginLeft={30}
          marginRight={30}
        >
          Thank you for completing Onboarding!
        </Typography>
        <Typography
          sx={{ padding: "5px" }}
          display="block"
          variant="body1"
          marginLeft={30}
          marginRight={30}
        >
          You will receive an email from HR regarding accesing your ADP account
          within the next 48 hours.
        </Typography>
        <Typography
          display="block"
          variant="body1"
          marginLeft={30}
          marginRight={30}
        >
          Be ready to receive some OCI swag on your first day! We're excited for
          your arrival and hope you have a great time with us here at OCI!
        </Typography>
      </Grid>
      <Grid item xs={12}>
        <img src={cake} alt="Cake" class="Cake" padding={30} />
      </Grid>
    </Grid>
  );
};

export default Congratulations;
