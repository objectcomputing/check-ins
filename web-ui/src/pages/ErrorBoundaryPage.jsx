import React, { useState } from "react";

import { Octokit } from "@octokit/core";
import { UPDATE_TOAST } from "../context/actions";

import { Button, Modal, TextField } from "@material-ui/core";

import "./ErrorBoundaryPage.css";

const ErrorFallback = ({ error }) => {
  const [open, setOpen] = useState(false);
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");

  const close = () => {
    setOpen(false);
  };

  const octokit = new Octokit({
    // need to generate personal access token with no expiration date or perhaps register as OAuth. This token expires in 1 day
    auth: "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",
  });

  const createGitIssue = async () => {
    let res = await octokit.request("POST /repos/oci-labs/check-ins/issues", {
      title: title,
      body: body,
    });
    if (res.status === 201) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `New issue ${title} created! Gratzie &#128512`,
        },
      });
    }
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
        <h1 className="error">Error: {error.message || error}</h1>
        <h1 className="new-issue-message">
          Would you mind creating a new issue and explaining what happened?
        </h1>
        <Button
          className="create-new-issue-button"
          color="primary"
          onClick={() => setOpen(true)}
        >
          Create New Issue
        </Button>
      </div>
      <Modal open={open} onClose={close}>
        <div className="create-new-issue-modal">
          <TextField
            required
            id="new-issue-title"
            label="Title"
            className="fullWidth"
            placeholder="Issue Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <TextField
            required
            id="new-issue-body"
            label="Body"
            className="fullWidth"
            placeholder="Issue Description"
            value={body}
            onChange={(e) => setBody(e.target.value)}
          />
          <div className="create-new-issue-modal-actions halfWidth">
            <Button onClick={close} color="secondary">
              Cancel
            </Button>
            <Button
              onClick={() => {
                createGitIssue();
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
