import React from "react";
import {Button, Typography} from "@mui/material";
import {Link} from "react-router-dom";

const LogoutPage = () => {

  return (
    <div className="logout-page">
      <Typography variant="h5">You have been automatically logged out</Typography>
      <Typography variant="body">Please click below to sign in again</Typography>
      <Link style={{marginTop: "2em", textDecoration: "none"}} to="/oauth/login/google">
        <Button variant="outlined">Return home</Button>
      </Link>
    </div>
  );

}

export default LogoutPage;