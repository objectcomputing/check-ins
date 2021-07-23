import React, {useState, useCallback} from "react";
import PropTypes from "prop-types";
import IconButton from "@material-ui/core/IconButton";
import {SentimentSatisfied, SentimentVeryDissatisfied, SentimentVerySatisfied} from "@material-ui/icons";
import {Popover} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import "./SentimentIcon.css";

const sentimentThreshold = 0.4;
const negativeColor = "#e74c3c";
const neutralColor = "#f39c12";
const positiveColor = "#27ae60";

const propTypes = {
  sentimentScore: PropTypes.number.isRequired
}

const SentimentIcon = (props) => {

  const [anchorElement, setAnchorElement] = useState(null);
  const [showSentimentPicker, setShowSentimentPicker] = useState(false);
  const [selectedSentiment, setSelectedSentiment] = useState(null);

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
      >
        {sentimentIcon}
      </IconButton>
    );
  }, [anchorElement, getSentimentStyles]);

  // Renders a sentiment button that is styled according to which sentiment option is selected within the popover
  const getSentimentOption = useCallback((sentimentScore) => {
    const [sentimentIcon, buttonStyle] = getSentimentStyles(sentimentScore, selectedSentiment === sentimentScore);
    return (
      <IconButton
        onClick={() => setSelectedSentiment(sentimentScore)}
        style={buttonStyle}
      >
        {sentimentIcon}
      </IconButton>
    );
  }, [selectedSentiment, getSentimentStyles]);

  const updateSentiment = (newSentiment) => {
    // prevent saving updated sentiment if it is the same as before
    if (
      (newSentiment < -sentimentThreshold && props.sentimentScore < -sentimentThreshold) ||
      (Math.abs(newSentiment) < sentimentThreshold && Math.abs(props.sentimentScore) < sentimentThreshold) ||
      (newSentiment > sentimentThreshold && props.sentimentScore > sentimentThreshold)
    ) {
      setAnchorElement(null);
      return;
    }

    console.log(`Updating to ${newSentiment}`);
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

  return (
    <React.Fragment>
      {getSentimentIcon(props.sentimentScore)}
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
                onClick={() => updateSentiment(selectedSentiment)}>
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