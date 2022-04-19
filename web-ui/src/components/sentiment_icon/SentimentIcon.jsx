import React, {useState, useCallback} from "react";
import PropTypes from "prop-types";
import IconButton from "@mui/material/IconButton";
import {NotInterested, SentimentSatisfied, SentimentVeryDissatisfied, SentimentVerySatisfied} from "@mui/icons-material";
import {Popover} from "@mui/material";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import "./SentimentIcon.css";

const sentimentThreshold = 0.4;
const negativeColor = "#e74c3c";
const neutralColor = "#f39c12";
const positiveColor = "#27ae60";

const propTypes = {
  sentimentScore: PropTypes.number
}

const SentimentIcon = (props) => {

  const [anchorElement, setAnchorElement] = useState(null);
  const [showSentimentPicker, setShowSentimentPicker] = useState(false);
  const [selectedSentiment, setSelectedSentiment] = useState(null);
  const [currentSentiment, setCurrentSentiment] = useState(props.sentimentScore);

  // Gets the appropriate icon, button styles, and icon styles based on the sentiment score
  const getSentimentStyles = useCallback((sentimentScore, isSelected) => {
    let sentimentIcon; // HTML element representing the sentiment icon
    let buttonStyle;  // styles to be applied to the icon button
    if (sentimentScore < -sentimentThreshold) {
      sentimentIcon = <SentimentVeryDissatisfied style={{color: isSelected ? "white" : negativeColor}}/>;
      buttonStyle = {backgroundColor: isSelected ? negativeColor : "transparent"};
    } else if (Math.abs(sentimentScore) <= sentimentThreshold) {
      sentimentIcon = <SentimentSatisfied style={{color: isSelected ? "white" : neutralColor}}/>;
      buttonStyle = {backgroundColor: isSelected ? neutralColor : "transparent"};
    } else {
      sentimentIcon = <SentimentVerySatisfied style={{color: isSelected ? "white" : positiveColor}}/>;
      buttonStyle = {backgroundColor: isSelected ? positiveColor : "transparent"};
    }

    return [sentimentIcon, buttonStyle];
  }, []);

  // Renders a sentiment button that is styled according to whether the popover is open
  const getSentimentIcon = useCallback((sentimentScore) => {
    const [sentimentIcon, buttonStyle] = getSentimentStyles(sentimentScore, !!anchorElement);
    return (
      <IconButton
        onClick={(event) => handlePopoverOpen(event)}
        style={buttonStyle}
        size="large">
        {sentimentIcon}
      </IconButton>
    );
  }, [anchorElement, getSentimentStyles]);

  // Renders a sentiment button that is styled according to which sentiment option is selected within the popover
  const getSentimentOption = useCallback((sentimentScore) => {
    const [sentimentIcon, buttonStyle] = getSentimentStyles(sentimentScore, selectedSentiment === sentimentScore);
    return (
      <IconButton
        key={sentimentScore}
        onClick={() => setSelectedSentiment(sentimentScore)}
        style={buttonStyle}
        size="large">
        {sentimentIcon}
      </IconButton>
    );
  }, [selectedSentiment, getSentimentStyles]);

  const updateSentiment = (newSentiment) => {
    if (typeof currentSentiment !== "number") return;  // type check
    if (selectedSentiment === null) return;  // do not attempt update if no option is selected
    // prevent saving updated sentiment if it is the same as before
    if (
      (newSentiment < -sentimentThreshold && currentSentiment < -sentimentThreshold) ||
      (Math.abs(newSentiment) < sentimentThreshold && Math.abs(currentSentiment) < sentimentThreshold) ||
      (newSentiment > sentimentThreshold && currentSentiment > sentimentThreshold)
    ) {
      setAnchorElement(null);
      return;
    }

    setCurrentSentiment(newSentiment);
    // TODO: Make appropriate API call to update the sentiment

    setAnchorElement(null);
  }

  const handlePopoverOpen = (event) => {
    setShowSentimentPicker(false);
    setSelectedSentiment(null);
    setAnchorElement(event.currentTarget);
  }

  const handlePopoverClose = () => {
    setAnchorElement(null);
  }

  if (props.sentimentScore === undefined || currentSentiment < -1 || 1 < currentSentiment) {
    return (
      <IconButton disabled size="large">
        <NotInterested style={{color: "gray"}}/>
      </IconButton>
    );
  }

  return (
    <React.Fragment>
      {getSentimentIcon(currentSentiment)}
      <Popover
        open={!!anchorElement}
        anchorEl={anchorElement}
        onClose={handlePopoverClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "center"
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "center"
        }}
      >
        <div className="sentiment-popover-content">
          {!showSentimentPicker ?
            <React.Fragment>
              <Typography variant="body1">Is this correct?</Typography>
              <Typography variant="body2" style={{fontSize: "10px", color: "gray"}}>If you believe the automatic sentiment detection may have made a mistake, you may choose to change it.</Typography>
              <div className="sentiment-action-buttons">
                <Button
                  style={{color: "gray"}}
                  onClick={() => setAnchorElement(null)}>
                  Dismiss
                </Button>
                <Button
                  color="primary"
                  onClick={() => setShowSentimentPicker(true)}>
                  Change
                </Button>
              </div>
            </React.Fragment> :
            <React.Fragment>
              <Typography variant="body1">What is the sentiment of this response?</Typography>
              <div className="sentiment-option-buttons">
                {[-1, 0, 1].map((possibleSentiment) => getSentimentOption(possibleSentiment))}
              </div>
              <Button
                color="primary"
                variant="outlined"
                onClick={() => updateSentiment(selectedSentiment)}
                disabled={selectedSentiment === null}>
                Save
              </Button>
            </React.Fragment>
          }
        </div>
      </Popover>
    </React.Fragment>
  );
}

SentimentIcon.propTypes = propTypes;

export default SentimentIcon;