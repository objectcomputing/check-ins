import React, { useContext, useEffect, useRef, useState } from 'react';

import { TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';

import { AppContext } from '../context/AppContext';
import CheckinReport from '../components/reports-section/CheckinReport';
import {
  selectCheckinPDLS,
  selectTeamMembersWithCheckinPDL
} from '../context/selectors';
import { isArrayPresent } from '../helpers/checks';
import { useQueryParameters } from '../helpers/query-parameters';

import './CheckinsReportPage.css';

const CheckinsReportPage = () => {
  const { state } = useContext(AppContext);
  const [selectedPdls, setSelectedPdls] = useState([]);
  const [planned, setPlanned] = useState(false);
  const [closed, setClosed] = useState(false);
  const [searchText, setSearchText] = useState('');

  const pdls = selectCheckinPDLS(state, closed, planned).sort((a, b) => {
    const aPieces = a.name.split(' ').slice(-1);
    const bPieces = b.name.split(' ').slice(-1);
    return aPieces.toString().localeCompare(bPieces);
  });

  const [filteredPdls, setFilteredPdls] = useState(pdls);

  const processedQPs = useRef(false);
  useQueryParameters(
    [
      {
        name: 'pdls',
        default: [],
        value: selectedPdls,
        setter(ids) {
          const newPdls = ids.map(id => pdls.find(pdl => pdl.id === id));
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

  useEffect(() => {
    if (!pdls) return;
    pdls.map(
      pdl => (pdl.members = selectTeamMembersWithCheckinPDL(state, pdl.id))
    );
    let newPdlList = pdls.filter(pdl => {
      pdl.members =
        pdl.members &&
        pdl.members.filter(member =>
          member?.name?.toLowerCase().includes(searchText.toLowerCase())
        );
      return pdl.members.length > 0;
    });

    setFilteredPdls(newPdlList);
  }, [pdls, searchText, state]);

  const onPdlChange = (event, newValue) => {
    let extantPdls = filteredPdls || [];
    newValue.forEach(val => {
      extantPdls = extantPdls.filter(pdl => pdl.id !== val.id);
    });
    extantPdls = [...new Set(extantPdls)];
    newValue = [...new Set(newValue)];
    if (newValue.length > 0) {
      setSelectedPdls(newValue);
      setFilteredPdls([...newValue]);
    } else {
      setSelectedPdls([]);
      setFilteredPdls(pdls);
    }
  };

  const handleClosed = () => {
    setClosed(!closed);
  };

  const handlePlanned = () => {
    setPlanned(!planned);
  };

  return (
    <div>
      <div className="filter-pdls-and-members">
        <Autocomplete
          id="pdlSelect"
          multiple
          options={pdls}
          value={selectedPdls || []}
          onChange={onPdlChange}
          getOptionLabel={option => option.name}
          renderInput={params => (
            <TextField
              {...params}
              label="Select PDLs..."
              placeholder="Choose which PDLs to display"
            />
          )}
        />
        <TextField
          label="Select employees..."
          placeholder="Member Name"
          value={searchText}
          onChange={e => {
            setSearchText(e.target.value);
          }}
        />
      </div>
      <div className="checkbox-row">
        <label htmlFor="closed">Include closed</label>
        <input id="closed" onClick={handleClosed} type="checkbox" />
        <label htmlFor="planned">Include planned</label>
        <input id="planned" onClick={handlePlanned} type="checkbox" />
      </div>
      {selectedPdls.length
        ? selectedPdls.map(pdl => (
            <CheckinReport
              closed={closed}
              key={pdl.id}
              pdl={pdl}
              planned={planned}
            />
          ))
        : filteredPdls.map(pdl => (
            <CheckinReport
              closed={closed}
              key={pdl.id}
              pdl={pdl}
              planned={planned}
            />
          ))}
    </div>
  );
};

export default CheckinsReportPage;
