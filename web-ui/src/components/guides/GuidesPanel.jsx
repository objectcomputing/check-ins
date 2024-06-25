import React, { useContext, useEffect, useState } from 'react';
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import { Card, CardHeader, List } from '@mui/material';

import { AppContext } from '../../context/AppContext.jsx';
import { selectCsrfToken, selectRoles } from '../../context/selectors.js';
import { getDocumentsForRoleId } from '../../api/document.js';

import GuideLink from './GuideLink';
import './GuidesPanel.css';

const fallbackPdfs = [
  {
    id: '1-member',
    name: 'Expectations Discussion Guide for Team Members',
    url: '/pdfs/Expectations_Discussion_Guide_for_Team_Members.pdf'
  },
  {
    id: '2-member',
    name: 'Expectations Worksheet',
    url: '/pdfs/Expectations_Worksheet.pdf'
  },
  {
    id: '3-member',
    name: 'Feedback Discussion Guide for Team Members',
    url: '/pdfs/Feedback_Discussion_Guide_for_Team_Members.pdf'
  },
  {
    id: '4-member',
    name: 'Development Discussion Guide for Team Members',
    url: '/pdfs/Development_Discussion_Guide_for_Team_Members.pdf'
  },
  {
    id: '5-member',
    name: 'Individual Development Plan',
    url: '/pdfs/Individual_Development_Plan.pdf'
  }
];

export const fetchDocumentsForRole = (roleName, allRoles, csrf, setDocuments, fallback, mockuments) => {
  useEffect(() => {
    async function getDocuments() {
      if (mockuments && mockuments.length > 0) {
        return mockuments;
      } else {
        const memberRoleId = allRoles.find(role => role.role === roleName)?.id;
        const res = await getDocumentsForRoleId(memberRoleId, csrf);
        const responseBody = res.payload?.data && !res.error
          ? res.payload.data
          : undefined;
        return responseBody?.length > 0 ? responseBody : fallback;
      }
    }

    if (csrf) {
      getDocuments().then(docs => setDocuments(docs));
    }
  }, [csrf]);
};

export const generate = (title, documents) => {
  return (
    <Card>
      <CardHeader avatar={<PdfIcon />} title={title} />
      <List dense>
        {documents.map(doc => (
          <GuideLink key={doc.id} id={doc.id} name={doc.name} description={doc.description} url={doc.url} />
        ))}
      </List>
    </Card>
  );
};

const GuidesPanel = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [documents, setDocuments] = useState([]);
  fetchDocumentsForRole('MEMBER', selectRoles(state), csrf, setDocuments, fallbackPdfs, state.mockuments);

  return generate('Team Member Resources', documents);
};

export default GuidesPanel;
