import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';

const steps = [
  'Review Period Is Open',
  'Review Period Is Closed'
];

export default function ReviewPeriodStepper({reviewPeriod}) {
  return (
    <Box sx={{ width: '100%' }}>
      <Stepper activeStep={reviewPeriod?.open === true ? 0 : 1} alternativeLabel>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
    </Box>
  );
}