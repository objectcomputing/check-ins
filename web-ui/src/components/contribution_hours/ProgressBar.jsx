import React from "react";
import PropTypes from "prop-types";
import makeStyles from '@mui/styles/makeStyles';
import LinearProgress from "@mui/material/LinearProgress";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

function LinearProgressWithLabel(props) {
  return (
    <Box display="flex" alignItems="center">
      <Box width="100%" mr={1}>
        <LinearProgress variant="buffer" {...props} />
      </Box>
    </Box>
  );
}

const propTypes = {
  /**
   * The value of the progress indicator for the determinate and buffer variants.
   * Value between 0 and 100.
   */
  billableHours: PropTypes.number,
  contributionHours: PropTypes.number.isRequired,
  targetHours: PropTypes.number.isRequired,
  ptoHours: PropTypes.number,
};

const useStyles = makeStyles({
  root: {
    width: "100%",
  },
});

const LinearBuffer = ({
  billableHours,
  contributionHours = 925,
  targetHours = 1850,
  ptoHours = 0,
}) => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <LinearProgressWithLabel
        variant={billableHours ? "buffer" : "determinate"}
        value={
          billableHours
            ? (billableHours / targetHours) * 100
            : (contributionHours / targetHours) * 100
        }
        valueBuffer={
          billableHours ? (contributionHours / targetHours) * 100 : null
        }
      />
      <Typography
        align="right"
        variant="body2"
        color="textSecondary"
        style={{ display: "block" }}
      >
        Billable Hours: {billableHours} - Contribution Hours:{" "}
        {contributionHours} - Target Hours: {targetHours} - PTO Hours:{" "}
        {ptoHours}
      </Typography>
    </div>
  );
};
LinearBuffer.propTypes = propTypes;
export default LinearBuffer;
