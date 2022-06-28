import react from "react";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";

const DocumentSigningPage = () => {
  return (
    <div>
      <center>
        <h1>Interal Document Signing</h1>
      </center>
      <Link to="/equipment">
        <Button>Go to work equipment page</Button>
      </Link>
      <Link to="/congratulations">
        <Button>Complete</Button>
      </Link>
    </div>
  );
};

export default DocumentSigningPage;
