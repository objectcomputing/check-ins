import React from "react";
import { useState } from 'react'
import "./FeedbackRequestPage.css";
import FeedbackSubmissionTips from "../components/feedback_submission_tips/FeedbackSubmissionTips";
import FeedbackSubmitForm from "../components/feedback_submit_form/FeedbackSubmitForm";

const FeedbackSubmitPage = () => {

  const [showTips, setShowTips] = useState(true);

  return (
    <div className="feedback-submit-page">
      {showTips ?
        <FeedbackSubmissionTips onNextClick={() => setShowTips(false)} /> :
        <FeedbackSubmitForm requesteeName={"JohnDoe"}/>
      }
    </div>
  );
};
export default FeedbackSubmitPage;