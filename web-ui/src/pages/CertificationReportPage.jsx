import { format } from 'date-fns';
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
  Tooltip,
  Typography
} from '@mui/material';

import ConfirmationDialog from '../components/dialogs/ConfirmationDialog';
import MemberSelector from '../components/member_selector/MemberSelector';
import { AppContext } from '../context/AppContext';
import { selectProfileMap } from '../context/selectors';
import DatePickerField from '../components/date-picker-field/DatePickerField';
import './CertificationReportPage.css';

const modalWidth = 600;

const center = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)'
};

const endpointBaseUrl = 'http://localhost:3000/certification';

const formatDate = date =>
  date instanceof Date
    ? format(date, 'yyyy-MM-dd')
    : `${date.$y}-${date.$M + 1}-${date.$D}`;

const newCertification = { date: formatDate(new Date()) };
const tableColumns = ['Member', 'Name', 'Description', 'Date Earned', 'Image'];

const CertificationReportPage = () => {
  const { state } = useContext(AppContext);
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [imageDialogOpen, setImageDialogOpen] = useState(false);
  const [selectedCertification, setSelectedCertification] =
    useState(newCertification);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [sortColumn, setSortColumn] = useState('Member');
  const [sortAscending, setSortAscending] = useState(true);

  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const loadCertifications = async () => {
    try {
      const res = await fetch(endpointBaseUrl);
      const certifications = await res.json();
      sortCertifications(certifications);
      setCertifications(certifications);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (profileMap) loadCertifications();
  }, [profileMap]);

  useEffect(() => {
    if (!profileMap) return;
    sortCertifications(certifications);
    setCertifications([...certifications]);
  }, [profileMap, sortAscending, sortColumn]);

  const addCertification = () => {
    setSelectedCertification(newCertification);
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

  const confirmDelete = cert => {
    setSelectedCertification(cert);
    setConfirmDeleteOpen(true);
  };

  const certificationRow = cert => {
    const profile = profileMap[cert.memberId];
    return (
      <tr key={cert.id}>
        <td>{profile?.name ?? 'unknown'}</td>
        <td>{cert.name}</td>
        <td>{cert.description}</td>
        <td>{formatDate(new Date(cert.date))}</td>
        <td onClick={() => selectImage(cert)}>
          <img src={cert.imageUrl} />
        </td>
        <td>
          <Tooltip title="Edit">
            <IconButton
              aria-label="Edit"
              onClick={() => editCertification(cert)}
            >
              <Edit />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton aria-label="Delete" onClick={() => confirmDelete(cert)}>
              <Delete />
            </IconButton>
          </Tooltip>
        </td>
      </tr>
    );
  };

  const deleteCertification = async cert => {
    const url = endpointBaseUrl + '/' + cert.id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      setCertifications(certifications =>
        certifications.filter(c => c.id !== cert.id)
      );
    } catch (err) {
      console.error(err);
    }
  };

  const editCertification = cert => {
    setSelectedCertification(cert);
    setSelectedProfile(profileMap[cert.memberId]);
    setDialogOpen(true);
  };

  const selectImage = cert => {
    if (!cert.imageUrl) return;
    setSelectedCertification(cert);
    setImageDialogOpen(true);
  };

  const saveCertification = async () => {
    const { id } = selectedCertification;
    const url = id ? `${endpointBaseUrl}/${id}` : endpointBaseUrl;
    selectedCertification.memberId = selectedProfile.id;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedCertification)
      });
      const newCertification = await res.json();
      setCertifications(certifications => {
        if (id) {
          const index = certifications.findIndex(c => c.id === id);
          certifications[index] = newCertification;
        } else {
          certifications.push(newCertification);
        }
        return [...certifications];
      });
    } catch (err) {
      console.error(err);
    }
    setDialogOpen(false);
  };

  const sortCertifications = certifications => {
    certifications.sort((c1, c2) => {
      const v1 = certValue(c1);
      const v2 = certValue(c2);
      const compare = sortAscending
        ? v1.localeCompare(v2)
        : v2.localeCompare(v1);
      // console.log('v1 =', v1, 'v2 =', v2, 'compare =', compare);
      return compare;
    });
    // console.log('sortCertifications: certifications =', certifications);
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
          onClick={addCertification}
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
          <tbody>{certifications.map(certificationRow)}</tbody>
        </table>
      </div>

      <Dialog open={imageDialogOpen} onClose={() => setImageDialogOpen(false)}>
        <DialogTitle>Certification Image</DialogTitle>
        <DialogContent>
          {selectedCertification?.imageUrl && (
            <img
              src={selectedCertification.imageUrl}
              style={{ width: '100%' }}
            />
          )}
        </DialogContent>
      </Dialog>

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteCertification(selectedCertification)}
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
          {selectedCertification.id ? 'Edit' : 'Add'} Certification
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
          <TextField
            className="fullWidth"
            label="Name"
            placeholder="Certification Name"
            required
            onChange={e =>
              setSelectedCertification({
                ...selectedCertification,
                name: e.target.value
              })
            }
            value={selectedCertification?.name ?? ''}
          />
          <TextField
            className="fullWidth"
            label="Description"
            placeholder="Description"
            required
            onChange={e =>
              setSelectedCertification({
                ...selectedCertification,
                description: e.target.value
              })
            }
            value={selectedCertification?.description ?? ''}
          />
          <DatePickerField
            date={new Date(selectedCertification.date)}
            label="Date Earned"
            setDate={date =>
              setSelectedCertification({
                ...selectedCertification,
                date: formatDate(date)
              })
            }
          />
          <TextField
            className="fullWidth"
            label="Image URL"
            placeholder="Image URL"
            onChange={e =>
              setSelectedCertification({
                ...selectedCertification,
                imageUrl: e.target.value
              })
            }
            value={selectedCertification?.imageUrl ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={saveCertification}>Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default CertificationReportPage;
