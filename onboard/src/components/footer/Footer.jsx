import { Button } from "@mui/material";
import "./Footer.css";

export default function Footer({ handleNextButton }) {
  return (
    <div className="wrapFooter">
      <Button
        variant="contained"
        color="success"
        size="large"
        onClick={handleNextButton}
      >
        Next
      </Button>
    </div>
  );
}
