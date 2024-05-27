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

const newCertification = { date: format(new Date(), 'yyyy-MM-dd') };

const CertificationReportPage = () => {
  const { state } = useContext(AppContext);
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [imageDialogOpen, setImageDialogOpen] = useState(false);
  const [selectedCertification, setSelectedCertification] =
    useState(newCertification);
  const [selectedProfile, setSelectedProfile] = useState(null);

  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const loadCertifications = async () => {
    try {
      const res = await fetch(endpointBaseUrl);
      const data = await res.json();
      setCertifications(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadCertifications();
  }, []);

  const addCertification = () => {
    setSelectedCertification(newCertification);
    setDialogOpen(true);
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
        <td>{format(new Date(cert.date), 'yyyy-MM-dd')}</td>
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
              <th>Member</th>
              <th>Name</th>
              <th>Description</th>
              <th>Date Earned</th>
              <th>Image</th>
              <th>Actions</th>
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
        <DialogTitle>Edit Certification</DialogTitle>
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
            date={new Date(selectedCertification?.date ?? null)}
            label="Date Earned"
            setDate={date =>
              setSelectedCertification({
                ...selectedCertification,
                date
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
