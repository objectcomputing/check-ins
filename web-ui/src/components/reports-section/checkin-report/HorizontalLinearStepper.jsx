import React from 'react';
import { Box, Stepper, Step, StepLabel, Typography } from '@mui/material';

/** @type {CheckinStatus[]} */
const steps = ['Not Started', 'In Progress', 'Done'];

export default function HorizontalLinearStepper({ step = 0 }) {
  const [activeStep, setActiveStep] = React.useState(step);
  const [skipped, setSkipped] = React.useState(new Set());

  const isStepOptional = step => step === -1;
  const isStepSkipped = step => skipped.has(step);

  return (
    <Box sx={{ width: '100%', my: 1 }}>
      <Stepper activeStep={activeStep}>
        {steps.map((label, index) => {
          const stepProps = {};
          const labelProps = {};
          if (isStepOptional(index)) {
            labelProps.optional = (
              <Typography variant="caption">Optional</Typography>
            );
          }
          if (isStepSkipped(index)) {
            stepProps.completed = false;
          }
          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
    </Box>
  );
}
