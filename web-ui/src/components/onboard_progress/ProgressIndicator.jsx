import React from "react";
import PropTypes from "prop-types";
import CircularProgress from "@mui/material/CircularProgress";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

function CircularProgressWithLabel(props) {
  return (
    <Box sx={{position: "relative", display: "inline-flex" }}>
      <CircularProgress sx={{marginTop:"20px"}} variant="determinate" {...props} />
      <Box
        sx={{
          top: 0,
          left: 0,
          bottom: 0,
          right: 0,
          position: "absolute",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Typography variant="caption" component="div" color="text.secondary">
          {`${Math.round(props.value)}%`}
        </Typography>
      </Box>
    </Box>
  );
}

CircularProgressWithLabel.propTypes = {
  /**
   * The value of the progress indicator for the determinate variant.
   * Value between 0 and 100.
   * @default 0
   */
  value: PropTypes.number.isRequired,
};

export default function ProgressIndicator(props) {
  let completedDocument = props.dataDocument.reduce((total, current) => {
    if (current.completed === "Yes") total += 1;
    return total;
  }, 0);
  let completedSurvey = props.dataSurvey.reduce((total, current) => {
    if (current.completed === "Yes") total += 1;
    return total;
  }, 0);
  let currentProgress = ((completedDocument+completedSurvey) / (props.dataDocument.length + props.dataSurvey.length)) * 100;
  return <CircularProgressWithLabel value={currentProgress} />;
}
