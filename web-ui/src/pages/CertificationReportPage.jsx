import { format } from 'date-fns';
//TODO: Use useCallback to prevent recreating functions?
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { AddCircleOutline, Delete, Edit } from '@mui/icons-material';
import {
  Autocomplete,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Tooltip
} from '@mui/material';

import ConfirmationDialog from '../components/dialogs/ConfirmationDialog';
import { AppContext } from '../context/AppContext';
import { selectProfileMap } from '../context/selectors';
import DatePickerField from '../components/date-picker-field/DatePickerField';
import './CertificationReportPage.css';

const certificationBaseUrl = 'http://localhost:3000/certification';
const earnedCertificationBaseUrl = 'http://localhost:3000/earned-certification';

const formatDate = date =>
  date instanceof Date
    ? format(date, 'yyyy-MM-dd')
    : `${date.$y}-${date.$M + 1}-${date.$D}`;

const newEarned = { date: formatDate(new Date()) };
const tableColumns = ['Member', 'Name', 'Description', 'Date Earned', 'Image'];

const CertificationReportPage = () => {
  const { state } = useContext(AppContext);
  const [certifications, setCertifications] = useState([]);
  const [certificationMap, setCertificationMap] = useState({});
  const [earnedCertifications, setEarnedCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [imageDialogOpen, setImageDialogOpen] = useState(false);
  const [selectedCertification, setSelectedCertification] = useState(null);
  const [selectedEarned, setSelectedEarned] = useState(newEarned);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [sortColumn, setSortColumn] = useState('Member');
  const [sortAscending, setSortAscending] = useState(true);

  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const loadCertifications = async () => {
    try {
      let res = await fetch(certificationBaseUrl);
      const certs = await res.json();
      setCertifications(certs.sort((c1, c2) => c1.name.localeCompare(c2.name)));

      const certMap = certs.reduce((map, cert) => {
        map[cert.id] = cert;
        return map;
      }, {});
      setCertificationMap(certMap);

      res = await fetch(earnedCertificationBaseUrl);
      const earned = await res.json();
      sortEarnedCertifications(earned);
      setEarnedCertifications(earned);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (profileMap) loadCertifications();
  }, [profileMap]);

  useEffect(() => {
    if (!profileMap) return;
    sortEarnedCertifications(earnedCertifications);
    setEarnedCertifications([...earnedCertifications]);
  }, [profileMap, sortAscending, sortColumn]);

  const addEarnedCertification = () => {
    setSelectedEarned(newEarned);
    setDialogOpen(true);
  };

  const certValue = cert => {
    switch (sortColumn) {
      case 'Date Earned':
        return cert.date;
      case 'Description':
        return cert.description;
      case 'Image':
        return cert.imageUrl || '';
      case 'Member':
        const profile = profileMap[cert.memberId];
        return profile?.name ?? '';
      case 'Name':
        return cert.name;
    }
  };

  const confirmDelete = earned => {
    setSelectedEarned(earned);
    setConfirmDeleteOpen(true);
  };

  const earnedCertificationRow = earned => {
    const profile = profileMap[earned.memberId];
    return (
      <tr key={earned.id}>
        <td>{profile?.name ?? 'unknown'}</td>
        <td>{certificationMap[earned.certificationId]?.name ?? 'unknown'}</td>
        <td>{earned.description}</td>
        <td>{formatDate(new Date(earned.date))}</td>
        <td onClick={() => selectImage(earned)}>
          <img src={earned.imageUrl} />
        </td>
        <td>
          <Tooltip title="Edit">
            <IconButton
              aria-label="Edit"
              onClick={() => editEarnedCertification(earned)}
            >
              <Edit />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              aria-label="Delete"
              onClick={() => confirmDelete(earned)}
            >
              <Delete />
            </IconButton>
          </Tooltip>
        </td>
      </tr>
    );
  };

  const deleteEarnedCertification = async cert => {
    const url = earnedCertificationBaseUrl + '/' + cert.id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      setEarnedCertifications(earned => earned.filter(c => c.id !== cert.id));
    } catch (err) {
      console.error(err);
    }
  };

  const editEarnedCertification = earned => {
    setSelectedEarned(earned);
    setSelectedProfile(profileMap[earned.memberId]);
    setDialogOpen(true);
  };

  const selectImage = earned => {
    if (!earned.imageUrl) return;
    setSelectedEarned(earned);
    setImageDialogOpen(true);
  };

  const saveEarnedCertification = async () => {
    const { id } = selectedEarned;
    const url = id
      ? `${earnedCertificationBaseUrl}/${id}`
      : earnedCertificationBaseUrl;
    selectedEarned.memberId = selectedProfile.id;
    selectedEarned.certificationId = selectedCertification.id;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedEarned)
      });
      const newEarned = await res.json();
      setEarnedCertifications(earned => {
        if (id) {
          const index = earned.findIndex(c => c.id === id);
          earned[index] = newEarned;
        } else {
          earned.push(newEarned);
        }
        return [...earned];
      });
    } catch (err) {
      console.error(err);
    }
    setDialogOpen(false);
  };

  const sortEarnedCertifications = earned => {
    earned.sort((e1, e2) => {
      const v1 = certValue(e1);
      const v2 = certValue(e2);
      const compare = sortAscending
        ? v1.localeCompare(v2)
        : v2.localeCompare(v1);
      // console.log('v1 =', v1, 'v2 =', v2, 'compare =', compare);
      return compare;
    });
  };

  const sortIndicator = column => {
    if (column !== sortColumn) return '';
    return ' ' + (sortAscending ? '🔼' : '🔽');
  };

  const sortTable = column => {
    if (column === sortColumn) {
      setSortAscending(ascending => !ascending);
    } else {
      setSortColumn(column);
      setSortAscending(true);
    }
  };

  return (
    <div id="certification-report-page">
      <div className="column">
        <IconButton
          aria-label="Add"
          classes={{ root: 'add-button' }}
          onClick={addEarnedCertification}
        >
          <AddCircleOutline />
        </IconButton>
        <table>
          <thead>
            <tr>
              {tableColumns.map(column => (
                <th
                  key={column}
                  onClick={() => sortTable(column)}
                  style={{ cursor: 'pointer' }}
                >
                  {column}
                  {sortIndicator(column)}
                </th>
              ))}
              <th key="Actions">Actions</th>
            </tr>
          </thead>
          <tbody>{earnedCertifications.map(earnedCertificationRow)}</tbody>
        </table>
      </div>

      <Dialog open={imageDialogOpen} onClose={() => setImageDialogOpen(false)}>
        <DialogTitle>Certification Image</DialogTitle>
        <DialogContent>
          {selectedEarned?.imageUrl && (
            <img src={selectedEarned.imageUrl} style={{ width: '100%' }} />
          )}
        </DialogContent>
      </Dialog>

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteEarnedCertification(selectedEarned)}
        question="Are you sure you want to delete this certification?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />

      <Dialog
        classes={{ root: 'certification-report-dialog' }}
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
      >
        <DialogTitle>
          {selectedEarned.id ? 'Edit' : 'Add'} Certification
        </DialogTitle>
        <DialogContent>
          <Autocomplete
            getOptionLabel={profile => profile.name || ''}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, profile) => {
              setSelectedProfile({ ...profile });
            }}
            options={profiles}
            renderInput={params => (
              <TextField
                {...params}
                className="fullWidth"
                label="Team Member"
              />
            )}
            value={selectedProfile}
          />
          <Autocomplete
            getOptionLabel={cert => cert?.name || 'unknown'}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            onChange={(event, cert) => {
              setSelectedCertification({ ...cert });
            }}
            options={certifications}
            renderInput={params => (
              <TextField
                {...params}
                className="fullWidth"
                label="Certification Name"
              />
            )}
            value={selectedCertification}
          />
          <TextField
            className="fullWidth"
            label="Description"
            placeholder="Description"
            required
            onChange={e =>
              setSelectedEarned({
                ...selectedEarned,
                description: e.target.value
              })
            }
            value={selectedEarned?.description ?? ''}
          />
          <DatePickerField
            date={new Date(selectedEarned.date)}
            label="Date Earned"
            setDate={date =>
              setSelectedEarned({
                ...selectedEarned,
                date: formatDate(date)
              })
            }
          />
          <TextField
            className="fullWidth"
            label="Image URL"
            placeholder="Image URL"
            onChange={e =>
              setSelectedEarned({
                ...selectedEarned,
                imageUrl: e.target.value
              })
            }
            value={selectedEarned?.imageUrl ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={saveEarnedCertification}>Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default CertificationReportPage;
