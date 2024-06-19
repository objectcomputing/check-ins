import React, { useContext, useState, useCallback } from 'react';
import { styled, useTheme } from '@mui/material/styles';
import SwipeableViews from 'react-swipeable-views';
import PropTypes from 'prop-types';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken, selectCurrentUser, selectProfile } from '../../context/selectors';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import {
  AppBar,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Modal,
  Tab,
  Tabs,
  Typography
} from '@mui/material';
import FeedbackSubmitForm from '../feedback_submit_form/FeedbackSubmitForm';
import SelectUserModal from './SelectUserModal';
import { cancelFeedbackRequest, updateFeedbackRequest } from '../../api/feedback';
import { UPDATE_TOAST } from '../../context/actions';

const propTypes = {
  selfReview: PropTypes.any,
  reviews: PropTypes.arrayOf(PropTypes.any),
  memberProfile: PropTypes.any,
  reloadReviews: PropTypes.func
};
const displayName = 'TeamMemberReview';

const PREFIX = displayName;
const classes = {
  actionButtons: `${PREFIX}-actionButtons`,
  buttonRow: `${PREFIX}-buttonRow`,
  periodModal: `${PREFIX}-periodModal`
};

const Root = styled('div')(({ theme }) => ({
  [`& .${classes.actionButtons}`]: {
    margin: '0.5em 0 0 1em',
    ['@media (max-width:820px)']: {
      // eslint-disable-line no-useless-computed-key
      padding: '0'
    }
  },
  [`& .${classes.buttonRow}`]: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    margin: '0 0 1em 0'
  }
}));

const TabPanel = ({ children, value, index, ...other }) => {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`full-width-tabpanel-${index}`}
      aria-labelledby={`full-width-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
};

TabPanel.propTypes = {
  children: PropTypes.any,
  dir: PropTypes.string,
  index: PropTypes.number,
  value: PropTypes.number
};
TabPanel.displayName = 'TabPanel';

const a11yProps = index => ({
  id: `full-width-tab-${index}`,
  'aria-controls': `full-width-tabpanel-${index}`
});

const TeamMemberReview = ({
  selfReview,
  reviews,
  memberProfile,
  reloadReviews
}) => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const currentUser = selectCurrentUser(state);
  const theme = useTheme();
  const [value, setValue] = useState(0);
  const [reassignOpen, setReassignOpen] = useState(false);
  const [cancelOpen, setCancelOpen] = useState(false);

  const handleOpenReassign = useCallback(
    () => setReassignOpen(true),
    [setReassignOpen]
  );
  const handleCloseReassign = useCallback(
    () => setReassignOpen(false),
    [setReassignOpen]
  );
  const handleOpenCancel = useCallback(
    () => setCancelOpen(true),
    [setCancelOpen]
  );
  const handleCloseCancel = useCallback(
    () => setCancelOpen(false),
    [setCancelOpen]
  );

  const review = reviews && reviews[value - 1];
  const recipient = selectProfile(state, review?.recipientId);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const handleChangeIndex = index => {
    setValue(index);
  };

  const handleCancelClick = useCallback(() => {
    const cancelRequest = async () => {
      const res = await cancelFeedbackRequest(review, csrf);
      const cancellationResponse =
        res && res.payload && res.payload.status === 200 && !res.error
          ? res.payload.data
          : null;
      if (!cancellationResponse) {
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast:
              'There was an error cancelling the review. Please contact your administrator.'
          }
        });
      }
      return cancellationResponse;
    };

    handleCloseCancel();
    if (csrf) {
      cancelRequest().then(res => {
        if (res) {
          reloadReviews();
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'success',
              toast: 'Review canceled'
            }
          });
        }
      });
    }
  }, [csrf, handleCloseCancel, review, reloadReviews]);

  const handleReassign = useCallback(
    assignee => {
      const reassignRequest = async () => {
        review.recipientId = assignee.id;
        const res = await updateFeedbackRequest(review, csrf);
        const updateResponse =
          res && res.payload && res.payload.status === 200 && !res.error
            ? res.payload.data
            : null;
        if (!updateResponse) {
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: 'error',
              toast:
                'There was an error reassigning the review. Please contact your administrator.'
            }
          });
        }
        return updateResponse;
      };

      handleCloseReassign();
      if (csrf) {
        reassignRequest().then(res => {
          if (res) {
            reloadReviews();
            window.snackDispatch({
              type: UPDATE_TOAST,
              payload: {
                severity: 'success',
                toast: 'Review reassigned'
              }
            });
          }
        });
      }
    },
    [csrf, handleCloseReassign, review, reloadReviews]
  );

  let selfReviewIcon = <HourglassEmptyIcon />;
  if (selfReview?.status.toUpperCase() === 'SUBMITTED') {
    selfReviewIcon = <CheckCircleIcon />;
  }

  return (
    <Root>
      <Box sx={{ bgcolor: 'background.paper', width: '100%' }}>
        <AppBar position="static">
          <Tabs
            value={value}
            onChange={handleChange}
            indicatorColor="secondary"
            textColor="inherit"
            variant="fullWidth"
          >
            <Tab
              icon={selfReviewIcon}
              label={
                memberProfile?.firstName
                  ? memberProfile?.firstName + "'s Self-Review"
                  : 'Self-Review'
              }
              {...a11yProps(0)}
            />
            {reviews &&
              reviews.map((review, index) => {
                const reviewer = selectProfile(state, review?.recipientId);
                let label = reviewer?.firstName + "'s Review";

                if (reviewer?.id === currentUser?.id) {
                  label = 'Your Review';
                }

                let icon = <HourglassEmptyIcon />;
                if (review?.status.toUpperCase() === 'SUBMITTED') {
                  icon = <CheckCircleIcon />;
                }

                return (
                  <Tab icon={icon} label={label} {...a11yProps(index + 1)} />
                );
              })}
          </Tabs>
        </AppBar>
        <SwipeableViews
          axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
          index={value}
          onChangeIndex={handleChangeIndex}
        >
          <TabPanel value={value} index={0} dir={theme.direction}>
            {selfReview && selfReview.id ? (
              <FeedbackSubmitForm
                requesteeName={
                  memberProfile?.firstName + ' ' + memberProfile?.lastName
                }
                requestId={selfReview?.id}
                request={selfReview}
                reviewOnly={true}
              />
            ) : (
              <Typography variant="h5">
                {memberProfile?.firstName} has not started their self-review.
              </Typography>
            )}
          </TabPanel>
          {reviews &&
            reviews.map((review, index) => {
              const reviewer = selectProfile(state, review?.recipientId);
              const requesteeName = memberProfile?.name;

              let readOnly = true;
              if (
                reviewer?.id === currentUser?.id &&
                'SUBMITTED' !== review?.status?.toUpperCase()
              ) {
                readOnly = false;
              }

              return (
                <TabPanel value={value} index={index + 1} dir={theme.direction}>
                  {review?.status.toUpperCase() !== 'SUBMITTED' && (
                    <div className={classes.buttonRow}>
                      <Button
                        onClick={handleOpenCancel}
                        className={classes.actionButtons}
                        variant="outlined"
                        color="secondary"
                      >
                        Cancel
                      </Button>
                      <Button
                        onClick={handleOpenReassign}
                        className={classes.actionButtons}
                        variant="outlined"
                        color="primary"
                      >
                        Reassign
                      </Button>
                    </div>
                  )}
                  <FeedbackSubmitForm
                    requesteeName={requesteeName}
                    requestId={review?.id}
                    request={review}
                    reviewOnly={readOnly}
                  />
                </TabPanel>
              );
            })}
        </SwipeableViews>
        <SelectUserModal
          userLabel="Reviewer"
          open={reassignOpen}
          onSelect={handleReassign}
          onClose={handleCloseReassign}
        />
        <Modal open={cancelOpen} onClose={handleCloseCancel}>
          <Card className="cancel-feedback-request-modal">
            <CardHeader
              title={
                <Typography variant="h5" fontWeight="bold">
                  Cancel Review
                </Typography>
              }
            />
            <CardContent>
              <Typography variant="body1">
                Are you sure you want to cancel the review sent to{' '}
                <b>{recipient?.name}</b> on <b>{review?.sendDate}</b>? The
                recipient will not be able to respond to this request once it is
                canceled.
              </Typography>
            </CardContent>
            <CardActions>
              <Button color="secondary" onClick={handleCloseReassign}>
                No, Keep Feedback Request
              </Button>
              <Button color="primary" onClick={handleCancelClick}>
                Yes, Cancel Feedback Request
              </Button>
            </CardActions>
          </Card>
        </Modal>
      </Box>
    </Root>
  );
};

TeamMemberReview.displayName = displayName;
TeamMemberReview.propTypes = propTypes;

export default TeamMemberReview;
