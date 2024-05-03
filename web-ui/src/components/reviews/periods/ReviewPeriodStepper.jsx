import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';

const options = [
  { label: 'Planning', option: 'PLANNING' },
  { label: 'Awaiting Approval', option: 'AWAITING_APPROVAL' },
  { label: 'Open', option: 'OPEN' },
  { label: 'Closed', option: 'CLOSED' }
];

export default function ReviewPeriodStepper({ reviewPeriod }) {
  return (
    <Box sx={{ width: '100%' }}>
      <Stepper
        activeStep={reviewPeriod?.reviewStatus === option ? 0 : 1}
        alternativeLabel
      >
        {options.map((label, option) => (
          <Step key={option}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
    </Box>
  );
}
