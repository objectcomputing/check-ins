import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { UPDATE_TOAST } from "../context/actions";
import { selectCsrfToken } from "../context/selectors";
import { newGitHubIssue } from "../api/github";

import { Button, Modal, TextField } from "@mui/material";

import { Editor } from '@tinymce/tinymce-react';

import "./ErrorBoundaryPage.css";
import { sanitizeQuillElements } from "../helpers/sanitizehtml";

const ErrorFallback = ({ error }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [open, setOpen] = useState(false);
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [link, setLink] = useState("");

  const close = () => {
    setOpen(false);
  };

  const createNewGitIssue = async () => {
    if (body === "" || title === "") {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Must have a Body and a Title",
        },
      });
      return;
    }
    //Clean new issue of potentially malicious content in body
    //before upload to server
    let sanitizeBody = sanitizeQuillElements(body)
    let res = await newGitHubIssue(sanitizeBody, title, csrf);
    if (res && res.payload) {
      setLink(res.payload.data[0].html_url);
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: !res.error ? "success" : "error",
          toast: !res.error
            ? `New issue ${title} created! Gratzie &#128512`
            : res.error.message,
        },
      });
    }
  };

  const handleBodyChange = (content) => {
    setBody(content);
  };

  return (
    <div className="error-boundary" role="alert">
      <div className="message-section">
        <h1 className="message">
          Something went wrong
          <span aria-label="sad face" role="img">
            &#128533;
          </span>
        </h1>
        <h1 className="error">Error: {error?.message || error}</h1>
        <h1 className="new-issue-message">
          Would you mind creating a new issue and explaining what happened?
        </h1>
        <Button
          className="create-new-issue-button"
          color="primary"
          onClick={() => setOpen(true)}
        >
          Create New GitHub Issue
        </Button>
        {link !== "" && (
          <h3 className="link">
            Check out your issue here! &nbsp; <a href={link}>{link}</a>
          </h3>
        )}
      </div>
      <Modal open={open} onClose={close}>
        <div className="create-new-issue-modal">
          <TextField
            required
            id="new-issue-title"
            label="Title"
            placeholder="Issue Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <Editor
            required
            placeholder="Issue Description"
            style={{ height: "175px", marginBottom: "30px" }}
            value={body}
            onChange={handleBodyChange}
          />
          <div className="create-new-issue-modal-actions">
            <Button onClick={close} color="secondary">
              Cancel
            </Button>
            <Button
              onClick={() => {
                createNewGitIssue();
                close();
              }}
              color="primary"
            >
              Submit Issue
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default ErrorFallback;
