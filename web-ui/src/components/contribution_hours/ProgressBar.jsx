import React from "react";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import LinearProgress from "@material-ui/core/LinearProgress";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";

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
        {billableHours && <span>Billable Hours: {billableHours} - </span>}
        Contribution Hours: {contributionHours} - Target Hours: {targetHours}
      </Typography>
      <Typography
        align="right"
        variant="body2"
        color="textSecondary"
        style={ptoHours ? { display: "block" } : { display: "none" }}
      >
        PTO Hours: {ptoHours}
      </Typography>
    </div>
  );
};
LinearBuffer.propTypes = propTypes;
export default LinearBuffer;
