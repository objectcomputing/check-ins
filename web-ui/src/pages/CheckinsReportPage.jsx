import React, { useContext, useEffect, useState, useRef } from 'react';

import { AppContext } from '../context/AppContext';
import {
  selectCheckinPDLS,
  selectTeamMembersWithCheckinPDL,
  selectMappedPdls
} from '../context/selectors';

import {
  Grid,
  Typography,
  IconButton,
  Box,
  ButtonGroup,
  Tooltip
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

import CheckinReport from '../components/reports-section/checkin-report/CheckinReport';
import MemberSelector from '../components/member_selector/MemberSelector';
import { FilterType } from '../components/member_selector/member_selector_dialog/MemberSelectorDialog';
import {
  getQuarterBeginEnd,
  useQueryParameters,
  isArrayPresent
} from '../helpers';

import './CheckinsReportPage.css';

/**
 * Sort Members by the last name extracted from the full name.
 * @param {MemberProfile} a - First Member object
 * @param {MemberProfile} b - Second Member object
 * @returns {number} - Comparison result for sorting
 */
function sortByLastName(a, b) {
  if (!a.name || !b.name) return 0;
  const lastNameA = a.name.split(' ').slice(-1)[0];
  const lastNameB = b.name.split(' ').slice(-1)[0];
  return lastNameA.localeCompare(lastNameB);
}

/**
 * Sort PDLs by the number of members they have (if available),
 * with PDLs having more members sorted first.
 * PDLs without a 'members' property are treated as having 0 members.
 * @param {PDLProfile} a - First PDL object
 * @param {PDLProfile} b - Second PDL object
 * @returns {number} - Comparison result for sorting
 */
function sortByMembers(a, b) {
  const membersA = a.members ? a.members.length : 0;
  const membersB = b.members ? b.members.length : 0;
  return membersB - membersA;
}

const CheckinsReportPage = () => {
  const { state } = useContext(AppContext);
  const [selectedPdls, setSelectedPdls] = useState(
    /** @type {PDLProfile[]} */ ([])
  );
  const [planned, setPlanned] = useState(true);
  const [closed, setClosed] = useState(true);

  const [reportDate, setReportDate] = useState(new Date());
  const { startOfQuarter, endOfQuarter } = getQuarterBeginEnd(reportDate);

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
    isNextButton
      ? setReportDate(new Date(reportDate.setMonth(reportDate.getMonth() + 3)))
      : setReportDate(new Date(reportDate.setMonth(reportDate.getMonth() - 3)));
  };

  /** @type {PDLProfile[]} */
  const pdls = selectCheckinPDLS(state, closed, planned);

  const processedQPs = useRef(false);
  useQueryParameters(
    [
      {
        name: 'pdls',
        default: [],
        value: selectedPdls,
        setter(ids) {
          const newPdls = ids
            .map(id => pdls.find(pdl => pdl.id === id))
            .filter(Boolean);
          setSelectedPdls(newPdls);
        },
        toQP(newPdls) {
          if (isArrayPresent(newPdls)) {
            const ids = newPdls.map(pdl => pdl.id);
            return ids.join(',');
          } else {
            return [];
          }
        }
      }
    ],
    [pdls],
    processedQPs
  );

  // Set the selected PDLs to the mapped PDLs unless they are already set
  useEffect(() => {
    if (pdls.length > 0) return;
    const mapped = selectMappedPdls(state);
    setSelectedPdls(mapped);
  }, [state]);

  // Set the mapped PDLs to the PDLs with members
  useEffect(() => {
    if (!pdls) return;
    pdls.forEach(
      pdl => (pdl.members = selectTeamMembersWithCheckinPDL(state, pdl.id))
    );
    pdls.filter(pdl => pdl.members.length > 0);
  }, [pdls, state]);

  return (
    <div className="checkins-report-page">
      <MemberSelector
        initialFilters={[{ type: FilterType.ROLE, value: 'PDL' }]}
        title="Select PDLs"
        selected={selectedPdls}
        onChange={setSelectedPdls}
        exportable
        expand={false}
      />
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
        {selectedPdls.length > 0 ? (
          selectedPdls
            .sort(sortByLastName)
            .sort(sortByMembers)
            .map(pdl => {
              return (
                <CheckinReport
                  closed={closed}
                  key={pdl.id}
                  pdl={pdl}
                  planned={planned}
                  reportDate={reportDate}
                />
              );
            })
        ) : (
          <div className="checkins-report-page-no-data">
            <h2>No PDLs selected</h2>
            <Typography variant="body1">
              Please select some PDLs using the Member Selector.
            </Typography>
          </div>
        )}
      </div>
    </div>
  );
};

export default CheckinsReportPage;
