import React, { useContext, useEffect, useState, useRef } from 'react';

import { AppContext } from '../context/AppContext';
import {
  selectNormalizedMembers,
  selectHasCheckinsReportPermission,
  noPermission,
} from '../context/selectors';

import {
  Grid,
  IconButton,
  Box,
  ButtonGroup,
  Tooltip,
  Typography
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

import TeamMemberMap from '../components/reports-section/checkin-report/TeamMemberMap';
import MemberSelector from '../components/member_selector/MemberSelector';
import { FilterType } from '../components/member_selector/member_selector_dialog/MemberSelectorDialog';
import {
  getQuarterBeginEnd,
  isArrayPresent,
  getQuarterDisplay
} from '../helpers';

import './CheckinsReportPage.css';

const CheckinsReportPage = () => {
  const { state } = useContext(AppContext);
  const [planned, setPlanned] = useState(true);
  const [closed, setClosed] = useState(true);
  const [searchText, setSearchText] = useState('');
  const [reportDate, setReportDate] = useState(new Date());
  const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(reportDate);

  const members = selectNormalizedMembers(state, searchText);

  // Set the report date to today less one month on first load
  useEffect(() => {
    setReportDate(new Date(new Date().setMonth(new Date().getMonth() - 1)));
  }, []);

  const handleQuarterClick = evt => {
    /** @type {HTMLButtonElement} */
    const button = evt.currentTarget;
    const isNextButton = button.attributes
                               .getNamedItem('aria-label')
                               .value.includes('Next');
    setReportDate(new Date(reportDate.setMonth(reportDate.getMonth() +
                                               (isNextButton ? 3 : -3))));
  };

  // Keyboard navigation for changing quarters.
  useEffect(() => {
    const handleKeyDown = evt => {
      if (evt.key === 'ArrowLeft') {
        document.querySelector('button[aria-label="Previous quarter`"]')
                .click();
      } else if (evt.key === 'ArrowRight') {
        document.querySelector('button[aria-label="Next quarter`"]').click();
      }
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, []);

  return selectHasCheckinsReportPermission(state) ? (
    <div className="checkins-report-page">
      <Box sx={{ display: 'flex', justifyContent: 'center' }}>
        <ButtonGroup variant="text" aria-label="Basic button group">
          <Tooltip title="Previous quarter">
            <IconButton
              aria-label="Previous quarter`"
              onClick={handleQuarterClick}
              size="large"
            >
              <ArrowBackIcon style={{ fontSize: '1.2em' }} />
            </IconButton>
          </Tooltip>
          <Typography
            variant="h6"
            sx={{ fontSize: '1.5rem', alignContent: 'center', p: 1 }}
          >
            <nobr>{getQuarterDisplay(reportDate)}</nobr>
          </Typography>
          <Tooltip title="Next quarter">
            <IconButton
              aria-label="Next quarter`"
              onClick={handleQuarterClick}
              size="large"
            >
              <ArrowForwardIcon style={{ fontSize: '1.2em' }} />
            </IconButton>
          </Tooltip>
        </ButtonGroup>
        <Grid container component="dl" className="checkins-report-page-dates">
          <Grid item>
            <Typography component="dt" variant="h6">
              Start of Quarter
            </Typography>
            <Typography component="dd" variant="body2">
              {startOfQuarter.toDateString()}
            </Typography>
          </Grid>
          <Grid item>
            <Typography component="dt" variant="h6">
              End of Quarter
            </Typography>
            <Typography component="dd" variant="body2">
              {endOfQuarter.toDateString()}
            </Typography>
          </Grid>
        </Grid>
      </Box>
      <div className="checkins-report-page-reports">
        {members
          ? <TeamMemberMap
              members={members}
              closed={closed}
              planned={planned}
              reportDate={reportDate}
            />
          : <></>
        }
      </div>
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default CheckinsReportPage;
