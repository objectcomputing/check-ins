import React, {useEffect, useContext, useState, useRef} from "react";
import PropTypes from "prop-types";
import { UPDATE_TOAST, UPDATE_REVIEW_PERIODS } from "../../../context/actions";
import { AppContext } from "../../../context/AppContext";
import FeedbackSubmitForm from "../../feedback_submit_form/FeedbackSubmitForm";
import { getReviewPeriods } from "../../../api/reviewperiods.js";
import { createFeedbackRequest, findSelfReviewRequestsByPeriodAndTeamMember } from "../../../api/feedback.js";
import {
  selectCsrfToken,
  selectCurrentUserId,
  selectCurrentUser,
  selectReviewPeriod,
} from "../../../context/selectors";
import DateFnsUtils from "@date-io/date-fns";
const dateUtils = new DateFnsUtils();

const propTypes = {
  message: PropTypes.string,
  onSelect: PropTypes.func,
};
const displayName = "SelfReview";

const SelfReview = ({ periodId, onBack }) => {
  const { state, dispatch } = useContext(AppContext);
  const currentUserId = selectCurrentUserId(state);
  const memberProfile = selectCurrentUser(state);
  const csrf = selectCsrfToken(state);
  const [selfReview, setSelfReview] = useState(null);
  const period = selectReviewPeriod(state, periodId);
  const loadingReview = useRef(false);

  useEffect(() => {
    const getAllReviewPeriods = async () => {
      const res = await getReviewPeriods(csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: data});
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch]);

  useEffect(() => {
    const createSelfReview = async () => {
      if(!memberProfile?.supervisorid) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "You do not have an assigned supervisor. Please contact your HR administrator.",
          },
        });
      } else {
        const res = await createFeedbackRequest({
          creatorId: memberProfile?.supervisorid,
          requesteeId: currentUserId,
          recipientId: currentUserId,
          templateId: period?.selfReviewTemplateId,
          reviewPeriodId: period?.id,
          sendDate: dateUtils.format(new Date(), 'yyyy-MM-dd'),
          status: "pending",
        }, csrf);
        const data =
          res &&
          res.payload &&
          res.payload.data &&
          res.payload.status === 201 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setSelfReview(data);
        } else {
          loadingReview.current = false;
        }
      }
    };

    const getReviewRequest = async () => {
      const res = await findSelfReviewRequestsByPeriodAndTeamMember(period, currentUserId, csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data && data.length > 0) {
        setSelfReview(data[0]);
      } else {
        createSelfReview();
      }
    };

    if (csrf && !selfReview && currentUserId && memberProfile && period && !loadingReview.current) {
      loadingReview.current = true;
      getReviewRequest();
    }
  }, [csrf, period, selfReview, currentUserId, memberProfile, dispatch]);

  return selfReview && selfReview.id && (
    <FeedbackSubmitForm requesteeName={memberProfile?.firstName+" "+memberProfile?.lastName} requestId={selfReview?.id} request={selfReview} reviewOnly={"SUBMITTED" === selfReview?.status?.toUpperCase()} />
  );
};

SelfReview.propTypes = propTypes;
SelfReview.displayName = displayName;

export default SelfReview;
