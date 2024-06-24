import { useContext, useState } from 'react';

import { AppContext } from '../../context/AppContext';
import { selectCsrfToken, selectRoles, selectUserProfile } from '../../context/selectors.js';

import { fetchDocumentsForRole, generate } from './GuidesPanel.jsx';
import './GuidesPanel.css';

const fallbackPdfs = [
  {
    id: '1',
    name: 'Development Discussion Guide for PDLs',
    url: '/pdfs/Development_Discussion_Guide_for_PDLs.pdf'
  },
  {
    id: '2',
    name: 'Expectations Discussion Guide for PDLs',
    url: '/pdfs/Expectations_Discussion_Guide_for_PDLs.pdf'
  },
  {
    id: '3',
    name: 'Feedback Discussion Guide for PDLs',
    url: '/pdfs/Feedback_Discussion_Guide_for_PDLs.pdf'
  }
];

const PDLGuidesPanel = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const isPdl = selectUserProfile(state)?.role?.includes('PDL');

  if (isPdl) {
    const [documents, setDocuments] = useState([]);
    fetchDocumentsForRole('PDL', selectRoles(state), csrf, setDocuments, fallbackPdfs, state.mockuments);
    return generate('Development Lead Guides', documents);
  } else {
    return null;
  }
};

export default PDLGuidesPanel;
