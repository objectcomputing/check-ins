import React, { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import optionsArr from './reviewStatus.json';
import { toInteger } from 'lodash';

export default function ReviewPeriodStepper({ reviewPeriod }) {
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    let index = 0;
    let revPeriod = reviewPeriod?.reviewStatus;
    optionsArr.map((opt, i) => {
      if (opt.option === revPeriod) {
        index = i;
      }
    });
    setActiveIndex(toInteger(index));
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
