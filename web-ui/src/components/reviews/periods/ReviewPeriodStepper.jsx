import React, { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import optionsArr from './reviewStatus.json';

export default function ReviewPeriodStepper({ reviewPeriod }) {
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    let revPeriod = reviewPeriod?.reviewStatus;
    const index = optionsArr.findIndex(opt => opt.option === revPeriod);
    setActiveIndex(index);
  }, [reviewPeriod]);

  return (
    <Box sx={{ width: '100%' }}>
      <Stepper activeStep={activeIndex} alternativeLabel>
        {optionsArr.map(({ label, option }) => (
          <Step key={option}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
    </Box>
  );
}
