import React, {useContext, useEffect, useState} from "react";
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
import {AppContext} from "../../context/AppContext";
import {selectRoles} from "../../context/selectors";

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

const roleSectionMap = {
  "ADMIN": "Admin Documents",
  "PDL": "Development Lead Guides",
  "MEMBER": "Team Member Resources"
};

const propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func,
  onSave: PropTypes.func,
  document: PropTypes.shape({
    name: PropTypes.string,
    url: PropTypes.string,
    description: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.object)
  })
};

const Transition = React.forwardRef((props, ref) => {
  return <Slide direction="up" ref={ref} {...props}/>
});

const DocumentModal = ({ open, onClose, onSave, document }) => {
  const defaultName = document?.name || "";
  const defaultUrl = document?.url || "";
  const defaultDescription = document?.description || "";

  const { state } = useContext(AppContext);
  const allRoles = selectRoles(state);

  const [name, setName] = useState(defaultName);
  const [url, setUrl] = useState(defaultUrl);
  const [description, setDescription] = useState(defaultDescription);
  const [selectedRoles, setSelectedRoles] = useState(new Set());
  const [defaultRoles, setDefaultRoles] = useState(new Set());
  const [nameError, setNameError] = useState("");
  const [urlError, setUrlError] = useState("");

  // Reset local state when closed
  useEffect(() => {
    if (!open) {
      setName(defaultName);
      setUrl(defaultUrl);
      setDescription(defaultDescription);
      setSelectedRoles(defaultRoles);
      setNameError("");
      setUrlError("");
    }
  }, [open, defaultName, defaultUrl, defaultDescription, defaultRoles]);

  // Select default roles when roles load
  useEffect(() => {
    const defaultRoles = allRoles
      .filter(role => !!document?.roles?.find(docRole => docRole.roleDocumentId.roleId === role.id))
      .map(role => role.role);
    setDefaultRoles(new Set(defaultRoles));
  }, [document, allRoles]);

  useEffect(() => {
    setSelectedRoles(defaultRoles);
  }, [defaultRoles]);

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

    const documentInfo = {
      name: name,
      url: sanitizedUrl,
      description: description,
      roles: selectedRoles
    };
    onSave(documentInfo);
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
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <Tooltip arrow title="Open in New Tab">
                    <IconButton onClick={() => {
                      window.open(url.toString(), "_blank");
                    }} edge="end" style={{ marginRight: 0 }}>
                      <OpenLinkIcon/>
                    </IconButton>
                  </Tooltip>
                </InputAdornment>
              )
            }}
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
              {allRoles.map(role => (
                <FormControlLabel
                  key={role.id}
                  label={roleSectionMap[role.role]}
                  control={
                    <Checkbox
                      checked={selectedRoles.has(role.role)}
                      onChange={(event) => {
                        const selected = new Set(selectedRoles);
                        if (event.target.checked) {
                          selected.add(role.role);
                        } else {
                          selected.delete(role.role);
                        }
                        setSelectedRoles(selected);
                      }}
                    />
                  }
                />
              ))}
            </FormGroup>
          </FormControl>
        </div>
      </div>
    </StyledDialog>
  );

}

DocumentModal.propTypes = propTypes;

export default DocumentModal;