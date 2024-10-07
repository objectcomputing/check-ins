import React, { useContext, useEffect, useState, useRef } from 'react';

import { AppContext } from '../context/AppContext';
import {
  selectTerminatedMembersAsOfDateWithPDLRole,
  selectCheckinPDLS,
  selectMappedPdls,
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

import CheckinReport from '../components/reports-section/checkin-report/CheckinReport';
import MemberSelector from '../components/member_selector/MemberSelector';
import { FilterType } from '../components/member_selector/member_selector_dialog/MemberSelectorDialog';
import {
  getQuarterBeginEnd,
  useQueryParameters,
  isArrayPresent,
  getQuarterDisplay
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

/**
 * Sort PDLs by whether they have terminated members,
 * with PDLs having terminated members sorted last.
 * PDLs without a 'terminationDate' property are treated as not terminated.
 * @param {PDLProfile} a - First PDL object
 * @param {PDLProfile} b - Second PDL object
 * @returns {number} - Comparison result for sorting
 */
function sortByTerminated(a, b) {
  const terminatedA = a.terminationDate ? 1 : 0;
  const terminatedB = b.terminationDate ? 1 : 0;
  return terminatedA - terminatedB;
}

const CheckinsReportPage = () => {
  const { state } = useContext(AppContext);
  const [selectedPdls, setSelectedPdls] = useState(
    /** @type {PDLProfile[]} */ ([])
  );
  const [planned, setPlanned] = useState(true);
  const [closed, setClosed] = useState(true);

  const [searchText, setSearchText] = useState('');

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
          newPdls.length > 0 && setSelectedPdls(newPdls);
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

  // Update selected PDLs when processedQPs is updated
  useEffect(() => {
    if (selectedPdls.length === 0 && processedQPs.current) {
      setSelectedPdls(selectMappedPdls(state));
    }
  }, [processedQPs.current]);

  // Set the mapped PDLs to a full list of their members
  useEffect(() => {
    if (!pdls) return;
    pdls.forEach(pdl => {
      const allMembers = selectNormalizedMembers(state, searchText).filter(
        member => member.pdlId === pdl.id
      );
      pdl.members = allMembers;
    });
    pdls.filter(pdl => pdl.members.length > 0);
  }, [pdls, state]);

  // Include PDLs who have terminated within the quarter
  useEffect(() => {
    const pdlsTerminatedInQuarter = selectTerminatedMembersAsOfDateWithPDLRole(
      state,
      startOfQuarter
    );
    pdlsTerminatedInQuarter.forEach(pdl => {
      const allMembers = selectNormalizedMembers(state, searchText).filter(
        member => member.pdlId === pdl.id
      );
      pdl.members = allMembers;
    });
    pdlsTerminatedInQuarter.forEach(pdl => {
      if (!pdls.find(p => p.id === pdl.id)) {
        pdls.push(pdl);
      }
    });
  }, [state, startOfQuarter]);

  // Keyboard navigation for changing quarters.
  useEffect(() => {
    const handleKeyDown = evt => {
      if (evt.key === 'ArrowLeft') {
        document
          .querySelector('button[aria-label="Previous quarter`"]')
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
        {pdls.length > 0 ? (
          pdls
            .concat(selectedPdls.filter(pdl => !pdls.includes(pdl)))
            .sort(sortByLastName)
            .sort(sortByMembers)
            .sort(sortByTerminated)
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
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default CheckinsReportPage;
