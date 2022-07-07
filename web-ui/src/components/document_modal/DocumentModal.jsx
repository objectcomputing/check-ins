import React, {useEffect, useState} from "react";
import {
  AppBar,
  Button,
  Checkbox,
  Dialog,
  FormControlLabel,
  FormGroup,
  IconButton, Slide,
  TextField,
  Toolbar,
  Typography
} from "@mui/material";
import PropTypes from "prop-types";
import CloseIcon from "@mui/icons-material/Close";
import SaveIcon from "@mui/icons-material/Save";
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
  document: PropTypes.object,
};

const Transition = React.forwardRef((props, ref) => {
  return <Slide direction="up" ref={ref} {...props}/>
});

const DocumentModal = ({ open, onClose, onSave, document }) => {

  const defaultName = document?.name || "";
  const defaultUrl = document?.url || "";
  const defaultDescription = document?.description || "";

  const [name, setName] = useState(defaultName);
  const [url, setUrl] = useState(defaultUrl);
  const [description, setDescription] = useState(defaultDescription);
  const [nameError, setNameError] = useState("");
  const [urlError, setUrlError] = useState("");
  const [accessForPdls, setAccessForPdls] = useState(false);
  const [accessForMembers, setAccessForMembers] = useState(false);

  // Reset local state when closed
  useEffect(() => {
    if (!open) {
      setName(defaultName);
      setUrl(defaultUrl);
      setDescription(defaultDescription);
      setNameError("");
      setUrlError("");
      setAccessForPdls(false);
      setAccessForMembers(false);
    }
  }, [open, defaultName, defaultUrl, defaultDescription]);

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

    if (document) {
      onSave({
        ...document,
        name: name,
        url: sanitizedUrl,
        description: description
      });
    } else {
      onSave({
        name: name,
        url: sanitizedUrl,
        description: description
      })
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
          <Typography variant="h5" fontWeight="bold">Accessible to:</Typography>
          <FormGroup>
            <FormControlLabel
              label="Members"
              control={
              <Checkbox
                checked={accessForMembers}
                onChange={(event) => setAccessForMembers(event.target.checked)}
              />}
            />
            <FormControlLabel
              label="PDLs"
              control={
              <Checkbox
                checked={accessForPdls}
                onChange={(event) => setAccessForPdls(event.target.checked)}
              />}
            />
          </FormGroup>
        </div>
      </div>
    </StyledDialog>
  );

}

DocumentModal.propTypes = propTypes;

export default DocumentModal;