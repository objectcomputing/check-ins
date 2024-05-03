import React from 'react';
import ReviewPeriodStepper from './ReviewPeriodStepper';
import { AppContextProvider } from '../../../context/AppContext';

const reviewPeriod = {
  id: "b0b8a6f3-2d15-4923-8552-2f729f58ea0f",
  name: "Test",
  reviewStatus: 'OPEN',
  launchDate: "2024-05-03T15:53:56.673Z",
  selfReviewCloseDate: "2024-05-03T15:53:56.673Z",
  closeDate: "2024-05-03T15:53:56.673Z",
  reviewTemplateId: "d1e94b60-47c4-4945-87d1-4dc88f088e57",
  selfReviewTemplateId: "d1e94b60-47c4-4945-87d1-4dc88f088e57"
}
it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <ReviewPeriodStepper reviewPeriod={reviewPeriod} />
    </AppContextProvider>
  );
});