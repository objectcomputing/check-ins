import React from 'react';
import ReviewPeriodStepper from './ReviewPeriodStepper';

export default {
  component: ReviewPeriodStepper,
  title: 'Check Ins/Review Period Stepper',
  decorators: [
    ReviewPeriodStepper => (
      <div style={{ width: '400px', height: '400px' }}>
        <ReviewPeriodStepper />
      </div>
    )
  ]
};

const Template = args => {
  return <ReviewPeriodStepper {...args} />;
};

const reviewPeriodStepperData = {
  reviewPeriod:  {
        id: "b0b8a6f3-2d15-4923-8552-2f729f58ea0f",
        name: "Test",
        reviewStatus: "OPEN",
        launchDate: "2024-05-03T15:53:56.673Z",
        selfReviewCloseDate: "2024-05-03T15:53:56.673Z",
        closeDate: "2024-05-03T15:53:56.673Z",
        reviewTemplateId: "d1e94b60-47c4-4945-87d1-4dc88f088e57",
        selfReviewTemplateId: "d1e94b60-47c4-4945-87d1-4dc88f088e57"
    }
};

export const ReviewPeriodSteppers = Template.bind({});
ReviewPeriodSteppers.args = {
  ...reviewPeriodStepperData
};
