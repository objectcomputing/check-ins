import React, { useContext, useEffect, useState } from 'react';
import { Card, CardContent, CardHeader } from '@mui/material';
import PropTypes from 'prop-types';
import IconButton from '@mui/material/IconButton';
import VisibilityIcon from '@mui/icons-material/Visibility';
import withStyles from '@mui/styles/withStyles';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { green } from '@mui/material/colors';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';
import { getMember } from '../../api/member';

import './TemplateCard.css';

const cutText = (text, maxCharacters) => {
  if (!text) {
    text = '';
  }
  let shortenedText = text;
  if (text.length > maxCharacters) {
    shortenedText = `${text.substring(0, maxCharacters)}...`;
  }
  return shortenedText;
};

const templateCardHeaderStyles = ({ palette, breakpoints }) => {
  const space = 8;
  return {
    root: {
      minWidth: 256
    },
    header: {
      padding: `1px ${space}px 0`,
      display: 'flex',
      alignItems: 'center',
      flexDirection: 'row',
      justifyContent: 'space-between',
      maxHeight: '30px'
    }
  };
};

const TemplateCardHeader = withStyles(templateCardHeaderStyles, {
  name: 'TemplateCardHeader'
})(({ classes, selected, allowPreview = false, onPreview }) => (
  <div className={classes.root}>
    <div className={classes.header}>
      {allowPreview && (
        <IconButton onClick={onPreview} aria-label="show more" size="large">
          <VisibilityIcon />
        </IconButton>
      )}
      {selected && (
        <CheckCircleIcon style={{ color: green[500] }}>
          checkmark-image
        </CheckCircleIcon>
      )}
    </div>
  </div>
));

const propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
  creatorId: PropTypes.string.isRequired,
  isAdHoc: PropTypes.bool,
  onPreviewClick: PropTypes.func,
  onCardClick: PropTypes.func
};

const TemplateCard = props => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [creatorName, setCreatorName] = useState('');

  const handlePreviewClick = e => {
    e.stopPropagation();
    props.onPreviewClick(e);
  };

  // Get name of the template creator
  useEffect(() => {
    async function getCreatorName() {
      if (props.creatorId) {
        let res = await getMember(props.creatorId, csrf);
        let creatorProfile =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : null;
        setCreatorName(creatorProfile ? creatorProfile.name : '');
      }
    }
    if (csrf) {
      getCreatorName();
    }
  }, [props.creatorId, csrf]);

  return (
    <Card onClick={props.onCardClick} className="feedback-template-card">
      <CardHeader
        component={TemplateCardHeader}
        selected={props.isSelected}
        allowPreview
        onPreview={handlePreviewClick}
      />
      <CardContent className="card-content">
        <div className="template-details">
          <h3 className="template-name">{cutText(props.title, 20)}</h3>
          <p className="description">{cutText(props.description, 90)}</p>
        </div>
        <p className="creator">
          Created by: <b>{creatorName}</b>
        </p>
        {props.isForExternalRecipient && (
            <label className="externalRecipientLabel" style={{ display: 'flex', alignItems: 'center' }}>
              <input
                  type="checkbox"
                  checked={props.isForExternalRecipient}
                  disabled
                  className="externalRecipient"
              />
              <span style={{ marginLeft: '5px' }}>For external recipients only</span>
            </label>
        )}
      </CardContent>
    </Card>
  );
};

TemplateCard.propTypes = propTypes;

export default TemplateCard;
