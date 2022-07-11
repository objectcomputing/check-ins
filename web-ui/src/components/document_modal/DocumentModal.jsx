import React, {useEffect, useState} from "react";
import {
  AppBar,
  Button,
  Checkbox,
  Dialog, FormControl,
  FormControlLabel,
  FormGroup,
  IconButton, InputAdornment, Slide,
  TextField,
  Toolbar, Tooltip,
  Typography
} from "@mui/material";
import PropTypes from "prop-types";
import CloseIcon from "@mui/icons-material/Close";
import SaveIcon from "@mui/icons-material/Save";
import HelpIcon from "@mui/icons-material/HelpOutline";
import OpenLinkIcon from "@mui/icons-material/OpenInNew";
import {styled} from "@mui/material/styles";
import "./DocumentModal.css";

const PREFIX = "DocumentModal";
const classes = {
  root: `${PREFIX}-root`,
  appBar: `${PREFIX}-appBar`
};

const StyledDialog = styled(Dialog)(() => ({
  [`& .${classes.appBar}`]: {
    position: "relative"
  }
}));

const propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func,
  onSave: PropTypes.func,
  document: PropTypes.shape({
    name: PropTypes.string,
    url: PropTypes.string,
    description: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.oneOf(["MEMBER", "PDL", "ADMIN"]))
  }),
  initialRole: PropTypes.oneOf(["MEMBER", "PDL", "ADMIN"])
};

const Transition = React.forwardRef((props, ref) => {
  return <Slide direction="up" ref={ref} {...props}/>
});

const DocumentModal = ({ open, onClose, onSave, document, initialRole }) => {
  const defaultName = document?.name || "";
  const defaultUrl = document?.url || "";
  const defaultDescription = document?.description || "";
  const memberSelected = document?.roles?.includes("MEMBER") || initialRole === "MEMBER";
  const pdlSelected = document?.roles?.includes("PDL") || initialRole === "PDL";
  const adminSelected = document?.roles?.includes("ADMIN") || initialRole === "ADMIN";

  const [name, setName] = useState(defaultName);
  const [url, setUrl] = useState(defaultUrl);
  const [description, setDescription] = useState(defaultDescription);
  const [accessForMembers, setAccessForMembers] = useState(memberSelected);
  const [accessForPdls, setAccessForPdls] = useState(pdlSelected);
  const [accessForAdmins, setAccessForAdmins] = useState(adminSelected);
  const [nameError, setNameError] = useState("");
  const [urlError, setUrlError] = useState("");

  // Reset local state when closed
  useEffect(() => {
    if (!open) {
      setName(defaultName);
      setUrl(defaultUrl);
      setDescription(defaultDescription);
      setNameError("");
      setUrlError("");
      setAccessForMembers(memberSelected);
      setAccessForPdls(pdlSelected);
      setAccessForAdmins(adminSelected);
    }
  }, [open, defaultName, defaultUrl, defaultDescription, memberSelected, pdlSelected, adminSelected]);

  const onSaveClick = () => {
    if (!name.trim() || !url.trim()) {
      if (!name.trim()) {
        setNameError("Document name must not be blank");
      }
      if (!url.trim()) {
        setUrlError("Document URL must not be blank");
      }

      return;
    }

    // Remove domain if present
    let sanitizedUrl = url;
    if (url.indexOf("/") >= 0) {
      sanitizedUrl = url.substring(url.indexOf("/"));
    } else {
      setUrlError("URL is missing '/' character");
      return;
    }

    const selectedRoles = [];
    if (accessForMembers) {
      selectedRoles.push("MEMBER");
    }
    if (accessForPdls) {
      selectedRoles.push("PDL");
    }
    if (accessForAdmins) {
      selectedRoles.push("ADMIN");
    }
    const newDocument = {
      name: name,
      url: sanitizedUrl,
      description: description,
      roles: selectedRoles
    };

    if (document) {
      onSave({
        ...document,
        ...newDocument
      });
    } else {
      onSave(newDocument);
    }
  }

  return (
    <StyledDialog
      className="document-modal"
      fullScreen
      open={open}
      TransitionComponent={Transition}
    >
      <AppBar className={classes.appBar}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="close"
            size="large"
            onClick={() => onClose()}>
            <CloseIcon/>
          </IconButton>
          <Typography variant="h6" sx={{ ml: 2, flex: 1 }}>
            {document ? "Edit" : "Create"} Document
          </Typography>
          <Button
            onClick={() => onSaveClick()}
            color="inherit"
            startIcon={<SaveIcon/>}
          >
            Save
          </Button>
        </Toolbar>
      </AppBar>
      <div className="document-modal-content">
        <div className="document-modal-fields">
          <TextField
            label="Document Name"
            variant="outlined"
            fullWidth
            value={name}
            onChange={(event) => {
              setName(event.target.value);
              setNameError("");
            }}
            error={!!nameError}
            helperText={nameError}
          />
          <TextField
            label="URL"
            variant="outlined"
            fullWidth
            value={url}
            onChange={(event) => {
              setUrl(event.target.value);
              setUrlError("");
            }}
            error={!!urlError}
            helperText={urlError}
            endAdornment={
              <InputAdornment position="end">
                <IconButton edge="end">
                  <OpenLinkIcon/>
                </IconButton>
              </InputAdornment>
            }
          />
          <TextField
            label="Description (optional)"
            variant="outlined"
            fullWidth
            minRows={3}
            multiline
            value={description}
            onChange={(event) => setDescription(event.target.value)}
          />
        </div>
        <div>
          <div className="document-modal-roles-header">
            <Typography variant="h6" fontWeight="bold">Add to Sections</Typography>
            <Tooltip arrow title="This document will appear on the check-ins page in the selected sections">
              <HelpIcon style={{ color: "gray", marginLeft: "2rem"}}/>
            </Tooltip>
          </div>
          <FormControl fullWidth>
            <FormGroup>
              <FormControlLabel
                label="Team Member Resources"
                control={
                  <Checkbox
                    checked={accessForMembers}
                    onChange={(event) => setAccessForMembers(event.target.checked)}
                  />}
              />
              <FormControlLabel
                label="Development Lead Guides"
                control={
                  <Checkbox
                    checked={accessForPdls}
                    onChange={(event) => setAccessForPdls(event.target.checked)}
                  />}
              />
              <FormControlLabel
                label="Admin Documents"
                control={
                  <Checkbox
                    checked={accessForAdmins}
                    onChange={(event) => setAccessForAdmins(event.target.checked)}
                  />}
              />
            </FormGroup>
          </FormControl>
        </div>
      </div>
    </StyledDialog>
  );

}

DocumentModal.propTypes = propTypes;

export default DocumentModal;