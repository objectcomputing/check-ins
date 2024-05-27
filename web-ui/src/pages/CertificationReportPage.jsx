import { format } from 'date-fns';
import React, { useContext, useEffect, useState } from 'react';
import { Box, Modal, Typography } from '@mui/material';

import { AppContext } from '../context/AppContext';
import { selectProfileMap } from '../context/selectors';
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

const CertificationReportPage = () => {
  const { state } = useContext(AppContext);
  const [certifications, setCertifications] = useState([]);
  const [selectedCertificate, setSelectedCertificate] = useState(null);
  console.log('CertificationReportPage.jsx : certifications =', certifications);
  const [modalOpen, setModalOpen] = useState(false);

  const loadCertifications = async () => {
    const url = 'http://localhost:3000/certification';
    try {
      const res = await fetch(url);
      const data = await res.json();
      console.log(
        'CertificationReportPage.jsx loadCertifications: data =',
        data
      );
      setCertifications(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadCertifications();
  }, []);

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
      </tr>
    );
  };

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
    </div>
  );
};

export default CertificationReportPage;
