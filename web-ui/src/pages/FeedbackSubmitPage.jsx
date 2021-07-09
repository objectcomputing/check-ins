import React from "react";
import "./FeedbackRequestPage.css";
import FeedbackSubmissionTips from "../components/feedback_submission_tips/FeedbackSubmissionTips";



const FeedbackSubmitPage = () => {

  return (
    <div className="feedback-submit-page">
      <FeedbackSubmissionTips/>
    </div>
  );
};
export default FeedbackSubmitPage;