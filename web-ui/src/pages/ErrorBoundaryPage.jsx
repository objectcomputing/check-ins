import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { selectCsrfToken } from "../context/selectors";
import { Octokit } from "@octokit/core";
import { UPDATE_TOAST } from "../context/actions";

import { Button, Modal, TextField } from "@material-ui/core";

import "./ErrorBoundaryPage.css";

const ErrorFallback = ({ error }) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [open, setOpen] = useState(false);
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [owner, setOwner] = useState("");
  const close = () => {
    setOpen(false);
  };

  const octokit = new Octokit({
    auth: "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",
  });

  const createGitIssue = async () => {
    await octokit.request("POST /repos/oci-labs/check-ins/issues", {
      title: "New issue",
      body: body,
    });
  };

  return (
    <div className="error-boundary" role="alert">
      <div className="message-section">
        <h1 className="message">Something went wrong &#128533;</h1>
        <h1 className="error">Error: {error.message || error}</h1>
        <h1 className="new-issue-message">
          Would you mind creating a new issue and explaining what happened?
        </h1>
        <Button
          className="create-new-issue"
          color="primary"
          onClick={() => setOpen(true)}
        >
          Create New Issue
        </Button>
      </div>
      <Modal
        open={open}
        onClose={close}
        aria-labelledby="edit-guild-modal-title"
      >
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
          <TextField
            required
            id="new-issue-owner"
            label="Owner"
            className="fullWidth"
            placeholder="What's your GitHub name?"
            value={owner}
            onChange={(e) => setOwner(e.target.value)}
          />
          <div className="create-new-issue-actions halfWidth">
            <Button onClick={close} color="secondary">
              Cancel
            </Button>
            <Button
              onClick={() => {
                let res = await createGitIssue();
                console.log({ res });
                if (!res.error && res.status === 201) {
                  window.snackDispatch({
                    type: UPDATE_TOAST,
                    payload: {
                      severity: "success",
                      toast: `New issue ${title} created! Gratzie &#128512`,
                    },
                  });
                }
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
