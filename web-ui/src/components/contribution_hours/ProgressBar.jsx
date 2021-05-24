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
      <Box borderRadius={16} minWidth={35}>
        <Typography variant="body2" color="textSecondary">{`${Math.round(
          props.value
        )}%`}</Typography>
      </Box>
    </Box>
  );
}

LinearProgressWithLabel.propTypes = {
  /**
   * The value of the progress indicator for the determinate and buffer variants.
   * Value between 0 and 100.
   */
  value: PropTypes.number.isRequired,
  bufferValue: PropTypes.number.isRequired,
  billableHours: PropTypes.number,
  contributionHours: PropTypes.number.isRequired,
  targetHours: PropTypes.number.isRequired,
};

const useStyles = makeStyles({
  root: {
    width: "100%",
  },
});

export default function LinearBuffer() {
  const classes = useStyles();
  const [billableHours] = React.useState(23);
  const [contributionHours] = React.useState(95);

  return (
    <div className={classes.root}>
      <LinearProgressWithLabel
        variant={billableHours ? "buffer" : "determinate"}
        value={billableHours ? billableHours : 50}
        valueBuffer={billableHours ? contributionHours : null}
      />
    </div>
  );
}
