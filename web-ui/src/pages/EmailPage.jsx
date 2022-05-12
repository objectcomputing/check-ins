import React, {useContext, useEffect, useState} from "react";
import {
  Alert,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Modal,
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
import {AppContext} from "../context/AppContext";
import mjml2html from "mjml-browser";
import ReactHtmlParser from "react-html-parser";
import {UPDATE_TOAST} from "../context/actions";
import {sendEmail} from "../api/notifications";

import "./EmailPage.css";


const Root = styled("div")({
  margin: "2rem"
});

const UploadFileStep = ({ emailFile, emailContents, onEmailFileChange, onEmailContentsChange }) => {

  const handleFileUpload = (event) => {
    if (event.target.files && event.target.files[0]) {
      const fileReader = new FileReader();
      fileReader.onload = (e) => {
        const content = e.target.result.toString();
        onEmailContentsChange(content);
      }

      const file = event.target.files[0];
      onEmailFileChange(file);
      fileReader.readAsText(file);
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
        <Typography variant="body1" fontWeight={emailFile ? "bold" : "normal"}>{emailFile ? emailFile.name : "No file uploaded"}</Typography>
        {emailContents &&
          <TextareaAutosize
            className="file-preview"
            style={{overflow: "auto"}}
            value={emailContents}
            minRows={3}
            maxRows={emailContents.split("\n").length}
            readOnly/>
        }
      </div>
    </>
  );
}

const PreviewEmailStep = ({ emailContents, emailSubjectError, emailSubject, onSubjectChange }) => {

  if (!emailContents) {
    return (
      <div className="missing-preview-message">
        <Typography variant="h6">Preview not available</Typography>
      </div>
    );
  }

  const { html } = mjml2html(emailContents);
  const emailPreview = ReactHtmlParser(html);

  return (
    <>
      <div className="email-subject-container">
        <Typography
          variant="body1"
          fontWeight="bold"
          style={{marginRight: "1rem", marginTop: "6px"}}>
          Subject:
        </Typography>
        <TextField
          fullWidth
          error={emailSubjectError}
          helperText={emailSubjectError ? "Email is missing subject" : ""}
          value={emailSubject}
          onChange={(event) => {
            onSubjectChange(event.target.value);
          }}
        />
      </div>
      {emailPreview}
    </>
  );
}

const SendEmailStep = ({ testEmail, onTestEmailChange, onSendTestEmail }) => {
  const [emailError, setEmailError] = useState(false)

  const handleSendButtonClick = () => {
    let regEmail = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-\d]+\.)+[a-zA-Z]{2,}))$/
    if (!regEmail.test(testEmail)) {
      setEmailError(true);
    } else {
      onSendTestEmail(testEmail);
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
            onTestEmailChange(event.target.value);
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

const EmailPage = () => {
  const { state } = useContext(AppContext);
  const { memberProfiles, csrf } = state;
  const [currentStep, setCurrentStep] = useState(0);
  const [emailFile, setEmailFile] = useState(null);
  const [emailContents, setEmailContents] = useState("");
  const [emailSubject, setEmailSubject] = useState("");
  const [emailSubjectError, setEmailSubjectError] = useState(false);
  const [testEmail, setTestEmail] = useState("");
  const [testEmailSent, setTestEmailSent] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [activeMembers, setActiveMembers] = useState([]);
  const steps = ["Upload File", "Preview Email", "Send Email"];

  useEffect(() => {
    const unterminatedMembers = memberProfiles.filter(member => member.terminationDate === null);
    setActiveMembers(unterminatedMembers);
  }, [memberProfiles]);

  const sendTestEmail = () => {

    if (!emailSubject.trim() || !emailContents || !csrf) {
      return;
    }

    sendEmail(emailSubject, emailContents, testEmail, csrf).then(res => {
      let toastMessage, toastStatus;
      if (res && res.payload && res.payload.status === 200 && !res.error) {
        setTestEmailSent(true);
        toastStatus = "success";
        toastMessage = `Sent a test email to ${testEmail}`;
      } else {
        toastStatus = "error";
        toastMessage = `Failed to send test email to ${testEmail}`;
      }
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: toastStatus,
          toast: toastMessage
        }
      });
    });
  }

  const sendEmailToAllMembers = () => {
    setConfirmationDialogOpen(false);
    window.snackDispatch({
      type: UPDATE_TOAST,
      payload: {
        severity: "success",
        toast: `Sent email to ${activeMembers.length} members`
      }
    });
  }

  const handleNextClick = () => {
    switch (currentStep) {
      case 0:
        setCurrentStep(currentStep + 1);
        break;
      case 1:
        if (emailSubject.trim().length > 0) {
          setCurrentStep(currentStep + 1);
        } else {
          setEmailSubjectError(true);
        }
        break;
      case 2:
        setConfirmationDialogOpen(true);
        break;
      default:
        console.warn(`Invalid step in stepper: ${currentStep}`);
    }
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
          {currentStep === 0 && (
            <UploadFileStep
              emailFile={emailFile}
              emailContents={emailContents}
              onEmailFileChange={(file) => setEmailFile(file)}
              onEmailContentsChange={(content) => setEmailContents(content)}
            />
          )}
          {currentStep === 1 && (
            <PreviewEmailStep
              emailContents={emailContents}
              emailSubjectError={emailSubjectError}
              emailSubject={emailSubject}
              onSubjectChange={(subject) => {
                setEmailSubject(subject);
                setEmailSubjectError(false);
              }}
            />
          )}
          {currentStep === 2 && (
            <SendEmailStep
              testEmail={testEmail}
              onTestEmailChange={(address) => setTestEmail(address)}
              onSendTestEmail={sendTestEmail}/>
          )}
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
          disabled={currentStep === steps.length - 1 && !emailFile}
          onClick={handleNextClick}>
          {currentStep === steps.length - 1 ? "Send" : "Next"}
        </Button>
      </div>
      <Modal open={confirmationDialogOpen}>
        <Card className="send-email-to-all-confirmation-dialog">
          <CardHeader title={<Typography variant="h5" fontWeight="bold">Send Email</Typography>}/>
          <CardContent>
            <Typography variant="body1">
              You are about to send the email <b>{emailFile?.name}</b> to everyone in Check-Ins ({activeMembers.length} members). Are you sure?
            </Typography>
            {!testEmailSent &&
              <Alert severity="warning" style={{marginTop: "1rem"}}>
                Caution: You have not sent a test email to check the email formatting.
              </Alert>
            }
          </CardContent>
          <CardActions>
            <Button style={{ color: "gray" }} onClick={() => setConfirmationDialogOpen(false)}>
              Cancel
            </Button>
            <Button color="primary" onClick={emailFile && sendEmailToAllMembers} disabled={!emailFile}>
              Send
            </Button>
          </CardActions>
        </Card>
      </Modal>
    </Root>
  );
};

export default EmailPage;