import { format } from 'date-fns';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { Delete, Edit } from '@mui/icons-material';
import { Box, IconButton, Modal, Tooltip, Typography } from '@mui/material';

import { AppContext } from '../context/AppContext';
import { selectProfileMap } from '../context/selectors';
import ConfirmationDialog from '../components/dialogs/ConfirmationDialog';
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
  const [selectedCertificate, setSelectedCertificate] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);

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
        <td onClick={() => selectCertificate(cert)}>
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

  const editCertification = cert => {};

  const selectCertificate = cert => {
    setSelectedCertificate(cert);
    setModalOpen(true);
  };

  return (
    <div id="certification-report-page">
      <table>
        <thead>
          <tr>
            <th>Member</th>
            <th>Name</th>
            <th>Description</th>
            <th>Date</th>
            <th>Image</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>{certifications.map(certificationRow)}</tbody>
      </table>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
        <Box sx={modalStyle}>
          <Typography id="modal-modal-title" variant="h6" component="h2">
            Certificate Image
          </Typography>
          {selectedCertificate?.imageUrl && (
            <img src={selectedCertificate.imageUrl} style={{ width: '100%' }} />
          )}
        </Box>
      </Modal>

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteCertification(selectedCertificate)}
        question="Are you sure you want to delete this certification?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />
    </div>
  );
};

export default CertificationReportPage;
