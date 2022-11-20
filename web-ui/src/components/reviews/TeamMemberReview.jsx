import React, {useContext, useCallback, useEffect, useState} from "react";
import SwipeableViews from 'react-swipeable-views';
import PropTypes from 'prop-types';
import {AppContext} from "../../context/AppContext";
import {selectCurrentUser, selectProfile} from "../../context/selectors";
import { useTheme } from '@mui/material/styles';
import AppBar from '@mui/material/AppBar';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import FeedbackSubmitForm from "../feedback_submit_form/FeedbackSubmitForm";

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
}

TabPanel.propTypes = {
  children: PropTypes.any,
  dir: PropTypes.string,
  index: PropTypes.number,
  value: PropTypes.number,
}
TabPanel.displayName = "TabPanel";

const a11yProps = (index) => ({
  id: `full-width-tab-${index}`,
  'aria-controls': `full-width-tabpanel-${index}`,
});

const TeamMemberReview = ({selfReview, reviews, memberProfile}) => {
  const {state} = useContext(AppContext);
  const currentUser = selectCurrentUser(state);
  const theme = useTheme();
  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const handleChangeIndex = (index) => {
    setValue(index);
  };

  return (
    <Box sx={{ bgcolor: 'background.paper', width: "100%" }}>
      <AppBar position="static">
        <Tabs
          value={value}
          onChange={handleChange}
          indicatorColor="secondary"
          textColor="inherit"
          variant="fullWidth"
        >
          <Tab label={memberProfile?.firstName ? memberProfile?.firstName + "'s Self-Review" : "Self-Review"} {...a11yProps(0)} />
          {
            reviews && reviews.map((review, index) => {
              const reviewer = selectProfile(state, review?.recipientId);
              let label = reviewer?.firstName + "'s Review";

              if(reviewer?.id === currentUser?.id) {
                label = "Your Review";
              }

              return (<Tab label={label} {...a11yProps(index+1)} />);
            })
          }
        </Tabs>
      </AppBar>
      <SwipeableViews
        axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
        index={value}
        onChangeIndex={handleChangeIndex}
      >
        <TabPanel value={value} index={0} dir={theme.direction}>
          {(selfReview && selfReview.id) ? (
            <FeedbackSubmitForm requesteeName={memberProfile?.firstName+" "+memberProfile?.lastName} requestId={selfReview?.id} request={selfReview} reviewOnly={true} />
          ) : (
            <Typography variant="h5">{memberProfile?.firstName} has not started their self-review.</Typography>
          )}
        </TabPanel>
        {
          reviews && reviews.map((review, index) => {
            const reviewer = selectProfile(state, review?.recipientId);
            const requesteeName = memberProfile?.name;

            let readOnly = true;
            if(reviewer?.id === currentUser?.id && "SUBMITTED" !== review?.status?.toUpperCase()) {
              readOnly = false;
            }

            return (
              <TabPanel value={value} index={index+1} dir={theme.direction}>
                <FeedbackSubmitForm requesteeName={requesteeName} requestId={review?.id} request={review} reviewOnly={readOnly} />
              </TabPanel>
            );
          })
        }
      </SwipeableViews>
    </Box>
  );
}

export default TeamMemberReview;