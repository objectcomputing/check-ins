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

  const review = reviews && reviews[value];
  const recipient = selectProfile(state, review?.recipientId);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const handleChangeIndex = index => {
    setValue(index);
  };

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
            {reviews &&
              reviews.filter(r => !!r).map((review, index) => {
                const reviewer = review.recipientId == memberProfile?.id ?
                                 memberProfile :
                                 selectProfile(state, review.recipientId);
                let label = reviewer?.firstName + "'s Review";
                if (reviewer?.id === currentUser?.id) {
                  label = 'Your Review';
                }

                let icon = <HourglassEmptyIcon />;
                if (review.status.toUpperCase() === 'SUBMITTED') {
                  icon = <CheckCircleIcon />;
                }

                return (
                  <Tab key={index}
                       icon={icon}
                       label={label}
                       {...a11yProps(index)} />
                );
              })}
          </Tabs>
        </AppBar>
        <SwipeableViews
          axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
          index={value}
          onChangeIndex={handleChangeIndex}
        >
          {reviews &&
            reviews.filter(r => !!r).map((review, index) => {
              const reviewer = selectProfile(state, review.recipientId);
              const requestee = selectProfile(state, review.requesteeId);
              const requesteeName = requestee?.name;
              const readOnly = (reviewer?.id !== currentUser?.id ||
                                review.status?.toUpperCase() === 'SUBMITTED');

              return (
                <TabPanel key={index}
                          value={value}
                          index={index}
                          dir={theme.direction}>
                  <FeedbackSubmitForm
                    requesteeName={requesteeName}
                    requestId={review.id}
                    request={review}
                    reviewOnly={readOnly}
                  />
                </TabPanel>
              );
            })}
        </SwipeableViews>
      </Box>
    </Root>
  );
};

TeamMemberReview.displayName = displayName;
TeamMemberReview.propTypes = propTypes;

export default TeamMemberReview;
