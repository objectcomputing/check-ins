import React, {useState} from "react";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Step,
  StepLabel,
  Stepper,
  TextareaAutosize,
  Typography
} from "@mui/material";
import {styled} from "@mui/material/styles";
import LeftArrowIcon from "@mui/icons-material/KeyboardArrowLeft";
import RightArrowIcon from "@mui/icons-material/KeyboardArrowRight";
import UploadFileIcon from "@mui/icons-material/UploadFile";

import "./EmailPage.css";

const Root = styled("div")({
  margin: "2rem"
});

const EmailPage = () => {

  const [file, setFile] = useState(null);
  const [fileContents, setFileContents] = useState("");
  const [currentStep, setCurrentStep] = useState(0);

  const handleFileUpload = (event) => {
    if (event.target.files && event.target.files[0]) {
      const fileReader = new FileReader();
      fileReader.onload = (e) => {
        setFileContents(e.target.result.toString());
      }
      setFile(event.target.files[0]);
      fileReader.readAsText(event.target.files[0]);
    }
  }

  return (
    <Root className="email-page">
      <Stepper activeStep={currentStep}>
        <Step>
          <StepLabel>Upload File</StepLabel>
        </Step>
        <Step>
          <StepLabel>Preview Email</StepLabel>
        </Step>
        <Step>
          <StepLabel>Send Test Email</StepLabel>
        </Step>
        <Step>
          <StepLabel>Send Email</StepLabel>
        </Step>
      </Stepper>
      <Card className="current-step-content-card">
        <CardHeader title="Upload File"/>
        <CardContent>
          <Typography variant="body1">Select a MJML file to render the email. The file must have a .mjml extension.</Typography>
          <Button variant="contained" component="label" startIcon={<UploadFileIcon/>} disableElevation>
            Choose File
            <input
              type="file"
              accept=".mjml"
              hidden
              onChange={handleFileUpload}
            />
          </Button>
          <div className="file-preview-container">
            <Typography variant="body1" fontWeight={file ? "bold" : "normal"}>{file ? file.name : "No file uploaded"}</Typography>
            {fileContents &&
              <TextareaAutosize
                className="file-preview"
                style={{overflow: "auto"}}
                value={fileContents}
                minRows={3}
                maxRows={fileContents.split("\n").length}
                readOnly/>
            }
          </div>
        </CardContent>
      </Card>
      <div className="stepper-button-container">
        <Button
          className="stepper-button"
          variant="outlined"
          startIcon={<LeftArrowIcon/>}
          disabled={currentStep === 0}
          onClick={() => currentStep > 0 ? setCurrentStep(currentStep - 1) : null}>
          Back
        </Button>
        <Button
          className="stepper-button"
          variant="contained"
          endIcon={<RightArrowIcon/>}
          onClick={() => currentStep < 3 ? setCurrentStep(currentStep + 1) : null}>
          Next
        </Button>
      </div>
    </Root>
  );
};

export default EmailPage;