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
  TextField,
  Typography,
} from "@mui/material";
import {styled} from "@mui/material/styles";
import LeftArrowIcon from "@mui/icons-material/KeyboardArrowLeft";
import RightArrowIcon from "@mui/icons-material/KeyboardArrowRight";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import SendIcon from "@mui/icons-material/Send";

import "./EmailPage.css";

const Root = styled("div")({
  margin: "2rem"
});

const EmailPage = () => {

  const [file, setFile] = useState(null);
  const [fileContents, setFileContents] = useState("");
  const [currentStep, setCurrentStep] = useState(0);
  const steps = ["Upload File", "Preview Email", "Send Email"];

  const sendTestEmail = (emailAddress) => {
    console.log(`Sending email to ${emailAddress}`);
  }

  const sendEmailToAllMembers = () => {
    console.log("Sending email to everyone");
  }

  const UploadFileStep = () => {

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
      <>
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
      </>
    );
  }

  const PreviewEmailStep = () => {
    return (
      <></>
    );
  }

  const SendEmailStep = () => {
    const [testEmail, setTestEmail] = useState("");
    const [emailError, setEmailError] = useState(false)

    const handleSendButtonClick = () => {
      let regEmail = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-\d]+\.)+[a-zA-Z]{2,}))$/
      if (!regEmail.test(testEmail)) {
        setEmailError(true);
      } else {
        sendTestEmail(testEmail);
      }
    }

    return (
      <>
        <Typography variant="body1">To test this email and see how it will appear in an inbox, you can send it to an email address you have access to. If you are satisfied with the email, click the send button at the bottom of the page to send to everyone.</Typography>
        <div className="send-test-email-container">
          <TextField
            className="send-test-email-input"
            label="Email address"
            placeholder="example@objectcomputing.com"
            variant="outlined"
            value={testEmail}
            error={emailError}
            helperText={emailError ? "Invalid email address" : ""}
            onChange={(event) => {
              setTestEmail(event.target.value);
              setEmailError(false);
            }}
            InputProps={{
              style: {borderTopRightRadius: 0, borderBottomRightRadius: 0}
            }}
          />
          <Button
            className="send-test-email-button"
            variant="contained"
            disableElevation
            endIcon={<SendIcon/>}
            disabled={testEmail.trim().length === 0 || emailError}
            onClick={handleSendButtonClick}
          >
            Send Test Email
          </Button>
        </div>
      </>
    );
  }

  return (
    <Root className="email-page">
      <Stepper activeStep={currentStep}>
        {steps.map((step) => (
          <Step key={step}>
            <StepLabel>{step}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Card className="current-step-content-card">
        <CardHeader title={steps[currentStep]}/>
        <CardContent>
          {currentStep === 0 && <UploadFileStep/>}
          {currentStep === 1 && <PreviewEmailStep/>}
          {currentStep === 2 && <SendEmailStep/>}
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
          color={currentStep === steps.length - 1 ? "success" : "primary"}
          endIcon={currentStep === steps.length - 1 ? <SendIcon/> : <RightArrowIcon/>}
          onClick={() => currentStep < steps.length - 1
            ? setCurrentStep(currentStep + 1)
            : sendEmailToAllMembers()
          }>
          {currentStep === steps.length - 1 ? "Send" : "Next"}
        </Button>
      </div>
    </Root>
  );
};

export default EmailPage;