import React, { useEffect, useState } from 'react';
import { TextField } from '@mui/material';
import PropTypes from 'prop-types';
const propTypes = {
  onFormChange: PropTypes.func
};

const AdHocCreationForm = props => {
  const [title, setTitle] = useState('Ad Hoc');
  const [description, setDescription] = useState('');
  const [isForExternalRecipient, setIsForExternalRecipient] = useState(false);
  const [question, setQuestion] = useState('');

  useEffect(() => {
    props.onFormChange({
      title: title
        , description: description
        , question: question
        , isForExternalRecipient: isForExternalRecipient
    }); // eslint-disable-next-line
  }, [title, description, question, isForExternalRecipient]);

  return (
      <React.Fragment>
          <TextField
              label="Title"
              placeholder="Ad Hoc"
              fullWidth
              margin="normal"
              required={true}
              value={title}
              onChange={event => {
                  setTitle(event.target.value);
              }}
          />
          <TextField
              label="Description"
              placeholder="Give a brief description of the template (optional)"
              fullWidth
              margin="normal"
              value={description}
              onChange={event => {
                  setDescription(event.target.value);
              }}
          />
          <label style={{display: 'flex', alignItems: 'center'}}>
              <input
                  type="checkbox"
                  checked={isForExternalRecipient}
                  onChange={event => {
                      setIsForExternalRecipient(event.target.checked);
                  }}
              />
              <span style={{marginLeft: '5px'}}>For external recipients only</span>
          </label>
          <TextField
              label="Ask a feedback question"
              placeholder="How is your day going?"
              fullWidth
              multiline
              maxRows={10}
              margin="normal"
              required={true}
              value={question}
              onChange={event => {
                  setQuestion(event.target.value);
              }}
          />
      </React.Fragment>
  );
};

AdHocCreationForm.propTypes = propTypes;

export default AdHocCreationForm;
