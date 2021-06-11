import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";

import "./FeedbackRequestPage.css";

const useStyles = makeStyles({
    root: {
        background: 'transparent'
    },

});


function getSteps() {
    return ["Select recipients", "Select template", "Set due date", "Done!"];
}

const FeedbackRequestPage = () => {
    const [activeStep, setActiveStep] = useState(0);
    const steps = getSteps();
    const classes = useStyles();

    const handleNext = () => {
        setActiveStep((prevActiveStep) => prevActiveStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
        setURLStep((prev) => {
            return {
                ...prev,
                search: `?step=${activeStep}`
            }
        })
     };

    return (
        <div className="feedback-request-page">
            <div className="header-container">
                <Typography variant="h4">Feedback Request for <b>John Doe</b></Typography>
                <div>
                    {activeStep === steps.length ? (
                        <div>
                            <Typography>
                                All steps completed!
                            </Typography>
                        </div>
                    ) : (
                        <div>
                            <Button disabled={activeStep === 0} onClick={handleBack}>
                                Back
                            </Button>

                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleNext}
                            >
                                {activeStep === steps.length - 1 ? "Finish" : "Next"}
                            </Button>
                        </div>
                    )}
                </div>
            </div>
            <Stepper activeStep={activeStep} className={classes.root}>
                {steps.map((label) => {
                    const stepProps = {};
                    const labelProps = {};
                    return (
                        <Step key={label} {...stepProps}>
                            <StepLabel {...labelProps} key = {label}>{label}</StepLabel>
                        </Step>
                    );
                })}
            </Stepper>
            <div className="current-urlStep-content">
                {/* Render components conditionally based on current urlStep */}
                {/* if urlStep==0 then render step0 */}
                {/* etc... */}
            </div>
        </div>
        
    );
}

export default FeedbackRequestPage;