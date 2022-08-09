import React from "react";
import { Button } from "@mui/material";
import "./Footer.css";

export default function Footer({ handleNextButton, handleFinalButton, index }) {
  return (
    <div className="wrapFooter">
      {index < 6 ? (
        <Button
          className="nextButton"
          variant="contained"
          color="success"
          size="large"
          onClick={handleNextButton}
        >
          Next
        </Button>
      ) : (
        <Button
          className="finalButton"
          variant="contained"
          color="success"
          size="large"
          onClick={handleFinalButton}
        >
          Logout
        </Button>
      )}
    </div>
  );
}
