import React, { useContext, useEffect, useState } from 'react';

import { AppContext } from '../context/AppContext';
import {
  selectCheckinPDLS,
  selectTeamMembersWithCheckinPDL,
  selectMappedPdls
} from '../context/selectors';

import CheckinReport from '../components/reports-section/CheckinReport';
import MemberSelector from '../components/member_selector/MemberSelector';
import { FilterType } from '../components/member_selector/member_selector_dialog/MemberSelectorDialog';

import { Typography } from '@mui/material';

import './CheckinsReportEnhancedPage.css';

/**
 * @typedef {Object} PDL
 * @property {string} id
 * @property {string} name
 * @property {string} title
 */

/**
 * Sort PDLS by the last name extracted from the full name.
 * @param {PDL} a - First PDL object
 * @param {PDL} b - Second PDL object
 * @returns {number} - Comparison result for sorting
 */
function sortByLastName(a, b) {
  if (!a.name || !b.name) return;
  const lastNameA = a.name.split(' ').slice(-1)[0];
  const lastNameB = b.name.split(' ').slice(-1)[0];
  return lastNameA.localeCompare(lastNameB);
}

const CheckinsReportEnhancedPage = () => {
  const { state } = useContext(AppContext);
  const [mappedPDLs, setMappedPDLs] = useState(/** @type {PDL[]} */ ([]));
  const [selectedPdls, setSelectedPdls] = useState(/** @type {PDL[]} */ ([]));
  const [planned, setPlanned] = useState(false);
  const [closed, setClosed] = useState(false);

  /** @type {PDL[]} */
  const pdls = selectCheckinPDLS(state, closed, planned).sort(sortByLastName);

  // Set the selected PDLs to the mapped PDLs
  useEffect(() => {
    const mapped = selectMappedPdls(state);
    setSelectedPdls(mapped);
  }, [state]);

  // Set the mapped PDLs to the PDLs with members
  useEffect(() => {
    if (!pdls) return;
    pdls.forEach(
      pdl => (pdl.members = selectTeamMembersWithCheckinPDL(state, pdl.id))
    );
    const pdlsWithMembers = pdls.filter(pdl => pdl.members.length > 0);
    setMappedPDLs(pdlsWithMembers);
  }, [pdls, state]);

  // console.log({ selectedPdls });

  return (
    <div className="checkins-report-page">
      <MemberSelector
        initialFilters={[{ type: FilterType.ROLE, value: 'PDL' }]}
        title="Select PDLs"
        selected={selectedPdls}
        onChange={setSelectedPdls}
        listHeight={180}
        exportable
      />
      <Typography variant="h6">Assigned check-ins by selected PDL</Typography>
      {selectedPdls.length > 0 ? (
        selectedPdls.sort(sortByLastName).map(pdl => {
          return (
            <CheckinReport
              closed={closed}
              key={pdl.id}
              pdl={pdl}
              planned={planned}
            />
          );
        })
      ) : (
        <div className="checkins-report-page__no-data">
          <h2>No data to display</h2>
        </div>
      )}
    </div>
  );
};

export default CheckinsReportEnhancedPage;
