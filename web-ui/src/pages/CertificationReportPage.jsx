import { format } from 'date-fns';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { Delete, Edit } from '@mui/icons-material';
import {
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

import { AppContext } from '../context/AppContext';
import { selectProfileMap } from '../context/selectors';
import ConfirmationDialog from '../components/dialogs/ConfirmationDialog';
import DatePickerField from '../components/date-picker-field/DatePickerField';
import './CertificationReportPage.css';

const modalWidth = 600;

const center = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)'
};

const modalStyle = {
  bgcolor: 'background.paper',
  border: '2px solid black',
  boxShadow: 24,
  padding: '1rem',
  width: modalWidth,
  ...center
};

const endpointBaseUrl = 'http://localhost:3000/certification';

const CertificationReportPage = () => {
  const { state } = useContext(AppContext);
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [imageDialogOpen, setImageDialogOpen] = useState(false);
  const [selectedCertificate, setSelectedCertificate] = useState(null);

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

  const confirmDelete = cert => {
    setSelectedCertificate(cert);
    setConfirmDeleteOpen(true);
  };

  const certificationRow = cert => {
    const profile = selectProfileMap(state)[cert.memberId];
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
    setSelectedCertificate(cert);
    setDialogOpen(true);
  };

  const selectImage = cert => {
    setSelectedCertificate(cert);
    setImageDialogOpen(true);
  };

  const updateCertification = cert => {
    console.log(
      'CertificationReportPage.jsx updateCertification: cert =',
      cert
    );
    setDialogOpen(false);
  };

  return (
    <div id="certification-report-page">
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

      <Dialog open={imageDialogOpen} onClose={() => setImageDialogOpen(false)}>
        <DialogTitle>Certification Image</DialogTitle>
        <DialogContent>
          {selectedCertificate?.imageUrl && (
            <img src={selectedCertificate.imageUrl} style={{ width: '100%' }} />
          )}
        </DialogContent>
      </Dialog>

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteCertification(selectedCertificate)}
        question="Are you sure you want to delete this certification?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
        <DialogTitle>Edit Certification</DialogTitle>
        <DialogContent
          sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: '1rem'
          }}
        >
          <TextField
            className="fullWidth"
            label="Name*"
            placeholder="Certificate Name"
            required
            onChange={e => (selectedCertificate.name = e.target.value)}
            value={selectedCertificate?.name ?? ''}
          />
          <TextField
            className="fullWidth"
            label="Description"
            placeholder="Description"
            required
            onChange={e => (selectedCertificate.description = e.target.value)}
            value={selectedCertificate?.description ?? ''}
          />
          <DatePickerField
            date={new Date(selectedCertificate?.date ?? null)}
            setDate={date => (certification.date = date)}
            label="Date Earned"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={updateCertification}>Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default CertificationReportPage;
