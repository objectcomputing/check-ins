import React, {useContext, useEffect, useState} from "react";
import {
  Alert, Avatar, AvatarGroup,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Modal,
  Step,
  StepLabel,
  Stepper,
  TextField, Tooltip,
  Typography,
} from "@mui/material";
import {styled} from "@mui/material/styles";
import LeftArrowIcon from "@mui/icons-material/KeyboardArrowLeft";
import RightArrowIcon from "@mui/icons-material/KeyboardArrowRight";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import SendIcon from "@mui/icons-material/Send";
import EditIcon from "@mui/icons-material/Edit";
import CheckIcon from "@mui/icons-material/CheckCircle";
import {AppContext} from "../context/AppContext";
import mjml2html from "mjml-browser";
import ReactHtmlParser from "react-html-parser";
import {UPDATE_TOAST} from "../context/actions";
import {sendEmail} from "../api/notifications";

import "./EmailPage.css";
import TransferList from "../components/transfer_list/TransferList";
import {getAvatarURL} from "../api/api";
import MemberSelector from "../components/member_selector/MemberSelector.jsx";
import {selectCsrfToken, selectMemberProfiles} from "../context/selectors.js";


const Root = styled("div")({
  margin: "2rem"
});

const ChooseEmailFormatStep = ({ emailFormat, onEmailFormatChange, emailContents, emailSent }) => {

  const [formatDialog, setFormatDialog] = useState({open: false, format: null});

  const handleFormatButtonClick = (format) => {
    // Do nothing if the same button is clicked again
    if (format === emailFormat) {
      return;
    }

    // If the user tries to change the email format after composing an email, warn with dialog
    if (emailFormat && emailContents.length > 0 && format !== emailFormat) {
      setFormatDialog({open: true, format: format});
    } else {
      onEmailFormatChange(format);
    }
  }

  return (
    <>
      <div className="email-format-container">
        <Button
          className="email-format-button"
          style={emailFormat === "file" ? { borderColor: "green", backgroundColor: "#f7fff7" } : {}}
          disabled={emailSent}
          onClick={() => handleFormatButtonClick("file")}>
          <div className="email-format-button-content">
            {emailFormat === "file" &&
              <CheckIcon style={{ position: "absolute", right: "1rem", top: "1rem", color: "green" }}/>
            }
            <UploadFileIcon sx={{ fontSize: "80px", marginBottom: "1rem" }}/>
            <Typography variant="h6" fontWeight="bold">MJML File</Typography>
            <Typography variant="body2" color="gray">Create an email with a custom format using MJML</Typography>
          </div>
        </Button>
        <Button
          className="email-format-button"
          style={emailFormat === "text" ? { borderColor: "green", backgroundColor: "#f7fff7" } : {}}
          disabled={emailSent}
          onClick={() => handleFormatButtonClick("text")}>
          <div className="email-format-button-content">
            {emailFormat === "text" &&
              <CheckIcon style={{position: "absolute", right: "1rem", top: "1rem", color: "green"}}/>
            }
            <EditIcon sx={{ fontSize: "80px", marginBottom: "1rem" }} />
            <Typography variant="h6" fontWeight="bold">Text</Typography>
            <Typography variant="body2" color="gray">Write a simple email with no formatting</Typography>
          </div>
        </Button>
      </div>
      <Modal open={formatDialog.open}>
        <Card className="change-email-format-confirmation-dialog">
          <CardHeader title={<Typography variant="h5" fontWeight="bold">Change Email Format</Typography>}/>
          <CardContent>
            <Typography>You are attempting to change the format of this email, but you have already written a draft in the following step. Changing the format will reset this draft.</Typography>
          </CardContent>
          <CardActions>
            <Button style={{ color: "gray" }} onClick={() => setFormatDialog({open: false, format: null})}>
              Cancel
            </Button>
            <Button color="secondary" onClick={() => {
              onEmailFormatChange(formatDialog.format);
              setFormatDialog({open: false, format: null});
            }}>
              Discard Email Draft
            </Button>
          </CardActions>
        </Card>
      </Modal>
    </>
  );
}

const ComposeEmailStep = ({ emailFormat, emailContents, emailSubject, onSubjectChange, onEmailContentsChange, emailSent }) => {

  const [emailPreview, setEmailPreview] = useState(null);

  const handleFileUpload = (event) => {
    if (event.target.files && event.target.files[0]) {
      const fileReader = new FileReader();
      fileReader.onload = (e) => {
        const mjmlContent = e.target.result.toString();
        const { html } = mjml2html(mjmlContent);
        onEmailContentsChange(html);
      }

      const file = event.target.files[0];
      fileReader.readAsText(file);
    }
  }

  useEffect(() => {
    if (emailContents && emailFormat === "file") {
      const preview = ReactHtmlParser(emailContents);
      setEmailPreview(preview);
    }
  }, [emailFormat, emailContents]);

  if (emailFormat === "file") {
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
            placeholder="Write a subject for the email"
            disabled={emailSent}
            value={emailSubject}
            onChange={(event) => {
              onSubjectChange(event.target.value);
            }}
          />
        </div>

        {emailContents
          ? emailPreview
          : <>
            <Typography variant="body1">Select a MJML file to render the email. The file must have a .mjml extension.</Typography>
            <Button
              variant="contained"
              component="label"
              startIcon={<UploadFileIcon/>}
              style={{ marginBottom: "2rem" }}
              disableElevation
              disabled={emailSent}>
              Choose File
              <input
                type="file"
                accept=".mjml"
                hidden
                onChange={handleFileUpload}
              />
            </Button>
            <div className="missing-preview-message">
              <Typography variant="h6">Preview not available</Typography>
            </div>
          </>
        }
      </>
    );
  } else if (emailFormat === "text") {
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
            placeholder="Write a subject for the email"
            disabled={emailSent}
            value={emailSubject}
            onChange={(event) => {
              onSubjectChange(event.target.value);
            }}
          />
        </div>
        <TextField
          variant="outlined"
          fullWidth
          multiline
          minRows={3}
          maxRows={20}
          placeholder="Write your email here..."
          value={emailContents}
          onChange={(event) => onEmailContentsChange(event.target.value)}
        />
      </>
    );
  }

  return <></>
}

const SelectRecipientsStep = ({ testEmail, onTestEmailChange, onSendTestEmail, recipientOptions, recipients, onRecipientsChange, emailSent }) => {

  const [emailError, setEmailError] = useState(false);

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
      <Typography variant="body1">To test this email and see how it will appear in an inbox, you can send it to an email address you have access to. If you are satisfied with this email, then select recipients from the list below.</Typography>
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
      <MemberSelector
        onChange={(selectedMembers) => onRecipientsChange(selectedMembers)}
        title="Recipients"
        outlined
      />
      {/*<TransferList*/}
      {/*  leftList={recipientOptions}*/}
      {/*  rightList={recipients}*/}
      {/*  leftLabel="Select Recipients"*/}
      {/*  rightLabel="Recipients"*/}
      {/*  onListsChanged={(lists) => onRecipientsChange(lists)}*/}
      {/*  disabled={emailSent}*/}
      {/*/>*/}
    </>
  );
}

const EmailPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const memberProfiles = selectMemberProfiles(state);
  const [currentStep, setCurrentStep] = useState(0);
  const [emailFormat, setEmailFormat] = useState(null);
  const [emailContents, setEmailContents] = useState("");
  const [emailSubject, setEmailSubject] = useState("");
  const [recipientOptions, setRecipientOptions] = useState([]);
  const [recipients, setRecipients] = useState([]);
  const [testEmail, setTestEmail] = useState("");
  const [testEmailSent, setTestEmailSent] = useState(false);
  const [confirmationDialogOpen, setConfirmationDialogOpen] = useState(false);
  const [activeMembers, setActiveMembers] = useState([]);
  const [emailSent, setEmailSent] = useState(false);
  const steps = ["Choose Email Format", "Compose Email", "Select Recipients"];

  useEffect(() => {
    const unterminatedMembers = memberProfiles.filter(member => member.terminationDate === null);
    setActiveMembers(unterminatedMembers);
  }, [memberProfiles]);

  useEffect(() => {
    setRecipientOptions(activeMembers);
  }, [activeMembers]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [currentStep]);

  const sendTestEmail = () => {

    if (!emailSubject.trim() || !emailContents || !csrf) {
      return;
    }

    sendEmail(`Test Email - ${emailSubject}`, emailContents, emailFormat === "file", [testEmail], csrf).then(res => {
      let toastMessage, toastStatus;
      if (res && res.payload && res.payload.status === 201 && !res.error) {
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

  const sendEmailToAllRecipients = () => {
    setConfirmationDialogOpen(false);

    if (!emailSubject.trim() || !emailContents || !csrf) {
      return;
    }

    const recipientEmails = recipients.map((member) => member.workEmail);

    sendEmail(emailSubject, emailContents, emailFormat === "file", recipientEmails, csrf).then(res => {
      let toastMessage, toastStatus;
      if (res && res.payload && res.payload.status === 201 && !res.error) {
        setEmailSent(true);
        toastStatus = "success";
        toastMessage = `Sent email to ${recipients.length} member${recipients.length === 1 ? "" : "s"}`;
      } else {
        toastStatus = "error";
        toastMessage = "Failed to send email";
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

  const handleNextClick = () => {
    switch (currentStep) {
      case 0:
      case 1:
        setCurrentStep(currentStep + 1);
        break;
      case 2:
        setConfirmationDialogOpen(true);
        break;
      default:
        console.warn(`Invalid step in stepper: ${currentStep}`);
    }
  }

  const nextButtonEnabled = () => {
    switch (currentStep) {
      case 0:
        return !!emailFormat;
      case 1:
        return emailSubject.trim().length > 0 && !!emailContents;
      case 2:
        return !!emailContents && recipients.length > 0 && !emailSent;
      default:
        console.warn(`Invalid step in stepper: ${currentStep}`);
        return false;
    }
  }

  const stepCompleted = (step) => {
    if (emailSent) {
      return true;
    }

    switch (step) {
      case 0:
        return emailFormat && currentStep > 0;
      case 1:
        return !!emailContents && !!emailSubject && currentStep > 1;
      case 2:
        return recipients.length > 0 && emailSent;
      default:
        console.warn(`Invalid step in stepper: ${currentStep}`);
    }

    return false;
  }

  return (
    <Root className="email-page">
      <Stepper activeStep={currentStep}>
        {steps.map((step, index) => (
          <Step key={step} completed={stepCompleted(index)}>
            <StepLabel>{step}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Card className="current-step-content-card">
        <CardHeader title={steps[currentStep]}/>
        <CardContent>
          {currentStep === 0 && (
            <ChooseEmailFormatStep
              emailFormat={emailFormat}
              onEmailFormatChange={(format) => {
                setEmailFormat(format);
                setEmailContents("");
              }}
              emailContents={emailContents}
              emailSent={emailSent}
            />
          )}
          {currentStep === 1 && (
            <ComposeEmailStep
              emailFormat={emailFormat}
              emailContents={emailContents}
              onEmailContentsChange={(content) => setEmailContents(content)}
              emailSubject={emailSubject}
              onSubjectChange={(subject) => setEmailSubject(subject)}
              emailSent={emailSent}
            />
          )}
          {currentStep === 2 && (
            <SelectRecipientsStep
              testEmail={testEmail}
              onTestEmailChange={(address) => setTestEmail(address)}
              onSendTestEmail={sendTestEmail}
              recipientOptions={recipientOptions}
              recipients={recipients}
              emailSent={emailSent}
              onRecipientsChange={(recipients) => {
                setRecipients(recipients);
              }}
            />
          )}
        </CardContent>
      </Card>
      <div className="stepper-button-container">
        <Button
          className="stepper-button"
          variant="outlined"
          startIcon={<LeftArrowIcon/>}
          disabled={currentStep === 0}
          onClick={() => {
            if (currentStep > 0) {
              setCurrentStep(currentStep - 1);
            }
          }}>
          Back
        </Button>
        <Button
          className="stepper-button"
          variant="contained"
          color={currentStep === steps.length - 1 ? "success" : "primary"}
          endIcon={currentStep === steps.length - 1 ? <SendIcon/> : <RightArrowIcon/>}
          disabled={!nextButtonEnabled()}
          onClick={handleNextClick}>
          {currentStep === steps.length - 1 ? "Send" : "Next"}
        </Button>
      </div>
      <Modal open={confirmationDialogOpen}>
        <Card className="send-email-to-all-confirmation-dialog">
          <CardHeader title={<Typography variant="h5" fontWeight="bold">Send Email</Typography>}/>
          <CardContent>
            <Typography variant="body1">
              You are about to send this email to {recipients.length} member{recipients.length === 1 ? "" : "s"} in Check-Ins. Are you sure?
            </Typography>
            <div className="recipient-group-container">
              <AvatarGroup max={16}>
                {recipients.map((member) => (
                  <Tooltip key={member.id} title={member.name} arrow>
                    <Avatar src={getAvatarURL(member.workEmail)}/>
                  </Tooltip>
                ))}
              </AvatarGroup>
            </div>
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
            <Button color="primary" onClick={emailContents && sendEmailToAllRecipients} disabled={!emailContents}>
              Send
            </Button>
          </CardActions>
        </Card>
      </Modal>
    </Root>
  );
};

export default EmailPage;